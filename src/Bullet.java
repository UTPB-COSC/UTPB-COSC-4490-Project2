package src;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.List;

public class Bullet {

	JFXFlappy game;

	private final double speed = 10.0;
	private final double radius;
	private final double diameter;
	private final RadialGradient bronzeGradient;

	private double xPos;
	private final double xVel;
	private double yPos;
	private final double yVel;
	public boolean isAlive = true;

	public Bullet(JFXFlappy g, double startX, double startY, double endX, double endY) {
		game = g;

		radius = Util.SCREEN_HEIGHT / 100.0;
		diameter = radius * 2.0;

		bronzeGradient = new RadialGradient(
				0,                     // focusAngle
				0.1,                   // focusDistance (light offset)
				0.3, 0.3,              // centerX, centerY (relative to the shape)
				0.7,                   // radius
				true,                  // proportional
				CycleMethod.NO_CYCLE, // no repeating
				List.of(
						new Stop(0.0, Color.web("#f9c784")),   // highlight
						new Stop(0.4, Color.web("#c57f17")),   // midtone bronze
						new Stop(1.0, Color.web("#5e3b1d"))    // shadow
				)
		);

		xPos = startX; yPos = startY;
		//double slope = (endY - startY) / (endX - startX);
		double theta = Math.atan2(endY - startY, endX - startX);
		xVel = Math.cos(theta) * speed;
		yVel = Math.sin(theta) * speed;
	}

	public void update(double delta) {
		xPos += xVel * delta;
		yPos += yVel * delta;
		if (xPos + radius < 0 || xPos > Util.SCREEN_WIDTH + radius || yPos + radius < 0 || yPos > Util.SCREEN_HEIGHT + radius) {
			isAlive = false;
		}
	}

	public void collide(Pipe pipe) {
		if (xPos + diameter < pipe.xPos)
			return;
		if (xPos > pipe.xPos + pipe.width)
			return;
		if (yPos + diameter > pipe.yPos || yPos < pipe.yPos - pipe.gap)
		{
			isAlive = false;
		}
	}

	public void collide(Bricks brick) {
		if (xPos + diameter < brick.xPos)
			return;
		if (xPos > brick.xPos + brick.width)
			return;
		if (yPos + diameter < brick.yPos)
			return;
		if (yPos > brick.yPos + brick.height)
			return;
		brick.health -= 1;
		isAlive = false;
	}

	public void drawBullet(GraphicsContext gc) {
		gc.setFill(bronzeGradient);
		gc.fillOval(xPos - radius, yPos - radius, diameter, diameter);
	}
}
