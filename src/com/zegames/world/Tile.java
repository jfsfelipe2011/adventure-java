package com.zegames.world;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    private final BufferedImage sprite;
    private final int x;
    private final int y;
    private final Camera camera;

    public Tile(BufferedImage sprite, int x, int y, Camera camera) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.camera = camera;
    }

    public void render (Graphics graphics) {
        graphics.drawImage(
                this.sprite,
                this.x - this.camera.getX(),
                this.y - this.camera.getY(),
                null
        );
    }
}
