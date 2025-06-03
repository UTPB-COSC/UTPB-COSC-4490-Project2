package src;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

public class Game implements Runnable
{
	private final GameCanvas canvas;

	private final double rateTarget = 100.0;
	public double waitTime = 1000.0 / rateTarget;
	public double rate = 1000 / waitTime;

	private final int runningAverageTime = 30;
	public final int updateBufferSize = (int)(runningAverageTime * rateTarget);
	public double[] updateBuffer = new double[updateBufferSize];
	public int updateBufferIndex = 0;
	public double lastUpdateTime = 0.0;

	public Bird bird;
	public int mouseX;
	public int mouseY;
	public int fireRate = 10;
	public int fireCounter = 0;
	public boolean firing = false;

	public Pipe[] pipes = new Pipe[5];
	private int pipeCount = 1;

	public int highScore = 0;
	public int score = 0;

	public Toolkit tk;
	public boolean debug = true;
	public boolean running = true;
	public double volume = 0.3;
	public boolean randomGaps = false;
	public double difficulty = 0.0;
	public boolean ramping = false;

	private int pipeWidth;
	private int pipeHeight;
	public BufferedImage pipeImage;
	public BufferedImage flippedPipe;

	public BufferedImage[] cloudImage = new BufferedImage[21];
	private final int cloudCap = 20;
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

			bird = new Bird(this, tk);

			BufferedImage image = ImageIO.read(new File("pipe.png"));

			pipeWidth = tk.getScreenSize().width / 16;
			pipeHeight = (int)(((double)pipeWidth / (double)image.getWidth()) * image.getHeight());

			Image temp = image.getScaledInstance(pipeWidth, pipeHeight, BufferedImage.SCALE_SMOOTH);
			pipeImage = new BufferedImage(pipeWidth, pipeHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics g = pipeImage.getGraphics();
			g.drawImage(temp, 0, 0, null);
			g.dispose();

			AffineTransform at = new AffineTransform();
			at.rotate(Math.PI, image.getWidth() / 2, image.getHeight() / 2);
			AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage flipped = ato.filter(image, null);

			temp = flipped.getScaledInstance(pipeWidth, pipeHeight, BufferedImage.SCALE_SMOOTH);
			flippedPipe = new BufferedImage(pipeWidth, pipeHeight, BufferedImage.TYPE_INT_ARGB);
			g = flippedPipe.getGraphics();
			g.drawImage(temp, 0, 0, null);
			g.dispose();

			Pipe pipe = new Pipe(this, tk, tk.getScreenSize().height / 2, pipeWidth, pipeHeight);
			pipes[0] = pipe;

			image = ImageIO.read(new File("clouds.png"));
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
					if (running)
						bird.flap();
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
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
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

				bird.update();
				for (int i = 0; i < pipes.length; i++) {
					if (pipes[i] == null)
						continue;

					if (pipes[i].update()) {
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

					if (pipes[i].spawnable && pipes[i].xPos < 3 * tk.getScreenSize().width / 4) {
						pipes[i].spawnable = false;
						int min = tk.getScreenSize().height / 4;
						int range = min * 2;
						int y = (int) (Math.random() * range) + min;
						Pipe pipe = new Pipe(this, tk, y, pipeWidth, pipeHeight);
						pipes[pipeCount] = pipe;
						pipeCount++;
						if (pipeCount >= pipes.length)
							pipeCount = 0;
					}

					if (bird.collide(pipes[i])) {
						running = !running;
					}
				}
			}

			long deltaTime = System.nanoTime() - startTime;
			lastUpdateTime = deltaTime / 1_000_000.0;
			updateBuffer[updateBufferIndex % updateBufferSize] = lastUpdateTime;
			updateBufferIndex += 1;

			long sleep = (long) waitTime - (System.nanoTime() - startTime) / 1_000_000;
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

		bird.reset();
		pipes = new Pipe[5];
		Pipe p = new Pipe(this, tk, tk.getScreenSize().height / 2, pipeWidth, pipeHeight);
		pipes[0] = p;
		pipeCount = 1;
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
