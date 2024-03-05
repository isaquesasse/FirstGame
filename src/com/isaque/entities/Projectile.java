package com.isaque.entities;

import com.isaque.main.Game;
import com.isaque.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Projectile extends Entity{

    private double dx;
    private double dy;
    private double spd = 4;
    private int life = 52, curLife = 0;

    public Projectile(double x, double y, int width, int height, BufferedImage sprite, double dx, double dy) {
        super(x, y, width, height, sprite);
        this.dx = dx;
        this.dy = dy;
    }

    public void tick() {
        x+=dx*spd;
        y+=dy*spd;
        curLife++;
        if(curLife == life) {
            Game.projectiles.remove(this);
            return;
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.yellow);
        g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y,3,3);
    }

}
