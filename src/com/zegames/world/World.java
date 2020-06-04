package com.zegames.world;

import com.zegames.entities.*;
import com.zegames.graficos.Spritesheet;
import com.zegames.main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World {
    public static Tile[] tiles;
    public static int WIDTH;
    public static int HEIGHT;
    public static int TILE_SIZE = 16;
    private static Camera cameraW = null;
    public static List<Enemy> enemies;
    private final Player player;

    public World(
            String path,
            Spritesheet spritesheet,
            Player player,
            Camera camera
    ) {
        cameraW = camera;
        this.player = player;

        /* random
            this.player.setX(0);
            this.player.setY(0);

            WIDTH = 100;
            HEIGHT = 100;
            tiles = new Tile[WIDTH * HEIGHT];
            enemies = new ArrayList<>();

            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    tiles[i + (j * WIDTH)] = new WallTile(
                            spritesheet.getSprite(16, 0, 16, 16),
                            i * 16,
                            j * 16,
                            camera
                    );
                }
            }

            int direction = 0;
            int xx = 0;
            int yy = 0;

            for (int i = 0; i < 200; i++) {
                tiles[xx + (yy * WIDTH)] = new FloorTile(
                        spritesheet.getSprite(0, 0, 16, 16),
                        xx * 16,
                        yy * 16,
                        camera
                );

                if (direction == 0) {
                    if (xx < WIDTH) {
                        xx++;
                    }
                } else if (direction == 1) {
                    if (xx > 0) {
                        xx--;
                    }
                } else if (direction == 2) {
                    if (yy < HEIGHT) {
                        yy++;
                    }
                } else if (direction == 3) {
                    if (yy > 0) {
                        yy--;
                    }
                }

                if (Game.RAND.nextInt(100) < 30) {
                    direction = Game.RAND.nextInt(4);
                }
            }
        */

        try {
            BufferedImage map = ImageIO.read(getClass().getResource(path));
            int[] pixels = new int[map.getWidth() * map.getHeight()];

            tiles = new Tile[map.getWidth() * map.getHeight()];
            WIDTH = map.getWidth();
            HEIGHT = map.getHeight();
            enemies = new ArrayList<>();

            map.getRGB(0,0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

            for (int x = 0; x < map.getWidth(); x++) {
                for (int y = 0; y < map.getHeight(); y++) {
                    tiles[x + (y * map.getWidth())] = new FloorTile(
                            spritesheet.getSprite(0, 0, 16, 16),
                            x * 16,
                            y * 16,
                            camera
                    );

                    switch (pixels[x + (y * map.getWidth())]) {
                        case 0xFFFFFFFF:
                            tiles[x + (y * map.getWidth())] = new WallTile(
                                    spritesheet.getSprite(16, 0, 16, 16),
                                    x * 16,
                                    y * 16,
                                    camera
                            );
                            break;
                        case 0xFF0026FF:
                            player.setX(x * 16);
                            player.setY(y * 16);
                            break;
                        case 0xFFFF0000:
                            Enemy enemy = new Enemy(x * 16, y * 16, 16, 16,
                                    spritesheet.getSprite(7 * 16, 16, 16, 16),
                                    camera, player, spritesheet
                            );

                            Game.entities.add(enemy);
                            enemies.add(enemy);
                            break;
                        case 0xFFFF6A00:
                            Game.entities.add(new Weapon(x * 16, y * 16, 16, 16,
                                    spritesheet.getSprite(7 * 16, 0, 16, 16),
                                    camera));
                            break;
                        case 0xFFFF7F7F:
                            Game.entities.add(new LifePack(x * 16, y * 16, 16, 16,
                                    spritesheet.getSprite(6 * 16, 0, 16, 16),
                                    camera));
                            break;
                        case 0xFFFFD800:
                            Game.entities.add(new Bullet(x * 16, y * 16, 16, 16,
                                    spritesheet.getSprite(6 * 16, 16, 16, 16),
                                    camera));
                            break;
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void generateParticles(int amount, int x, int y) {
        for (int i = 0; i < amount; i++) {
            Game.entities.add(new Particle(x, y, 1, 1, null, cameraW));
        }
    }

    public static boolean isFreeDynamic(int nextX, int nextY, int width, int height) {
        int x1 = nextX / TILE_SIZE;
        int y1 = nextY / TILE_SIZE;

        int x2 = (nextX + width - 1) / TILE_SIZE;
        int y2 = nextY / TILE_SIZE;

        int x3 = nextX / TILE_SIZE;
        int y3 = (nextY + height - 1) / TILE_SIZE;

        int x4 = (nextX + width - 1) / TILE_SIZE;
        int y4 = (nextY + height - 1) / TILE_SIZE;

        return !((tiles[x1 + (y1 * WIDTH)] instanceof WallTile) ||
                (tiles[x2 + (y2 * WIDTH)] instanceof WallTile) ||
                (tiles[x3 + (y3 * WIDTH)] instanceof WallTile) ||
                (tiles[x4 + (y4 * WIDTH)] instanceof WallTile));
    }

    public static boolean isFree(int nextX, int nextY) {
        int x1 = nextX / TILE_SIZE;
        int y1 = nextY / TILE_SIZE;

        int x2 = (nextX + TILE_SIZE - 1) / TILE_SIZE;
        int y2 = nextY / TILE_SIZE;

        int x3 = nextX / TILE_SIZE;
        int y3 = (nextY + TILE_SIZE - 1) / TILE_SIZE;

        int x4 = (nextX + TILE_SIZE - 1) / TILE_SIZE;
        int y4 = (nextY + TILE_SIZE - 1) / TILE_SIZE;

        return !((tiles[x1 + (y1 * WIDTH)] instanceof WallTile) ||
                (tiles[x2 + (y2 * WIDTH)] instanceof WallTile) ||
                (tiles[x3 + (y3 * WIDTH)] instanceof WallTile) ||
                (tiles[x4 + (y4 * WIDTH)] instanceof WallTile));
    }

    public void render(Graphics graphics) {
        int numberOfTiles = 4;
        int xStart = cameraW.getX() >> numberOfTiles;
        int yStart = cameraW.getY() >> numberOfTiles;
        int xFinal = xStart + (Game.WIDTH >> numberOfTiles);
        int yFinal = yStart + (Game.HEIGHT >> numberOfTiles);

        for (int x = xStart; x <= xFinal; x++) {
            for (int y = yStart; y <= yFinal; y++) {
                if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) {
                    continue;
                }

                Tile tile = tiles[x + (y * WIDTH)];
                tile.render(graphics);
            }
        }
    }

    public void renderMinimap(int[] pixels) {
        Arrays.fill(pixels, 0);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (tiles[i + (j * WIDTH)] instanceof WallTile) {
                    pixels[i + (j * WIDTH)] = 0xFFFFFF;
                }
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            pixels[(int) (enemy.getX() / 16) + ((int) (enemy.getY() / 16) * WIDTH)] = 0xFF0000;
        }

        pixels[(int) (this.player.getX() / 16) + ((int) (this.player.getY() / 16) * WIDTH)] = 0x0000FF;
    }
}
