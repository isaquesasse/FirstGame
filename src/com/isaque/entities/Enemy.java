package com.isaque.entities;

import com.isaque.main.Game;
import com.isaque.world.Camera;
import com.isaque.world.World;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {

    private double spd = 0.3;
    private int maskx = 10, masky = 10, maskw = 10, maskh = 10;
    private int frames = 0, maxFrames = 6, index = 0, maxIndex = 9, maxHitIndex = 8, hitIndex = 0;
    private BufferedImage[] sprites;
    private BufferedImage[] hitting;
    private int life = 3;
    private boolean isDamaged = false, isHitting = false;
    private int damageFrames = 10, damageCurrent = 0;

    public Enemy(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, null);
        sprites = new BufferedImage[10];
        hitting = new BufferedImage[9];
        sprites[0] = Game.spritesheet.getSprite(0, 96, 16, 16);
        hitting[0] = Game.spritesheet.getSprite(0,112,16,16);
        for (int i = 0; i <= 9; i++) {
            sprites[i] = Game.spritesheet.getSprite((i * 16), 96, 16, 16);
        }
        for (int i = 0; i <= 8; i++) {
            hitting[i] = Game.spritesheet.getSprite((i*16),112,16,16);
        }
    }

    public void tick() {
        if (!isCollidingWithPlayer()) { //checa se está colidindo com o player
            if (Game.player.getX() - x > -160 && Game.player.getY() - y > -160 && Game.player.getX() - x < 160 && Game.player.getY() - y < 160) { //checa se o player está perto para seguir
                if (Game.rand.nextInt(100) < 80) { //80% de chance do inimigo andar, para randomizar a velocidade e não ficarem muito juntos
                    if ((int) x < Game.player.getX() && World.isFree((int) (x + spd), this.getY())
                            && !isColliding((int) (x + spd), this.getY())) {
                        x += spd;
                    } else if ((int) x > Game.player.getX() && World.isFree((int) (x - spd), this.getY())
                            && !isColliding((int) (x - spd), this.getY())) {
                        x -= spd;
                    }

                    if ((int) y > Game.player.getY() && World.isFree(this.getX(), (int) (y - spd))
                            && !isColliding(this.getX(), (int) (y - spd))) {
                        y -= spd;
                    } else if ((int) y < Game.player.getY() && World.isFree(this.getX(), (int) (y + spd))
                            && !isColliding(this.getX(), (int) (y + spd))) {
                        y += spd;
                    }
                    frames++;
                    if (frames == maxFrames) {
                        frames = 0;
                        index++;
                        if (index > maxIndex) {
                            index = 0;
                        }
                    }
                }
            }
        } else { //se não, estamos colidindo
            isHitting = true;
            if (Game.rand.nextInt(100) < 3) {
                Game.player.life-=Game.rand.nextInt(3);
                Game.player.isDamaged = true;
            }
            frames++;
            if (frames == maxFrames) {
                frames = 0;
                hitIndex++;
                if (hitIndex > maxHitIndex) {
                    hitIndex = 0;
                }
            }
        }
        collidingHit();

        if(life <= 0) {
            destroySelf();
            return;
        }

        if(isDamaged) {
            this.damageCurrent++;
            if(this.damageCurrent == this.damageFrames) {
                this.damageCurrent = 0;
                this.isDamaged = false;
            }
        }
    }

    public void destroySelf() {
        Game.enemies.remove(this);
        Game.entities.remove(this);
        return;
    }

    public void collidingHit() {
        for(int i = 0;i < Game.projectiles.size(); i++) {
            Entity e = Game.projectiles.get(i);
            if(e instanceof Projectile) {
                if(Entity.isColliding(this,e)) {;
                    isDamaged = true;
                    if (Game.rand.nextInt(100) < 5) {
                        life-= 3;
                    } else {
                        life--;
                    }
                    Game.projectiles.remove(i);
                    return;
                }
            }
        }

    }

    public boolean isCollidingWithPlayer() {
        Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
        Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
        return enemyCurrent.intersects(player);
    }

    public boolean isColliding(int xnext, int ynext) {
        Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);


        for (int i = 0; i < Game.enemies.size(); i++) {
            Enemy e = Game.enemies.get(i);
            if (e == this)
                continue; //se no looping chegar nessa classe, é para ignorar e continuar
            Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
            if (enemyCurrent.intersects(targetEnemy)) {
                return true;
            }
        }
        return false;
    }

    public void render(Graphics g) {
        if(!isDamaged) {
            g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
        } else {
            g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
        }
//        g.setColor(Color.red); //descomentar para ver as colisões
//        g.fillRect(this.getX()+maskx-Camera.x,this.getY()+masky-Camera.y,maskw,maskh);
        //super.render(g); //chamando da entity pai o que o método faz
    }

}
