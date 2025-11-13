package com.uni.gameclient.game.models;

import com.sun.tools.javac.Main;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class GameBoard {
    private BoardSize size;
    private Tile[][] tiles;

    private GameBoard(BoardSize size) {
        this.size = size;
        this.tiles = new Tile[size.getRows()][size.getCols()];

    }
    public BoardSize getSize() {
        return size;
    }
    public Tile[][] getTiles() {
        return tiles;
    }


    public void setTile(int row, int col, Tile tile) {
        this.tiles[row][col] = tile;
    }

    // Tile generation and assignment
    public static GameBoard generateBoard(BoardSize size) {
        GameBoard board = new GameBoard(size);
        int rows = size.getRows();
        int cols = size.getCols();

        //ecken generieren
        board.setTile(0, 0, new Tile(List.of("RIGHT", "DOWN"), "CORNER"));
        board.setTile(0, cols - 1, new Tile(List.of("LEFT", "DOWN"), "CORNER"));
        board.setTile(rows - 1, 0, new Tile(List.of("UP", "RIGHT"), "CORNER"));
        board.setTile(rows - 1, cols - 1, new Tile(List.of("UP", "LEFT"), "CORNER"));

        //Randkreuzungen generieren
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                if(i%2 == 0 && j % 2 == 0){
                    boolean isEdge = (i == 0 || j == 0 || i == rows -1 || j == cols -1);
                    if(isEdge && board.getTiles()[i][j] == null){
                        List<String> entrances = generateEdgeCrossEntrances(i, j, rows, cols);
                        Tile t = new Tile(entrances, "CROSS");
                        board.setTile(i, j, t);
                    }
                }
            }
        }

        // Innenkreuzungen generieren
        for (int i = 2; i < rows - 1; i += 2) {
            for (int j = 2; j < cols - 1; j += 2) {
                if (board.getTiles()[i][j] == null) {
                    Tile t = new Tile(generateEntrancesForTypeWithRandomRotation("CROSS"), "CROSS");
                    board.setTile(i, j, t);
                }
            }
        }

        // Restliche Tiles zufällig füllen
        fillRandomTiles(board);
        try{

            System.out.println("Working dir: " + System.getProperty("user.dir"));

            String pathBonus = System.getProperty("user.dir") + "/src/main/resources/treasure/treasure.txt";

            List<String> lines = Files.readAllLines(Paths.get(pathBonus));

            //SetTreasures(board, lines);
        } catch (Exception e){
            e.printStackTrace();
        }

        return board;
    }

    private static void fillRandomTiles(GameBoard board) {
        int rows = board.getSize().getRows();
        int cols = board.getSize().getCols();
        int totalTiles = rows * cols;
        int totalCards = totalTiles + 1; // +1 for the spare tile

        //basiswerte aus 7x7 Board
        int baseRows = 7;
        int baseCols = 7;
        int baseCorners = 20;
        int baseCrosses = 18;
        int baseStraights = 12;

        // Skalierungsfaktoren
        int corners = totalCards * baseCorners / (baseRows * baseCols + 1);
        int crosses = totalCards * baseCrosses / (baseRows * baseCols + 1);
        int straights = totalCards * baseStraights / (baseRows * baseCols + 1);

        // rundungsfehler ausgleichen
        int sum = corners + crosses + straights;
        int diff = totalCards - sum;
        if (diff > 0) straights += diff;

        // bereits gesetzte tiles zählen und die anzahl reduzieren
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile t = board.getTiles()[i][j];
                if (t != null && t.getType() != null) {
                    switch (t.getType()) {
                        case "CORNER":
                            corners--;
                            break;
                        case "CROSS":
                            crosses--;
                            break;
                        case "STRAIGHT":
                            straights--;
                            break;
                    }
                }
            }
        }

        //Restkarten in eine Liste packen
        List<String> remainingTiles = new ArrayList<>();
        for (int i = 0; i < corners; i++) remainingTiles.add("CORNER");
        for (int i = 0; i < crosses; i++) remainingTiles.add("CROSS");
        for (int i = 0; i < straights; i++) remainingTiles.add("STRAIGHT");

        Collections.shuffle(remainingTiles);

        //Restkarten zuweisen
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board.getTiles()[i][j] == null && index < remainingTiles.size()) {
                    String type = remainingTiles.get(index++);
                    Tile t = new Tile(generateEntrancesForTypeWithRandomRotation(type), type);
                    board.setTile(i, j, t);
                }
            }
        }
    }
    private static List<String> generateEdgeCrossEntrances(int row, int col, int rows, int cols) {
        List<String> entrances = new ArrayList<>();

        boolean topEdge = (row == 0);
        boolean bottomEdge = (row == rows - 1);
        boolean leftEdge = (col == 0);
        boolean rightEdge = (col == cols - 1);

        List<String> allDirs = List.of("UP", "RIGHT", "DOWN", "LEFT");
        entrances.addAll(allDirs);

        if (topEdge) entrances.remove("UP");
        if (bottomEdge) entrances.remove("DOWN");
        if (leftEdge) entrances.remove("LEFT");
        if (rightEdge) entrances.remove("RIGHT");

   
        if (entrances.size() < 3) {
            if (topEdge) entrances.add("DOWN");
            if (bottomEdge) entrances.add("UP");
            if (leftEdge) entrances.add("RIGHT");
            if (rightEdge) entrances.add("LEFT");
        }

        return entrances;
    }
    private static List<String> generateEntrancesForTypeWithRandomRotation(String type) {
    Random rnd = new Random();
        switch (type) {
            case "CORNER":
            List<List<String>> corners = List.of(
                List.of("UP", "RIGHT"),
                List.of("RIGHT", "DOWN"),
                List.of("DOWN", "LEFT"),
                List.of("LEFT", "UP")
            );
            return new ArrayList<>(corners.get(rnd.nextInt(corners.size())));

        case "STRAIGHT":
            List<List<String>> straights = List.of(
                List.of("UP", "DOWN"),
                List.of("LEFT", "RIGHT")
            );
            return new ArrayList<>(straights.get(rnd.nextInt(straights.size())));

        case "CROSS":
            List<List<String>> crosses = List.of(
                List.of("UP", "LEFT", "RIGHT"),
                List.of("UP", "RIGHT", "DOWN"),
                List.of("RIGHT", "DOWN", "LEFT"),
                List.of("DOWN", "LEFT", "UP")
            );
            return new ArrayList<>(crosses.get(rnd.nextInt(crosses.size())));

        default:
            return new ArrayList<>();
    }


}

    private static void SetTreasures(GameBoard board, List<String> treasure) {

        int number_field = board.size.getCols() * board.size.getRows();
        Integer[] occupied_positions = new Integer[number_field];
        for (int i = 0; i < occupied_positions.length; i++) {
            occupied_positions[i] = 0;
        }
        int index=0;

        int counttreasures = treasure.size();
        Treasure [] treasures = new Treasure[counttreasures];

        for(String str : treasure) {
            int id =1;
            treasures[id] = new Treasure(id, str);
            occupied_positions[index++] = id++;
        }


        List<Integer> list = Arrays.asList(occupied_positions);
        Collections.shuffle(list);
        for (int i = 0; i < number_field; i++) {
            int row = i / board.getSize().getCols();
            int col = i % board.getSize().getCols();
            if (list.get(i) != 1) {
                Tile t = board.getTiles()[row][col];
                if (t != null) {
                    t.setTreasure(treasures[list.get(i)]);
                    board.setTile(row,col,t);
                }
            }
        }
    }

    //DEMO 
    public static void printBoard(GameBoard board) {
    Tile[][] tiles = board.getTiles();
    int rows = board.getSize().getRows();
    int cols = board.getSize().getCols();

    System.out.println("=== GAME BOARD VISUALIZATION ===");
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            Tile t = tiles[i][j];
            if (t == null) {
                System.out.print(" - ");
            } else {
                System.out.print(String.format("%-3s", getTileSymbol(t)));
            }
        }
        System.out.println();
    }
}


