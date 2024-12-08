import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents a rectangular platform on which the ball can land.
 * Platforms serve as static obstacles or floors within the level.
 */
public class Platform {
    private final int x, y, width, height;

    /**
     * Creates a platform at the specified coordinates with the given width and height.
     * @param x The x-coordinate of the platform (top-left corner).
     * @param y The y-coordinate of the platform (top-left corner).
     * @param width The width of the platform.
     * @param height The height of the platform.
     */
    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHeight() { return height; }
    public int getWidth() { return width; }

    /**
     * Draws the platform as a filled rectangle.
     */
    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect(x, y, width, height);
    }

    /**
     * Returns the bounding box (rectangle) of this platform.
     * Useful for more advanced collision detection if needed.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Checks if the ball is exactly on top of the platform.
     * This method was in your original code. In many cases, you might rely
     * on collision methods in the Ball or the main game loop rather than calling this.
     */
    public boolean isBallOnPlatform(Ball ball) {
        int ballBottom = ball.getY() + ball.getRadius();
        int platformTop = this.y;
        return ballBottom >= platformTop && ballBottom <= platformTop + ball.getRadius() &&
               ball.getX() >= this.x && ball.getX() <= this.x + this.width;
    }
}
