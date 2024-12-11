import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameWindow extends JPanel implements KeyListener {
    private Ball ball;
    private List<Spike> spikes;
    private List<Platform> platforms;
    private Goal goal;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private LevelManager levelManager;
    public static boolean isLeftPressed = false;
    public static boolean isRightPressed = false;
    private long levelStartTime;
    private Timer gameTimer; // Timer for the game loop
    private boolean isPaused = false;
    private Image backgroundImage;
    private boolean playedBounceSound = false;
    private boolean debugMode = false;
    private long frameCount = 0;
    private long updateCount = 0;
    private long lastUpdateTime = System.nanoTime(); // To track delta time
    private Villain villain;
    private List<ProjectileSpike> projectiles = new ArrayList<>();
    private ParticleManager particleManager = new ParticleManager();
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            gameTimer.stop(); // Stop the timer if paused
        } else {
            lastUpdateTime = System.nanoTime(); // Reset the timer so deltaTime doesn't accumulate
            gameTimer.start(); // Restart the timer when resumed
        }
        repaint();
    }

    private void toggleDebugMode() {
        debugMode = !debugMode;
    }

    public void playBounceSound() {
        if (!playedBounceSound) {
            playSound("soundEffects/bouncy.wav");
            playedBounceSound = true;
        }
    }

    public void resetBounceSound() {
        playedBounceSound = false;
    }

    public void playWinSound() {
        playSound("soundEffects/levelSucceeded.wav");
    }

    public void playLoseSound() {
        playSound("soundEffects/lost.wav");
    }

    private void playSound(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameWindow() {
        JFrame frame = new JFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.add(this);
        frame.addKeyListener(this);

        levelManager = new LevelManager();
        initializeGameObjects();

        frame.setVisible(true);
        // backgroundImage = new ImageIcon("src/bgm.jpg").getImage();

        // Setup a game loop using a Timer (16ms per frame = ~60 FPS)
        gameTimer = new Timer(5, e -> gameLoop());


        gameTimer.start();
    }

    private void initializeGameObjects() {
        // screenSize is now a class-level field, so no need to redefine it here.
    
        villain = new Villain(200, screenSize.height - 300); // position the villain somewhere
    
        int windowWidth = (int) screenSize.getWidth();
        int windowHeight = (int) screenSize.getHeight();
    
        ball = new Ball(windowWidth / 2, windowHeight - 100, 30);
    
        LevelManager.LevelData levelData = levelManager.loadLevel();
        if (levelData != null) {
            platforms = levelData.platforms;
            spikes = levelData.spikes;
            goal = levelData.goal;
        }
        levelStartTime = System.currentTimeMillis();
    }
    

    private void gameLoop() {
        if (isPaused) {
            lastUpdateTime = System.nanoTime();
            return; 
        }
    
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastUpdateTime) / 1e9;
        lastUpdateTime = currentTime;
    
        if (!gameOver && !gameWon) {
            ball.updatePosition(deltaTime);
    
            // Update villain
            villain.update(deltaTime, ball, projectiles);
    
            // Update projectiles
            Iterator<ProjectileSpike> it = projectiles.iterator();
            while (it.hasNext()) {
                ProjectileSpike p = it.next();
                p.update(deltaTime);
    
                if (p.intersectsBall(ball)) {
                    gameOver = true;
                    it.remove();
                    particleManager.spawnEffect(ball.getX(), ball.getY()); 
                    playLoseSound(); // Consider playing the lose sound here
                } else if (p.isOutOfBounds()) {
                    it.remove();
                }
            }
    
            // Handle collisions with spikes
            if (spikes != null) {
                for (Spike spike : spikes) {
                    if (spike.intersects(ball)) {
                        gameOver = true;
                        playLoseSound();
                        return;
                    }
                }
            }
    
            // Handle collisions with platforms
            if (platforms != null) {
                for (Platform platform : platforms) {
                    if (ball.intersectsPlatform(platform)) {
                        ball.handlePlatformCollision(platform);
                    }
                }
            }
    
            // Check for goal
            if (goal != null && goal.isBallInGoal(ball)) {
                gameWon = true;
                playWinSound();
            }
        }
    
        // Update particle effects
        particleManager.update(deltaTime);
    
        frameCount++;
        repaint(); 
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Render background
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Pause overlay
        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150)); // Transparent dark overlay
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Verdana", Font.BOLD, 42));
            g.drawString("Game Paused", getWidth() / 2 - 120, getHeight() / 2 - 100);
            g.setFont(new Font("Verdana", Font.PLAIN, 36));
            g.drawString("Press R to Reset", getWidth() / 2 - 100, getHeight() / 2 - 40);
            g.drawString("Press Q to Quit", getWidth() / 2 - 100, getHeight() / 2);
            g.drawString("Press P to Resume", getWidth() / 2 - 100, getHeight() / 2 + 40);
            return;
        }

        // Draw gameplay elements
        ball.draw(g);
        if (spikes != null) {
            for (Spike spike : spikes) {
                spike.draw(g);
            }
        }

        if (platforms != null) {
            for (Platform platform : platforms) {
                platform.draw(g);
            }
        }

        if (goal != null) {
            goal.draw(g);
        }
        villain.draw(g);
        for (ProjectileSpike p : projectiles) {
        p.draw(g);
        }
