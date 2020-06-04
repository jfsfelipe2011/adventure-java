package com.zegames.main;

import java.awt.*;
import java.io.*;

public class Menu {
    private final String[] options = {"New Game", "Load", "Exit", "Continue"};
    private final Game game;
    private int currentOption;
    private final int maxOption;
    private boolean down;
    private boolean up;
    private boolean enter;
    private boolean pause;
    private boolean saveExists = false;
    private boolean saveGame = false;

    public Menu(Game game) {
        this.game = game;
        this.currentOption = 0;
        this.maxOption = this.options.length - 2;
        this.down = false;
        this.up = false;
        this.enter = false;
        this.pause = false;
        this.saveExists = false;
        this.saveGame = false;
    }

    public void moveUp() {
        this.up = true;
    }

    public void moveDown() {
        this.down = true;
    }

    public void pressEnter() {
        this.enter = true;
    }

    public void pressPause() {
        this.pause = true;
    }

    public void tick() {
        File file = new File("save.txt");

        this.saveExists = file.exists();

        if (this.up) {
            this.up = false;
            this.currentOption--;

            if (this.currentOption < 0) {
                this.currentOption = this.maxOption;
            }
        }

        if (this.down) {
            this.down = false;
            this.currentOption++;

            if (this.currentOption > this.maxOption) {
                this.currentOption = 0;
            }
        }

        if (this.enter) {
            this.enter = false;

            if (this.options[this.currentOption].equals(this.options[0])
                    || this.options[this.currentOption].equals(this.options[3])) {
                Game.gameState = "NORMAL";

                if (this.options[this.currentOption].equals(this.options[0])) {
                    File file1 = new File("save.txt");

                    if (file1.exists()) {
                        file.delete();
                    }

                    this.pause = false;
                }
            } else if (this.options[this.currentOption].equals(this.options[1])) {
                if (this.saveExists) {
                    String saver = this.loadGame(10);

                    applySave(saver);
                }
            } else if (this.options[this.currentOption].equals(this.options[2])) {
                System.exit(1);
            }
        }
    }

    public void applySave(String string) {
        String[] spl = string.split("/");

        for (int i = 0; i < spl.length; i++) {
            String[] spl2 = spl[i].split(":");

            switch (spl2[0]) {
                case "level":
                    this.game.restartGame("/level" + spl2[1] + ".png");
                    Game.gameState = "NORMAL";
                    this.pause = false;
                    break;
                case "life":
                    this.game.definePlayerLife(Double.parseDouble(spl2[1]));
                    break;
            }
        }
    }

    public String loadGame(int encode) {
        String line = "";
        File file = new File("save.txt");

        if (file.exists()) {
            try {
                String singleLine = null;

                BufferedReader reader = new BufferedReader(new FileReader("save.txt"));

                try {
                    while ((singleLine = reader.readLine()) != null) {
                        String[] transition = singleLine.split(":");

                        char[] val = transition[1].toCharArray();
                        transition[1] = "";

                        for (int i = 0; i < val.length; i++) {
                            val[i] -= encode;
                            transition[1] += val[i];
                        }

                        line += transition[0] + ":" + transition[1] + "/";
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }
        }

        return line;
    }

    public void saveGame(String[] fields, int[] values, int encode) {
        BufferedWriter write = null;

        try {
            write = new BufferedWriter(new FileWriter("save.txt"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        for (int i = 0; i < fields.length; i++) {
            String current = fields[i] + ":";

            char[] valuesChar = Integer.toString(values[i]).toCharArray();

            for (int j = 0; j < valuesChar.length; j++) {
                valuesChar[j] += encode;
                current += valuesChar[j];
                System.out.println(current);
            }

            try {
                assert write != null;
                write.write(current);

                if (i < fields.length - 1) {
                    write.newLine();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        try {
            assert write != null;
            write.flush();
            write.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void render(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
        graphics.setColor(new Color(0, 110, 56));
        graphics.setFont(new Font("arial", Font.BOLD, 36));
        graphics.drawString("Adventure Game",
                (Game.WIDTH * Game.SCALE) / 2 - 140, (Game.HEIGHT * Game.SCALE) / 2 - 160);

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("arial", Font.BOLD, 24));

        if (this.pause) {
            graphics.drawString(this.options[3], (Game.WIDTH * Game.SCALE) / 2 - 50, 200);
        } else {
            graphics.drawString(this.options[0], (Game.WIDTH * Game.SCALE) / 2 - 60, 200);
        }

        graphics.drawString(this.options[1], (Game.WIDTH * Game.SCALE) / 2 - 30, 240);
        graphics.drawString(this.options[2], (Game.WIDTH * Game.SCALE) / 2 - 25, 280);

        if (this.options[this.currentOption].equals(this.options[0])) {
            graphics.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 80, 200);
        } else if (this.options[this.currentOption].equals(this.options[1])) {
            graphics.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 80, 240);
        } else if (this.options[this.currentOption].equals(this.options[2])) {
            graphics.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 80, 280);
        }
    }
}
