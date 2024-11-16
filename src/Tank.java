package src;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class Tank
{
    Game game;
    Toolkit tk;

    public int yPos;
    public int xPos;

    public double yVel = 0.0;
    public double yAcc = 0.1;
    public double xVel = 0.0;
    public double xAcc = 0.1;
    public double maxVel = 5.0;

    public double targetRotation = 0; // Target angle based on velocity
    public double currentRotation = 0; // Current rotation angle
    public double rotationSpeed = 0.1; // Per Frame rotation rate

    public boolean movingNorth = false;
    public boolean movingSouth = false;
    public boolean movingWest = false;
    public boolean movingEast = false;

    public int width;
    public int height;
    public int diagonal;

    private BufferedImage tankBodyImage;
    private BufferedImage tankTurretImage;
    /*                      Animation Framework left in place for future use.
    private int animframe = 0;
    private int animRate = 4;
    private int frameCount = 0;
    */

    public Tank(Game g, Toolkit tk) throws IOException {
        game = g;
        this.tk = tk;

        // Load the original tank body image
        BufferedImage originalTankBody = ImageIO.read(new File("TankBody1.2.png"));
        int originalWidth = originalTankBody.getWidth();
        int originalHeight = originalTankBody.getHeight();

        // Calculate diagonal for square side length
        diagonal = (int) Math.ceil(Math.sqrt(originalWidth * originalWidth + originalHeight * originalHeight));

        // Create square BufferedImage to contain the tank with rotation
        tankBodyImage = new BufferedImage(diagonal, diagonal, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tankBodyImage.createGraphics();

        // Center the original image in the square image
        int xOffset = (diagonal - originalWidth) / 2;
        int yOffset = (diagonal - originalHeight) / 2;
        g2d.drawImage(originalTankBody, xOffset, yOffset, null);
        g2d.dispose();

        // Set width and height to the diagonal length to maintain a square for rotation
        width = diagonal;
        height = diagonal;

        //Reintro anim framework here.

        tankTurretImage = ImageIO.read(new File("TankTurret1.3.png"));

        reset();
    }

    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    public void drawTank(Graphics g)
    {
        // Draw tank body
        // Calculate the angle in radians based on the velocity vector
        if (xVel != 0 || yVel != 0) {
            targetRotation = Math.atan2(yVel, xVel);
        }

        // Calculate the shortest rotation direction
        double angleDifference = normalizeAngle(targetRotation - currentRotation);
        // Normalize both angles to avoid unnecessary long rotation
        currentRotation = normalizeAngle(currentRotation);
        targetRotation = normalizeAngle(targetRotation);
            
        // Smoothly rotate towards the target rotation using the shortest path; + clockwise, - counterclockwise
        if (Math.abs(angleDifference) > rotationSpeed) {
            if (angleDifference > 0) {
                currentRotation += rotationSpeed;
            } else {
                currentRotation -= rotationSpeed;
            }
        } else {
            // If the angle difference is small enough, snap to the target rotation
            currentRotation = targetRotation;
        }
            
        // Apply rotation to image using AffineTransform
        AffineTransform at = new AffineTransform();
        at.rotate(currentRotation, width / 2, height / 2);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage tankBody = ato.filter(tankBodyImage, null);
        g.drawImage(tankBody, xPos, yPos, null);

        //Reintro anim framework here.

        // Draw tank turret
        // Calculate the angle between the tank's position and the mouse cursor
        double mouseAngle = Math.atan2(game.mouseY - (yPos + height / 2), game.mouseX - (xPos + width / 2));

        // Apply rotation using AffineTransform
        AffineTransform atMouse = new AffineTransform();
        atMouse.rotate(mouseAngle, width / 2, height / 2);
        AffineTransformOp atoMouse = new AffineTransformOp(atMouse, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage tankTurret = atoMouse.filter(tankTurretImage, null);
        g.drawImage(tankTurret, xPos, yPos, null);

        if (game.debug)
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);

            // Get the rotated corners of the player image
            Point2D[] corners = getRotatedCorners(xPos, yPos, currentRotation);

            // Draw lines between each consecutive corner to form the bounding box
            for (int i = 0; i < corners.length; i++) {
                Point2D start = corners[i];
                Point2D end = corners[(i + 1) % corners.length]; // Ensures the last corner connects to the first
                g2d.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
            }
        }
    }

    public void fire()
    {
        //implement firing of gun
    }

    public boolean collide(Bomber bomber)
    {
        Point2D[] playerCorners = getRotatedCorners(xPos, yPos, currentRotation);
        
        // Define the bomber’s core collision bounds
        int bomberCoreX = bomber.xPos + bomber.width / 2;

        // Check if any of the player’s rotated corners overlap with the bomber’s core
        for (Point2D corner : playerCorners) {
            if (corner.getX() >= bomberCoreX && 
                corner.getX() <= bomber.xPos + bomber.width && 
                corner.getY() >= bomber.yPos && 
                corner.getY() <= bomber.yPos + bomber.width) {
                collide();
                return true;
            }
        }

        
        //Poorly implement screen bounding for the player within our bomber logic because it works
        
        int screenWidth = tk.getScreenSize().width;
        int screenHeight = tk.getScreenSize().height;
        
        // Variables to track required adjustments
        double xAdjustment = 0;
        double yAdjustment = 0;
        
        // Assuming getRotatedCorners() gives us an array of Points representing the player's four corners
        // Check each corner's position relative to screen boundaries
        for (Point2D corner : playerCorners) {
            if (corner.getX() < 0) {
                xVel = 0;
                xAdjustment = Math.max(xAdjustment, -corner.getX()); // Move right
            } else if (corner.getX() > screenWidth) {
                xVel = 0;
                xAdjustment = Math.min(xAdjustment, screenWidth - corner.getX()); // Move left
            }
        
            if (corner.getY() < 0) {
                yVel = 0;
                yAdjustment = Math.max(yAdjustment, -corner.getY()); // Move down
            } else if (corner.getY() > screenHeight) {
                yVel = 0;
                yAdjustment = Math.min(yAdjustment, screenHeight - corner.getY()); // Move up
            }
        }
        
        // Apply adjustments to keep the player within bounds
        xPos += xAdjustment;
        yPos += yAdjustment;

        return false;
    }

    private void collide()
    {
        new Thread(() ->
        {
            try
            {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File("collide.wav").getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(20f * (float) Math.log10(game.volume));
                clip.start();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }).start();
    }

    public Point2D[] getRotatedCorners(int xPos, int yPos, double rotation) {
        Point2D[] corners = new Point2D[4];

        // Calculate the center of the tank (rotation point)
        double centerX = xPos + width / 2.0;
        double centerY = yPos + height / 2.0;

        // Define the dimensions of the actual player image
        double actualWidth = 102;
        double actualHeight = 61;

        // Define the four corners of the smaller rectangle (actual image) relative to the center
        double[][] cornerOffsets = {
            {-actualWidth / 2.0, -actualHeight / 2.0}, // Top-left
            { actualWidth / 2.0, -actualHeight / 2.0}, // Top-right
            { actualWidth / 2.0,  actualHeight / 2.0}, // Bottom-right
            {-actualWidth / 2.0,  actualHeight / 2.0}  // Bottom-left
        };

        // Rotate each corner around the center by the specified rotation angle
        for (int i = 0; i < 4; i++) {
            double offsetX = cornerOffsets[i][0];
            double offsetY = cornerOffsets[i][1];

            // Calculate rotated coordinates for each corner
            double rotatedX = offsetX * Math.cos(rotation) - offsetY * Math.sin(rotation);
            double rotatedY = offsetX * Math.sin(rotation) + offsetY * Math.cos(rotation);

            // Translate the rotated corner back to world space
            corners[i] = new Point((int) (centerX + rotatedX), (int) (centerY + rotatedY));
        }

        return corners;
    }

    // Movement flags per direction
    public void moveNorth() {
        movingNorth = true;
    }
    
    public void moveSouth() {
        movingSouth = true;
    }
    
    public void moveWest() {
        movingWest = true;
    }
    
    public void moveEast() {
        movingEast = true;
    }
    
    public void stopNorth() {
        movingNorth = false;
    }
    
    public void stopSouth() {
        movingSouth = false;
    }
    
    public void stopWest() {
        movingWest = false;
    }
    
    public void stopEast() {
        movingEast = false;
    }

    public void reset()
    {
        xPos = tk.getScreenSize().width / 2;
        yPos = tk.getScreenSize().height / 2;

        movingEast = false;
        movingWest = false;
        movingNorth = false;
        movingSouth = false;

        yVel = 0.0;
        xVel = 0.0;
        targetRotation = 0.0;
        currentRotation = 0.0;
    }

    public void update()
    {
    // Vertical movement
    if (movingNorth) {
        yVel -= yAcc; // Accelerate upwards
        if (yVel < -maxVel) yVel = -maxVel; // Cap at max upwards velocity
    } else if (movingSouth) {
        yVel += yAcc; // Accelerate downwards
        if (yVel > maxVel) yVel = maxVel; // Cap at max downwards velocity
    } else {
        // Decelerate if no vertical movement
        if (yVel > 0) {
            yVel -= yAcc; // Decelerate downwards
            if (yVel < 0) yVel = 0; // Stop at 0
        } else if (yVel < 0) {
            yVel += yAcc; // Decelerate upwards
            if (yVel > 0) yVel = 0; // Stop at 0
        }
    }

    // Horizontal movement
    if (movingWest) {
        xVel -= xAcc; // Accelerate left
        if (xVel < -maxVel) xVel = -maxVel; // Cap at max leftwards velocity
    } else if (movingEast) {
        xVel += xAcc; // Accelerate right
        if (xVel > maxVel) xVel = maxVel; // Cap at max rightwards velocity
    } else {
        // Decelerate if no horizontal movement
        if (xVel > 0) {
            xVel -= xAcc; // Decelerate rightwards
            if (xVel < 0) xVel = 0; // Stop at 0
        } else if (xVel < 0) {
            xVel += xAcc; // Decelerate leftwards
            if (xVel > 0) xVel = 0; // Stop at 0
        }
    }

    // Update position based on velocities
    xPos += xVel;
    yPos += yVel;
    }
}
