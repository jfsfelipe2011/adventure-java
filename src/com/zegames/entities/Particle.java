package com.zegames.entities;

import com.zegames.main.Game;
import com.zegames.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Particle extends Entity {
    private int life;
    private final double directionX;
    private final double directionY;

    public Particle(double x, double y, int width, int height, BufferedImage sprite, Camera camera) {
        super(x, y, width, height, sprite, camera);

        this.life = 0;
        this.directionX = Game.RAND.nextGaussian();
        this.directionY = Game.RAND.nextGaussian();
    }

    public double getSpeed() {
        return 2;
    }

    @Override
    public void tick() {
        this.x = this.directionX * this.getSpeed();
        this.y = this.directionY * this.getSpeed();
        this.life++;

        if (this.life == 15) {
            Game.entities.remove(this);
        }
    }

    public void render(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.fillRect((int) this.x - this.camera.getX(), (int) this.y - this.camera.getY(), this.width, this.height);
    }
}
