import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PirateBattleshipGame extends JFrame implements Runnable {

    private GameCanvas gameCanvas;
    private volatile boolean running = true;  // Allows for a graceful exit from the game loop

    public PirateBattleshipGame() {
        setTitle("Pirate Battleship Game");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameCanvas = new GameCanvas();
        add(gameCanvas);

        // Add key listener for movement and restarting
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gameCanvas.handleKeyPress(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                gameCanvas.handleKeyRelease(e.getKeyCode());
            }
        });

        setVisible(true);

        // Start the game loop in a new thread
        new Thread(this).start();
    }

    @Override
    public void run() {
        final int targetFPS = 60;
        final long optimalTime = 1_000_000_000 / targetFPS; // Optimal time per frame in nanoseconds

        while (running) {
            long startTime = System.nanoTime();

            // Update game state and repaint the canvas
            gameCanvas.updateGame();
            gameCanvas.repaint();

            // Calculate elapsed time and sleep to maintain target frame rate
            long elapsedTime = System.nanoTime() - startTime;
            long sleepTime = (optimalTime - elapsedTime) / 1_000_000; // Convert to milliseconds

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new PirateBattleshipGame();
    }
}
