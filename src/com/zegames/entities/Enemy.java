package com.zegames.entities;

import com.zegames.graficos.Spritesheet;
import com.zegames.main.Game;
import com.zegames.main.Sound;
import com.zegames.world.AStar;
import com.zegames.world.Camera;
import com.zegames.world.Vector2i;
import com.zegames.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {
    private final Player player;
    private int frames;
    private int index;
    private final BufferedImage[] sprites;
    private final BufferedImage damageSprite;
    private int life;
    private boolean isDamaged;
    private int damageFrames;

    public Enemy(
            double x,
            double y,
            int width,
            int height,
            BufferedImage sprite,
            Camera camera,
            Player player,
            Spritesheet spritesheet
    ) {
        super(x, y, width, height, sprite, camera);

        this.player = player;
        this.frames = 0;
        this.index = 0;
        this.isDamaged = false;
        this.life = 10;
        this.damageFrames = 0;
        this.depth = 0;

        this.sprites = new BufferedImage[2];
        this.sprites[0] = spritesheet.getSprite(112, 16, 16, 16);
        this.sprites[1] = spritesheet.getSprite(128, 16, 16, 16);
        this.damageSprite = spritesheet.getSprite(144, 16, 16, 16);
    }

    public double getSpeed() {
        return 1;
    }

    public int getLife() {
        return life;
    }

    public boolean isCollidingWithPlayer() {
        Rectangle enemyCurrent = new Rectangle(
                (int) this.x + this.maskX, (int) this.y + this.maskY, this.maskWidth, this.maskHeight
        );

        Rectangle player = new Rectangle(
                (int) this.player.getX(), (int) this.player.getY(), 16, 16
        );

        return enemyCurrent.intersects(player);
    }

    @Override
    public void tick() {
        /* Without A*
        if (this.calculateDistance((int) this.x, (int) this.y, (int) this.player.getX(),
                (int) this.player.getY()) < 70) {
            if (!this.isColiddingWithPlayer()) {
                if ((int) this.x < (int) this.player.getX()
                        && World.isFree((int) (this.x + this.getSpeed()), (int) this.y)
                        && this.isColidding((int) (this.x + this.getSpeed()), (int) this.y)) {
                    this.x += this.getSpeed();
                } else if ((int) this.x > (int) this.player.getX()
                        && World.isFree((int) (this.x - this.getSpeed()), (int) this.y)
                        && this.isColidding((int) (this.x - this.getSpeed()), (int) this.y)) {
                    this.x -= this.getSpeed();
                }

                if ((int) this.y < (int) this.player.getY()
                        && World.isFree((int) this.x, (int) (this.y + this.getSpeed()))
                        && this.isColidding((int) this.x, (int) (this.y + this.getSpeed()))) {
                    this.y += this.getSpeed();
                } else if ((int) this.y > (int) this.player.getY()
                        && World.isFree((int) this.x, (int) (this.y - this.getSpeed()))
                        && this.isColidding((int) this.x, (int) (this.y - this.getSpeed()))) {
                    this.y -= this.getSpeed();
                }
            } else {
                if (Game.RAND.nextInt(100) < 10) {
                    this.player.causeDamage(Game.RAND.nextInt(3));
                    Sound.hurtEffect.play();
                }
            }
        }*/

        // A*
        this.maskX = 5;
        this.maskY = 5;
        this.maskWidth = 8;
        this.maskHeight = 8;

        if (!this.isCollidingWithPlayer()) {
            if (this.path == null || this.path.size() == 0) {
                Vector2i start = new Vector2i((int) (this.x / 16), (int) (this.y / 16));
                Vector2i end = new Vector2i((int) (this.player.getX() / 16), (int) (this.player.getY() / 16));

                path = AStar.findPath(Game.world, start, end);
            }
        } else {
            if (Game.RAND.nextInt(100) < 10) {
                this.player.causeDamage(Game.RAND.nextInt(3));
                //Sound.hurtEffect.play();
                Sound.hurt.play();
            }
        }

        if (Game.RAND.nextInt(100) < 25) {
            this.followPath(path);
        }

        if (Game.RAND.nextInt(100) < 5) {
            Vector2i start = new Vector2i((int) (this.x / 16), (int) (this.y / 16));
            Vector2i end = new Vector2i((int) (this.player.getX() / 16), (int) (this.player.getY() / 16));

            path = AStar.findPath(Game.world, start, end);
        }

        this.frames++;

        if (this.frames == 20) {
            this.frames = 0;
            this.index++;

            if (this.index > 1) {
                this.index = 0;
            }
        }

        if (this.isDamaged) {
            this.damageFrames++;

            if (this.damageFrames == 5) {
                this.damageFrames = 0;
                this.isDamaged = false;
            }
        }

        this.collidingBullet();

        if (this.life <= 0) {
            World.enemies.remove(this);
        }
    }

    public void collidingBullet() {
        for(int i = 0; i < Game.bullets.size(); i++) {
            BulletShoot bulletShoot = Game.bullets.get(i);

            if (Entity.isColidding(this, bulletShoot)) {
                Game.bullets.remove(bulletShoot);
                this.life--;
                this.isDamaged = true;
                return;
            }
        }
    }

    @Override
    public void render(Graphics graphics) {
        if (!isDamaged) {
            graphics.drawImage(
                    this.sprites[this.index],
                    (int) this.x - this.camera.getX(),
                    (int) this.y - this.camera.getY(),
                    null
            );
        } else {
            graphics.drawImage(
                    this.damageSprite,
                    (int) this.x - this.camera.getX(),
                    (int) this.y - this.camera.getY(),
                    null
            );
        }
    }
}
