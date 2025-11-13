
package com.uni.gameclient.game.classes;

import com.uni.gameclient.game.models.*;
import javafx.beans.binding.Bindings;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.uni.gameclient.game.models.GameBoard.*;

/**
 * Wiederverwendbares Spielfeld als JavaFX-Region.
 * - Zeichnet ein GameBoard (beliebige Größe, z.B. 7x7)
 * - Klick-Listener pro Feld
 * - Highlight/Selection
 */
public class SpielfeldAnzeigeFX2 extends Region {

    /* ---------- Model ---------- */

    private GameBoard game_board;

    /* ---------- Öffentliche API ---------- */

    /** Listener für Klicks auf ein Feld */
    @FunctionalInterface
    public interface SquareClickListener {
        /** @param file 0..cols-1, @param rank 0..rows-1 (von oben nach unten), @param algebraic z.B. "e4" */
        void onSquareClicked(int file, int rank, String algebraic);
    }

    public SpielfeldAnzeigeFX2() {
        BoardSize boardSize = new BoardSize(9, 9);
        this.game_board = GameBoard.generateBoard(boardSize);

        initCanvas();
        setWidth(640);
        setHeight(640);
    }

    public SpielfeldAnzeigeFX2(GameBoard game_board) {
        this.game_board = game_board;

        initCanvas();
        setWidth(640);
        setHeight(640);
    }

    /** Listener registrieren (optional) */
    public void setSquareClickListener(SquareClickListener l) {
        this.clickListener = l;
    }

    /** Koordinaten an/aus */
    public void setShowCoordinates(boolean show) {
        this.showCoords = show;
        redraw();
    }

    /** Farben anpassen (hell, dunkel, highlight) */
    public void setColors(Color light, Color dark, Color highlight) {
        if (light != null) this.light = light;
        if (dark != null) this.dark = dark;
        if (highlight != null) this.highlight = highlight;
        redraw();
    }

    /* ---------- Interna ---------- */

    private final Canvas canvas = new Canvas();

    // Caches für Images (Performance!)
    private final Map<String, Image> tileCache     = new HashMap<>();
    private final Map<String, Image> bonusCache    = new HashMap<>();
    private final Map<String, Image> treasureCache = new HashMap<>();

    private boolean showCoords = true;

    // Auswahl/Highlight
    private int selRow = -1;
    private int selCol = -1;
    private int highlightRow = -1, highlightCol = -1;
    private SquareClickListener clickListener;

    // Farben
    private Color light     = Color.rgb(240, 217, 181);          // hell
    private Color dark      = Color.rgb(181, 136,  99);          // dunkel
    private Color highlight = Color.color(1.0, 0.84, 0, 0.47);   // gold, alpha

