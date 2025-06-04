package src;

import javafx.scene.canvas.GraphicsContext;

public class Pipe
{
	JFXFlappy game;

	public double yPos;
	public double xPos;

	public double defaultVel = 3.0;
	public double xVel = 3.0;

	public double width;
	public double height;
	public double gap;

	private boolean scoreable = true;
	public boolean spawnable = true;

	public Pipe(JFXFlappy g, int y, int w, int h)
	{
		game = g;

		xPos = Util.SCREEN_WIDTH;

		width = w;
		height = h;
		if (g.randomGaps) {
			double range = Util.SCREEN_HEIGHT / 3;
			gap = Util.SCREEN_HEIGHT / 6 + (int) (Math.random() * range);
		} else {
			gap = Util.SCREEN_HEIGHT / 3;
		}

		yPos = y + gap / 2;
	}

	public void drawPipe(GraphicsContext gc)
	{
		gc.save();
		gc.translate(xPos + width / 2.0, yPos + height / 2.0);
		gc.drawImage(game.pipeImage, -width / 2.0, -height / 2.0, width, height);
		gc.restore();

		gc.save();
		gc.translate(xPos + width / 2.0, yPos - gap - height / 2.0);
		gc.rotate(180);
		gc.drawImage(game.pipeImage, -width / 2.0, -height / 2.0, width, height);
		gc.restore();
	}

	public boolean update(double delta)
	{
		xVel = (defaultVel + game.difficulty) * delta;
		xPos -= xVel;

		if (scoreable && xPos < Util.SCREEN_WIDTH / 2)
		{
			scoreable = false;
			return true;
		}
		return false;
	}
}
