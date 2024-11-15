package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Game implements Runnable {
    private final GameCanvas canvas;
    public JFrame frame;
    public Player player;
    public Bomb bomb;
    public Enemy[] enemies;
    public Tile[][] map;
    public int mapWidth = 15;
    public int mapHeight = 11;
    public int tileSize = 50;
    public boolean running = true;
    public boolean gameOver = false;
    public boolean gameWon = false; 
    public double rate = 60.0;
    public GameState gameState = GameState.RUNNING;
    public long startTime;
    public int timer = 60;
    public boolean timerRunning = true;
    public boolean debugMode = false;
    public Bomb currentBomb;
    private int frameCounter = 0;
    private int updateCounter = 0;
    private int fps = 0;
    private int ups = 0;

    public Game() {
        frame = new JFrame("BombMakeGoBoom");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((mapWidth * tileSize) + 200, (mapHeight * tileSize) + 80);
        frame.setResizable(true);
        frame.setVisible(true);
        timer = 60;
        startTime = System.currentTimeMillis();
        timerRunning = true;

        map = new Tile[mapHeight][mapWidth];
        player = new Player(1, 1, this);
        enemies = new Enemy[1];
        enemies[0] = new Enemy(13, 9, this);
        currentBomb = null;
        initMap();

        canvas = new GameCanvas(this);
        frame.add(canvas);
        canvas.setPreferredSize(new Dimension(mapWidth * tileSize, mapHeight * tileSize));

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        Thread drawLoop = new Thread(canvas);
        drawLoop.start();
        canvas.reset();
    }

    private void handleKeyPress(KeyEvent e) {
        if (gameState == GameState.RUNNING) {
            player.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
                playSound("pause.wav");
                gameState = GameState.PAUSED;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE && currentBomb == null) {
                currentBomb = new Bomb(player.x, player.y, this);
            }
            // if(Enemy.takeDamage()){
            //     playSound("cheer.wav");
            // }
            // if(bomb.exploded == true){
            //     playSound("pause.wav");
                
            // }

            if (e.getKeyCode() == KeyEvent.VK_K) { 
                debugMode = !debugMode;
            }
        } else if (gameState == GameState.PAUSED) {
            if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                playSound("pause.wav");
                gameState = GameState.RUNNING;
            }
            if (e.getKeyCode() == KeyEvent.VK_N) {
                playSound("menu.wav");
                resetGame();
            }
            if (e.getKeyCode() == KeyEvent.VK_E) {
                playSound("pause.wav");
                System.exit(0);
            }
            // if(e.getKeyCode()== KeyEvent.VK_ALT && e.getKeyCode()== KeyEvent.VK_F4){
            //     System.exit(0);
            // }
        } else if (gameOver ) {
            if (e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                playSound("menu.wav");
                resetGame();
            }
            if (e.getKeyCode() == KeyEvent.VK_E) {
                System.exit(0);
            }
        }else if (gameWon) {
            if (e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                playSound("menu.wav");
                resetGame();
            }
            if (e.getKeyCode() == KeyEvent.VK_E) {
                System.exit(0);
            }
        }
    }

    private void initMap() {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (y == 0 || y == mapHeight - 1 || x == 0 || x == mapWidth - 1 || (x % 2 == 0 && y % 2 == 0)) {
                    map[y][x] = new Tile(Tile.Type.WALL);
                } else {
                    if (Math.random() < 0.2) {
                        map[y][x] = new Tile(Tile.Type.BLOCK);
                    } else {
                        map[y][x] = new Tile(Tile.Type.EMPTY);
                    }
                }
            }
        }
        map[1][1] = new Tile(Tile.Type.EMPTY);
        map[1][2] = new Tile(Tile.Type.EMPTY);
        map[2][1] = new Tile(Tile.Type.EMPTY);

        map[9][12] = new Tile(Tile.Type.EMPTY);
        map[9][13] = new Tile(Tile.Type.EMPTY);
        map[8][13] = new Tile(Tile.Type.EMPTY);
    }

    public void resetGame() {
        running = true;
        gameOver = false;
        player.alive= true;
        gameWon= false;
        //enemy.alive=true;
        gameState = GameState.RUNNING;

        startTime = System.currentTimeMillis();
        timer = 60;

        player.x = 1;
        player.y = 1;

        initMap();

        enemies[0] = new Enemy(13, 9, this);
    }
    
    public void checkCollision() {
        for (Enemy enemy : enemies) {
            if (enemy != null && player.x == enemy.x && player.y == enemy.y) {
                gameOver();
            }
        }
    }
    

    public void gameOver() {
        playSound("gameover.wav");
        running = false;
        gameOver = true;
        gameState = GameState.GAME_OVER;
    }
    public void gameWon() {
        System.out.println("Congratulations! You've won the game.");
        playSound("cheer.wav");
        running = false;
        gameWon = true;
        gameState = GameState.GAME_WON;
    }
    private void playSound(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void drawMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        if (gameState == GameState.PAUSED) {
            g.drawString("PAUSED", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Continue Playing(P)", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 + 10);
            g.drawString("New Game(N)", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 + 50);
            g.drawString("Exit(E)", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 + 90);
        
        } else if (gameState == GameState.GAME_OVER) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER", (canvas.getWidth() / 2) - 120, canvas.getHeight() / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("New Game(N)", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 + 10);
            g.drawString("Exit(E)", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 + 50);
        }
        
        else if (gameState == GameState.GAME_WON) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Congratulations! You Won", (canvas.getWidth() / 2) - 120, canvas.getHeight() / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("New Game(N)", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 + 10);
            g.drawString("Exit(E)", (canvas.getWidth() / 2) - 100, canvas.getHeight() / 2 + 50);
            
        }

    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1_000_000_000 / rate;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;
        while (true) {
            if (running) {
                long now = System.nanoTime();
                if (now - lastTime >= nsPerUpdate) {
                    player.update();
                    for (Enemy enemy : enemies) {
                        if (enemy != null) {
                            enemy.update();
                        }
                    }
                    checkCollision();

                    if (currentBomb != null) {
                        currentBomb.update();
                    }
                    updates++;
                    updateCounter++;

                    if (timerRunning) {
                        long remTime = (System.currentTimeMillis() - startTime) / 1000;
                        if (remTime >= 60) {
                            gameOver();
                        }
                    }
                    lastTime = now;
                }
                canvas.repaint();
                frames++;
                frameCounter++;
                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    fps = frames;
                    ups = updates;
                    frames = 0;
                    updates = 0;
                }
            }
            try {
                Thread.sleep((long) (1000.0 / rate));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    public int getFPS() {
        return fps;
    }

    public int getUPS() {
        return ups;
    }

    public int getFrameCounter() {
        return frameCounter;
    }

    public int getUpdateCounter() {
        return updateCounter;
    }

    public static void main(String[] args) {
        Game game = new Game();
        Thread logicLoop = new Thread(game);
        logicLoop.start();
    }
}
