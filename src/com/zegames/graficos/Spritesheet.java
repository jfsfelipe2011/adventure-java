package com.zegames.graficos;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Spritesheet {
    private BufferedImage spritesheet;

    public Spritesheet(String path) {
        try {
            this.spritesheet = ImageIO.read(getClass().getResource(path));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public BufferedImage getSprite(int x, int y, int width, int height) {
        return this.spritesheet.getSubimage(x, y, width, height);
    }
}
