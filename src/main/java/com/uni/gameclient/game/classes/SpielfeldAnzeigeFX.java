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

import java.awt.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import java.util.Map;

import static com.uni.gameclient.game.models.GameBoard.*;


/**
 * Wiederverwendbares 7x7-Spielfeld als JavaFX-Region.
 * - Lädt Wegkarten-Bilder aus /pieces/*.png
 * - Klick-Listener pro Feld
 * - Highlight/Selection
 * - Wegekarten-String (7 Ränge per '/')
 */
public class SpielfeldAnzeigeFX extends Region {


    GameBoard game_board;

    /* ---------- Öffentliche API ---------- */

    /** Listener für Klicks auf ein Feld */
    @FunctionalInterface
    public interface SquareClickListener {
        /** @param file 0..6 (a..g), @param rank 0..6 (7..1 von oben nach unten), @param algebraic z.B. "e4" */
        void onSquareClicked(int file, int rank, String algebraic);
    }

    public SpielfeldAnzeigeFX() {
        GameBoard game_board;
        BoardSize boardSize = new BoardSize(7,7);
        game_board = GameBoard.generateBoard(boardSize);
        this.game_board = game_board;

        initCanvas();
        setWidth(640);
        setHeight(640);
    }

    public SpielfeldAnzeigeFX(GameBoard game_board) {
        this.game_board = game_board;
        ladeWegKartenImages();
        initCanvas();

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
    private final Map<Character, Image> pieceImages = new HashMap<>();
    private boolean showCoords = true;

    // Auswahl/Highlight
    private int selRow = -1;
    private int selCol = -1;
    private int highlightRow = -1, highlightCol = -1;
    private SquareClickListener clickListener;

    // Farben
    private Color light = Color.rgb(240, 217, 181); // hell
    private Color dark  = Color.rgb(181, 136,  99); // dunkel
    private Color highlight = Color.color(1.0, 0.84, 0, 0.47); // gold, alpha

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
            if (e.getX() >= g.x0 && e.getY() >= g.y0 && e.getX() < g.x0 + g.size && e.getY() < g.y0 + g.size) {
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

    private void ladeWegKartenImages() {
        String[] tokens = {"╔", "╗", "╝", "╚", "═", "║", "╠", "╦", "╣", "╩"};
        for (String t : tokens) {
            char key = t.charAt(0);
            String pieceName;
            switch (key) {
                case '╔': pieceName = "ecke_BC"; break;
                case '╗': pieceName = "ecke_CD"; break;
                case '╝': pieceName = "ecke_AD"; break;
                case '╚': pieceName = "ecke_AB"; break;
                case '═': pieceName = "gerade_BD"; break;
                case '║': pieceName = "gerade_AC"; break;
                case '╠': pieceName = "kreuzung_ABC"; break;
                case '╦': pieceName = "kreuzung_BCD"; break;
                case '╣': pieceName = "kreuzung_ACD"; break;
                case '╩': pieceName = "kreuzung_ABD"; break;
                default: continue;
            }
            String path = "/pieces/" + pieceName + ".png";
            try (InputStream in = getClass().getResourceAsStream(path)) {
                if (in != null) {
                    pieceImages.put(key, new Image(in));
                } else {
                    System.err.println("Bild nicht gefunden: " + path);
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Laden: " + path + " → " + e);
            }
        }
    }

    private void redraw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();

        // Hintergrund
        g.setFill(Color.rgb(32, 33, 36));
        g.fillRect(0, 0, W, H);

        Geom geom = computeGeom();

        // Koordinaten
        if (showCoords) {
            g.setFill(Color.DARKGRAY);
            g.setFont(Font.font(Math.max(12, geom.cell * 0.18)));
            for (int c = 0; c < game_board.getSize().getCols(); c++) {
                String s = String.valueOf(c + 1);
                double tw = computeTextWidth(g.getFont(), s);
                double x = geom.x0 + c * geom.cell + geom.cell / 2.0 - tw / 2.0;
                g.fillText(s, x, geom.y0 - 6);
                g.fillText(s, x, geom.y0 + geom.size + g.getFont().getSize() + 4);
            }
            for (int r = 0; r < game_board.getSize().getRows(); r++) {
                String s = String.valueOf(r + 1);
                double tw = computeTextWidth(g.getFont(), s);
                double y = geom.y0 + r * geom.cell + (geom.cell + g.getFont().getSize()) / 2.0 - 2;
                g.fillText(s, geom.x0 - tw - 6, y);
                g.fillText(s, geom.x0 + geom.size + 6, y);
            }
        }

        // Felder + Inhalte
        Tile[][] tiles= game_board.getTiles();
        for (int r = 0; r < game_board.getSize().getRows(); r++) {
            for (int c = 0; c < game_board.getSize().getCols(); c++) {
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

                Image img= getImgageForTile(tiles[r][c]);
                if (img != null) {
                    double pad = 0; // ggf.  geom.cell * 0.05
                    g.drawImage(img, x + pad, y + pad, geom.cell - 2 * pad, geom.cell - 2 * pad);
                } else {
                   // Fallback: Unicode-Zeichen
                   g.setFill(Color.BLACK);
                   g.setFont(Font.font(geom.cell * 0.7));
                   String sym = getTileSymbol(tiles[r][c]);
                   double tw = computeTextWidth(g.getFont(), sym);
                   double tx = x + (geom.cell - tw) / 2.0;
                   double ty = y + (geom.cell + g.getFont().getSize()) / 2.0 - 4;
                   g.fillText(sym, tx, ty);
                }
            }
        }
    }

    private Image getImgageForTile(Tile tile)  {
        // Hier müsste die Logik implementiert werden, um das richtige Bild basierend auf den Eingängen der Kachel zu bestimmen.
         String tilename= getTileImageName(tile);
         String bonusType="";
         bonusType= tile.getBonus().getType();



         String treasureName= "";
         treasureName= tile.getTreasure().getName();

         String pathTile = "/pieces/" + tilename;
         Image img_tile = null;
         try (InputStream in = getClass().getResourceAsStream(pathTile)) {
            if (in != null) {
                img_tile = new Image(in);
            } else {
                System.err.println("Bild Tile nicht gefunden: " + pathTile);
            }
         }catch (IOException e) {}
        String pathBonus = "/bonus/" + bonusType+".png";
        Image img_bonus = null;
        try (InputStream in = getClass().getResourceAsStream(pathBonus)) {
            if (in != null) {
                img_bonus = new Image(in);
            } else {
                System.err.println("Bild Bonus nicht gefunden: " + pathBonus);
            }
        }catch (IOException e) {
        }
        String pathTreasure = "/treasure/" + treasureName +".png";
        Image img_treasure = null;
        try (InputStream in = getClass().getResourceAsStream(pathTreasure)) {
            if (in != null) {
                img_treasure = new Image(in);
            } else {
                System.err.println("Bild treasure nicht gefunden: " + pathTreasure);
            }
        }catch (IOException e) {
        }

        return  overlayBonusTtreasure(img_tile, img_bonus, img_treasure);
    }
    private static Image overlayBonusTtreasure(Image tile, Image bonus, Image treasure) {
        double width = tile.getWidth();
        double height = tile.getHeight();

        // Canvas, auf dem wir zeichnen
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Grundbild
        gc.drawImage(tile, 0, 0);

        // Bonus (zentriert)
        if (bonus != null) {
            double x = (width - bonus.getWidth()) / 2;
            double y = (height - bonus.getHeight()) / 2;
            gc.drawImage(bonus, x, y);
        }

        // Treasure (zentriert)
        if (treasure != null) {
            double x = (width - treasure.getWidth()) / 2;
            double y = (height - treasure.getHeight()) / 2;
            gc.drawImage(treasure, x, y);
        }

        // Ergebnis als JavaFX-Image zurückgeben
        return canvas.snapshot(null, null);
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
        int cell = Math.max(1, size / game_board.getSize().getRows());
        size = cell * game_board.getSize().getRows();
        int x0 = (W - size) / 2;
        int y0 = (H - size) / 2;

        cg.x0 = x0; cg.y0 = y0; cg.size = size; cg.cell = cell;
        return cg;
    }

    /** "e4" -> [file,rank] (0..6, 0..6), rank 0 = oberste Reihe (7) */
    private int[] fromAlg(String alg) {
        if (alg == null || alg.length() != 2) throw new IllegalArgumentException("Algebraisch erwartet, z.B. e2");
        char f = Character.toLowerCase(alg.charAt(0));
        char r = alg.charAt(1);
        if (f < 'a' || f > 'g' || r < '1' || r > (char)game_board.getSize().getRows()) throw new IllegalArgumentException("Ungültig: " + alg);
        int file = f - 'a';
        int rank = game_board.getSize().getRows() - (r - '0'); // '7'->0, '1'->6
        return new int[]{file, rank};
    }

    /** [file,rank] -> "e4" (a..g, 1..7) */
    private String toAlg(int file, int rank) {
        return "" + (char)('a' + file) + (game_board.getSize().getRows() - rank);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);

        redraw();
    }
}
