package com.uni.gameclient.socket.controller;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class ReconnectingWebSocketClient {

    private Consumer<String> onMessageListener;
    private Consumer<String> onStatChangeListener;

    private  URI serverUri;
    private WebSocketClient client;
    private final AtomicBoolean shouldReconnect = new AtomicBoolean(true);
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private final ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private Timer reconnectTimer;

    // Configuration
    private final int maxReconnectAttempts = 10;
    private final long minReconnectDelay = 1000; // 1 second
    private final long maxReconnectDelay = 30000; // 30 seconds
    private final double reconnectDecay = 1.5;

    public ReconnectingWebSocketClient(String  serverUriStr) {
        try {
            this.serverUri = new URI(serverUriStr);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Ungültige websocket.server-uri: " + serverUriStr, e);
        }
        //connect();
    }

    public void setUri(String  serverUriStr) {
        try {
            this.serverUri = new URI(serverUriStr);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Ungültige websocket.server-uri: " + serverUriStr, e);
        }
    }

    public void setOnMessageListener(Consumer<String> listener) {
        this.onMessageListener = listener;
    }

    protected void onMessageReceived(String message) {
        if (onMessageListener != null) {
            onMessageListener.accept(message);
        }
    }

    public void onStatChangeListener(Consumer<String> listener) {
        this.onStatChangeListener = listener;
    }

    protected void onStatChangeReceived(String message) {
        if (onStatChangeListener != null) {
            onStatChangeListener.accept(message);
        }
    }

    public  void connect() {
        client = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected successfully");
                reconnectAttempts.set(0);

                // Send queued messages
                String message;
                while ((message = messageQueue.poll()) != null) {
                    send(message);
                }

                onStatChangeReceived("CONNECTED to " + serverUri.toString());
            }

            @Override
            public void onMessage(String message) {
                onMessageReceived(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Connection closed: " + code + " - " + reason);

                if (shouldReconnect.get() && !isMaxReconnectsReached()) {
                    scheduleReconnect();
                }

                onDisconnected(code, reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket error: " + ex.getMessage());
                onErrorOccurred(ex);
            }
        };

        // Configure client
        client.setConnectionLostTimeout(10);
        client.setTcpNoDelay(true);
        client.setReuseAddr(true);

        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Connection interrupted: " + e.getMessage());
        }
    }

    private void scheduleReconnect() {
        long delay = getReconnectDelay();
        int attempt = reconnectAttempts.incrementAndGet();

        System.out.println(String.format(
                "Reconnecting in %d ms (attempt #%d)",
                delay, attempt
        ));

        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }

        reconnectTimer = new Timer();
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (shouldReconnect.get()) {
                    connect();
                }
            }
        }, delay);
    }

    private long getReconnectDelay() {
        long delay = (long) (minReconnectDelay *
                Math.pow(reconnectDecay, reconnectAttempts.get()));
        return Math.min(delay, maxReconnectDelay);
    }

    private boolean isMaxReconnectsReached() {
        return reconnectAttempts.get() >= maxReconnectAttempts;
    }

    public void send(String message) {
        if (client != null && client.isOpen()) {
            client.send(message);
        } else {
            // Queue message for sending after reconnection
            messageQueue.offer(message);
        }
    }

    public void sendBinary(byte[] data) {
        if (client != null && client.isOpen()) {
            client.send(data);
        }
    }

    public void disconnect() {
        shouldReconnect.set(false);
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
        if (client != null) {
            client.close(1000, "Client disconnecting");
        }
    }
   public void SendText(String message) {
        send(message);
    }


    // Override these methods for custom behavior

    public  void onConnected() {  }
    //protected void onMessageReceived(String message) {}
    protected void onDisconnected(int code, String reason) {}
    protected void onErrorOccurred(Exception ex) {}
}