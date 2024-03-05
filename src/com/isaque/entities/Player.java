package com.isaque.entities;

import com.isaque.graphics.Spritesheet;
import com.isaque.main.Game;
import com.isaque.world.Camera;
import com.isaque.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static java.lang.Math.atan2;

public class Player extends Entity{

    public boolean right,up,left,down;
    public int right_dir = 3, left_dir = 2, up_dir = 1, down_dir = 0;
    public int dir = down_dir;
    public double spd = 1;

    private int frames = 0, maxFrames = 10, index = 0, maxIndex = 3;
    private final BufferedImage[] rightPlayer;
    private final BufferedImage[] leftPlayer;
    private final BufferedImage[] backPlayer;
    private final BufferedImage[] frontPlayer;
    private BufferedImage playerDamage;
    public int bullet,maxBullet = 10;
    public boolean weapon;
    public static double life = 0, maxLife=100;
    public boolean isDamaged = false;
    private int damageFrames = 0;
    public boolean shoot = false;
    public int mx,my;

    public Player(int x, int y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);

        rightPlayer = new BufferedImage[4];
        leftPlayer = new BufferedImage[4];
        backPlayer = new BufferedImage[4];
        frontPlayer = new BufferedImage[4];
        playerDamage = Game.spritesheet.getSprite(48,0,16,16);
        for(int i = 0; i < 4; i++) {
            rightPlayer[i] = Game.spritesheet.getSprite((i*16), 16, 16, 16);
        }
        for(int i = 0; i < 4; i++) {
            leftPlayer[i] = Game.spritesheet.getSprite((i*16), 32, 16, 16);
        }
        for(int i = 0; i < 4; i++) {
            backPlayer[i] = Game.spritesheet.getSprite((i*16), 48, 16, 16);
        }
        for(int i = 0; i < 4; i++) {
            frontPlayer[i] = Game.spritesheet.getSprite((i*16), 64, 16, 16);
        }
    }

    public void tick() {
        boolean moved = false;
        //movimento do jogador
        //x
        if(right && World.isFree((int)(x+spd),this.getY())) {
            moved = true;
            dir = right_dir;
            x+=spd;
        } else if(left && World.isFree((int)(x-spd),this.getY())) {
            moved = true;
            dir = left_dir;
            x-=spd;
        }
        //y
        if(up && World.isFree(this.getX(),(int)(y-spd))) {
            moved = true;
            dir = up_dir;
            y-=spd;
        } if(down && World.isFree(this.getX(),(int)(y+spd))) {
            moved = true;
            dir = down_dir;
            y+=spd;
        }

        if(moved) {
            frames++;
            if(frames == maxFrames) {
                frames = 0;
                index++;
                if(index > maxIndex) {
                    index = 0;
                }
            }
        }
        checkCollisionLifePack();
        checkCollisionBullet();
        checkCollisionWeapon();

        if(isDamaged) {
            this.damageFrames++;
            if(this.damageFrames == 8) {
                this.damageFrames = 0;
                isDamaged = false;
            }
        }

//        if(shoot && weapon && bullet > 0) {
//            bullet--;
//            shoot = false;
//            int dx=0;
//            int dy=0;
//            int px=0;
//            int py=0;
//            if(dir == right_dir) {
//                dx = 1;
//                px=19;
//                py=5;
//            } else if(dir == left_dir) {
//                dx = -1;
//                px=-19;
//                py=-5;
//            } else if(dir == up_dir) {
//                dy = -1;
//                px=8;
//                py=-7;
//            } else if (dir == down_dir) {
//                dy = 1;
//                px=8;
//                py=15;
//            }
//
//            Projectile projectile = new Projectile(this.getX()+px,this.getY()+py,width,height,null,dx,dy);
//            Game.projectiles.add(projectile);
//        } //Sistema de tiro com teclado

        if(shoot && weapon && bullet > 0) {
            bullet--;
            shoot = false;
            double angle = atan2(my-(this.getY()+8 - Camera.y),mx-(this.getX()+8 - Camera.x));
            double dx=Math.cos(angle);
            double dy=Math.sin(angle);
            int px=0;
            int py=0;


            Projectile projectile = new Projectile(this.getX()+px,this.getY()+py,width,height,null,dx,dy);
            Game.projectiles.add(projectile);
        }

        if(life<=0) {
            //Game over
            Game.gameState = "GAME_OVER";
        }

        Camera.x = Camera.clamp(getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
        Camera.y = Camera.clamp(getY() - (Game.HEIGTH/2), 0, World.HEIGHT*16 - Game.HEIGTH);
    }
    public void checkCollisionBullet() {
        if(bullet < 10) {
            for (int i = 0; i < Game.entities.size(); i++) {
                Entity e = Game.entities.get(i);
                if (e instanceof Bullet) {
                    if (Entity.isColliding(this, e)) {
                        bullet += 1;
                        if (bullet >= 10)
                            bullet = 10;
                        Game.entities.remove(e);
                    }
                }
            }
        }
    }
    public void checkCollisionWeapon() {
        for(int i = 0; i < Game.entities.size(); i++) {
            Entity e = Game.entities.get(i);
            if(e instanceof Weapon) {
                if(Entity.isColliding(this,e)) {
                    weapon = true;
                    Game.entities.remove(e);
                }
            }
        }
    }
    public void checkCollisionLifePack() {
        for(int i = 0; i < Game.entities.size(); i++) {
            Entity e = Game.entities.get(i);
            if(e instanceof LifePack) {
                if(Entity.isColliding(this,e)) {
                    life+=10;
                    if(life >= 100)
                        life = 100;
                    Game.entities.remove(e);
                }
            }
        }
    }


    public void render(Graphics g) {
        if(!isDamaged) {
            if (dir == right_dir) {
                g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
                if(weapon) {
                    g.drawImage(Entity.RWEAPON_EN,this.getX() - Camera.x + 10,this.getY() - Camera.y,null);
                }
            } else if (dir == left_dir) {
                g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
                if(weapon) {
                    g.drawImage(Entity.LWEAPON_EN,this.getX() - Camera.x - 10,this.getY() - Camera.y,null);
                }
            }
            if (dir == up_dir) {
                if(weapon) {
                    g.drawImage(Entity.UWEAPON_EN,this.getX() - Camera.x,this.getY() - Camera.y - 8,null);
                }
                g.drawImage(backPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
            } else if (dir == down_dir) {
                g.drawImage(frontPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
                if(weapon) {
                    g.drawImage(Entity.DWEAPON_EN,this.getX() - Camera.x,this.getY() - Camera.y + 8,null);
                }
            }
        } else {
            g.drawImage(playerDamage,this.getX() - Camera.x, this.getY() - Camera.y, null);
        }
    }
}
