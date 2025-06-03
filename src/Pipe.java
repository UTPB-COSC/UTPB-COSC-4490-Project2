package src;

import java.awt.*;

public class Pipe
{
	Game game;

	private Toolkit tk;

	public int yPos;
	public int xPos;

	public double defaultVel = 3.0;
	public double xVel = 3.0;

	public int width;
	public int height;
	public int gap;

	private boolean scoreable = true;
	public boolean spawnable = true;

	public Pipe(Game g, Toolkit tk, int y, int w, int h)
	{
		game = g;
		this.tk = tk;

		xPos = tk.getScreenSize().width;

		width = w;
		height = h;
		if (g.randomGaps) {
			int range = tk.getScreenSize().height / 3;
			gap = tk.getScreenSize().height / 6 + (int) (Math.random() * range);
		} else {
			gap = tk.getScreenSize().height / 3;
		}

		yPos = y + gap / 2;
	}

	public void drawPipe(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(game.pipeImage, xPos, yPos, null);
		g2d.drawImage(game.flippedPipe, xPos, yPos - gap - height, null);
	}

	public boolean update()
	{
		xVel = defaultVel + game.difficulty;
		xPos -= xVel;

		if (scoreable && xPos < tk.getScreenSize().width / 2)
		{
			scoreable = false;
			return true;
		}
		return false;
	}
}