    private void initCanvas() {
        getChildren().add(canvas);

        // Canvas an Größe der Region binden
        canvas.widthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(200, getWidth()),
                widthProperty()));
        canvas.heightProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(200, getHeight()),
                heightProperty()));

        // Bei Größenänderung neu zeichnen
        canvas.widthProperty().addListener((obs, o, n) -> redraw());
        canvas.heightProperty().addListener((obs, o, n) -> redraw());

        // Maus-Interaktion
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            Geom g = computeGeom();
            if (e.getX() >= g.x0 && e.getY() >= g.y0 &&
                    e.getX() < g.x0 + g.size && e.getY() < g.y0 + g.size) {

                int col = (int) ((e.getX() - g.x0) / g.cell);
                int row = (int) ((e.getY() - g.y0) / g.cell);

                selRow = row;
                selCol = col;
                redraw();

                if (clickListener != null) {
                    int file = col;
                    int rank = row;
                    clickListener.onSquareClicked(file, rank, toAlg(col, row));
                }
            }
        });

        // Mindestgröße
        setMinSize(220, 220);
        setPrefSize(640, 640);
        setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }

    /* ---------- Bild-Loading mit Cache ---------- */

    private Image loadImageCached(Map<String, Image> cache, String path) {
        if (path == null) return null;
        return cache.computeIfAbsent(path, p -> {
            try (InputStream in = getClass().getResourceAsStream(p)) {
                if (in != null) {
                    return new Image(in);
                } else {
                    System.err.println("Bild nicht gefunden: " + p);
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Laden: " + p + " → " + e);
                return null;
            }
        });
    }

    /* ---------- Zeichnen ---------- */

    private void redraw() {
        if (game_board == null) return;

        GraphicsContext g = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();

        // Hintergrund
        g.setFill(Color.rgb(32, 33, 36));
        g.fillRect(0, 0, W, H);

        Geom geom = computeGeom();

        // Koordinaten
        if (showCoords) {
            g.setFill(Color.DARKGRAY);
            Font coordFont = Font.font(Math.max(12, geom.cell * 0.18));
            g.setFont(coordFont);

            int cols = game_board.getSize().getCols();
            int rows = game_board.getSize().getRows();

            // Spalten-Koordinaten (oben/unten): 1..cols
            for (int c = 0; c < cols; c++) {
                String s = String.valueOf(c + 1);
                double tw = computeTextWidth(coordFont, s);
                double x = geom.x0 + c * geom.cell + geom.cell / 2.0 - tw / 2.0;
                g.fillText(s, x, geom.y0 - 6);
                g.fillText(s, x, geom.y0 + geom.size + coordFont.getSize() + 4);
            }

            // Reihen-Koordinaten (links/rechts): 1..rows
            for (int r = 0; r < rows; r++) {
                String s = String.valueOf(r + 1);
                double tw = computeTextWidth(coordFont, s);
                double y = geom.y0 + r * geom.cell + (geom.cell + coordFont.getSize()) / 2.0 - 2;
                g.fillText(s, geom.x0 - tw - 6, y);
                g.fillText(s, geom.x0 + geom.size + 6, y);
            }
        }

        // Felder + Inhalte
        Tile[][] tiles = game_board.getTiles();
        int rows = game_board.getSize().getRows();
        int cols = game_board.getSize().getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean isDark = ((r + c) % 2 == 1);
                g.setFill(isDark ? dark : light);
                double x = geom.x0 + c * geom.cell;
                double y = geom.y0 + r * geom.cell;
                g.fillRect(x, y, geom.cell, geom.cell);

                // Highlight (Klick)
                if (r == selRow && c == selCol) {
                    g.setFill(highlight);
                    g.fillRect(x, y, geom.cell, geom.cell);
                    g.setStroke(Color.BLACK);
                    g.setLineWidth(2);
                    g.strokeRect(x + 1, y + 1, geom.cell - 2, geom.cell - 2);
                }

                // Optionales weiteres Highlight per API
                if (r == highlightRow && c == highlightCol) {
                    g.setStroke(Color.GOLD);
                    g.setLineWidth(3);
                    g.strokeRect(x + 1.5, y + 1.5, geom.cell - 3, geom.cell - 3);
                }

                Tile tile = tiles[r][c];

                // Bildnamen bestimmen
                String tileName = getTileImageName(tile); // aus GameBoard.* importiert
                Image imgTile = loadImageCached(tileCache,
                        tileName != null ? "/pieces/" + tileName : null);

                Image imgBonus = null;
                if (tile.getBonus() != null && tile.getBonus().getType() != null) {
                    String bonusType = tile.getBonus().getType();
                    imgBonus = loadImageCached(bonusCache, "/bonus/" + bonusType + ".png");
                }

                Image imgTreasure = null;
                if (tile.getTreasure() != null && tile.getTreasure().getName() != null) {
                    String treasureName = tile.getTreasure().getName();
                    imgTreasure = loadImageCached(treasureCache, "/treasure/" + treasureName + ".png");
                }

                if (imgTile != null) {
                    double pad = 0; // ggf. geom.cell * 0.05
                    double drawX = x + pad;
                    double drawY = y + pad;
                    double w = geom.cell - 2 * pad;
                    double h = geom.cell - 2 * pad;

                    // Grundtile
                    g.drawImage(imgTile, drawX, drawY, w, h);

                    // Zentrum für Overlays
                    double cx = drawX + w / 2.0;
                    double cy = drawY + h / 2.0;

                    // Bonus (zentriert)
                    if (imgBonus != null) {

                        double bw = imgBonus.getWidth();
                        double bh = imgBonus.getHeight();
                        g.drawImage(imgBonus, drawX, drawY, w, h);
                       // g.drawImage(imgBonus, cx - bw / 2.0, cy - bh / 2.0,30 ,30 );

                    }

                    // Treasure (zentriert)
                    if (imgTreasure != null) {
                        double tw = imgTreasure.getWidth();
                        double th = imgTreasure.getHeight();

                        //g.drawImage(imgTreasure, cx - tw / 2.0, cy - th / 2.0,);

                        g.drawImage(imgTreasure, drawX, drawY, w, h);
                    }
                } else {
                    // Fallback: Unicode-Zeichen
                    g.setFill(Color.BLACK);
                    Font pieceFont = Font.font(geom.cell * 0.7);
                    g.setFont(pieceFont);
                    String sym = getTileSymbol(tile); // aus GameBoard.* importiert
                    double tw = computeTextWidth(pieceFont, sym);
                    double tx = x + (geom.cell - tw) / 2.0;
                    double ty = y + (geom.cell + pieceFont.getSize()) / 2.0 - 4;
                    g.fillText(sym, tx, ty);
                }
            }
        }
    }

    private static double computeTextWidth(Font font, String s) {
        // Einfacher Näherungswert – reicht hier aus
        return s.length() * font.getSize() * 0.6;
    }

    private static final class Geom {
        int x0, y0, size, cell;
    }

    private Geom computeGeom() {
        Geom cg = new Geom();
        int W = (int) getWidth();
        int H = (int) getHeight();
        int margin = Math.max(20, Math.min(W, H) / 20);
        int size = Math.min(W, H) - 2 * margin;
        int rows = game_board.getSize().getRows();

        int cell = Math.max(1, size / rows);
        size = cell * rows;
        int x0 = (W - size) / 2;
        int y0 = (H - size) / 2;

        cg.x0 = x0;
        cg.y0 = y0;
        cg.size = size;
        cg.cell = cell;
        return cg;
    }

    /** "e4" -> [file,rank] (0..cols-1, 0..rows-1), rank 0 = oberste Reihe */
    private int[] fromAlg(String alg) {
        if (alg == null || alg.length() != 2) {
            throw new IllegalArgumentException("Algebraisch erwartet, z.B. e2");
        }
        char f = Character.toLowerCase(alg.charAt(0));
        char r = alg.charAt(1);

        int cols = game_board.getSize().getCols();
        int rows = game_board.getSize().getRows();

        if (f < 'a' || f >= (char) ('a' + cols)) {
            throw new IllegalArgumentException("Ungültige Datei: " + alg);
        }

        if (r < '1' || r > (char) ('0' + rows)) {
            throw new IllegalArgumentException("Ungültiger Rang: " + alg);
        }

        int file = f - 'a';
        int rank = rows - (r - '0'); // 'rows' -> 0, '1' -> rows-1

        return new int[]{file, rank};
    }

    /** [file,rank] -> "e4" (a.., 1..) */
    private String toAlg(int file, int rank) {
        int rows = game_board.getSize().getRows();
        return "" + (char) ('a' + file) + (rows - rank);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        // Kein redraw() hier – wird über width/height Listener gemacht
    }
}
