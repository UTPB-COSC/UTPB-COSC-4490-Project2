

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Boat {
    private int x, y;
    private int dx, dy;
    private BufferedImage boatImage;
    
    private final int startX;
    private final int startY;
    
    // Add the dimensions for the boat image
    private final int WIDTH = 50;  // Adjust based on actual image size
    private final int HEIGHT = 30; // Adjust based on actual image size
    
    private final int speed = 2; // Controls the speed of the boat
    private AudioPlayer moveSound;

    public Boat(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        resetPosition();
        loadBoatImage();
        try {
            moveSound = new AudioPlayer("src/assets/boatmovesound.wav"); // Load the sound
        } catch (Exception e) {
            System.out.println("Audio file not loaded. Ensure the path is correct.");
            e.printStackTrace();
            moveSound = null; // Default to null if the audio fails to load
        }
        resetPosition();
    
    }

    private void loadBoatImage() {
        try {
            // Load the boat image from assets folder
            boatImage = ImageIO.read(new File("src/assets/boat.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetPosition() {
        x = startX;
        y = startY;
        dx = 0;
        dy = 0;
        if (moveSound != null) {
            moveSound.stop();
        }
    }

    public void updatePosition() {
        x += dx * speed;
        y += dy * speed;

        if (dx != 0 || dy != 0) {
            if (moveSound != null) {
                moveSound.playLoop();
            }
        } else {
            if (moveSound != null) {
                moveSound.stop();
            }
        }
    }

    public void draw(Graphics g) {
        if (boatImage != null) {
            g.drawImage(boatImage, x, y, WIDTH, HEIGHT, null);
        }
    }

    public void setDirection(int key) {
        switch (key) {
            case KeyEvent.VK_LEFT:
                dx = -1;
                break;
            case KeyEvent.VK_UP:
                dy = -1;
                break;
            case KeyEvent.VK_RIGHT:
                dx = 1;
                break;
            case KeyEvent.VK_DOWN:
                dy = 1;
                break;
        }
    }

    public void stopMoving() {
        dx = 0;
        dy = 0;
        if (moveSound != null) {
            moveSound.stop();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
