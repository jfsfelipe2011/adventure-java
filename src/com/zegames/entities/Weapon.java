package com.zegames.entities;

import com.zegames.world.Camera;

import java.awt.image.BufferedImage;

public class Weapon  extends Entity {
    public Weapon(double x, double y, int width, int height, BufferedImage sprite, Camera camera) {
        super(x, y, width, height, sprite, camera);
    }
}