public static String getTileSymbol(Tile t) {
    List<String> e = t.getEntrances();
    if (e == null || e.isEmpty()) return " ? ";

    // Ecken
    if (e.size() == 2 && e.contains("UP") && e.contains("RIGHT")) return "╚";
    if (e.size() == 2 && e.contains("RIGHT") && e.contains("DOWN")) return "╔";
    if (e.size() == 2 && e.contains("DOWN") && e.contains("LEFT")) return "╗";
    if (e.size() == 2 && e.contains("LEFT") && e.contains("UP")) return "╝";

    // Gerade
    if (e.size() == 2 && e.contains("LEFT") && e.contains("RIGHT")) return "═";
    if (e.size() == 2 && e.contains("UP") && e.contains("DOWN")) return "║";

    // T-Stücke
    if (e.size() == 3) {
        if (!e.contains("UP")) return "╦";
        if (!e.contains("RIGHT")) return "╣";
        if (!e.contains("DOWN")) return "╩";
        if (!e.contains("LEFT")) return "╠";
    }

    // Fallback
    return " ? ";
}

    public static String getTileImageName(Tile t) {
        List<String> e = t.getEntrances();
        if (e == null || e.isEmpty()) return " ? ";

        // Ecken
        if (e.size() == 2 && e.contains("UP") && e.contains("RIGHT")) return "ecke_AB.png";
        if (e.size() == 2 && e.contains("RIGHT") && e.contains("DOWN")) return "ecke_BC.png";
        if (e.size() == 2 && e.contains("DOWN") && e.contains("LEFT")) return "ecke_CD.png";
        if (e.size() == 2 && e.contains("LEFT") && e.contains("UP")) return "ecke_AD.png";

        // Gerade
        if (e.size() == 2 && e.contains("LEFT") && e.contains("RIGHT")) return "gerade_BD.png";
        if (e.size() == 2 && e.contains("UP") && e.contains("DOWN")) return "gerade_AC.png";

        // T-Stücke
        if (e.size() == 3) {
            if (!e.contains("UP")) return "kreuzung_BCD.png";
            if (!e.contains("RIGHT")) return "kreuzung_ACD.png";
            if (!e.contains("DOWN")) return "kreuzung_ABD.png";
            if (!e.contains("LEFT")) return "kreuzung_ABC.png";
        }


        // Fallback
        return " ? ";
    }


}
