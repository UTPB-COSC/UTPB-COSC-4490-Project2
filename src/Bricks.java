package src;

import javafx.scene.canvas.GraphicsContext;

public class Bricks {

	private final JFXFlappy game;

	public double xPos;
	public final double yPos;
	public final double width;
	public final double height;

	private final double defaultVel = 3.0;
	private double xVel = 3.0;

	public int health = 3;

	public Bricks(JFXFlappy g, double x, double y, double w, double h) {
		game = g;
		xPos = x;
		yPos = y;
		width = w;
		height = h;
	}

	public void update(double delta) {
		xVel = (defaultVel + game.difficulty) * delta;
		xPos -= xVel;
	}

	public void drawBricks(GraphicsContext gc) {
		gc.save();
		gc.translate(xPos + width / 2.0, yPos + height / 2.0);
		switch(health) {
			case 3 -> gc.drawImage(game.brickImage0, -width / 2.0, -height / 2.0, width, height);
			case 2 -> gc.drawImage(game.brickImage1, -width / 2.0, -height / 2.0, width, height);
			case 1 -> gc.drawImage(game.brickImage2, -width / 2.0, -height / 2.0, width, height);
		}
		gc.restore();
	}

}
