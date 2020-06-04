package com.zegames.entities;

import com.zegames.graficos.Spritesheet;
import com.zegames.main.Game;
import com.zegames.world.Camera;
import com.zegames.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    private boolean right;
    private boolean left;
    private boolean up;
    private boolean down;
    private boolean jump;
    private boolean jumping;
    private boolean jumpUp;
    private boolean jumpDown;

    private final int right_direction = 0;
    private final int left_direction = 1;
    private int direction;

    private int frames;
    private int index;
    private boolean moved;
    private double life;
    private int ammo;
    private boolean isDamaged;
    private int damageFrames;
    private boolean hasGun;
    private boolean shoot;
    private boolean mouseShoot;
    private final int jumpFrames;
    private final int jumpSpeed;
    private int jumpCurrent;

    private final BufferedImage[] rightPlayer;
    private final BufferedImage[] leftPlayer;
    private final BufferedImage[] playerDamage;
    private final BufferedImage[] gun;

    private int mouseX;
    private int mouseY;

    public Player(
            int x,
            int y,
            int width,
            int height,
            BufferedImage sprite,
            Spritesheet spritesheet,
            Camera camera
    ) {
        super(x, y, width, height, sprite, camera);

        this.right = false;
        this.left = false;
        this.up = false;
        this.down = false;
        this.jump = false;
        this.jumping = false;
        this.jumpUp = false;
        this.jumpDown = false;

        this.frames = 0;
        this.index = 0;
        this.moved = false;
        this.life = 100;
        this.ammo = 0;
        this.isDamaged = false;
        this.damageFrames = 0;
        this.hasGun = false;
        this.shoot = false;
        this.mouseShoot = false;
        this.jumpFrames = 30;
        this.jumpSpeed = 2;
        this.jumpCurrent = 0;
        this.depth = 1;

        this.direction = this.right_direction;

        this.rightPlayer = new BufferedImage[4];
        this.leftPlayer = new BufferedImage[4];
        this.playerDamage = new BufferedImage[2];
        this.gun = new BufferedImage[2];

        this.playerDamage[0] = spritesheet.getSprite(0, 16, 16, 16);
        this.playerDamage[1] = spritesheet.getSprite(16, 16, 16, 16);
        this.gun[0] = spritesheet.getSprite(128, 0, 16, 16);
        this.gun[1] = spritesheet.getSprite(144, 0, 16, 16);

        for (int i = 0; i < 4; i++) {
            this.rightPlayer[i] = spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
            this.leftPlayer[i] = spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
        }
    }

    public void setLife(double life) {
        this.life = life;
    }

    public double getSpeed() {
        return 1.2;
    }

    public void moveRight() {
        this.right = true;
    }

    public void moveLeft() {
        this.left = true;
    }

    public void moveUp() {
        this.up = true;
    }

    public void moveDown() {
        this.down = true;
    }

    public void stopMoveRight() {
        this.right = false;
    }

    public void stopMoveLeft() {
        this.left = false;
    }

    public void stopMoveUp() {
        this.up = false;
    }

    public void stopMoveDown() {
        this.down = false;
    }

    public void idle() {
        this.index = 0;
    }

    public double getLife() {
        return life;
    }

    public void walkX() {
        this.x++;

        this.frames++;

        if (this.frames == 5) {
            this.frames = 0;
            this.index++;

            if (this.index > 3) {
                this.index = 0;
            }
        }
    }

    public void causeDamage(int damage) {
        if (this.life - damage < 0) {
            this.life = 0;
            return;
        }

        this.life -= damage;
        this.isDamaged = true;
    }

    public void addLife(int life) {
        if (this.life + life > 100) {
            this.life = 100;
            return;
        }

        this.life += life;
    }

    public int getAmmo() {
        return ammo;
    }

    public void addAmmo(int ammo) {
        if (this.ammo + ammo > 100) {
            this.ammo = 100;
            return;
        }

        this.ammo += ammo;
    }

    public void addGun() {
        this.hasGun = true;
    }

    public void shooting() {
        this.shoot = true;
    }

    public void stopShooting() {
        this.shoot = false;
    }

    public void mouseShooting(int mouseX, int mouseY) {
        this.mouseShoot = true;
        this.mouseX = mouseX / 3;
        this.mouseY = mouseY / 3;
    }

    public void stopMouseShooting() {
        this.mouseShoot = false;
    }

    @Override
    public void tick() {
        if (this.jump) {
            if (!this.jumping) {
                this.jump = false;
                this.jumping = true;
                this.jumpUp = true;
            }
        }

        if (this.jumping) {
            if (this.jumpUp) {
                this.jumpCurrent += this.jumpSpeed;
            } else if (this.jumpDown) {
                this.jumpCurrent -= this.jumpSpeed;

                if (this.jumpCurrent <= 0) {
                    this.jumping = false;
                    this.jumpDown = false;
                    this.jumpUp = false;
                }
            }

            this.z = jumpCurrent;

            if (this.jumpCurrent >= this.jumpFrames) {
                this.jumpDown = true;
                this.jumpUp = false;
            }
        }

        this.moved = false;

        if (this.right && World.isFree((int) (this.x + this.getSpeed()), (int) y)) {
            this.moved = true;
            this.direction = this.right_direction;
            this.x += this.getSpeed();
        } else if (this.left && World.isFree((int) (this.x - this.getSpeed()), (int) y)) {
            this.moved = true;
            this.direction = this.left_direction;
            this.x -= this.getSpeed();
        }

        if (this.up && World.isFree((int) this.x, (int) (y - this.getSpeed()))) {
            this.moved = true;
            this.y -= this.getSpeed();
        } else if (this.down && World.isFree((int) this.x, (int) (y + this.getSpeed()))) {
            this.moved = true;
            this.y += this.getSpeed();
        }

        if (moved) {
            this.frames++;

            if (this.frames == 5) {
                this.frames = 0;
                this.index++;

                if (this.index > 3) {
                    this.index = 0;
                }
            }
        }

        if (this.isDamaged) {
            this.damageFrames++;

            if (this.damageFrames == 5) {
                this.damageFrames = 0;
                this.isDamaged = false;
            }
        }

        if (this.shoot && this.hasGun && this.ammo > 0) {
            int directionX = 1;
            int x = 12;

            if (this.direction == this.left_direction) {
                directionX = -1;
                x = 0;
            }

            BulletShoot bulletShoot = new BulletShoot(
                    (int) this.getX() + x,
                    (int) this.getY() + 7,
                    3,
                    3,
                    null,
                    this.camera,
                    directionX,
                    0
            );

            Game.bullets.add(bulletShoot);
            this.shoot = false;
            this.ammo--;
        }

        if (this.mouseShoot && this.hasGun && this.ammo > 0) {
            int x = 12;

            if (this.direction == this.left_direction) {
                x = 0;
            }

            double angle = Math.atan2(
                    this.mouseY - ((int) this.getY() + 8 - this.camera.getY()),
                    this.mouseX - ((int) this.getX() + x - this.camera.getX())
            );

            double directionX = Math.cos(angle);
            double directionY = Math.sin(angle);

            BulletShoot bulletShoot = new BulletShoot(
                    (int) this.getX() + x,
                    (int) this.getY() + 7,
                    3,
                    3,
                    null,
                    this.camera,
                    directionX,
                    directionY
            );

            Game.bullets.add(bulletShoot);
            this.mouseShoot = false;
            this.ammo--;
        }

        if (this.life <= 0) {
            Game.gameState = "GAME_OVER";
            return;
        }

        this.camera.setX(
                this.camera.clamp(
                        ((int) this.x) - (Game.WIDTH / 2),
                        0,
                        (World.WIDTH * World.TILE_SIZE ) - Game.WIDTH)
        );

        this.camera.setY(
                this.camera.clamp(
                        ((int) this.y) - (Game.HEIGHT / 2),
                        0,
                        (World.HEIGHT * World.TILE_SIZE) - Game.HEIGHT
                )
        );
    }

    @Override
    public void render(Graphics graphics) {
        if (!this.isDamaged) {
            if (this.direction == this.right_direction) {
                graphics.drawImage(
                        this.rightPlayer[index],
                        ((int) this.x) - this.camera.getX(),
                        ((int) this.y) - this.camera.getY() - this.z,
                        null
                );

                if (this.hasGun) {
                    graphics.drawImage(
                            this.gun[0],
                            ((int) this.x) - this.camera.getX(),
                            ((int) this.y) - this.camera.getY() - this.z,
                            null
                    );
                }
            } else if (this.direction == this.left_direction) {
                graphics.drawImage(
                        this.leftPlayer[index],
                        ((int) this.x) - this.camera.getX(),
                        ((int) this.y) - this.camera.getY() - this.z,
                        null
                );

                if (this.hasGun) {
                    graphics.drawImage(
                            this.gun[1],
                            ((int) this.x) - this.camera.getX(),
                            ((int) this.y) - this.camera.getY() - this.z,
                            null
                    );
                }
            }
        } else {
            if (this.direction == this.right_direction) {
                graphics.drawImage(
                        this.playerDamage[0],
                        ((int) this.x) - this.camera.getX(),
                        ((int) this.y) - this.camera.getY() - this.z,
                        null
                );
            } else if (this.direction == this.left_direction) {
                graphics.drawImage(
                        this.playerDamage[1],
                        ((int) this.x) - this.camera.getX(),
                        ((int) this.y) - this.camera.getY() - this.z,
                        null
                );
            }
        }

        if (this.jumping) {
            graphics.setColor(Color.BLACK);
            graphics.fillOval(
                    ((int) this.x) - this.camera.getX() + 3,
                    ((int) this.y) - this.camera.getY() + 16,
                    8,
                    8
            );
        }
    }

    public void jump() {
        this.jump = true;
    }
}
