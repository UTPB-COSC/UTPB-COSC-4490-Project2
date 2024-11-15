package src;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Player implements KeyListener {
    public int x, y; 
    public boolean alive = true;
    private Game game;
    private BufferedImage playerImg;
    private boolean isBombActive = false;

    public Player(int x, int y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;
        


        try {
            playerImg = ImageIO.read(new File("hero_game.png"));
            System.out.println("Player image loaded successfully.");

        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    // keys for movement
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (canMoveTo(x, y - 1)) y--;
                break;
            case KeyEvent.VK_DOWN:
                if (canMoveTo(x, y + 1)) y++;
                break;
            case KeyEvent.VK_LEFT:
                if (canMoveTo(x - 1, y)) x--;
                break;
            case KeyEvent.VK_RIGHT:
                if (canMoveTo(x + 1, y)) x++;
                break;
         }
    }


    private boolean canMoveTo(int newX, int newY) {
        return game.map[newY][newX].type == Tile.Type.EMPTY; 
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void update() {
    }


    public void draw(Graphics g) {
        if (playerImg != null && alive) {
          
            Image scaledImage = playerImg.getScaledInstance(game.tileSize, game.tileSize, Image.SCALE_SMOOTH);
            g.drawImage(scaledImage, x * game.tileSize, y * game.tileSize, null);
        }
    }
}
