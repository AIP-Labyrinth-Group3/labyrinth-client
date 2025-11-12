package com.uni.gameclient.game.classes;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

    public class Pixelbild  {
        public void start()  {

            try {
                // 1x1 leeres (transparentes) FX-Image
                WritableImage image = new WritableImage(1, 1);

                // Nach AWT konvertieren und speichern
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File("./0.png"));

                System.out.println("Fertig: 0.png");
            }catch (IOException e) {}

            }


    }
