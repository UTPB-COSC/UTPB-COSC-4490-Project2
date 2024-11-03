//Aswin Lohani 



import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;



public class PirateBattleshipGame extends JFrame {

    private GameCanvas gameCanvas;

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

        // Start the game loop
        startGameLoop();
    }

    private void startGameLoop() {
        // Game loop runs indefinitely
        while (true) {
            try {
                Thread.sleep(16);  // Delay to achieve ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Continuously update the game state
            gameCanvas.updateGame();
        }
    }

    public static void main(String[] args) {
        new PirateBattleshipGame();
    }
}
