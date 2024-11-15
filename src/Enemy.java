package src;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Enemy {
    public int x, y; 
    public boolean alive = true;
    private Game game;
    private BufferedImage enemyImg;  
    private Random random = new Random();
    private long lastMoveTime = 0;
    private final long moveCooldown = 1000;

    public Enemy(int x, int y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;

        try {
            enemyImg = ImageIO.read(new File("enemy_game.png"));
        } catch (IOException e) {
            e.printStackTrace();
            enemyImg = null;
        }
        lastMoveTime = System.currentTimeMillis();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastMoveTime >= moveCooldown) {
            int playerX = game.player.x;
            int playerY = game.player.y;
            int distanceToPlayer = Math.abs(playerX - x) + Math.abs(playerY - y);

            
            if (distanceToPlayer <= 5) {
                if (playerX < x && canMoveTo(x - 1, y)) {
                    x--;  
                } else if (playerX > x && canMoveTo(x + 1, y)) {
                    x++;  
                } else if (playerY < y && canMoveTo(x, y - 1)) {
                    y--;  
                } else if (playerY > y && canMoveTo(x, y + 1)) {
                    y++;  
                }
            } else {
                int direction = random.nextInt(4);
                switch (direction) {
                    case 0:  // Up
                        if (canMoveTo(x, y - 1)) y--;
                        break;
                    case 1:  // Down
                        if (canMoveTo(x, y + 1)) y++;
                        break;
                    case 2:  // Left
                        if (canMoveTo(x - 1, y)) x--;
                        break;
                    case 3:  // Right
                        if (canMoveTo(x + 1, y)) x++;
                        break;
                }
            }

            lastMoveTime = currentTime; 
        }
    }

    private boolean canMoveTo(int newX, int newY) {
        if (newX >= 0 && newX < game.mapWidth && newY >= 0 && newY < game.mapHeight) {
            return game.map[newY][newX].type == Tile.Type.EMPTY;
        }
        return false;
    }

    public void draw(Graphics g) {
        if (enemyImg != null && alive) {
            g.drawImage(enemyImg, x * game.tileSize, y * game.tileSize, game.tileSize, game.tileSize, null);
        }
    }
}
