package com.zegames.entities;

import com.zegames.main.Game;
import com.zegames.world.Camera;
import com.zegames.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BulletShoot extends Entity {
    private final double directionX;
    private final double directionY;
    private int life;

    public BulletShoot(
            double x,
            double y,
            int width,
            int height,
            BufferedImage sprite,
            Camera camera,
            double directionX,
            double directionY
    ) {
        super(x, y, width, height, sprite, camera);

        this.directionX = directionX;
        this.directionY = directionY;
        this.life = 0;
    }

    public double getSpeed() {
        return 1;
    }

    @Override
    public void tick() {
        boolean free = World.isFreeDynamic(
                (int) (this.x + (this.directionX * this.getSpeed())),
                (int) (this.y + (this.directionY * this.getSpeed())),
                3,
                3
        );

        if (free) {
            this.x += this.directionX * this.getSpeed();
            this.y += this.directionY * this.getSpeed();
        } else {
            Game.bullets.remove(this);
        }

        this.life++;

        if (life == 50) {
            Game.bullets.remove(this);
        }
    }

    @Override
    public void render(Graphics graphics) {
        graphics.setColor(Color.YELLOW);
        graphics.fillOval(
                (int) this.x - this.camera.getX(),
                (int) this.y - this.camera.getY(),
                this.width,
                this.height
        );
    }
}
