import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;

public class ProjectileSpike {
    private double x, y;
    private double vx, vy;
    private double gravity = 900; // same gravity as ball
    private int size = 10;

    public ProjectileSpike(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void update(double deltaTime) {
        vy += gravity * deltaTime;
        x += vx * deltaTime;
        y += vy * deltaTime;
    }

    public void draw(Graphics g) {
        // Draw like a spike (triangle) or a small red circle for simplicity
        g.setColor(Color.RED);
        g.fillOval((int)x - size/2, (int)y - size/2, size, size);
    }

    public boolean intersectsBall(Ball ball) {
        // Simple bounding circle check
        double dx = ball.getX() - x;
        double dy = ball.getY() - y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        return dist < (ball.getRadius() + size/2);
    }

    public boolean isOutOfBounds() {
        // If the projectile goes off-screen, you can remove it
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return (x < 0 || x > screenSize.getWidth() || y > screenSize.getHeight());
    }
}