particleManager.draw(g);

        // Debug info
        if (debugMode) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Verdana", Font.PLAIN, 16));
            g.drawString("FPS: " + frameCount, 10, 20);
            g.drawString("UPS: " + updateCount, 10, 40);
            g.drawString("Ball Position: (" + ball.getX() + ", " + ball.getY() + ")", 10, 60);
            g.drawString("Velocity: (" + ball.getVelocityX() + ", " + ball.getVelocityY() + ")", 10, 80);

            // Draw hitboxes
            if (platforms != null) {
                for (Platform platform : platforms) {
                    g.setColor(Color.BLACK);
                    g.drawRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
                }
            }
            if (spikes != null) {
                for (Spike spike : spikes) {
                    g.setColor(Color.BLACK);
                    g.drawRect(spike.getX(), spike.getY() - spike.getSize(), spike.getSize(), spike.getSize());

                }
            }
        }

        // Game over screen
        if (gameOver) {
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Verdana", Font.BOLD, 52));
            g.drawString("AHHH! SPIKES!!", getWidth() / 2 - 152, getHeight() / 2 + 2);
            g.setColor(Color.RED);
            g.drawString("AHHH! SPIKES!!", getWidth() / 2 - 150, getHeight() / 2);
            g.setFont(new Font("Verdana", Font.BOLD, 30));
            g.setColor(Color.BLACK);
            g.drawString("Press ENTER to play again", getWidth() / 2 - 100, getHeight() / 2 + 80);
            return; // Exit after rendering game over screen
        }

        // Game won screen
        if (gameWon) {
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Verdana", Font.BOLD, 56));
            g.drawString("Level Complete!", getWidth() / 2 - 142, getHeight() / 2 + 2);
            g.setColor(new Color(0, 180, 0)); // Vibrant green
            g.drawString("Level Complete!", getWidth() / 2 - 140, getHeight() / 2);
            return; // Exit after rendering game won screen
        }

        // Display level information briefly
        if (System.currentTimeMillis() - levelStartTime < 2000) {
            g.setColor(Color.black);
            g.setFont(new Font("Verdana", Font.BOLD, 36));
            g.drawString("Level " + levelManager.currentLevel, getWidth() / 2 - 50, 100);
        }
    }

    private void resetGame() {
        if (gameWon) {
            levelManager.advanceLevel();
        }
        gameOver = false;
        gameWon = false;
        initializeGameObjects();
    }

    public static int getFloorHeight() {
        return Toolkit.getDefaultToolkit().getScreenSize().height;
    }

    public static int getWallWidth() {
        return Toolkit.getDefaultToolkit().getScreenSize().width;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Pause functionality
        if (e.getKeyCode() == KeyEvent.VK_P) {
            togglePause();
            return; // Prevent further processing when toggling pause
        }

        // Handle menu actions if paused
        if (isPaused) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                resetGame();
                togglePause(); // Exit pause menu after reset
            }
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                System.exit(0); // Quit the game
            }
            return; // Ignore all other inputs while paused
        }

        // Debug mode toggle
        if (e.getKeyCode() == KeyEvent.VK_D) {
            toggleDebugMode();
            return;
        }

        // Handle normal gameplay inputs
        if (!gameOver && !gameWon) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                ball.setVelocityX(-250);
                isLeftPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                ball.setVelocityX(250);
                isRightPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE && ball.isOnGround()) {
                ball.setVelocityY(-700);
                ball.onGround = false;
            }
        }

        // Handle restart if game over or won
        if ((gameOver || gameWon) && e.getKeyCode() == KeyEvent.VK_ENTER) {
            resetGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isPaused) {
            return; // Ignore key releases if the game is paused
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            isLeftPressed = false;
            if (!isRightPressed) {
                ball.setVelocityX(0); // You could remove this to rely entirely on friction.
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isRightPressed = false;
            if (!isLeftPressed) {
                ball.setVelocityX(0); // You could remove this to rely entirely on friction.
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new GameWindow();
    }
}
