import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Ball {
    private double x, y;         // Current position
    private double oldX, oldY;   // Previous position (before this frame's movement)
    private int radius;
    private double velocityX, velocityY; 
    private final double gravity = 900;           // Gravity (pixels/s^2)
    private final double horizontalDeceleration = 600; // Horizontal deceleration (pixels/s^2)
    private final int stopThreshold = 5;          // Threshold below which horizontal velocity stops
    public boolean onGround = false;              // True if ball is on a platform this frame
    private Image ballImage;

    public Ball(int startX, int startY, int radius) {
        this.x = startX;
        this.y = startY;
        this.radius = radius;
        this.velocityX = 0;
        this.velocityY = 0;
        loadImage();
    }

    private void loadImage() {
        try {
            ballImage = ImageIO.read(new File("balley.png")); // Adjust to the actual path
        } catch (IOException e) {
            System.err.println("Failed to load ball image.");
            e.printStackTrace();
        }
    }

    public int getX() { return (int) x; }
    public int getY() { return (int) y; }
    public int getRadius() { return radius; }
    public int getVelocityX() { return (int) velocityX; }
    public int getVelocityY() { return (int) velocityY; }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void updatePosition(double deltaTime) {
        // Store old positions for collision direction checks
        oldX = x;
        oldY = y;

        // Apply gravity and move vertically first
        velocityY += gravity * deltaTime;
        y += velocityY * deltaTime;

        // Apply horizontal friction as a deceleration if no keys are pressed
        if (!GameWindow.isLeftPressed && !GameWindow.isRightPressed) {
            double frictionForce = horizontalDeceleration * deltaTime;
            if (Math.abs(velocityX) < frictionForce) {
                velocityX = 0;
            } else {
                velocityX -= Math.signum(velocityX) * frictionForce;
            }
        }

        // Move horizontally after vertical movement is done
        x += velocityX * deltaTime;

        // We don't set onGround = false here yet, because we might check collisions right after this
        // in the main game loop. After collision handling, if no top collision occurred, onGround will remain false.
        onGround = false; 
    }

    public void setVelocityX(int velocityX) { 
        this.velocityX = velocityX; 
    }

    public void setVelocityY(int velocityY) { 
        this.velocityY = velocityY; 
    }

    // We have removed the bounce() method that handled window boundaries.
    // If you need boundary collisions, treat the window edges like static platforms.

    public boolean intersectsPlatform(Platform platform) {
        // Simple bounding box check
        return (x + radius > platform.getX() &&
                x - radius < platform.getX() + platform.getWidth() &&
                y + radius > platform.getY() &&
                y - radius < platform.getY() + platform.getHeight());
    }

    public void handlePlatformCollision(Platform platform) {
        // Use old and new positions to determine the collision side.
        // Check vertical collision first:
        boolean verticalCollision = false;

        // Top collision: If previously the ball was above the platform (oldY + radius <= platformTop)
        // and now it has moved down into it (y + radius >= platformTop)
        if (oldY + radius <= platform.getY() && y + radius > platform.getY()) {
            // Correct position
            y = platform.getY() - radius;
            // Stop vertical movement (or apply small bounce if desired)
            velocityY = 0;
            onGround = true;
            verticalCollision = true;
        }
        // Bottom collision: If previously ball was below the platform 
        // (oldY - radius >= platformBottom) and now y - radius < platformBottom
        else if (oldY - radius >= platform.getY() + platform.getHeight() && (y - radius < platform.getY() + platform.getHeight())) {
            y = platform.getY() + platform.getHeight() + radius;
            velocityY = 0; // Stop upward movement
            // No onGround here because hitting bottom means you're under the platform
            verticalCollision = true;
        }

        // If no vertical collision was detected, check horizontal collision:
        // Only makes sense if the ball overlaps and didn't collide vertically.
        if (!verticalCollision) {
            // Left side collision: ball moving from left to right
            // oldX + radius <= platform.getX() and now x + radius > platform.getX()
            if (oldX + radius <= platform.getX() && x + radius > platform.getX()) {
                x = platform.getX() - radius;
                velocityX = 0; 
            }
            // Right side collision: ball moving from right to left
            // oldX - radius >= platformRight and now x - radius < platformRight
            else if (oldX - radius >= platform.getX() + platform.getWidth() && (x - radius < platform.getX() + platform.getWidth())) {
                x = platform.getX() + platform.getWidth() + radius;
                velocityX = 0;
            }
        }

        // If no vertical collision occurred, onGround remains what it was before:
        // Since we reset onGround to false in updatePosition, it remains false unless top collision sets it true.
    }

    public void draw(Graphics g) {
        if (ballImage != null) {
            g.drawImage(ballImage, (int) x - radius, (int) y - radius, 2 * radius, 2 * radius, null);
        } else {
            // Fallback: Draw a simple circle if the image is not loaded
            g.setColor(java.awt.Color.RED);
            g.fillOval((int) x - radius, (int) y - radius, 2 * radius, 2 * radius);
        }
    }
}
