package src.Objects;

import src.Threads.Engine;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bullet {
    Engine engine;
    Toolkit tk;

    public int yPos;
    public int xPos;

    public double xVel = 0.0;
    public double yVel = 0.0;

    public int width;
    public int height;

    public Bullet(Engine e, Toolkit tk){
        engine = e;
        this.tk = tk;

        width = 10;
        height = 10;
    }

    public void drawBullet(Graphics g)
    {
        g.setColor(new Color(0xc0, 0xb0, 0x40));
        g.fillOval(xPos, yPos, width, height);

        g.drawLine(xPos + width/2, yPos+height/2, (int)(xPos + width/2 - xVel), (int)(yPos + height/2 - yVel));

        if (engine.debug)
        {
            g.setColor(Color.RED);
            g.drawRect(xPos, yPos, width, height);
        }
    }

    public boolean collide(Pipe pipe)
    {
        if (yPos < 0 || yPos + height > tk.getScreenSize().height)
        {
            collide();
            return true;
        }
        if (xPos + width< pipe.xPos)
            return false;
        if (xPos> pipe.xPos + pipe.width)
            return false;
        if (yPos + height> pipe.yPos || yPos< pipe.yPos - pipe.gap)
        {
            collide();
            return true;
        }
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
                gain.setValue(20f * (float) Math.log10(engine.volume));
                clip.start();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }).start();
    }

    public void update()
    {
        xPos += xVel;
        yPos += yVel;
    }
}
