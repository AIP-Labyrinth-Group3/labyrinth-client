package com.uni.gameclient.socket.config;

import com.uni.gameclient.socket.controller.ReconnectingWebSocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketConfig {

    private String actionUri;
    private String broadcastUri;

    @Bean("actionClient")
    public ReconnectingWebSocketClient actionClient(@Value("${websocket.server-uri-action}") String uri) {
        return new ReconnectingWebSocketClient(uri) {
            //@Override
            //protected void onMessageReceived(String message) {
                // spezifisches Verhalten für Action Client
            //    System.out.println("Action Client empfangen: " + message);
            //}
        };
    }

    @Bean("broadcastClient")
    public ReconnectingWebSocketClient broadcastClient(@Value("${websocket.server-uri-broadcast}") String uri) {
        return new ReconnectingWebSocketClient(uri) {
            //@Override
            //protected void onMessageReceived(String message) {
                // spezifisches Verhalten für Broadcast Client
            //    System.out.println("Broadcast Client empfangen: " + message);
            //}
        };
    }

    public void setActionUri(String actionUri) {
        this.actionUri = actionUri;
    };
    public void setBroadcastUri(String broadcastUri) {
        this.broadcastUri = broadcastUri;
    };
}
