

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.KeyEvent;

public class GameCanvas extends JPanel {
    private int screenWidth = 1000;
    private int screenHeight = 800;
    private ArrayList<Rock> rocks;
    private Boat boat;
    private boolean gameOver;  // Tracks if the game is over
    private boolean onTitleScreen = true;  // Title screen flag
    private Image seaBackground;
    private EnemyBoat enemyBoat;

    public GameCanvas() {
        rocks = new ArrayList<>();
        generateRocks();  // Static rocks generated once
        boat = new Boat(100, screenHeight / 2);  // Start the boat in the middle
        gameOver = false;  // Game starts with gameOver set to false
        setUpMouseListener();  // Set up mouse listener for clicking "Start Game"
        loadSeaBackground();  // Load the sea background
        enemyBoat = new EnemyBoat(400, 600, 100);  // Example patrol area
    }

    public void generateRocks() {
        // Add small rocks
        rocks.add(new Rock(200, 200, 50, 50, "src/assets/rock1.png"));  // Small rock
        rocks.add(new Rock(500, 150, 60, 60, "src/assets/rock2.png"));  // Small rock
        rocks.add(new Rock(650, 400, 55, 55, "src/assets/rock3.png"));  // Small rock
        
        // Add medium rocks
        rocks.add(new Rock(300, 300, 100, 100, "src/assets/rock1.png"));  // Medium rock
        rocks.add(new Rock(600, 250, 110, 110, "src/assets/rock2.png"));  // Medium rock
        rocks.add(new Rock(700, 400, 90, 90, "src/assets/rock3.png"));  // Medium rock
        
        // Add large rocks
        rocks.add(new Rock(800, 650, 150, 150, "src/assets/rock1.png"));  // Large rock
        rocks.add(new Rock(600, 100, 120, 130, "src/assets/rock2.png"));  // Large rock
        rocks.add(new Rock(750, 750, 140, 140, "src/assets/rock3.png"));  // Large rock
        
        // Add extra rocks for variety
        rocks.add(new Rock(550, 350, 70, 70, "src/assets/rock1.png"));   // Small-medium rock
        rocks.add(new Rock(350, 150, 80, 80, "src/assets/rock2.png"));   // Medium rock
        rocks.add(new Rock(450, 450, 60, 60, "src/assets/rock3.png"));   // Small rock
    }
    

    // Mouse listener for the "Start Game" button on the title screen
    private void setUpMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onTitleScreen) {
                    // Check if the "Start Game" button is clicked
                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    // Button position
                    int buttonX = screenWidth / 2 - 100;
                    int buttonY = screenHeight / 2;
                    int buttonWidth = 200;
                    int buttonHeight = 50;

                    if (mouseX >= buttonX && mouseY >= buttonY && mouseX <= buttonX + buttonWidth && mouseY <= buttonY + buttonHeight) {
                        onTitleScreen = false;  // Transition from title screen to game
                        resetGame();  // Initialize the game when starting
                    }
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (onTitleScreen) {
            // Draw the title screen
            drawTitleScreen(g);
        } else if (gameOver) {
            // Draw game over screen if game over
            drawGameOverScreen(g);
        } else {
            // Draw the sea (map) and game elements if the game is ongoing
            drawGameElements(g);
        }
    }

    private void drawTitleScreen(Graphics g) {
        // Background
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Title text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Pirate Battleship", screenWidth / 2 - 180, screenHeight / 3);

        // Draw "Start Game" button
        g.setColor(Color.BLACK);
        g.fillRect(screenWidth / 2 - 100, screenHeight / 2, 200, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Start Game", screenWidth / 2 - 60, screenHeight / 2 + 35);
    }

    private void drawGameOverScreen(Graphics g) {
        // Draw the sea (map)
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Draw rocks
        for (Rock rock : rocks) {
            rock.draw(g);
        }

        // Draw the boat
        boat.draw(g);

        // Game Over text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("GAME OVER", screenWidth / 2 - 120, screenHeight / 2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press 'R' to Restart", screenWidth / 2 - 100, screenHeight / 2 + 20);
    }

    private void loadSeaBackground() {
        try {
            seaBackground = ImageIO.read(new File("src/assets/sea.gif"));  // Load sea background
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawGameElements(Graphics g) {
        // Draw sea background
        if (seaBackground != null) {
            g.drawImage(seaBackground, 0, 0, screenWidth, screenHeight, null);
        }

        // Draw rocks
        for (Rock rock : rocks) {
            rock.draw(g);
        }

        // Draw the boat
        boat.draw(g);

        // Draw the enemy boat
        enemyBoat.draw(g);  
    }

    public void updateGame() {
        if (!onTitleScreen && !gameOver) {
            boat.updatePosition();  // Update boat position
            enemyBoat.updatePosition();  // Update enemy boat position

            // Check for collisions with rocks
            for (Rock rock : rocks) {
                if (boat.getBounds().intersects(rock.getBounds())) {
                    // Trigger game over
                    gameOver = true;  // Set game over state
                    break;
                }
            }

             // Check if player is in enemy detection range
        if (enemyBoat.isPlayerInRange(boat)) {
            // Implement logic for enemy interaction (e.g., chase or attack)
        }

         // Check for collision between player boat and enemy boat
         if (boat.getBounds().intersects(enemyBoat.getBounds())) {
            gameOver = true;  // End game on collision with enemy boat
        }

        repaint();
        }
    }

    // Method to restart the game
    public void resetGame() {
        boat.resetPosition();  // Reset boat to starting position
        gameOver = false;  // Reset the game over flag
        repaint();  // Repaint the screen to reflect the reset state
    }

    // Method to handle key press
    public void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_R && gameOver) {
            resetGame();  // Restart the game if 'R' is pressed
        } else if (!gameOver && !onTitleScreen) {
            // Let the boat move if the game is not over or on title screen
            boat.setDirection(keyCode);
        }
    }

    // Method to stop the boat's movement when a key is released
    public void handleKeyRelease(int keyCode) {
        if (!gameOver && !onTitleScreen) {
            boat.stopMoving();
        }
    }
}
