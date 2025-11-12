package com.uni.gameclient.game.classes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class Scoreboard {

    public VBox Scoreboard() {
        return createScoreBoard();
    }

    public VBox createScoreBoard() {

    // === Root-Container: alles untereinander stapeln ===
    VBox root = new VBox(10);
    root.setPadding(new Insets(10));
    root.setAlignment(Pos.TOP_CENTER);
    root.setBackground(new Background(
            new BackgroundFill(Color.web("#cfe6ff"), CornerRadii.EMPTY, Insets.EMPTY) // hellblau
    ));
    root.setPrefWidth(600);  // kannst du anpassen
    root.setPrefHeight(300); // kannst du anpassen

    // === (1) Haupttitel "ERGEBNISSE" ===
    Label titleErgebnisse = new Label("ERGEBNISSE");
    titleErgebnisse.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 24));
    titleErgebnisse.setTextFill(Color.RED); // wir machen gleich bunt -> siehe unten Trick
    titleErgebnisse.setStyle("-fx-effect: dropshadow( gaussian , black , 2 , 0 , 1 , 1 );");

    // Wenn du wirklich jeden Buchstaben andersfarbig willst wie im Bild,
    // kannst du anstatt Label ein HBox aus einzelnen farbigen Labels bauen:
    HBox colorfulErgebnisseTitle = createRainbowTitle("ERGEBNISSE");

    // === (2) Tabelle: Kopf + Spielerzeilen ===
    GridPane table = new GridPane();
    table.setHgap(15);
    table.setVgap(6);
    table.setPadding(new Insets(10));
    table.setAlignment(Pos.TOP_CENTER);

    // Spaltenbreiten optisch kontrollieren
    ColumnConstraints colRank = new ColumnConstraints(30);   // "#"
    ColumnConstraints colName = new ColumnConstraints(150);  // "NAME"
    ColumnConstraints colSchatz = new ColumnConstraints(80); // "SCHÄTZE"
    ColumnConstraints colAch = new ColumnConstraints(120);   // "ACHIEVEMENTS"
    ColumnConstraints colTotal = new ColumnConstraints(60);  // "TOTAL"
    table.getColumnConstraints().addAll(colRank, colName, colSchatz, colAch, colTotal);

    // Kopfzeile
    Label hRank  = headerLabel("#");
    Label hName  = headerLabel("NAME");
    Label hLoot  = headerLabel("SCHÄTZE");
    Label hAch   = headerLabel("ACHIEVEMENTS");
    Label hTotal = headerLabel("TOTAL");

    table.add(hRank,  0, 0);
    table.add(hName,  1, 0);
    table.add(hLoot,  2, 0);
    table.add(hAch,   3, 0);
    table.add(hTotal, 4, 0);

    // Beispiel-Datenzeilen
    addPlayerRow(table, 1, "mario_woodi", 6, 3, 9, 1);
    addPlayerRow(table, 2, "player2",     7, 1, 8, 2);
    addPlayerRow(table, 2, "player3",     3, 1, 4, 3);
    addPlayerRow(table, 4, "player4",     2, 0, 2, 4);

    // === (3) Untertitel "ACHIEVEMENTS" (bunt) ===
    HBox colorfulAchievementsTitle = createRainbowTitle("ACHIEVEMENTS");

    // === (4) Die weißen Kästchen (4 Felder nebeneinander) ===
    HBox achievementBoxes = new HBox(20);
    achievementBoxes.setAlignment(Pos.CENTER);
    achievementBoxes.setPadding(new Insets(5, 0, 10, 0));

    for (int i = 0; i < 4; i++) {
        TextField tf = new TextField();
        tf.setPrefWidth(120);
        tf.setPrefHeight(30);
        tf.setEditable(false);
        tf.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-font-family: 'Comic Sans MS';" +
                        "-fx-font-size: 12px;"
        );
        achievementBoxes.getChildren().add(tf);
    }

    // === (5) Deko-Balken ganz unten (grün / gelb / grün gestapelt)
    VBox bar = new VBox(0);
    Rectangle r1 = new Rectangle(600, 6, Color.web("#2a8d1c")); // dunkelgrün
    Rectangle r2 = new Rectangle(600, 6, Color.web("#e4d043")); // gold/gelb
    Rectangle r3 = new Rectangle(600, 6, Color.web("#2a8d1c")); // dunkelgrün
    bar.getChildren().addAll(new StackPane(r1), new StackPane(r2), new StackPane(r3));

    // === Alles zusammensetzen ===
    root.getChildren().addAll(
            colorfulErgebnisseTitle,
            table,
            colorfulAchievementsTitle,
            achievementBoxes,
            bar
    );

    return root;
}

/* -------------------------------------------------
   Hilfsfunktionen
   ------------------------------------------------- */

// bunte Überschrift wie im Screenshot (jeder Buchstabe eigene Farbe)
private HBox createRainbowTitle(String text) {
    Color[] colors = new Color[] {
            Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.ORANGE, Color.CYAN
    };

    HBox box = new HBox(0);
    box.setAlignment(Pos.CENTER);

    for (int i = 0; i < text.length(); i++) {
        char ch = text.charAt(i);
        Label lbl = new Label(String.valueOf(ch));
        lbl.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 20));
        lbl.setTextFill(colors[i % colors.length]);
        lbl.setStyle("-fx-effect: dropshadow( gaussian , black , 2 , 0 , 1 , 1 );");
        box.getChildren().add(lbl);
    }

    return box;
}

// Tabellen-Header-Label in gelb mit Outline-Effekt
private Label headerLabel(String text) {
    Label lbl = new Label(text);
    lbl.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
    lbl.setTextFill(Color.web("#fff7a3")); // leichtes Gelb
    lbl.setStyle(
            "-fx-effect: dropshadow(gaussian, black, 3, 0, 1, 1);" +
                    "-fx-text-fill: #fff7a3;"
    );
    return lbl;
}

// Eine Spielerzeile einfügen
// rowIndex = Zeile in der GridPane (1 = erste nach Header)
private void addPlayerRow(GridPane table,
                          int rank,
                          String name,
                          int schatz,
                          int ach,
                          int total,
                          int rowIndex) {

    Label lRank  = rowLabel(String.valueOf(rank));
    Label lName  = rowLabel(name);
    Label lLoot  = rowLabel(String.valueOf(schatz));
    Label lAch   = rowLabel(String.valueOf(ach));
    Label lTotal = rowLabel(String.valueOf(total));

    table.add(lRank,  0, rowIndex);
    table.add(lName,  1, rowIndex);
    table.add(lLoot,  2, rowIndex);
    table.add(lAch,   3, rowIndex);
    table.add(lTotal, 4, rowIndex);
}

private Label rowLabel(String text) {
    Label lbl = new Label(text);
    lbl.setFont(Font.font("Consolas", 14)); // monospace ähnlich Screenshot
    lbl.setTextFill(Color.BLACK);
    return lbl;
}}