package com.zegames.main;

import com.zegames.entities.*;
import com.zegames.graficos.Spritesheet;
import com.zegames.graficos.UI;
import com.zegames.world.Camera;
import com.zegames.world.World;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    public static JFrame frame;

    public static final int WIDTH = 240;
    public static final int HEIGHT = 160;
    public static Random RAND;
    public static String gameState = "MENU";
    public static final int SCALE = 3;

    private Thread thread;
    private boolean isRunning;
    private final BufferedImage image;

    public static List<Entity> entities = null;
    public static List<BulletShoot> bullets = null;
    public static World world;

    private Player player;
    private UI ui;
    private final Menu menu;
    private int currentLevel;
    private boolean showMessageGameOver;
    private int frames;
    private boolean restartGame;
    private boolean saveGame;

    private final int[] pixels;
    private BufferedImage lightmap = null;
    private int[] lightmapPixels;
    private int[] minimapPixels;
    private BufferedImage minimapa;

    public static final InputStream stream = ClassLoader.getSystemClassLoader()
            .getResourceAsStream("alterebro-pixel-font.ttf");
    private Font newFont;

    private final int enter = 1;
    private final int start = 2;
    private final int gaming = 3;
    private int stateScene;
    private int timeScene;

    public Game() {
        //Sound.musicBackgroup.loop();
        //Sound.music.loop();
        RAND = new Random();
        this.currentLevel = 1;
        this.showMessageGameOver = false;
        this.frames = 0;
        this.restartGame = false;
        this.saveGame = false;
        this.stateScene = this.enter;

        this.setPreferredSize(new Dimension(
                WIDTH * SCALE,
                HEIGHT * SCALE
        ));

        // Fullscreen
        /*this.setPreferredSize(new Dimension(
                Toolkit.getDefaultToolkit().getScreenSize()
        ));*/

        this.initFrame();
        this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        try {
            this.lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        this.lightmapPixels = new int[this.lightmap.getWidth() * this.lightmap.getHeight()];
        this.lightmap.getRGB(
                0,
                0,
                this.lightmap.getWidth(),
                this.lightmap.getHeight(),
                this.lightmapPixels,
                0,
                this.lightmap.getWidth()
        );
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        Camera camera = new Camera(0, 0);
        Spritesheet spritesheet = new Spritesheet("/spritesheet.png");
        entities = new ArrayList<>();
        bullets = new ArrayList<>();

        this.player = new Player(0, 0, 16, 16,
                spritesheet.getSprite(32, 0, 16, 16),
                spritesheet,
                camera
        );

        Npc npc = new Npc(16, 220, 16, 16,
                spritesheet.getSprite(0, 32, 16, 16),
                camera,
                this.player
        );

        entities.add(this.player);
        entities.add(npc);

        this.ui = new UI(this.player);
        world = new World(
                "/level1.png",
                spritesheet,
                this.player,
                camera
        );

        this.minimapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.minimapPixels = ((DataBufferInt) this.minimapa.getRaster().getDataBuffer()).getData();

        this.menu = new Menu(this);

        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        try {
            this.newFont = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public void initFrame() {
        frame = new JFrame("Adventure");
        frame.add(this);
        //frame.setUndecorated(true);
        frame.setResizable(false);
        frame.pack();

        Image image = null;
        try {
            image = ImageIO.read(getClass().getResource("/gunner.png"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        frame.setIconImage(image);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        this.isRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public synchronized void stop() {
        this.isRunning = false;

        try {
            this.thread.join();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void definePlayerLife(double life) {
        this.player.setLife(life);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void restartGame(String level) {
        Camera camera = new Camera(0, 0);
        Spritesheet spritesheet = new Spritesheet("/spritesheet.png");
        entities = new ArrayList<>();

        this.player = new Player(0, 0, 16, 16,
                spritesheet.getSprite(32, 0, 16, 16),
                spritesheet,
                camera
        );

        entities.add(player);

        this.ui = new UI(this.player);
        world = new World(
                level,
                spritesheet,
                this.player,
                camera
        );

        bullets.clear();

        this.minimapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.minimapPixels = ((DataBufferInt) this.minimapa.getRaster().getDataBuffer()).getData();
    }

    public void tick() {
        if (gameState.equals("NORMAL")) {
            if (this.stateScene == this.gaming) {
                if (this.saveGame) {
                    this.saveGame = false;

                    String[] fields = {"level", "life"};
                    int[] values = {this.currentLevel, (int) this.player.getLife()};

                    this.menu.saveGame(fields, values, 10);
                    System.out.println("Jogo salvo com sucesso!!");
                }

                this.restartGame = false;
                List<Entity> toRemove = new ArrayList<>();

                for (Entity entity : entities) {
                    if (Entity.isColidding(this.player, entity)) {
                        if (entity instanceof LifePack) {
                            if (this.player.getLife() < 100) {
                                this.player.addLife(30);
                                toRemove.add(entity);
                            }
                        } else if (entity instanceof Bullet) {
                            if (this.player.getAmmo() < 100) {
                                this.player.addAmmo(50);
                                toRemove.add(entity);
                            }
                        } else if (entity instanceof Weapon) {
                            this.player.addGun();
                            toRemove.add(entity);
                        }
                    }

                    if (entity instanceof Enemy) {
                        if (((Enemy) entity).getLife() <= 0) {
                            toRemove.add(entity);
                        }
                    }

                    entity.tick();
                }

                entities.removeAll(toRemove);

                for (int i = 0; i < bullets.size(); i++) {
                    bullets.get(i).tick();
                }

                if (World.enemies.size() == 0) {
                    this.currentLevel++;

                    if (currentLevel > 2) {
                        this.currentLevel = 1;
                    }

                    String newWorld = "/level" + this.currentLevel + ".png";
                    this.restartGame(newWorld);
                }
            } else {
                if (this.stateScene == this.enter) {
                    if (this.player.getX() < 150) {
                        this.player.walkX();
                    } else {
                        this.player.idle();
                        this.stateScene = this.start;
                    }
                } else if (this.stateScene == this.start) {
                    this.timeScene++;

                    if (this.timeScene == (60 * 1.5)) {
                        this.stateScene = this.gaming;
                    }
                }
            }
        } else if (gameState.equals("GAME_OVER")) {
            this.frames++;

            if (this.frames == 15) {
                this.frames = 0;

                this.showMessageGameOver = !this.showMessageGameOver;
            }

            if (this.restartGame) {
                this.restartGame = false;
                gameState = "NORMAL";
                this.restartGame("/level1.png");
            }
        } else if (gameState.equals("MENU")) {
            this.menu.tick();
        }
    }

    public void drawRectangleExample(int xOff, int yOff) {
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                int x = i + xOff;
                int y = j + yOff;

                if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
                    continue;

                pixels[x+ (y * WIDTH)] = 0xFFFF0000;
            }
        }
    }

    public void render() {
        BufferStrategy bufferStrategy = this.getBufferStrategy();

        if (bufferStrategy == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(0, 0, 0));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        world.render(graphics);

        Collections.sort(entities, Entity.comparator);
        for (Entity entity: entities) {
            entity.render(graphics);
        }

        this.appyLight();

        this.ui.render(graphics);

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).render(graphics);
        }

        graphics.dispose();
        graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(
                this.image,
                0,
                0,
                WIDTH * SCALE,
                HEIGHT * SCALE,
                null
        );

        // Full screen
        /*graphics.drawImage(
                this.image,
                0,
                0,
                (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(),
                null
        );*/

        // not used, only so example
        graphics.setFont(this.newFont);
        graphics.setFont(new Font("arial", Font.PLAIN, 20));
        graphics.setColor(Color.white);
        graphics.drawString("Ammo: " + this.player.getAmmo(), 595, 31);
        graphics.drawString((int) this.player.getLife() + "/ 100", 90, 31);

        world.renderMinimap(this.minimapPixels);
        graphics.drawImage(this.minimapa, 615, 375, World.WIDTH * 5, World.HEIGHT * 5, null);

        if (gameState.equals("GAME_OVER")) {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setColor(new Color(0, 0, 0, 150));
            graphics2D.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
            graphics.setFont(new Font("arial", Font.BOLD, 36));
            graphics.setColor(Color.white);
            graphics.drawString("Game Over!!", (WIDTH * SCALE) / 2 - 100, (HEIGHT * SCALE) / 2);
            graphics.setFont(new Font("arial", Font.BOLD, 20));

            if (this.showMessageGameOver) {
                graphics.drawString("Press Enter", (WIDTH * SCALE) / 2 - 50, (HEIGHT * SCALE) / 2 + 40);
            }
        } else if (gameState.equals("MENU")) {
            this.menu.render(graphics);
        }

        /*
         * To rotate objetcs
            Graphics2D graphics2D = (Graphics2D) graphics;
            double angleMouse = Math.atan2(
                     200 + 25 - this.mouseY,
                     200 + 25 - this.mouseX
            );
            graphics2D.rotate(angleMouse, 200 + 25, 200 + 25);
            graphics.setColor(Color.RED);
            graphics.fillRect(200, 200, 50, 50);*/

        bufferStrategy.show();
    }

    private void appyLight() {
        /*for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (lightmapPixels[i + (j * WIDTH)] == 0xFFFFFFFF) {
                    pixels[i + (j * WIDTH)] = 0;
                }
            }
        }*/
    }

    @Override
    public void run() {
        this.requestFocus();

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        int frames = 0;
        double timer = System.currentTimeMillis();

        while (this.isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                this.tick();
                this.render();
                delta--;

                frames++;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }

        this.stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Z) {
            this.player.jump();
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            this.player.moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            this.player.moveLeft();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            if (gameState.equals("MENU")) {
                this.menu.moveUp();
            } else {
                this.player.moveUp();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            if (gameState.equals("MENU")) {
                this.menu.moveDown();
            } else {
                this.player.moveDown();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_X) {
            this.player.shooting();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (gameState.equals("MENU")) {
                this.menu.pressEnter();
            } else {
                this.restartGame = true;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameState.equals("NORMAL")) {
                this.menu.pressPause();
                gameState = "MENU";
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (gameState.equals("NORMAL")) {
                this.saveGame = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.player.idle();

        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            this.player.stopMoveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            this.player.stopMoveLeft();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                this.player.stopMoveUp();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                this.player.stopMoveDown();
        }

        if (e.getKeyCode() == KeyEvent.VK_X) {
            this.player.stopShooting();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.player.mouseShooting(
                e.getX(),
                e.getY()
        );
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.player.stopMouseShooting();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
    }
}
