import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.awt.Color;  // Import Color for fallback rectangle


public class EnemyBoat {
    private int x, y;
    private int patrolStartX, patrolEndX;  // Horizontal boundaries for patrolling
    private int speed = 2;
    private int width = 50;  // Set dimensions to match image size or adjust as needed
    private int height = 30;
    private boolean movingRight = true;
    private Image enemyBoatImage;

    public EnemyBoat(int startX, int startY, int patrolEndX) {
        this.x = startX;
        this.y = startY;
        this.patrolStartX = startX;
        this.patrolEndX = patrolEndX;
        
        // Load the enemy boat image
        loadEnemyBoatImage();
    }

    private void loadEnemyBoatImage() {
        try {
            enemyBoatImage = ImageIO.read(new File("src/assets/enemyboat1.png"));
            width = enemyBoatImage.getWidth(null);  // Set width to match image size
            height = enemyBoatImage.getHeight(null);  // Set height to match image size
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePosition() {
        // Move within patrol area
        if (movingRight) {
            x += speed;
            if (x >= patrolEndX) {
                movingRight = false;
            }
        } else {
            x -= speed;
            if (x <= patrolStartX) {
                movingRight = true;
            }
        }
    }

    public void draw(Graphics g) {
        if (enemyBoatImage != null) {
            g.drawImage(enemyBoatImage, x, y, 55, 35, null);  // Draw image with a specific width and height
        } else {
            // Fallback to a red rectangle if image fails to load
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isPlayerInRange(Boat playerBoat) {
        int detectionRange = 100;  // Example range
        Rectangle detectionArea = new Rectangle(x - detectionRange, y - detectionRange, width + 2 * detectionRange, height + 2 * detectionRange);
        return detectionArea.intersects(playerBoat.getBounds());
    }
}