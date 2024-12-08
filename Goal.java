import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Represents a goal area. The ball must reach this area to win the level.
 * Rendered as a green oval, but currently collision checks are done via bounding box.
 */
public class Goal {
    private final int x, y, width, height;

    /**
     * Creates a goal at the specified position with given width and height.
     * @param x The x-coordinate of the goal.
     * @param y The y-coordinate of the goal.
     * @param width The width of the goal area.
     * @param height The height of the goal area.
     */
    public Goal(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Draws the goal as a green oval.
     */
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval(x, y, width, height);
    }

    /**
     * Checks if the ball's center is within the goal's bounding rectangle.
     * 
     * If you want a perfect elliptical collision check (since it's drawn as an oval),
     * you could consider using the equation of an ellipse. For now, this uses a simple
     * bounding box check, which might mean the ball can "score" even when touching corners
     * outside the oval.
     */
    public boolean isBallInGoal(Ball ball) {
        int ballCenterX = ball.getX();
        int ballCenterY = ball.getY();
        return (ballCenterX > x && ballCenterX < x + width) &&
               (ballCenterY > y && ballCenterY < y + height);
    }

    /**
     * Returns the bounding box of the goal.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
