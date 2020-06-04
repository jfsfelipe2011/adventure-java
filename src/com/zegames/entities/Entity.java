package com.zegames.entities;

import com.zegames.world.Camera;
import com.zegames.world.Node;
import com.zegames.world.Vector2i;
import com.zegames.world.World;

import java.util.Comparator;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected Camera camera;
    protected BufferedImage sprite;
    protected int maskX;
    protected int maskY;
    protected int maskWidth;
    protected int maskHeight;
    protected int z;
    protected List<Node> path;
    protected int depth;

    public static Comparator<Entity> comparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
            return Integer.compare(o1.getDepth(), o2.getDepth());

        }
    };

    public Entity(double x, double y, int width, int height, BufferedImage sprite, Camera camera) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        this.camera = camera;

        this.z = 0;
        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = width;
        this.maskHeight = height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaskX() {
        return maskX;
    }

    public void setMaskX(int maskX) {
        this.maskX = maskX;
    }

    public int getMaskY() {
        return maskY;
    }

    public void setMaskY(int maskY) {
        this.maskY = maskY;
    }

    public int getMaskWidth() {
        return maskWidth;
    }

    public void setMaskWidth(int maskWidth) {
        this.maskWidth = maskWidth;
    }

    public int getMaskHeight() {
        return maskHeight;
    }

    public void setMaskHeight(int maskHeight) {
        this.maskHeight = maskHeight;
    }

    public int getDepth() {
        return depth;
    }

    public void render(Graphics graphics) {
        graphics.drawImage(
                this.sprite,
                (int) this.x - this.camera.getX(),
                (int) this.y - this.camera.getY(),
                null
        );
    }

    public void tick() {}

    public static boolean isColidding(Entity entity1, Entity entity2) {
        Rectangle entity1Mask = new Rectangle(
                (int) entity1.getX() + entity1.getMaskX(),
                (int) entity1.getY() + entity1.getMaskY(),
                entity1.getMaskWidth(),
                entity1.getMaskHeight()
        );

        Rectangle entity2Mask = new Rectangle(
                (int) entity2.getX() + entity2.getMaskX(),
                (int) entity2.getY() + entity2.getMaskY(),
                entity2.getMaskWidth(),
                entity2.getMaskHeight()
        );

        return entity1Mask.intersects(entity2Mask) && entity1.z == entity2.z;
    }

    public double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public void followPath(List<Node> path) {
        if (path != null) {
            if (path.size() > 0) {
                Vector2i target = path.get(path.size() - 1).getTile();

                if (this.x < target.getX() * 16 && this.isColidding((int) this.x + 1, (int) this.y)) {
                    this.x++;
                } else if (this.x > target.getX() * 16 && this.isColidding((int) this.x - 1, (int) this.y)) {
                    this.x--;
                }

                if (this.y < target.getY() * 16 && this.isColidding((int) this.x, (int) this.y + 1)) {
                    this.y++;
                } else if (this.y > target.getY() * 16 && this.isColidding((int) this.x, (int) this.y - 1)) {
                    this.y--;
                }

                if (this.x == target.getX() * 16 && this.y == target.getY() * 16) {
                    this.path.remove(path.size() - 1);
                }
            }
        }
    }

    public boolean isColidding(int nextX, int nextY) {
        Rectangle enemyCurrent = new Rectangle(
                nextX + this.maskX, nextY + this.maskY, this.maskWidth, this.maskHeight
        );

        for (Enemy enemy: World.enemies) {
            if (enemy == this) {
                continue;
            }

            Rectangle targetEnemy = new Rectangle(
                    (int) enemy.getX() + this.maskX,
                    (int) enemy.getY() + this.maskY,
                    this.maskWidth,
                    this.maskHeight
            );

            if (enemyCurrent.intersects(targetEnemy)) {
                return false;
            }
        }

        return true;
    }
}
