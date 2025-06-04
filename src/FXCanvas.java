package src;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FXCanvas extends Canvas {

	private final JFXFlappy game;

	private WritableImage lastFrame;

	private boolean vsync = true;

	/**
	 * <h3>rateTarget</h3>
	 * <p>Sets the target framerate cap</p>
	 * <p>Units: frames per second</p>
	 */
	private double rateTarget = 240.0;

	/**
	 * <h3>rate</h3>
	 * <p>The current maximum possible frame rate</p>
	 */
	private double rate;

	/**
	 * <h3>nanoTarget</h3>
	 * </p>
	 * The nanosecond time expected to complete each frame
	 * Units: nanoseconds
	 */
	private double nanoTarget = 1_000_000_000.0 / rateTarget;

	public void setFPSTarget(double target) {
		rateTarget = target;
		nanoTarget = 1_000_000_000.0 / rateTarget;
	}

	/**
	 * <h3>runningAverageTime</h3>
	 * </p>
	 * For benchmarking, we build and maintain a running average of the frame time over a period of seconds
	 * Units: seconds
	 */
	private final int runningAverageTime = 10;	// Seconds

	/**
	 * <h3>frameBufferSize</h3>
	 * </p>
	 * The number of frame time values we need to maintain to achieve a running average long enough
	 */
	private final int frameBufferSize = (int)(rateTarget * runningAverageTime);

	/**
	 * <h3>frameBuffer</h3>
	 * </p>
	 * The array of double which contains the list of frame times for the running average
	 */
	private final double[] frameBuffer = new double[frameBufferSize];

	/**
	 * <h3>frameBufferIndex</h3>
	 * </p>
	 * An index value used to maintain the running average
	 */
	private int frameBufferIndex = 0;

	/**
	 * <h3>lastFrameTime</h3>
	 * </p>
	 * We also track the time cost of the last frame for benchmarking
	 */
	private double lastFrameTime = 0.0;

	public int cursor = 0;

	public FXCanvas(JFXFlappy g) {
		super(Util.SCREEN_WIDTH, Util.SCREEN_HEIGHT);
		game = g;

		lastFrame = new WritableImage((int)Util.SCREEN_WIDTH, (int)Util.SCREEN_HEIGHT);

		new AnimationTimer() {
			long lastTime = System.nanoTime();

			@Override
			public void handle(long now) {
				if (vsync) {
					double delta = (now - lastTime) / 10_000_000.0;
					System.out.printf("FPS = %.3f  Delta = %.3f%n", 100.0 / delta, delta);
					lastTime = now;

					// If running with an uncapped FPS, logic updates need to be scaled to the correct time delta between frames to maintain smooth gameplay
					// We implement it this way because in the future we want to allow players to uncap the framerate if desired
					game.update(delta);
					render(delta);
				}
			}
		}.start();

		Thread gameLoop = new Thread(() -> {
			long lastTime = System.nanoTime();

			while (true) {
				long now = System.nanoTime();
				if (!vsync) {
					if (now - lastTime >= nanoTarget) {
						double delta = (now - lastTime) / 10_000_000.0;
						lastTime = now;
						System.out.printf("FPS = %.3f  Delta = %.3f%n", 100.0 / delta, delta);

						// If running with an uncapped FPS, logic updates need to be scaled to the correct time delta between frames to maintain smooth gameplay
						// We implement it this way because in the future we want to allow players to uncap the framerate if desired
						game.update(delta);
						Platform.runLater(() -> render(delta));

						try {
							Thread.sleep(1);
						}
						catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				} else {
					lastTime = System.nanoTime();
				}
			}
		});
		gameLoop.setDaemon(true);
		gameLoop.start();
	}

	private double getAverageFPS() {
		int numEntries = Math.min(frameBufferIndex, frameBufferSize);
		double total = 0.0;
		for (int i = 0; i < numEntries; i++) {
			total += frameBuffer[i];
		}
		return total / numEntries;
	}

	void render(double delta) {
		long startTime = System.nanoTime();
		GraphicsContext gc = this.getGraphicsContext2D();

		gc.setFill(Color.CYAN);
		gc.fillRect(0, 0, Util.SCREEN_WIDTH, Util.SCREEN_HEIGHT);

		for (int i = 0; i < game.clouds.length; i++)
		{
			if (game.clouds[i] != null && !game.clouds[i].passed) {
				game.clouds[i].drawCloud(gc);
			}
		}

		game.bird.drawBird(gc, delta);

		/*
		g2d.setColor(Color.RED);
		g2d.drawOval(game.mouseX - crosshairSize, game.mouseY - crosshairSize, crosshairSize*2, crosshairSize*2);
		g2d.drawLine(game.mouseX - crosshairSize, game.mouseY, game.mouseX+crosshairSize, game.mouseY);
		g2d.drawLine(game.mouseX, game.mouseY-crosshairSize, game.mouseX, game.mouseY+crosshairSize);
		*/

		for (int i = 0; i < game.pipes.length; i++)
		{
			if (game.pipes[i] != null)
				game.pipes[i].drawPipe(gc);
		}

		gc.setStroke(Color.BLACK);
		if (game.running) {
			gc.strokeText(String.format("Score: %d", game.score), 25, 25);
			gc.strokeText(String.format("High Score: %d", game.highScore), 25, 50);
		} else {
			gc.strokeText(String.format("%s Reset Game", cursor == 0 ? ">" : " "), 25, 25);
			gc.strokeText(String.format("%s Exit Game", cursor == 1 ? ">" : " "), 25, 50);
			StringBuilder vol = new StringBuilder();
			for (int i = 0; i < 11; i++)
			{
				if ((int) (game.volume * 10) == i)
				{
					vol.append("|");
				} else {
					vol.append("-");
				}
			}
			gc.strokeText(String.format("%s Volume %s", cursor == 2 ? ">" : " ", vol), 25, 75);
			gc.strokeText(String.format("%s Randomize Gaps %s", cursor == 3 ? ">" : " ", game.randomGaps ? "(ON)" : "(OFF)"), 25, 100);
			StringBuilder dif = new StringBuilder();
			for (double i = 0.0; i <= 3.0; i+= 0.5)
			{
				if (game.difficulty == i)
				{
					dif.append("|");
				} else {
					dif.append("-");
				}
			}
			gc.strokeText(String.format("%s Difficulty %s", cursor == 4 ? ">" : " ", dif), 25, 125);
			gc.strokeText(String.format("%s Ramping %s", cursor == 5 ? ">" : " ", game.ramping ? "(ON)" : "(OFF)"), 25, 150);
			gc.strokeText(String.format("%s Debug Mode %s", cursor == 6 ? ">" : " ", game.debug ? "(ON)" : "(OFF)"), 25, 175);
		}
		double avgFPS = getAverageFPS();
		if (game.debug) {
			gc.strokeText(String.format("FPS = %.2f", rate), 200, 25);
			gc.strokeText(String.format("Frame Time Avg = %.2f ms", avgFPS), 300, 25);
			gc.strokeText(String.format("Last Frame Time = %.2f ms", lastFrameTime), 500, 25);
		}

		//this.snapshot(null, lastFrame);

		long deltaTime = System.nanoTime() - startTime;
		lastFrameTime = deltaTime / 1_000_000.0;
		frameBuffer[frameBufferIndex % frameBufferSize] = lastFrameTime;
		frameBufferIndex += 1;

		rate = 1_000.0 / avgFPS;
		//System.out.printf("rate = %.2f%n", rate);
	}
}
