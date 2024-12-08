import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents a spike hazard. The spike is rendered as a triangle and damages the ball on contact.
 */
public class Spike {
    private final int x, y, size;

    /**
     * Creates a spike at the specified coordinates.
     * The "spike" is drawn as a triangle pointing upwards.
     * @param x The left x-coordinate of the spike's bounding box.
     * @param y The bottom y-coordinate of the spike (triangle base is on this line).
     * @param size The width and height of the triangular spike.
     */
    public Spike(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * Draws the spike as a filled red triangle.
     */
    public void draw(Graphics g) {
        int[] xPoints = { x, x + size / 2, x + size };
        int[] yPoints = { y, y - size, y };
        g.setColor(Color.RED);
        g.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Checks if the ball intersects the spike using a simple bounding box approximation.
     * For more accurate collision (since it's a triangle), you could implement
     * a more complex point-in-triangle test.
     */
    public boolean intersects(Ball ball) {
        int ballX = ball.getX();
        int ballY = ball.getY();
        int ballRadius = ball.getRadius();
        // Bounding box check:
        return (ballX + ballRadius > x && ballX - ballRadius < x + size &&
                ballY + ballRadius > y - size && ballY - ballRadius < y);
    }

    /**
     * Returns the bounding box of the spike (its surrounding rectangle).
     * Even though the spike is a triangle, the bounding box can help with simple collision checks.
     */
    public Rectangle getBounds() {
        // The bounding box covers the area of the triangle.
        return new Rectangle(x, y - size, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
}
