package com.uni.gameclient.game.classes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.gameclient.game.models.BoardSize;
import com.uni.gameclient.game.models.GameBoard;
import com.uni.gameclient.socket.controller.ReconnectingWebSocketClient;
import com.uni.gameclient.game.database.DataManipulation;
import com.uni.gameclient.game.database.Gameserver;
import com.uni.gameclient.rest.model.PostSend;
import com.uni.gameclient.rest.service.Gameserverservice;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;


public class MainframeUi {


    //Rest GameServer Service
    private  Gameserverservice gameserverservice;

    // UI state
    private Stage stage;
    // Platzhalter für den Bereich inden die veschiedenen Ansichten rein sollen
    private VBox viewContentBox;
    //Platzhalter linkes Panel
    private VBox controlContentBox;
    //Platzhalter Alles
    private HBox allContentBox;
    //Textfelder für verbundene Serverinfo
    private TextField textfield_connected_server_name;
    private TextField textfield_connected_server_uri;
    private TextField textfield_connected_server_maxplayers;
    private TextField textfield_connected_server_current_player_count;
    private TextField textfield_connected_server_status;
    private TextArea actionLog;




    private Gameserver active_gameserver;

    // die verschiednen Anzeigen für das rechte Panel
    private GameBoard gameBoard;
    private VBox scoreboardBox;
    private SpielfeldAnzeigeFX spielfeldanzeige;
    private SpielfeldAnzeigeFX2 spielfeldanzeige2;


    // dein Zustand
    private int Visibletile = 0; // oder 1


    //private TextArea actionLog;

    private final ReconnectingWebSocketClient actionClient;
    private final ReconnectingWebSocketClient broadcastClient;



    public MainframeUi(ReconnectingWebSocketClient actionClient,
                       ReconnectingWebSocketClient broadcastClient
                       ,Gameserverservice gameserverservice) {
        this.actionClient = actionClient;
        this.broadcastClient = broadcastClient;
        this.gameserverservice = gameserverservice;
    }


