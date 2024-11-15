import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;

public class EnemyBoat {
    private int x, y;
    private int dx, dy;
    private BufferedImage enemyBoatImage;

    // Dimensions of the enemy boat, slightly larger than player boat
    private final int WIDTH = 60;  // Adjust size as needed
    private final int HEIGHT = 40; // Adjust size as needed

    private final int patrolX1, patrolY1, patrolX2, patrolY2; // Patrol boundaries
    private final int speed = 2;  // Speed of the enemy boat

    public EnemyBoat(int startX, int startY, int patrolX1, int patrolY1, int patrolX2, int patrolY2) {
        this.x = startX;
        this.y = startY;
        this.patrolX1 = patrolX1;
        this.patrolY1 = patrolY1;
        this.patrolX2 = patrolX2;
        this.patrolY2 = patrolY2;
        this.dx = speed;
        this.dy = 0;
        loadEnemyBoatImage();
    }

    private void loadEnemyBoatImage() {
        try {
            enemyBoatImage = ImageIO.read(new File("src/assets/enemyboat1.png"));
        } catch (IOException e) {
            System.out.println("Error loading enemy boat image.");
            e.printStackTrace();
        }
    }

    public void updatePosition() {
        x += dx;
        y += dy;

        // Reverse direction when reaching patrol area boundaries
        if (x <= patrolX1 || x >= patrolX2 - WIDTH) {
            dx = -dx;
        }
        if (y <= patrolY1 || y >= patrolY2 - HEIGHT) {
            dy = -dy;
        }
    }

    public void draw(Graphics g) {
        if (enemyBoatImage != null) {
            g.drawImage(enemyBoatImage, x, y, WIDTH, HEIGHT, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // Method to check if player boat is in range (optional)
    public boolean isPlayerInRange(Boat playerBoat) {
        int detectionRange = 150; // Set range as needed
        return getBounds().intersects(playerBoat.getBounds());
    }
}
