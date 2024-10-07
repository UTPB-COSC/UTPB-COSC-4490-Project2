package src.Threads;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawLoop extends RateLimited
{

    private Engine engine;
    private JPanel panel;
    private Graphics graphics;
    Toolkit tk;

    public int cursor = 0;
    public int crosshairSize = 30;

    public DrawLoop(Engine engine, Graphics g, Toolkit tk)
    {
        this.engine = engine;
        panel = new JPanel();
        graphics = g;
        this.tk = tk;
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void run() {
        while(true)
        {
            long startTime = System.nanoTime();

            int width = tk.getScreenSize().width;
            int height = tk.getScreenSize().height;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setColor(Color.CYAN);
            g2d.fillRect(0, 0, width, height);

            for (int i = 0; i < engine.clouds.length; i++)
            {
                if (engine.clouds[i] != null && !engine.clouds[i].passed)
                    engine.clouds[i].drawCloud(g2d);
            }

            engine.bird.drawBird(g2d);

            g2d.setColor(Color.RED);
            g2d.drawOval(engine.mouseX - crosshairSize, engine.mouseY - crosshairSize, crosshairSize*2, crosshairSize*2);
            g2d.drawLine(engine.mouseX - crosshairSize, engine.mouseY, engine.mouseX+crosshairSize, engine.mouseY);
            g2d.drawLine(engine.mouseX, engine.mouseY-crosshairSize, engine.mouseX, engine.mouseY+crosshairSize);

            for (int i = 0; i < engine.pipes.length; i++)
            {
                if (engine.pipes[i] != null)
                    engine.pipes[i].drawPipe(g2d);
            }

            g2d.setColor(Color.BLACK);
            if (engine.running) {
                g2d.drawString(String.format("Score: %d", engine.score), 25, 25);
                g2d.drawString(String.format("High Score: %d", engine.highScore), 25, 50);
            } else {
                g2d.drawString(String.format("%s Reset Game", cursor == 0 ? ">" : " "), 25, 25);
                g2d.drawString(String.format("%s Exit Game", cursor == 1 ? ">" : " "), 25, 50);
                String vol = "";
                for (int i = 0; i < 11; i++)
                {
                    if ((int) (engine.volume * 10) == i)
                    {
                        vol += "|";
                    } else {
                        vol += "-";
                    }
                }
                g2d.drawString(String.format("%s Volume %s", cursor == 2 ? ">" : " ", vol), 25, 75);
                g2d.drawString(String.format("%s Randomize Gaps %s", cursor == 3 ? ">" : " ", engine.randomGaps ? "(ON)" : "(OFF)"), 25, 100);
                String dif = "";
                for (double i = 0.0; i <= 3.0; i+= 0.5)
                {
                    if (engine.difficulty == i)
                    {
                        dif += "|";
                    } else {
                        dif += "-";
                    }
                }
                g2d.drawString(String.format("%s Difficulty %s", cursor == 4 ? ">" : " ", dif), 25, 125);
                g2d.drawString(String.format("%s Ramping %s", cursor == 5 ? ">" : " ", engine.ramping ? "(ON)" : "(OFF)"), 25, 150);
                g2d.drawString(String.format("%s Debug Mode %s", cursor == 6 ? ">" : " ", engine.debug ? "(ON)" : "(OFF)"), 25, 175);
            }
            if (engine.debug) {
                g2d.drawString(String.format("FPS = %.1f", getRate()), 200, 25);
                g2d.drawString(String.format("UPS = %.1f", engine.getRate()), 200, 50);
            }

            graphics.drawImage(image, 0, 0, null);

            limit(startTime);
        }
    }
}
