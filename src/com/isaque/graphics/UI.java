package com.isaque.graphics;

import com.isaque.entities.Entity;
import com.isaque.entities.Player;
import com.isaque.main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UI {

    public void render(Graphics g) {
        //full life
        g.setColor(Color.black);
        g.fillRect(6,5,76,8);
        //life
        g.setColor(new Color(172,50,50));
        g.fillRect(6,5,(int)((Game.player.life/Game.player.maxLife)*76),8);
        g.drawImage(Entity.HEALTHBAR_UI,1,1,null);
    }

}
