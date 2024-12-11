import java.awt.Color;
import java.awt.Graphics;

public class Particle {
    private double x, y;
    private double vx, vy;
    private double lifespan; // in seconds
    private double age = 0;
    private Color color;
    private double gravity = 900;

    public Particle(double x, double y, double vx, double vy, double lifespan, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lifespan = lifespan;
        this.color = color;
    }

    public boolean update(double deltaTime) {
        // Apply gravity
        vy += gravity * deltaTime;
        x += vx * deltaTime;
        y += vy * deltaTime;
        age += deltaTime;
        return age < lifespan; // Return false if particle is "dead"
    }

    public void draw(Graphics g) {
        float alpha = 1.0f - (float)(age / lifespan); // fade out over time
        Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha*255));
        g.setColor(c);
        g.fillOval((int)x, (int)y, 5, 5);
    }
}
