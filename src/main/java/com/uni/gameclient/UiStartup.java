package com.uni.gameclient;


import com.uni.gameclient.game.classes.MainframeUi;
import com.uni.gameclient.socket.controller.ReconnectingWebSocketClient;
import com.uni.gameclient.game.database.DataManipulation;
import com.uni.gameclient.rest.service.Gameserverservice;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UiStartup {

    private final ReconnectingWebSocketClient actionClient;
    private final ReconnectingWebSocketClient broadcastClient;
    private final MainframeUi mainframeui;

    private   Gameserverservice gameserverservice;
    private Object SpielfeldAnzeigeFX;

    public UiStartup(@Qualifier("actionClient") ReconnectingWebSocketClient actionClient,
                     @Qualifier("broadcastClient") ReconnectingWebSocketClient broadcastClient) {
        this.gameserverservice = new Gameserverservice();
        this.actionClient = actionClient;
        this.broadcastClient = broadcastClient;
        this .mainframeui = new MainframeUi(this.actionClient, this.broadcastClient, this.gameserverservice);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // JavaFX initialisieren (nur einmal)
        try {
            Platform.startup(() -> { /* JavaFX runtime initialized */ });
        } catch (IllegalStateException ignored) {
            // Platform bereits gestartet - ignorieren
        } catch (Throwable t) {
            // bei Problemen nicht abstürzen lassen
            System.err.println("Fehler beim Initialisieren von JavaFX: " + t.getMessage());
        }
        DataManipulation dataManipulation = new DataManipulation();

        mainframeui.show();

    }




}