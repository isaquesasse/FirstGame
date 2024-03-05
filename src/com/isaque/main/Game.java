package com.isaque.main;

import com.isaque.entities.*;
import com.isaque.graphics.Spritesheet;
import com.isaque.graphics.UI;
import com.isaque.world.Camera;
import com.isaque.world.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    public static final int WIDTH = 240, HEIGTH = 160, SCALE = 3;

    private BufferedImage image;
    public static List<Entity> entities;
    public static List<Projectile> projectiles;
    public static List<Enemy> enemies;
    public static Spritesheet spritesheet;
    public static Spritesheet itens;
    public static Spritesheet hud;
    public static World world;
    public static Player player;
    public static Random rand;
    private int CUR_LEVEL = 1, MAX_LEVEL = 2;
    public static String gameState = "NORMAL";
    public UI ui;

    public Game() {
        rand = new Random();
        addKeyListener(this); //this = não está em nenhuma outra classe, então usara essa, ou seja "this"
        addMouseListener(this);
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGTH * SCALE));
        initFrame();
        //Inicializando objetos
        ui = new UI();
        image = new BufferedImage(WIDTH, HEIGTH, BufferedImage.TYPE_INT_RGB);
        entities = new ArrayList<Entity>();
        enemies = new ArrayList<Enemy>();
        projectiles = new ArrayList<Projectile>();
        //world após spritesheet pois ele precisa ser carregado antes de iniciar o world
        spritesheet = new Spritesheet("/spritesheet.png");
        itens = new Spritesheet("/itemsprites.png");
        hud = new Spritesheet("/hudsprites.png");
        player = new Player(0, 0, 16, 16, spritesheet.getSprite(0, 16, 16, 16));
        entities.add(player);
        world = new World("/level1.png");
    }

    public void initFrame() {
        frame = new JFrame("Graphics");
        frame.add(this); //adiciona o this, que é o canvas. pega as propriedades das linhas de cima
        frame.setResizable(false); //não deixa redimencionar a janela
        frame.pack(); //necessário ficar após o canva para acionar algumas coisas
        frame.setLocationRelativeTo(null); //para a janela ficar no centro da tela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //quando clicar para fechar, de fato fecha tudo
        frame.setVisible(true); //visiviel ao iniciar
    }

    public synchronized void start() {
        thread = new Thread(this); //this = mesma class, pois já está implementando o runnable
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void tick() { //tick = update do jogo
        if(gameState == "NORMAL") {
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);
                e.tick();
            }

            for (int i = 0; i < projectiles.size(); i++) {
                projectiles.get(i).tick();
            }

            if (enemies.size() == 0) {
                CUR_LEVEL++;
                if (CUR_LEVEL > MAX_LEVEL) {
                    CUR_LEVEL = 1;
                }
                String newWorld = "level" + CUR_LEVEL + ".png";
                System.out.println(newWorld);
                World.restartGame(newWorld);
            }
        } else if(gameState == "GAME_OVER") {

        }
    }

    public void render() { //render = renderização do jogo
        BufferStrategy bs = this.getBufferStrategy(); //bufferStrategy é uma sequencia de buffers para otimizar a renderização
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGTH);

        // "Inicio" da renderização do jogo
        // Graphics2D g2 = (Graphics2D) g; //objeto com gráfico 2D mas é igual varíavel G, que transforma em tipos de gráfico 2D. O nome disso ("(Graphics2D) g") é Cast

        world.render(g);
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            e.render(g);
        }
        for(int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).render(g);
        }
        ui.render(g);

        // "Fim" da renderização do jogo

        g.dispose(); //Para ajudar na otimização
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGTH * SCALE, null);
        //weapon hud
        if (player.weapon && player.bullet > 0) {
            g.drawImage(Entity.RWEAPON_EN, 542, 1, 16 * SCALE, 16 * SCALE, null);
            for (int i = 0; i < player.maxBullet; i++) {
                g.drawImage(Entity.CAPSULE_UI, 576 + (i * 12), 1, 16 * SCALE, 16 * SCALE, null);
            }
            for (int i = 0; i < player.bullet; i++) {
                g.drawImage(Entity.BULLET_EN, 576 + (i * 12), 1, 16 * SCALE, 16 * SCALE, null);
            }
        } else if (player.bullet > 0 || player.weapon) {
            if(!player.weapon) {
                g.drawImage(Entity.EMPTYWEAPON_UI, 542, 1, 16 * SCALE, 16 * SCALE, null);
            } else {
                g.drawImage(Entity.RWEAPON_EN, 542, 1, 16 * SCALE, 16 * SCALE, null);
            }
            for (int i = 0; i < player.maxBullet; i++) {
                g.drawImage(Entity.CAPSULE_UI, 576 + (i * 12), 1, 16 * SCALE, 16 * SCALE, null);
            }
            for (int i = 0; i < player.bullet; i++) {
                g.drawImage(Entity.BULLET_EN, 576 + (i * 12), 1, 16 * SCALE, 16 * SCALE, null);
            }
        }
        if(gameState == "GAME_OVER") {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0,0,0,100));
            g2.fillRect(0,0,WIDTH*SCALE,HEIGTH*SCALE);
            g.setFont(new Font("arial",Font.BOLD,40));
            g.setColor(new Color(203, 4, 4));
            g.drawString("GAME OVER!", (WIDTH*SCALE)/2 - 120,(HEIGTH*SCALE)/2 - 20);
            g.setFont(new Font("arial",Font.BOLD,24));
            g.setColor(Color.white);
            g.drawString("> Pressione ENTER para reiniciar.", (WIDTH*SCALE)/2 - 200,(HEIGTH*SCALE)/2 + 30);        }

        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime(); //pega o tempo em nanosegundos
        double amountOfTicks = 60.0; //frames por segundo
        double ns = 1000000000 / amountOfTicks; //dividindo 1 segundo em forma de nanosegundos pela quantidade de ticks
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        requestFocus(); //para focar a janela automaticamente ao abrir
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick(); //sempre dar update antes de render
                render();
                frames++;
                delta--;
            }
            if (System.currentTimeMillis() - timer >= 1000) {
                //System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }

        stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //X
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            player.right = true;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            player.left = true;
        }
        //Y
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            player.up = true;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            player.down = true;
        }
        //shoot
        if(e.getKeyCode() == KeyEvent.VK_Q) {
            player.shoot = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //X
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            player.right = false;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            player.left = false;
        }
        //Y
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            player.up = false;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            player.down = false;
        }
        //shoot
        if(e.getKeyCode() == KeyEvent.VK_Q) {
            player.shoot = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        player.shoot = true;
        player.mx = (e.getX() / SCALE);
        player.my = (e.getY() / SCALE);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        player.shoot = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
