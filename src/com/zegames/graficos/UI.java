package com.zegames.graficos;

import com.zegames.entities.Player;

import java.awt.*;

public class UI {
    private final Player player;

    public UI(Player player) {
        this.player = player;
    }

    public void render(Graphics graphics) {
        graphics.setColor(new Color(255, 0, 0));
        graphics.fillRect(8, 4 ,  70, 8);
        graphics.setColor(new Color(0, 200, 0));
        graphics.fillRect(8, 4 , (int) ((this.player.getLife()/100) * 70), 8);
        graphics.setColor(Color.white);
    }
}
