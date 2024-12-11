import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.Color;

public class ParticleManager {
    private List<Particle> particles = new ArrayList<>();

    public void spawnEffect(double x, double y) {
        // Spawn multiple particles at once
        for (int i = 0; i < 20; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double speed = 200 + Math.random() * 200;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 400; // initial upward burst
            double lifespan = 0.5 + Math.random(); 
            Color color = Color.YELLOW; // or random colors
            particles.add(new Particle(x, y, vx, vy, lifespan, color));
        }
    }

    public void update(double deltaTime) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            if (!p.update(deltaTime)) {
                it.remove();
            }
        }
    }

    public void draw(Graphics g) {
        for (Particle p : particles) {
            p.draw(g);
        }
    }
}
