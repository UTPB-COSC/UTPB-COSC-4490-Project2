package src;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FXCanvas extends Canvas {

	private final JFXFlappy game;

	public int menuCursor = 0;

	public double crosshairSize;

	public FXCanvas(JFXFlappy g) {
		super(Util.SCREEN_WIDTH, Util.SCREEN_HEIGHT);
		game = g;

		crosshairSize = Util.SCREEN_HEIGHT / 40.0;

		//lastFrame = new WritableImage((int)Util.SCREEN_WIDTH, (int)Util.SCREEN_HEIGHT);

		new AnimationTimer() {
			long lastTime = System.nanoTime();

			@Override
			public void handle(long now) {
				double delta = (now - lastTime) / 10_000_000.0;
				//System.out.printf("FPS = %.3f  Delta = %.3f%n", 100.0 / delta, delta);
				lastTime = now;

				// If running with an uncapped FPS, logic updates need to be scaled to the correct time delta between frames to maintain smooth gameplay
				// We implement it this way because in the future we want to allow players to uncap the framerate if desired
				game.update(delta);
				render(delta);
			}
		}.start();
	}

	void render(double delta) {
		GraphicsContext gc = this.getGraphicsContext2D();

		gc.setFill(Color.CYAN);
		gc.fillRect(0, 0, Util.SCREEN_WIDTH, Util.SCREEN_HEIGHT);

		for (Cloud cloud : game.clouds)
		{
			cloud.drawCloud(gc);
		}

		game.bird.drawBird(gc, delta);

		for (int i = 0; i < game.pipes.length; i++)
		{
			if (game.pipes[i] != null)
				game.pipes[i].drawPipe(gc);
		}

		for (Bullet bullet : game.bullets) {
			bullet.drawBullet(gc);
		}

		for (Bricks brick : game.bricks) {
			brick.drawBricks(gc);
		}

		gc.setStroke(Color.RED);
		gc.strokeOval(game.mouseX - crosshairSize, game.mouseY - crosshairSize, crosshairSize*2, crosshairSize*2);
		gc.strokeLine(game.mouseX - crosshairSize, game.mouseY, game.mouseX+crosshairSize, game.mouseY);
		gc.strokeLine(game.mouseX, game.mouseY-crosshairSize, game.mouseX, game.mouseY+crosshairSize);

		if (game.firing) {
			gc.strokeLine(game.bird.xPos+game.bird.width, game.bird.yPos+game.bird.height/2, game.mouseX, game.mouseY);
		}

		gc.setStroke(Color.BLACK);
		if (game.running) {
			gc.strokeText(String.format("Score: %d", game.score), 25, 25);
			gc.strokeText(String.format("High Score: %d", game.highScore), 25, 50);
		} else {
			gc.strokeText(String.format("%s Reset Game", menuCursor == 0 ? ">" : " "), 25, 25);
			gc.strokeText(String.format("%s Exit Game", menuCursor == 1 ? ">" : " "), 25, 50);
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
			gc.strokeText(String.format("%s Volume %s", menuCursor == 2 ? ">" : " ", vol), 25, 75);
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
			gc.strokeText(String.format("%s Difficulty %s", menuCursor == 4 ? ">" : " ", dif), 25, 100);
			gc.strokeText(String.format("%s Ramping %s", menuCursor == 5 ? ">" : " ", game.ramping ? "(ON)" : "(OFF)"), 25, 125);
			gc.strokeText(String.format("%s Debug Mode %s", menuCursor == 6 ? ">" : " ", game.debug ? "(ON)" : "(OFF)"), 25, 150);
		}
	}
}