    public HBox spielpanelAnzeigen2(Consumer<String> callback) {
        HBox hbox = new HBox(10); // spacing = 10px

        Button toggleViewBtn = new Button("toggle\n Fenster");
        Button toggleBoardBtn = new Button("toggle\n Spielfeld");
        Button message2      = new Button("Meldung 2");

        HBox bottomControls = new HBox(8, toggleViewBtn, toggleBoardBtn, message2);
        bottomControls.setPadding(new Insets(8));

        toggleViewBtn.setOnAction(e -> toggleVisibleTile());
        toggleBoardBtn.setOnAction(e -> toggleBoardLayout());

        message2.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Message2");
            alert.setHeaderText("Operation Successful2");
            alert.setContentText("The data was killed successfully!");
            alert.showAndWait();

            if (callback != null) {
                callback.accept("Button 2 wurde gedrückt");
            }
        });

        hbox.getChildren().addAll(bottomControls);
        return hbox;
    }



    public HBox spielpanelAnzeigen(     Consumer<String> callback) {
        // Inner panel (horizontal)
        HBox hbox = new HBox(20);
        // === Jetzt die Buttons ===
        Button toggleViewBtn = new Button("toggle\n Fenster");
        Button toggleBoardBtn = new Button("toggle\n Spielfeld");
        Button button_message2 = new Button("Meldung 2");
        HBox bottomControls = new HBox(8, toggleViewBtn, toggleBoardBtn, button_message2);
        bottomControls.setPadding(new Insets(8));

        toggleViewBtn.setOnAction(e ->
                toggleVisibleTile());
        toggleBoardBtn.setOnAction(e -> toggleBoardLayout());
        button_message2.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Message2");
            alert.setHeaderText("Operation Successful2");
            alert.setContentText("The data was killed successfully!");
            alert.showAndWait(); // Show and wait for user to close it
            // Sende Ergebnis an Elternkomponente
            callback.accept("Button 2 wurde gedrückt");
        });
        hbox.getChildren().addAll(bottomControls);
        return hbox;
    }


    private void toggleVisibleTile() {
        if (Visibletile == 0) {
            Visibletile = 1;
        } else {
            Visibletile = 0;
        }
        updateVisibleTileContent();
    }

    private void toggleBoardLayout() {
        gameBoard = GameBoard.generateBoard(gameBoard.getSize());
        updateVisibleTileContent();
    }



    private void updateVisibleTileContent() {
        // UI-Änderungen NUR auf dem FX-Thread
        Platform.runLater(() -> {
            viewContentBox.getChildren().clear();

            if (Visibletile == 1) {
                // Scoreboard anzeigen
                if (scoreboardBox == null) {
                    Scoreboard scoreboard = new Scoreboard();
                    scoreboardBox = scoreboard.Scoreboard(); // nehme an: baut VBox
                    scoreboardBox.setPadding(new Insets(10));

                }

                viewContentBox.getChildren().add(scoreboardBox);

            } else {
                // Spielfeld anzeigen
                if (spielfeldanzeige == null) {
                    if (gameBoard == null) {
                        BoardSize boardSize = new BoardSize(9,9);
                        gameBoard = GameBoard.generateBoard(boardSize);
                    }


                }
                spielfeldanzeige2 = new SpielfeldAnzeigeFX2(gameBoard);
                spielfeldanzeige2.setPadding(new Insets(10));
                viewContentBox.getChildren().add(spielfeldanzeige2);
            }
        });
    }


    public void getGameserverInfo() throws JsonProcessingException, SQLException {
        // Hier

        List<Gameserver> alleGameservers = gameserverservice.getAllGameserver();
        DataManipulation dataManipulation = new DataManipulation();
        dataManipulation.insertOrUpdateData(alleGameservers);
    }



    public void setGameserver(Gameserver gameserver) {
        this.active_gameserver = gameserver;
        this.textfield_connected_server_name.setText(gameserver.getName());
        this.textfield_connected_server_uri.setText(gameserver.getUri());
        this.textfield_connected_server_maxplayers.setText(String.valueOf(gameserver.getMaxPlayers()));
        this.textfield_connected_server_current_player_count.setText(String.valueOf(gameserver.getCurrentPlayerCount()));
        this.textfield_connected_server_status.setText(gameserver.getStatus());
        updateVisibleTileContent();
        this.actionClient.setUri(gameserver.getUri()+"/client/actions");
        this.broadcastClient.setUri(gameserver.getUri()+"/server/broadcast");
        this.actionClient.connect();
        this.broadcastClient.connect();
    }

   public void closeGameserver() {
        this.actionClient.disconnect();
        this.broadcastClient.disconnect();
   }



    public void show() {
        Platform.runLater(() -> {

            // ==== Panel alles zusammen====
            allContentBox = new HBox(10);
            allContentBox.setPadding(new Insets(10));
            allContentBox.setBorder(new Border(new BorderStroke(Paint.valueOf("black")
                    , BorderStrokeStyle.SOLID,
                    new CornerRadii(5), BorderWidths.DEFAULT
            )));

            // ==== Linkes Panel (Buttons + Dropdowns) ====
            controlContentBox = new VBox(10);
            controlContentBox.setPadding(new Insets(10));
            controlContentBox.setBorder(new Border(new BorderStroke(Paint.valueOf("black")
                    , BorderStrokeStyle.SOLID,
                    new CornerRadii(5), BorderWidths.DEFAULT
            )));

            // ==== rechtes Panel (Feldanzeigen) ====
            viewContentBox = new VBox(10);
            viewContentBox.setPadding(new Insets(10));

            Stage stage = new Stage();
            stage.setTitle("Das Verrückte-Labyrinth");

            //Elemente des Controlbox
            Button button_get_server = new Button("Serverliste aktualisieren");
            Button button_get_serverrest = new Button("Serverliste rest");

            Button button_connect_to_server = new Button("Verbinde Spielserver");

            Button button_disconnect_from_server = new Button("Trenne Spielserver");

            ComboBox combo_available_server = new ComboBox<>();
            combo_available_server.setPrefWidth(250);


            textfield_connected_server_name = new TextField("no name");
            textfield_connected_server_name.setPrefWidth(250);
            textfield_connected_server_uri = new TextField("not URI");
            textfield_connected_server_uri.setPrefWidth(250);
            textfield_connected_server_maxplayers = new TextField("0");
            textfield_connected_server_maxplayers.setPrefWidth(250);
            textfield_connected_server_current_player_count = new TextField("0");
            textfield_connected_server_current_player_count.setPrefWidth(250);
            textfield_connected_server_status = new TextField("not connected");
            textfield_connected_server_status.setPrefWidth(250);

            // === Zuerst TextAreas deklarieren ===
            actionLog = new TextArea();
            actionLog.setEditable(false);
            actionLog.setPrefWidth(500);
            actionLog.setPrefHeight(500);
            actionLog.setPromptText("ActionClient Nachrichten...");


            //controlContentBox.getChildren().addAll(button_get_server, combo_available_server,button_connect_to_server,button_disconnect_from_server,textfield_connected_server);

            button_get_server.setOnAction(e -> {
                try {
                    Gameserver[] newGameserver = new Gameserver[0];
                    List<Gameserver> serverList = DataManipulation.getAllServers();
                    Gameserver[] myGameserver = serverList.toArray(new Gameserver[0]);
                    combo_available_server.getItems().clear();
                    if(myGameserver.length>0){
                        for (int i = 0; i < myGameserver.length; i++) {
                            combo_available_server.getItems().add(myGameserver[i].getUri());
                        }
                        combo_available_server.getSelectionModel().select(0);
                    }


                    System.out.printf("[DEBUG] Hole Serverliste...\n");
                } catch (Exception ex) {

                }
            });

            button_connect_to_server.setOnAction(e -> {
                try {

                } catch (Exception ex) {
                }
            });

            button_disconnect_from_server.setOnAction(e -> {
                try {
                    closeGameserver();
                    System.out.printf("[DEBUG] Hole Serverliste...\n");
                } catch (Exception ex) {

                }
            });

            button_get_serverrest.setOnAction(e -> {
                try {
                    getGameserverInfo();
                    String ServerUri = combo_available_server.getSelectionModel().getSelectedItem().toString();
                    active_gameserver = DataManipulation.getServerByUri(ServerUri);
                    System.out.printf("[DEBUG] Hole Serverliste...\n");
                    setGameserver(active_gameserver);
                } catch (Exception ex) {

                }
            });


            HBox hbox_button_get_serverrest = new HBox(10, button_get_server, button_get_serverrest);

            HBox control= spielpanelAnzeigen(msg -> {
                Platform.runLater(() -> actionLog.appendText("[UI Callback] " + msg + "\n"));
            });

            VBox vbox_server_discover = new VBox(10);
            vbox_server_discover.getChildren().addAll(control,hbox_button_get_serverrest, combo_available_server);


            vbox_server_discover.setAlignment(Pos.CENTER_LEFT);
            vbox_server_discover.setPadding(new Insets(8));


            HBox hbox_server_connection = new HBox(10, button_connect_to_server, button_disconnect_from_server);
            hbox_server_connection.setAlignment(Pos.CENTER_LEFT);
            hbox_server_connection.setPadding(new Insets(8));


            Label label_name = new Label("Servername:");
            label_name.setPrefWidth(100);
            label_name.setAlignment(Pos.CENTER_RIGHT);
            HBox box_label_name = new HBox(10,label_name,textfield_connected_server_name);
            box_label_name.setAlignment(Pos.CENTER_LEFT);
            box_label_name.setPadding(new Insets(8));

            Label label_Uri = new Label("Server Uri:");
            label_Uri.setPrefWidth(100);
            label_Uri.setAlignment(Pos.CENTER_RIGHT);
            HBox box_label_uri = new HBox(10,label_Uri,textfield_connected_server_uri);
            box_label_uri.setAlignment(Pos.CENTER_LEFT);
            box_label_uri.setPadding(new Insets(8));

            Label label_maxplayers = new Label("Maximale Spieler :");
            label_maxplayers.setPrefWidth(100);
            label_maxplayers.setAlignment(Pos.CENTER_RIGHT);
            HBox box_label_maxplayers = new HBox(10,label_maxplayers,textfield_connected_server_maxplayers);
            box_label_maxplayers.setAlignment(Pos.CENTER_LEFT);
            box_label_maxplayers.setPadding(new Insets(8));

            Label label_current_players = new Label("Aktuelle Spieler:");
            label_current_players.setPrefWidth(100);
            label_current_players.setAlignment(Pos.CENTER_RIGHT);
            HBox box_label_current_players = new HBox(10,label_current_players,textfield_connected_server_current_player_count);
            box_label_current_players.setAlignment(Pos.CENTER_LEFT);
            box_label_current_players.setPadding(new Insets(8));

            Label label_state = new Label("Serverstatus:");
            label_state.setPrefWidth(100);
            label_state.setAlignment(Pos.CENTER_RIGHT);
            HBox box_label_state = new HBox(10,label_state,textfield_connected_server_status);
            box_label_state.setAlignment(Pos.CENTER_LEFT);
            box_label_state.setPadding(new Insets(8));




            VBox all_tools = new VBox(1,
                    vbox_server_discover,
                    hbox_server_connection,
                    box_label_name,
                    box_label_uri,
                    box_label_maxplayers,
                    box_label_current_players,
                    box_label_state,
                    actionLog);
            all_tools.setPadding(new Insets(10));
            all_tools.setPadding(new Insets(10));



            broadcastClient.setOnMessageListener(msg -> {
                Platform.runLater(() -> actionLog.appendText("[Broadcast] " + msg + "\n"));
            });
            broadcastClient.onStatChangeListener(msg -> {
                Platform.runLater(() -> actionLog.appendText("[StateChanged] " + msg + "\n"));
            });
            actionClient.onStatChangeListener(msg -> {
                Platform.runLater(() -> actionLog.appendText("[StateChanged] " + msg + "\n"));
            });








            controlContentBox.getChildren().addAll(all_tools);
            allContentBox.getChildren().addAll(controlContentBox, viewContentBox);



            updateVisibleTileContent();
            // *** <- HIER: dieser Box gehört Scoreboard ODER Spielfeld ***



            // === Fenster ===
            Scene scene = new Scene(allContentBox, 800, 800);
            stage.setScene(scene);
            stage.setOnCloseRequest(evt -> {
                try { actionClient.disconnect(); } catch (Exception ignored) {}
                try { broadcastClient.disconnect(); } catch (Exception ignored) {}
                Platform.exit();
            });
            stage.show();
        });
    }





}