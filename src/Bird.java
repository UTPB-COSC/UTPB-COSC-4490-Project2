package src;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.IOException;

public class Bird
{
	JFXFlappy game;

	public double yPos;
	public double xPos;

	public double yVel = 0.0;
	public double yAcc = 0.1;

	public double width;
	public double height;
	private double xGrace = 12;
	private double yGrace = 6;

	private final int numAnimFrames = 4;
	private final Image[] birdImage = new Image[numAnimFrames];
	private int animframe = 0;
	private double frameCount = 0.0;

	public Bird(JFXFlappy g) throws IOException {
		game = g;

		Image image = new Image(new FileInputStream("bird.png"));

		width = Util.SCREEN_WIDTH / 12;
		height = (width / image.getWidth()) * (image.getHeight() / 4.0);
		xGrace = Math.max(xGrace, width / 14);
		yGrace = Math.max(yGrace, height / 20);

		int fragHeight = (int)(image.getHeight() / numAnimFrames);

		for (int i = 0; i < numAnimFrames; i++) {
			WritableImage frame = new WritableImage(image.getPixelReader(), 0, i * fragHeight, (int) image.getWidth(), fragHeight);
			birdImage[i] = frame;
		}

		reset();
	}

	public void drawBird(GraphicsContext gc, double delta)
	{
		double rotation = Math.toDegrees(Math.tanh(yVel / 8.0 - 0.2));

		frameCount += delta;
		int animRate = Math.max((int)(numAnimFrames + yVel), 1);
		if ((int)frameCount % animRate == 0)
			animframe++;
		if(animframe >= birdImage.length)
			animframe = 0;

		gc.save();
		gc.translate(xPos + width / 2.0, yPos + height / 2.0);
		gc.rotate(rotation);
		gc.drawImage(birdImage[animframe], -width / 2.0, -height / 2.0, width, height);
		gc.restore();

		if (game.debug)
		{
			gc.setStroke(Color.RED);
			gc.strokeRect(xPos + xGrace, yPos + yGrace, width - xGrace * 2, height - yGrace * 2);
		}
	}

	public boolean collide(Pipe pipe)
	{
		if (yPos < 0 || yPos + height > Util.SCREEN_HEIGHT)
		{
			game.playClip("collide");
			return true;
		}
		if (xPos + width - xGrace < pipe.xPos)
			return false;
		if (xPos + xGrace > pipe.xPos + pipe.width)
			return false;
		if (yPos + height - yGrace > pipe.yPos || yPos + yGrace < pipe.yPos - pipe.gap)
		{
			game.playClip("collide");
			return true;
		}
		return false;
	}

	public boolean collide(Bricks brick) {
		if (xPos + width - xGrace < brick.xPos)
			return false;
		if (xPos + xGrace > brick.xPos + brick.width)
			return false;
		if (yPos + height - yGrace < brick.yPos)
			return false;
		if (yPos + yGrace > brick.yPos + brick.height)
			return false;
		game.playClip("collide");
		return true;
	}

	public void flap()
	{
		yVel -= 5.0;
		game.playClip("flap");
	}

	public void reset()
	{
		xPos = Util.SCREEN_WIDTH / 2;
		yPos = Util.SCREEN_HEIGHT / 3;

		yVel = 0.0;
	}

	public void update(double delta)
	{
		yVel += yAcc * delta;
		yPos += yVel * delta;
	}
}
