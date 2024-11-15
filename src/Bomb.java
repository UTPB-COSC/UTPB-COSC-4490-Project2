package src;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Bomb {
    public int x, y;  
    private Game game;
    private BufferedImage bombImg;  
    private BufferedImage explosionImg;
    private long placementTime; 
    private static final long EXPLOSION_DELAY = 1000; 
    public boolean exploded = false;
    private boolean soundPlayed = false;
    public Bomb(int x, int y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;

        try {
            bombImg = ImageIO.read(new File("bombpic.png"));
            System.out.println("Bomb image loaded successfully");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Bomb image doesn't load");
            bombImg = null;
        }
        

        placementTime = System.currentTimeMillis();
    }

    public void update() {
        if (!exploded && System.currentTimeMillis() - placementTime >= EXPLOSION_DELAY) {
            explode();
        }
    }

    private void explode() {
        exploded = true;
        if (!soundPlayed) {
            playExplosionSound();
            

            

            soundPlayed = true;  
        }
        try {
            explosionImg = ImageIO.read(new File("explosionimg.png"));
            System.out.println("Bomb Explosion image loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Bomb explosion image doesn't load");
            bombImg = null;
        }

        int[][] directions = {
            {0,0},
            {0, -1},  
            {0, 1},   
            {-1, 0},  
            {1, 0}    

        };
        for (int[] direction : directions) {
            int targetX = x + direction[0];
            int targetY = y + direction[1];
            if (targetX >= 0 && targetX < game.mapWidth && targetY >= 0 && targetY < game.mapHeight) {
                if (game.map[targetY][targetX].type == Tile.Type.BLOCK) {
                    game.map[targetY][targetX] = new Tile(Tile.Type.EMPTY);
                    
                    
                }                    
                if (targetX == game.player.x && targetY == game.player.y) {
                    game.player.alive= false;
                    game.gameOver();                    
                }

                for (Enemy enemy : game.enemies) {
                    if (enemy.x == targetX && enemy.y == targetY) {
                        enemy.alive= false;
                        game.gameWon();
                    }
                }
            }
        }
        game.currentBomb = null;
    }

    public void draw(Graphics g) {
        if (bombImg != null) {
            Image scaledImage = bombImg.getScaledInstance(game.tileSize, game.tileSize, Image.SCALE_SMOOTH);
            g.drawImage(scaledImage, x * game.tileSize, y * game.tileSize, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x * game.tileSize, y * game.tileSize, game.tileSize, game.tileSize);
        }
        if (exploded && explosionImg != null) {
            int[][] directions = {
                {0,0},
                {0, -1},  
                {0, 1},   
                {-1, 0},  
                {1, 0}    
    
            };
            for (int[] direction : directions) {
                int targetX = x + direction[0];
                int targetY = y + direction[1];
                if (targetX >= 0 && targetX < game.mapWidth && targetY >= 0 && targetY < game.mapHeight) {
                    Image scaledExplosion = explosionImg.getScaledInstance(game.tileSize, game.tileSize, Image.SCALE_SMOOTH);
                    g.drawImage(scaledExplosion, targetX * game.tileSize, targetY * game.tileSize, null);
                }
            }
        }
    }

    private void playExplosionSound() {
        try {
            File soundFile = new File("bombExplode.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();  // Play the sound
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // Handle exceptions for file not found or audio errors
        }
    }

    public boolean hasExploded() {
        return exploded;
    }
}
