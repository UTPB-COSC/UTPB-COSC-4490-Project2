package src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameCanvas extends JPanel implements Runnable
{

	private Game game;
	private Graphics graphics;
	Toolkit tk;
	private int scrWidth;
	private int scrHeight;
	private BufferedImage currentFrame;
	private BufferedImage lastFrame;

	private final double rateTarget = 60.0;
	private double waitTime = 1000.0 / rateTarget;
	private double rate = 1000.0 / waitTime;

	private final int runningAverageTime = 30;
	private final int frameBufferSize = (int)(rateTarget * runningAverageTime);
	private double[] frameBuffer = new double[frameBufferSize];
	private int frameBufferIndex = 0;
	private double lastFrameTime = 0.0;

	public int cursor = 0;
	public int crosshairSize = 30;

	public GameCanvas(Game game, Graphics g, Toolkit tk)
	{
		this.game = game;
		graphics = g;
		this.tk = tk;
		scrWidth = tk.getScreenSize().width;
		scrHeight = tk.getScreenSize().height;
		currentFrame = new BufferedImage(scrWidth, scrHeight, BufferedImage.TYPE_INT_ARGB);
	}

	private double getAverageFPS() {
		int numEntries = Math.min(frameBufferIndex, frameBufferSize);
		double total = 0.0;
		for (int i = 0; i < numEntries; i++) {
			total += frameBuffer[i];
		}
		return total / numEntries;
	}

	private double getAverageUPS() {
		int numEntries = Math.min(game.updateBufferIndex, game.updateBufferSize);
		double total = 0.0;
		for (int i = 0; i < numEntries; i++) {
			total += game.updateBuffer[i];
		}
		return total / numEntries;
	}

	@Override
	public void run() {
		while(true)
		{
			long startTime = System.nanoTime();
			Graphics2D g2d = currentFrame.createGraphics();

			g2d.setColor(Color.CYAN);
			g2d.fillRect(0, 0, scrWidth, scrHeight);

			long cloudStart = System.nanoTime();
			int numClouds = 0;
			for (int i = 0; i < game.clouds.length; i++)
			{
				if (game.clouds[i] != null && !game.clouds[i].passed) {
					game.clouds[i].drawCloud(g2d);
					numClouds++;
				}
			}
			long cloudDelta = System.nanoTime() - cloudStart;
			double cloudTime = cloudDelta / 1_000_000.0;

			game.bird.drawBird(g2d);

            /*
            g2d.setColor(Color.RED);
            g2d.drawOval(game.mouseX - crosshairSize, game.mouseY - crosshairSize, crosshairSize*2, crosshairSize*2);
            g2d.drawLine(game.mouseX - crosshairSize, game.mouseY, game.mouseX+crosshairSize, game.mouseY);
            g2d.drawLine(game.mouseX, game.mouseY-crosshairSize, game.mouseX, game.mouseY+crosshairSize);
            */

			for (int i = 0; i < game.pipes.length; i++)
			{
				if (game.pipes[i] != null)
					game.pipes[i].drawPipe(g2d);
			}

			g2d.setColor(Color.BLACK);
			if (game.running) {
				g2d.drawString(String.format("Score: %d", game.score), 25, 25);
				g2d.drawString(String.format("High Score: %d", game.highScore), 25, 50);
			} else {
				g2d.drawString(String.format("%s Reset Game", cursor == 0 ? ">" : " "), 25, 25);
				g2d.drawString(String.format("%s Exit Game", cursor == 1 ? ">" : " "), 25, 50);
				String vol = "";
				for (int i = 0; i < 11; i++)
				{
					if ((int) (game.volume * 10) == i)
					{
						vol += "|";
					} else {
						vol += "-";
					}
				}
				g2d.drawString(String.format("%s Volume %s", cursor == 2 ? ">" : " ", vol), 25, 75);
				g2d.drawString(String.format("%s Randomize Gaps %s", cursor == 3 ? ">" : " ", game.randomGaps ? "(ON)" : "(OFF)"), 25, 100);
				String dif = "";
				for (double i = 0.0; i <= 3.0; i+= 0.5)
				{
					if (game.difficulty == i)
					{
						dif += "|";
					} else {
						dif += "-";
					}
				}
				g2d.drawString(String.format("%s Difficulty %s", cursor == 4 ? ">" : " ", dif), 25, 125);
				g2d.drawString(String.format("%s Ramping %s", cursor == 5 ? ">" : " ", game.ramping ? "(ON)" : "(OFF)"), 25, 150);
				g2d.drawString(String.format("%s Debug Mode %s", cursor == 6 ? ">" : " ", game.debug ? "(ON)" : "(OFF)"), 25, 175);
			}
			if (game.debug) {
				g2d.drawString(String.format("FPS = %.1f", rate), 200, 25);
				g2d.drawString(String.format("UPS = %.1f", game.rate), 200, 50);
				g2d.drawString(String.format("Frame Time Avg = %.2f ms", getAverageFPS()), 300, 25);
				g2d.drawString(String.format("Update Time Avg = %.2f ms", getAverageUPS()), 300, 50);
				g2d.drawString(String.format("Last Frame Time = %.2f ms", lastFrameTime), 500, 25);
				g2d.drawString(String.format("Last Update Time = %.2f ms", game.lastUpdateTime), 500, 50);
				g2d.drawString(String.format("Num Clouds = %d", numClouds), 100, 75);
				g2d.drawString(String.format("Cloud Time = %.2f ms", cloudTime), 300, 75);
				g2d.drawString(String.format("Per Cloud Time = %.2f ms", cloudTime / numClouds), 500, 75);
			}

			graphics.drawImage(currentFrame, 0, 0, null);
			//lastFrame = currentFrame;

			long deltaTime = System.nanoTime() - startTime;
			lastFrameTime = deltaTime / 1_000_000.0;
			frameBuffer[frameBufferIndex % frameBufferSize] = lastFrameTime;
			frameBufferIndex += 1;

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
}
