package com.uni.gameclient.game.classes;

import com.uni.gameclient.socket.controller.ReconnectingWebSocketClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SomeService {
    private final ReconnectingWebSocketClient first;
    private final ReconnectingWebSocketClient second;

    public SomeService(@Qualifier("actionClient") ReconnectingWebSocketClient first,
                       @Qualifier("broadcastClient") ReconnectingWebSocketClient second) {
        this.first = first;
        this.second = second;
    }

    public void sendToBoth(String msg) {
        first.SendText(msg);
        second.SendText(msg);
    }
}