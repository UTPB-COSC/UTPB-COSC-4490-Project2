
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class DebugOverlay {
    private boolean debugMode = false;
    private long frameCount = 0;
    private long updateCount = 0;
    private long lastUpdateTime = System.nanoTime();
    private double fps = 0;
    private double ups = 0;

    public void toggleDebugMode() {
        debugMode = !debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void update() {
        if (!debugMode) return;

        frameCount++;
        long currentTime = System.nanoTime();
        long elapsed = currentTime - lastUpdateTime;

        if (elapsed >= 1_000_000_000) { // Update every second
            fps = frameCount;
            ups = updateCount;
            frameCount = 0;
            updateCount = 0;
            lastUpdateTime = currentTime;
        }
    }

    public void incrementUpdateCount() {
        if (debugMode) {
            updateCount++;
        }
    }

    public void draw(Graphics g, Boat boat, EnemyBoat enemyBoat, Iterable<Rock> rocks) {
        if (!debugMode) return;

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Draw FPS and UPS counters
        g.drawString("FPS: " + fps, 10, 20);
        g.drawString("UPS: " + ups, 10, 40);
        
        // Draw frame and update counters
        g.drawString("Frames: " + frameCount, 10, 60);
        g.drawString("Updates: " + updateCount, 10, 80);

        // Draw hitboxes for boat and enemy boat
        g.setColor(Color.RED);
        drawHitbox(g, boat.getBounds());
        drawHitbox(g, enemyBoat.getBounds());

        // Draw hitboxes for each rock
        for (Rock rock : rocks) {
            drawHitbox(g, rock.getBounds());
        }
    }

    private void drawHitbox(Graphics g, Rectangle bounds) {
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
