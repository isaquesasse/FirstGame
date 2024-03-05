package com.isaque.entities;

import com.isaque.main.Game;
import com.isaque.world.Camera;
import com.isaque.world.World;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    public static BufferedImage CAPSULE_UI = Game.hud.getSprite(0, 0, 16, 16);
    public static BufferedImage EMPTYWEAPON_UI = Game.hud.getSprite(16, 0, 16, 16);
    public static BufferedImage HEALTHBAR_UI = Game.hud.getSprite(0,32,96,16);
    public static BufferedImage LIFEPACK_EN = Game.itens.getSprite(0, 0, 16, 16);
    public static BufferedImage RWEAPON_EN = Game.itens.getSprite(16, 0, 16, 16);
    public static BufferedImage LWEAPON_EN = Game.itens.getSprite(32,0,16,16);
    public static BufferedImage DWEAPON_EN = Game.itens.getSprite(16, 16, 16, 16);
    public static BufferedImage UWEAPON_EN = Game.itens.getSprite(32,16,16,16);
    public static BufferedImage BULLET_EN = Game.itens.getSprite(48,0,16,16);
    public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(0, 96, 16, 16);
    public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(144,80,16,16);
    private int maskx,masky,maskw,maskh;

//    public static BufferedImage[] ENEMY_EN;

    //padrão para toda entidade
    protected double x;
    protected double y;
    protected int width;
    protected int height;

    private BufferedImage sprite;

    public Entity(double x, double y, int width, int height, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;

        this.maskx = 0;
        this.masky = 0;
        this.maskh = height;
        this.maskw = width;
    }

    public void setMask(int maskx, int masky, int maskw, int maskh) {
        this.maskx = maskx;
        this.masky = masky;
        this.maskh = maskh;
        this.maskw = maskw;
    }

    //setters
    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public int getX() {
        return (int) this.x;
    }

    //getters
    public int getY() {
        return (int) this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void tick() {}

    public static boolean isColliding(Entity e1, Entity e2) {
        Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx,e1.getY()+e1.masky,e1.maskw,e1.maskh);
        Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx,e2.getY()+e2.masky,e2.maskw,e2.maskh);

        return e1Mask.intersects(e2Mask);
    }

    public void render(Graphics g) {

        g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
//        g.setColor(Color.red); //descomentar para ver as colisões
//        g.fillRect(this.getX()+maskx-Camera.x,this.getY()+masky-Camera.y,maskw,maskh);
    }


}
