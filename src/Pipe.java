package src;

import javafx.scene.canvas.GraphicsContext;

public class Pipe
{
	JFXFlappy game;

	public double xPos = Util.SCREEN_WIDTH;
	public double yPos;

	public double defaultVel = 3.0;
	public double xVel = 3.0;

	public double width;
	public double height;
	public final double gap = Util.SCREEN_HEIGHT / 3;

	private boolean scoreable = true;
	public boolean spawnable = true;

	public Pipe(JFXFlappy g, int y, int w, int h, boolean bricks)
	{
		game = g;

		width = w;
		height = h;

		double gap2 = gap / 2;
		yPos = y + gap2;

		if (bricks) {
			Bricks b1 = new Bricks(g, xPos, yPos - gap, width, gap2);
			Bricks b2 = new Bricks(g, xPos, yPos - gap2, width, gap2);
			g.bricks.add(b1);
			g.bricks.add(b2);
		}
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
