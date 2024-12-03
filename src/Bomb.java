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
    private BufferedImage scaledImage;
    private BufferedImage scaledExplosion;
    private long placementTime; 
    private static final long EXPLOSION_DELAY = 1000;
    private static final long EXPLOSION_LIFE = 100;
    public boolean exploded = false;
    private boolean soundPlayed = false;
    public Bomb(int x, int y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;

        try {
            BufferedImage bombImg = ImageIO.read(new File("bombpic.png"));
            System.out.println("Bomb image loaded successfully");

            scaledImage = new BufferedImage(game.tileSize, game.tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics g = scaledImage.getGraphics();
            Image tempImage = bombImg.getScaledInstance(game.tileSize, game.tileSize, Image.SCALE_SMOOTH);
            g.drawImage(tempImage, 0, 0, null);
            g.dispose();

            BufferedImage explosionImg = ImageIO.read(new File("explosionimg.png"));
            System.out.println("Bomb Explosion image loaded successfully");

            scaledExplosion = new BufferedImage(game.tileSize, game.tileSize, BufferedImage.TYPE_INT_ARGB);
            g = scaledExplosion.getGraphics();
            tempImage = explosionImg.getScaledInstance(game.tileSize, game.tileSize, Image.SCALE_SMOOTH);
            g.drawImage(tempImage, 0, 0, null);
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Bomb image doesn't load");
            //bombImg = null;
            System.exit(1);
        }
        

        placementTime = System.currentTimeMillis();
    }

    public void update() {
        if (!exploded && System.currentTimeMillis() - placementTime >= EXPLOSION_DELAY) {
            explode();
        }
        if (exploded && System.currentTimeMillis() - placementTime >= EXPLOSION_DELAY + EXPLOSION_LIFE) {
            game.currentBomb = null;
        }
    }

    private void explode() {
        exploded = true;
        if (!soundPlayed) {
            playExplosionSound();

            soundPlayed = true;  
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
    }

    public void draw(Graphics g) {
        if (scaledImage != null) {
            g.drawImage(scaledImage, x * game.tileSize, y * game.tileSize, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x * game.tileSize, y * game.tileSize, game.tileSize, game.tileSize);
        }
        if (exploded && scaledExplosion != null) {
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
