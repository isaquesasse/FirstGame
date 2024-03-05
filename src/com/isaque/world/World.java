package com.isaque.world;

import com.isaque.entities.*;
import com.isaque.graphics.Spritesheet;
import com.isaque.main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class World {

    private static Tile[] tiles;
    public static int WIDTH,HEIGHT;
    public static final int TILE_SIZE = 16;
    private int frames = 0, maxFrames = 10, index = 0, maxIndex = 3;
    private final BufferedImage[] droppedWeapon;


    public World(String path) {
        droppedWeapon = new BufferedImage[8];
        for(int i = 0; i < 8; i++) {
            droppedWeapon[i] = Game.spritesheet.getSprite((i*16), 48, 16, 16);
        }
        try {
            BufferedImage map = ImageIO.read(getClass().getResource(path));
            int[] pixels = new int[map.getWidth() * map.getHeight()];
            WIDTH = map.getWidth();
            HEIGHT = map.getHeight();
            tiles = new Tile[map.getWidth() * map.getHeight()];
            map.getRGB(0,0,map.getWidth(),map.getHeight(),pixels,0,map.getWidth());
            //"0xFF" = para completar o hexadecimal antes da cor; "ff3232" = cor
            for(int xx = 0; xx < map.getWidth(); xx++) {
                for(int yy = 0; yy < map.getHeight(); yy++) {
                    int pixelAtual = pixels[xx + (yy*map.getWidth())];
                    //sempre é floor //abffff
                    if(Game.rand.nextInt(100) < 50) {
                        tiles[xx + (yy * WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
                    } else {
                        tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR_2);
                    }
                    if(pixelAtual == 0xFF000000) {
                        //floor
                        if(Game.rand.nextInt(100) < 50) {
                            tiles[xx + (yy * WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
                        } else {
                            tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR_2);
                        }
                    } else if (pixelAtual == 0xFFFFFFFF) {
                        //parede horizontal
                        tiles[xx + (yy * WIDTH)] = new WallTile(xx*16,yy*16,Tile.TILE_WALL);
                    } else if (pixelAtual == 0xFFABFFFF) {
                        //parede vertical
                        tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL2);
                    } else if (pixelAtual == 0xFF3F3FFF) {
                        //player
                        Game.player.setX(xx*16);
                        Game.player.setY(yy*16);
                    } else if (pixelAtual == 0xFFc957ff) {
                        //vida
                        LifePack pack = new LifePack(xx*16,yy*16,16,16,Entity.LIFEPACK_EN);
                        pack.setMask(4,4,8,8);
                        Game.entities.add(pack);
                    } else if (pixelAtual == 0xFFFF3232) {
                        //inimigo
                        Enemy en = new Enemy(xx*16,yy*16,16,16,Entity.ENEMY_EN);
                        Game.entities.add(en);
                        Game.enemies.add(en);
                    } else if (pixelAtual == 0xFFFB7A36) {
                        //arma
                        Weapon weapon = new Weapon(xx*16,yy*16,16,16,Entity.RWEAPON_EN);
                        weapon.setMask(4,5,7,6);
                        Game.entities.add(weapon);
                    } else if (pixelAtual == 0xFFFBF236) {
                        //munição
                        Bullet bullet = new Bullet(xx*16,yy*16,16,16,Entity.BULLET_EN);
                        bullet.setMask(6,5,4,8);
                        Game.entities.add(bullet);
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isFree(int xnext, int ynext) {

        int x1 = xnext / TILE_SIZE;
        int y1 = ynext / TILE_SIZE;

        int x2 = (xnext+TILE_SIZE-1) / TILE_SIZE;
        int y2 = ynext / TILE_SIZE;

        int x3 = xnext / TILE_SIZE;
        int y3 = (ynext+TILE_SIZE-1)  / TILE_SIZE;

        int x4 = (xnext+TILE_SIZE-1) / TILE_SIZE;
        int y4 = (ynext+TILE_SIZE-1)  / TILE_SIZE;

        return !(tiles[x1 + (y1*World.WIDTH)] instanceof WallTile ||
                tiles[x2 + (y2*World.WIDTH)] instanceof WallTile ||
                tiles[x3 + (y3*World.WIDTH)] instanceof WallTile ||
                tiles[x4 + (y4*World.WIDTH)] instanceof WallTile);

    }

    public void tick(Graphics g) {
        frames++;
        if(frames == maxFrames) {
            frames = 0;
            index++;
            if(index > maxIndex) {
                index = 0;
            }
        }
    }

    public static void restartGame(String level) {
        Game.entities.clear();
        Game.enemies.clear();
        Game.entities = new ArrayList<Entity>();
        Game.enemies = new ArrayList<Enemy>();
        //world após spritesheet pois ele precisa ser carregado antes de iniciar o world
        Game.spritesheet = new Spritesheet("/spritesheet.png");
        Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(0,16,16,16));
        Game.entities.add(Game.player);
        Game.world = new World("/"+level);
        return;
    }

    public void render(Graphics g) {
        int xstart = Camera.x/16;
        int ystart = Camera.y/16;

        int xfinal = xstart + Game.WIDTH/16;
        int yfinal = ystart + Game.HEIGTH/16;

        for(int xx = xstart; xx <= xfinal; xx++){
            for(int yy = ystart; yy <= yfinal; yy++) {
                if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
                        continue;
                Tile tile = tiles[xx + (yy*WIDTH)];
                tile.render(g);
            }
        }
    }

}
