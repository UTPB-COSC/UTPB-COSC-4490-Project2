package src.Objects;

import src.Threads.Engine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Cloud
{
    Toolkit tk;

    public int yPos;
    public int xPos;

    public double xVel;

    public int width;
    public int height;

    public BufferedImage image;
    public boolean passed;
    public int index;

    public Cloud(Engine e, Toolkit tk) {
        this.tk = tk;

        index = (int) (Math.random() * e.clouds.length);
        boolean duplicate = true;
        while(duplicate)
        {
            duplicate = false;
            for (int i = 0; i < e.clouds.length; i++)
            {
                if (e.clouds[i] != null && !e.clouds[i].passed && e.clouds[i].index == index)
                {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate)
                index = (int) (Math.random() * e.clouds.length);
        }

        int r = (int) (Math.random() * 3.0) + 3;
        width = tk.getScreenSize().width / r;
        height = (int)(((double)width / (double)e.cloudImage[index].getWidth()) * (e.cloudImage[index].getHeight()));

        Image temp = e.cloudImage[index].getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics x = image.getGraphics();
        x.drawImage(temp, 0, 0, null);
        x.dispose();

        xPos = tk.getScreenSize().width + 10;
        yPos = (int) (Math.random() * (4 * tk.getScreenSize().height / 5));

        xVel = (Math.random() * 0.2 + 0.2) * 5.0;
    }

    public void drawCloud(Graphics g)
    {
        g.drawImage(image, xPos, yPos, null);
    }

    public void update()
    {
        xPos -= xVel;
        if (xPos + width < -tk.getScreenSize().width / 2)
            passed = true;
    }
}
