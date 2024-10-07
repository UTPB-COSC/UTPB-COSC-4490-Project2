package src.Objects;

import src.Threads.Engine;
import src.Collision.Collideable;
import src.Drawing.Transformable;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bird extends Collideable implements Transformable
{
    Engine engine;
    Toolkit tk;

    public int yPos;
    public int xPos;

    public double yVel = 0.0;
    public double yAcc = 0.1;

    public int width;
    public int height;
    private int xGrace = 12;
    private int yGrace = 6;

    private BufferedImage[] birdImage = new BufferedImage[4];
    private int animframe = 0;
    private int animRate = 4;
    private int frameCount = 0;

    public Bird(Engine g, Toolkit tk) throws IOException {
        engine = g;
        this.tk = tk;

        BufferedImage image = ImageIO.read(new File("bird.png"));

        width = tk.getScreenSize().width / 12;
        height = (int)(((double)width / (double)image.getWidth()) * (image.getHeight() / 4));
        xGrace = Math.max(xGrace, width / 14);
        yGrace = Math.max(yGrace, height / 20);

        int fragHeight = image.getHeight() / 4;

        for (int i = 0; i < 4; i++)
        {
            Image temp = image.getSubimage(0, i * fragHeight, image.getWidth(), fragHeight).getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            birdImage[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics x = birdImage[i].getGraphics();
            x.drawImage(temp, 0, 0, null);
            x.dispose();
        }

        reset();
    }

    public void drawBird(Graphics g)
    {
        double rotation = Math.tanh(yVel / 8.0 - 0.2);
        AffineTransform at = new AffineTransform();
        at.rotate(rotation, width / 2, height / 2);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        frameCount++;
        animRate = Math.max((int)(4 + yVel), 1);
        if (frameCount % animRate == 0)
            animframe++;
        if(animframe >= birdImage.length)
            animframe = 0;

        BufferedImage bird = ato.filter(birdImage[animframe], null);
        g.drawImage(bird, xPos, yPos, null);

        if (engine.debug)
        {
            g.setColor(Color.RED);
            g.drawRect(xPos + xGrace, yPos + yGrace, width - xGrace * 2, height - yGrace * 2);
        }
    }

    public boolean collide(Pipe pipe)
    {
        if (yPos < 0 || yPos + height > tk.getScreenSize().height)
        {
            collide();
            return true;
        }
        if (xPos + width - xGrace < pipe.xPos)
            return false;
        if (xPos + xGrace > pipe.xPos + pipe.width)
            return false;
        if (yPos + height - yGrace > pipe.yPos || yPos + yGrace < pipe.yPos - pipe.gap)
        {
            collide();
            return true;
        }
        return false;
    }

    @Override
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

    public void flap()
    {
        yVel -= 5.0;

        new Thread(() ->
        {
            try
            {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File("flap.wav").getAbsoluteFile());
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

    public void reset()
    {
        xPos = tk.getScreenSize().width / 2;
        yPos = tk.getScreenSize().height / 3;

        yVel = 0.0;
    }

    public void update()
    {
        yVel += yAcc;
        yPos += yVel;
    }
}
