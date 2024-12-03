package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class GameCanvas extends JPanel implements Runnable {
    private Game game;
    private BufferedImage lastFrame = null;

    public GameCanvas(Game game) {
        this.game = game;

        // Handle window resizing
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                repaint(); 
            }
        });
    }
    
    public void reset() {
        game.frame.revalidate();
        game.frame.repaint();
    }
    @Override
    public void run() {
        while (true) {
            Graphics2D g2d = (Graphics2D) game.frame.getContentPane().getGraphics();
            BufferedImage frame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = frame.getGraphics();

            if (game.running) {
                //map draw
                for (int y = 0; y < game.mapHeight; y++) {
                    for (int x = 0; x < game.mapWidth; x++) {
                        game.map[y][x].draw(g, x * game.tileSize, y * game.tileSize, game.tileSize);
                    }
                }

                for (Enemy enemy : game.enemies) {
                    if (enemy != null) {
                        enemy.draw(g);
                    }
                }

                game.player.draw(g);

                if (game.currentBomb != null) {
                    game.currentBomb.draw(g);
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

                if (game.timerRunning) {
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 30));
                    String timeLeft = String.format("%02d:%02d",
                            (game.timer - (System.currentTimeMillis() - game.startTime) / 1000) / 60,
                            (game.timer - (System.currentTimeMillis() - game.startTime) / 1000) % 60);
                    g.drawString("Time Left: " + timeLeft,
                            (getWidth() - g.getFontMetrics().stringWidth("Time Left: " + timeLeft)) / 2, 30);
                }
            } else {
                if (lastFrame != null) {
                    g.drawImage(lastFrame, 0, 0, null);
                }

                if (game.gameState == GameState.PAUSED || game.gameState == GameState.GAME_OVER || game.gameState == GameState.GAME_WON) {
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(0, 0, getWidth(), getHeight());

                    g.setColor(Color.BLUE);
                    g.setFont(new Font("Arial", Font.BOLD, 50));
                    if (game.gameState == GameState.PAUSED) {
                        g.drawString("PAUSED", (getWidth() / 2) - 100, getHeight() / 2 - 50);
                        g.setFont(new Font("Arial", Font.PLAIN, 30));
                        g.drawString("Continue Playing(P)", (getWidth() / 2) - 100, getHeight() / 2 + 10);
                        g.drawString("New Game(N)", (getWidth() / 2) - 100, getHeight() / 2 + 50);
                        g.drawString("Exit(E)", (getWidth() / 2) - 100, getHeight() / 2 + 90);

                    } else if (game.gameState == GameState.GAME_OVER) {
                        g.setColor(Color.RED);
                        g.drawString("GAME OVER", (getWidth() / 2) - 120, getHeight() / 2 - 50);
                        g.setFont(new Font("Arial", Font.PLAIN, 30));
                        g.drawString("New Game(N)", (getWidth() / 2) - 100, getHeight() / 2 + 10);
                        g.drawString("Exit(E)", (getWidth() / 2) - 100, getHeight() / 2 + 50);
                    } else if (game.gameState == GameState.GAME_WON) {
                        g.setColor(Color.GREEN);
                        g.setFont(new Font("Arial", Font.BOLD, 30));
                        g.drawString("Congratulations! You Won", (getWidth() / 2) - 120, getHeight() / 2 - 50);
                        g.setFont(new Font("Arial", Font.PLAIN, 30));
                        g.drawString("New Game(N)", (getWidth() / 2) - 100, getHeight() / 2 + 10);
                        g.drawString("Exit(E)", (getWidth() / 2) - 100, getHeight() / 2 + 50);
                    }
                }
            }
            g2d.drawImage(frame, 0, 0, null);
            lastFrame = frame;

            try {
                Thread.sleep(1000 / (int) game.rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
