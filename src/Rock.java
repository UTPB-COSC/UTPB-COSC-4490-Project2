

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Rock {
    private int x, y;
    private int width, height;  // Add width and height for different sizes
    private Image rockImage;

    // Constructor that accepts x, y, width, and height
    public Rock(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rockImage = new ImageIcon(imagePath).getImage();  // Load the image
    }

    // Method to draw the rock
    public void draw(Graphics g) {
        g.drawImage(rockImage, x, y, width, height, null);  // Draw the image with custom width and height
    }

    // Method to get the boundaries for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);  // Return bounds based on custom width and height
    }
}
