package src;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Cloud
{
	public double yPos;
	public double xPos;

	public double xVel;

	public double width;
	public double height;

	public Image image;
	public boolean passed;
	public int index;

	public Cloud(JFXFlappy g) {
		index = (int) (Math.random() * g.numClouds);
		image = g.cloudImage[index];

		int r = (int) (Math.random() * 3.0) + 3;
		width = Util.SCREEN_WIDTH / r;
		height = (width / g.cloudImage[index].getWidth()) * (g.cloudImage[index].getHeight());

		xPos = Util.SCREEN_WIDTH + 10;
		yPos = Math.random() * ((4.0 * Util.SCREEN_HEIGHT) / 5.0);

		xVel = (Math.random() * 0.2 + 0.2) * 5.0;
	}

	public void drawCloud(GraphicsContext gc)
	{
		gc.save();
		gc.translate(xPos + width / 2.0, yPos + height / 2.0);
		gc.drawImage(image, -width / 2.0, -height / 2.0, width, height);
		gc.restore();
	}

	public void update(double delta)
	{
		xPos -= (int)(xVel * delta);
		if (xPos + width < -Util.SCREEN_WIDTH / 2)
			passed = true;
	}
}
