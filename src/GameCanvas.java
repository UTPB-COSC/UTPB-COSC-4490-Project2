package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GameCanvas extends JPanel implements Runnable {
    private Game game;

    public GameCanvas(Game game) {
        this.game = game;

        // Handle window resizing
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                repaint(); 
            }
        });
    }
   

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //map draw
        for (int y = 0; y < game.mapHeight; y++) {
            for (int x = 0; x < game.mapWidth; x++) {
                game.map[y][x].draw(g, x * game.tileSize, y * game.tileSize, game.tileSize);
            }
        }
        if (game.currentBomb != null) {
            game.currentBomb.draw(g);

        }
        
    
        game.player.draw(g);
        
        for (Enemy enemy : game.enemies) {
            if (enemy != null) {
                enemy.draw(g);
            }
        }
        

        if (game.timerRunning) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String timeLeft = String.format("%02d:%02d",
                (game.timer - (System.currentTimeMillis() - game.startTime) / 1000) / 60,
                (game.timer - (System.currentTimeMillis() - game.startTime) / 1000) % 60);
            g.drawString("Time Left: " + timeLeft,
                (getWidth() - g.getFontMetrics().stringWidth("Time Left: " + timeLeft)) / 2, 30);
        }

        if (game.gameState == GameState.PAUSED || game.gameOver|| game.gameWon) {
            game.drawMenu(g);
        }
        
        if (game.debugMode) {
            
            g.setColor(Color.WHITE);
            g.drawString("FPS: " + game.getFPS(), 10, 20);
            g.drawString("UPS: " + game.getUPS(), 10, 40);
            g.drawString("Frames: " + game.getFrameCounter(), 10, 60);
            g.drawString("Updates: " + game.getUpdateCounter(), 10, 80);

            
            g.setColor(Color.RED);
            
            int playerX = game.player.x * game.tileSize;
            int playerY = game.player.y * game.tileSize;
            g.drawRect(playerX, playerY, game.tileSize, game.tileSize);
            
            for (Enemy enemy : game.enemies) {
                int enemyX = enemy.x * game.tileSize;
                int enemyY = enemy.y * game.tileSize;
                g.drawRect(enemyX, enemyY, game.tileSize, game.tileSize);
            }
            if (game.currentBomb != null) {
                int bombX = game.currentBomb.x;
                int bombY = game.currentBomb.y;
            
                int[][] surroundingTiles = {
                    {bombX, bombY},         
                    {bombX, bombY - 1},
                    {bombX, bombY + 1},
                    {bombX - 1, bombY},     
                    {bombX + 1, bombY}      
                };
            
                g.setColor(Color.RED);
                for (int[] tile : surroundingTiles) {
                    int tileX = tile[0];
                    int tileY = tile[1];
                    if (tileX >= 0 && tileX < game.mapWidth && tileY >= 0 && tileY < game.mapHeight) {
                        Tile currentTile = game.map[tileY][tileX];
            
                        if (currentTile.type == Tile.Type.EMPTY || currentTile.type == Tile.Type.BLOCK) {
                            g.drawRect(tileX * game.tileSize, tileY * game.tileSize, game.tileSize, game.tileSize);
                        }
                    }
                }
            }
            
        }
    }
    
    public void reset() {
        game.frame.revalidate();
        game.frame.repaint();
    }
    @Override
    public void run() {
        
        while (game.running) {
            repaint();
            try {
                Thread.sleep(1000 / (int) game.rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
