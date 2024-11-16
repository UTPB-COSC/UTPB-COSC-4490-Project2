package src;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;

public class Game implements Runnable
{
    private final GameCanvas canvas;

    private final double rateTarget = 100.0;
    public double waitTime = 1000.0 / rateTarget;
    public double rate = 1000 / waitTime;

    public Tank tank;
    public int mouseX;
    public int mouseY;
    public int fireRate = 10;
    public int fireCounter = 0;
    public boolean firing = false;

    public Bomber[] bombers = new Bomber[5];
    private int bomberCount = 1;

    public int highScore = 0;
    public int score = 0;

    public Toolkit tk;
    public boolean debug = false;
    public boolean running = true;
    public double volume = 0.3;
    public boolean randomGaps = false;
    public double difficulty = 0.0;
    public boolean ramping = false;

    private int bomberWidth;
    private int bomberHeight;
    public BufferedImage bomberImage;

    public BufferedImage[] cloudImage = new BufferedImage[21];
    private final int cloudCap = 16;
    public Cloud[] clouds = new Cloud[cloudCap];
    private int cloudCount = 0;
    private final double cloudRate = 0.005;

    public Game()
    {
        JFrame frame = new JFrame("Game");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setUndecorated(true);

        tk = Toolkit.getDefaultToolkit();

        frame.setVisible(true);
        frame.requestFocus();

        try {
            File scoreFile = new File("score.txt");
            if(!scoreFile.exists())
            {
                highScore = 0;
            } else {
                try {
                    FileReader fr = new FileReader(scoreFile);
                    BufferedReader br = new BufferedReader(fr);
                    highScore = Integer.parseInt(br.readLine());
                    br.close();
                    fr.close();
                } catch (Exception ex) {
                    highScore = 0;
                }
            }

            tank = new Tank(this, tk);

            BufferedImage image = ImageIO.read(new File("BomberPlane1.1.png"));

            bomberWidth = image.getWidth();
            bomberHeight = image.getHeight();

            Image temp = image.getScaledInstance(bomberWidth, bomberHeight, BufferedImage.SCALE_SMOOTH);
            bomberImage = new BufferedImage(bomberWidth, bomberHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bomberImage.getGraphics();
            g.drawImage(temp, 0, 0, null);
            g.dispose();

            Bomber bomber = new Bomber(this, tk, tk.getScreenSize().height, bomberWidth, bomberHeight);
            bombers[0] = bomber;

            image = ImageIO.read(new File("clouds2.png"));
            int fragHeight = image.getHeight() / 21;
            for (int i = 0; i < cloudImage.length; i++)
            {
                temp = image.getSubimage(0, i * fragHeight, image.getWidth(), fragHeight);
                cloudImage[i] = new BufferedImage(image.getWidth(), fragHeight, BufferedImage.TYPE_INT_ARGB);
                g = cloudImage[i].getGraphics();
                g.drawImage(temp, 0, 0, null);
                g.dispose();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }

        canvas = new GameCanvas(this, frame.getGraphics(), tk);
        frame.add(canvas);

        Thread drawLoop = new Thread(canvas);
        drawLoop.start();

        frame.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent arg0){}
            @Override
            public void mouseExited(MouseEvent arg0){}
            @Override
            public void mouseReleased(MouseEvent arg0){}
            @Override
            public void mousePressed(MouseEvent arg0)
            {
                if(running)
                {
                    tank.fire();
                }
            }
            @Override
            public void mouseEntered(MouseEvent arg0){}
        });
        frame.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    if (running) {}
                        //May implement later
                }
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    running = !running;
                }
                if(e.getKeyCode() == KeyEvent.VK_UP)
                {
                    if (!running)
                    {
                        canvas.cursor--;
                        canvas.cursor = Math.max(canvas.cursor, 0);
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    if (!running)
                    {
                        canvas.cursor++;
                        canvas.cursor = Math.min(canvas.cursor, 6);
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                {
                    if (!running)
                    {
                        if (canvas.cursor == 2) {
                            volume += 0.1;
                            volume = Math.min(volume, 1.0);
                        }
                        if (canvas.cursor == 4) {
                            difficulty += 0.5;
                            difficulty = Math.min(difficulty, 3.0);
                        }
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_LEFT)
                {
                    if (!running)
                    {
                        if (canvas.cursor == 2) {
                            volume -= 0.1;
                            volume = Math.max(volume, 0.0);
                        }
                        if (canvas.cursor == 4) {
                            difficulty -= 0.5;
                            difficulty = Math.max(difficulty, 0.0);
                        }
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if (!running)
                    {
                        if (canvas.cursor == 0)
                            reset();
                        if (canvas.cursor == 1)
                            System.exit(0);
                        if (canvas.cursor == 3)
                            randomGaps = !randomGaps;
                        if (canvas.cursor == 5)
                            ramping = !ramping;
                        if (canvas.cursor == 6)
                            debug = !debug;
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_W)
                {
                    if (running)
                        tank.moveNorth();
                }
                if(e.getKeyCode() == KeyEvent.VK_A)
                {
                    if (running)
                        tank.moveWest();
                }
                if(e.getKeyCode() == KeyEvent.VK_S)
                {
                    if (running)
                        tank.moveSouth();
                }
                if(e.getKeyCode() == KeyEvent.VK_D)
                {
                    if (running)
                        tank.moveEast();
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_W)
                {
                    if (running)
                        tank.stopNorth();
                }
                if(e.getKeyCode() == KeyEvent.VK_A)
                {
                    if (running)
                        tank.stopWest();
                }
                if(e.getKeyCode() == KeyEvent.VK_S)
                {
                    if (running)
                        tank.stopSouth();
                }
                if(e.getKeyCode() == KeyEvent.VK_D)
                {
                    if (running)
                        tank.stopEast();
                }
            }
        });

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    firing = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    firing = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

            }
        });
    }

    @Override
    public void run()
    {
        while(true)
        {
            long startTime = System.nanoTime();

            if (running)
            {
                fireCounter -= 1;
                fireCounter = Math.max(fireCounter, 0);
                if (firing && fireCounter == 0)
                {
                    // spawn new bullet and add to bullet array
                    fireCounter = 10;
                }
                // for each bullet within drawable space:
                //   perform update
                //   check for collisions
                //   if collide():
                //     do something

                if (Math.random() < cloudRate)
                {
                    cloudCount++;
                    if (cloudCount >= clouds.length)
                        cloudCount = 0;
                    if (clouds[cloudCount] == null || clouds[cloudCount].passed)
                    {
                        Cloud c = new Cloud(this, tk);
                        clouds[cloudCount] = c;
                    }
                }
                for (int i = 0; i < clouds.length; i++)
                {
                    if(clouds[i] != null)
                        clouds[i].update();
                }

                tank.update();
                for (int i = 0; i < bombers.length; i++) {
                    if (bombers[i] == null)
                        continue;

                    if (bombers[i].update()) {
                        score += 1;

                        if (ramping && score % 10 == 0)
                        {
                            difficulty += 0.5;
                            difficulty = Math.min(difficulty, 3.0);
                        }

                        if (score > highScore)
                        {
                            new Thread(() -> {
                                highScore = score;
                                running = true;
                                try {
                                    File scoreFile = new File("score.txt");
                                    PrintWriter pw = new PrintWriter(scoreFile);
                                    pw.write(String.format("%d%n", highScore));
                                    pw.close();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }

                        new Thread(() -> {
                            try {
                                AudioInputStream ais = AudioSystem.getAudioInputStream(new File("score.wav").getAbsoluteFile());
                                Clip clip = AudioSystem.getClip();
                                clip.open(ais);
                                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                                gain.setValue(20f * (float) Math.log10(volume));
                                clip.start();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                    }

                    if (bombers[i].spawnable && bombers[i].xPos < 3 * tk.getScreenSize().width / 4) {
                        bombers[i].spawnable = false;
                        int min = tk.getScreenSize().height / 4;
                        int range = min * 2;
                        int y = (int) (Math.random() * range) + min;
                        Bomber pipe = new Bomber(this, tk, y, bomberWidth, bomberHeight);
                        bombers[bomberCount] = pipe;
                        bomberCount++;
                        if (bomberCount >= bombers.length)
                            bomberCount = 0;
                    }

                    if (tank.collide(bombers[i])) {
                        running = !running;
                    }
                }
            }

            long sleep = (long) waitTime - (System.nanoTime() - startTime) / 1000000;
            rate = 1000.0 / Math.max(waitTime - sleep, waitTime);

            try
            {
                Thread.sleep(Math.max(sleep, 0));
            } catch (InterruptedException ex)
            {
            }
        }
    }

    public void reset()
    {
        if (score > highScore)
        {
            highScore = score;
            running = true;
            try {
                File scoreFile = new File("score.txt");
                PrintWriter pw = new PrintWriter(scoreFile);
                pw.write(String.format("%d%n", highScore));
                pw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        tank.reset();
        bombers = new Bomber[5];
        Bomber p = new Bomber(this, tk, tk.getScreenSize().height / 2, bomberWidth, bomberHeight);
        bombers[0] = p;
        bomberCount = 1;
        score = 0;

        clouds = new Cloud[cloudCap];
        cloudCount = 0;

        running = true;
    }

    public static void main(String[] args)
    {
        Game game = new Game();

        Thread logicLoop = new Thread(game);
        logicLoop.start();
    }
}
