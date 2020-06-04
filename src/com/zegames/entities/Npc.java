package com.zegames.entities;

import com.zegames.main.Game;
import com.zegames.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Npc extends Entity {
    private final String[] sentences;
    private final Player player;
    private boolean showMessage;
    private int index;
    private int messageIndex;

    public Npc(double x, double y, int width, int height, BufferedImage sprite, Camera camera, Player player) {
        super(x, y, width, height, sprite, camera);

        this.messageIndex = Game.RAND.nextInt(4);
        this.player = player;
        this.showMessage = false;
        this.sentences = new String[]{
                "Hello! This work of ours is tough right partner ?!",
                "You're being chased! Look behind you!",
                "Rapadura is soft, but not sweet !!",
                "I really wanted to go home !!",
                "I am so weak that the monsters pass right by me !!"
        };
        this.depth = 2;
    }

    @Override
    public void tick() {
        this.showMessage = Math.abs(this.player.getX() - this.x) < 15 && Math.abs(this.player.getY() - this.y) < 15;

        if (this.showMessage) {
            if (this.index < this.sentences[this.messageIndex].length()) {
                this.index++;
            }
        }

        if (!this.showMessage) {
            this.messageIndex = Game.RAND.nextInt(4);
            this.index = 0;
        }
    }

    @Override
    public void render(Graphics graphics) {
        super.render(graphics);

        if (this.showMessage) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(
                    (int) this.x - this.camera.getX(),
                    (int) this.y - this.camera.getY() - 22,
                    224,
                    15
            );

            graphics.setColor(Color.BLUE);
            graphics.fillRect(
                    (int) this.x - this.camera.getX() + 1,
                    (int) this.y - this.camera.getY() - 21,
                    224 - 2,
                    15 - 2
            );

            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("arial", Font.BOLD, 9));
            graphics.drawString(this.sentences[this.messageIndex].substring(0, this.index), (int) this.x - this.camera.getX(),
                    (int) this.y - this.camera.getY() - 11);
        }
    }
}
