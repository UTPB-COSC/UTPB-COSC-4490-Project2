package src;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JFXFlappy extends Application {

	private FXCanvas canvas;

	public Bird bird;

	public Pipe[] pipes = new Pipe[5];
	private int pipeCount = 1;
	private int pipeWidth;
	private int pipeHeight;
	public Image pipeImage;

	public final int numClouds = 21;
	public Image[] cloudImage = new Image[numClouds];
	public List<Cloud> clouds = new ArrayList<>();
	private final double cloudRate = 800.0;
	private double nextCloud = 0.0;
	private double cloudCounter = 0.0;

	public double mouseX;
	public double mouseY;
	public boolean firing;
	private final double fireRate = 30.0;
	private double fireCounter = 0.0;
	public List<Bullet> bullets = new ArrayList<>();

	private final double brickRate = 0.3;
	public List<Bricks> bricks = new ArrayList<>();
	public Image brickImage0;
	public Image brickImage1;
	public Image brickImage2;

	public int highScore = 0;
	public int score = 0;

	public boolean debug = false;
	public boolean running = false;
	public double volume = 0.2;
	public boolean randomGaps = false;
	public double difficulty = 1.0;
	public boolean ramping = false;

	public Map<String, Clip> soundFX = new HashMap<>();

	@Override
	public void start(Stage stage) {
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setTitle("JavaFX Flappy Bird");

		canvas = new FXCanvas(this);

		Group root = new Group(canvas);
		Scene scene = new Scene(root, Color.CYAN);

		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.show();

		try {
			File scoreFile = new File("score.txt");
			if(!scoreFile.exists())
			{
				highScore = 0;
			} else {
				try {
					FileReader fr = new FileReader(scoreFile);
					BufferedReader br = new BufferedReader(fr);
					highScore = Integer.parseInt(br.readLine());
					br.close();
					fr.close();
				} catch (Exception ex) {
					highScore = 0;
				}
			}

			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("score.wav").getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			soundFX.put("score", clip);

			ais = AudioSystem.getAudioInputStream(new File("collide.wav").getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(ais);
			soundFX.put("collide", clip);

			ais = AudioSystem.getAudioInputStream(new File("flap.wav").getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(ais);
			soundFX.put("flap", clip);

			setVolume();

			bird = new Bird(this);

			pipeImage = new Image(new FileInputStream("pipe.png"));
			brickImage0 = new Image(new FileInputStream("brick0.png"));
			brickImage1 = new Image(new FileInputStream("brick1.png"));
			brickImage2 = new Image(new FileInputStream("brick2.png"));

			pipeWidth = (int)(Util.SCREEN_WIDTH / 16);
			pipeHeight = (int)(((double)pipeWidth / pipeImage.getWidth()) * pipeImage.getHeight());

			Pipe pipe = new Pipe(this, (int)(Util.SCREEN_HEIGHT / 2), pipeWidth, pipeHeight, false);
			pipes[0] = pipe;

			Image image = new Image(new FileInputStream("clouds.png"));
			int fragHeight = (int)image.getHeight() / numClouds;
			for (int i = 0; i < cloudImage.length; i++)
			{
				WritableImage frame = new WritableImage(image.getPixelReader(), 0, i * fragHeight, (int) image.getWidth(), fragHeight);
				cloudImage[i] = frame;
			}

		} catch (Exception ex) {
			System.out.printf("Fatal Error: Could not load necessary resources!%n%s%n", ex.getMessage());
			System.exit(0);
		}

		scene.setOnKeyPressed(e -> {
			KeyCode code = e.getCode();
			switch (code) {
				case KeyCode.SPACE -> {
					if (running) {
						bird.flap();
					}
				}
				case KeyCode.ESCAPE -> running = !running;
				case KeyCode.UP -> {
					if (!running)
					{
						canvas.menuCursor = Math.max(canvas.menuCursor - 1, 0);
					}
				}
				case KeyCode.DOWN -> {
					if (!running) {
						canvas.menuCursor = Math.min(canvas.menuCursor + 1, 6);
					}
				}
				case KeyCode.RIGHT -> {
					if (!running) {
						switch (canvas.menuCursor) {
							case 2 -> {
								volume = Math.min(volume + 0.1, 1.0);
								setVolume();
							}
							case 4 -> difficulty = Math.min(difficulty + 0.5, 3.0);
						}
					}
				}
				case KeyCode.LEFT -> {
					if (!running) {
						switch (canvas.menuCursor) {
							case 2 -> {
								volume = Math.max(volume - 0.1, 0.0);
								setVolume();
							}
							case 4 -> difficulty = Math.max(difficulty - 0.5, 0.0);
						}
					}
				}
				case KeyCode.ENTER -> {
					if (!running) {
						switch (canvas.menuCursor) {
							case 0 -> reset();
							case 1 -> System.exit(0);
							case 3 -> randomGaps = !randomGaps;
							case 5 -> ramping = !ramping;
							case 6 -> debug = !debug;
						}
					}
				}
			}
		});

		canvas.setOnMousePressed(e -> {
			switch (e.getButton()) {
				case MouseButton.PRIMARY -> firing = true;
			}
		});

		canvas.setOnMouseReleased(e -> {
			switch (e.getButton()) {
				case MouseButton.PRIMARY -> firing = false;
			}
		});

		canvas.setOnMouseMoved(e -> {
			mouseX = e.getX();
			mouseY = e.getY();
		});

		canvas.setOnMouseDragged(e -> {
			mouseX = e.getX();
			mouseY = e.getY();
		});
	}

	public void playClip(String key) {
		new Thread(() -> {
			if (soundFX.containsKey(key)) {
				Clip clip = soundFX.get(key);
				if (clip.isRunning()) {
					clip.stop();  // Optional: stop if still playing
				}
				clip.setFramePosition(0);  // Rewind to beginning
				clip.start();              // Play again
			}
		}).start();
	}

	private void setVolume() {
		new Thread(() -> {
			for (Clip clip : soundFX.values()) {
				FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				gain.setValue(20f * (float) Math.log10(Math.max(volume, 0.0001f)));
			}
		}).start();
	}

	public void reset()
	{
		if (score > highScore)
		{
			highScore = score;
			try {
				File scoreFile = new File("score.txt");
				PrintWriter pw = new PrintWriter(scoreFile);
				pw.write(String.format("%d%n", highScore));
				pw.close();
			} catch (Exception ex) {
				System.out.printf("Error: failed to save high score to file!%n%s%n", ex.getMessage());
			}
		}

		bird.reset();
		pipes = new Pipe[5];
		Pipe p = new Pipe(this, (int)(Util.SCREEN_HEIGHT / 2.0), pipeWidth, pipeHeight, false);
		pipes[0] = p;
		pipeCount = 1;
		score = 0;

		clouds = new ArrayList<>();
		nextCloud = 0.0;
		cloudCounter = 0.0;

		fireCounter = fireRate;
		bricks = new ArrayList<>();

		running = true;
	}

	public void update(double delta) {
		if (running)
		{
			fireCounter += delta;
			if (firing && fireCounter >= fireRate)
			{
				// spawn new bullet and add to bullet array
				Bullet b = new Bullet(this, bird.xPos+bird.width/2, bird.yPos+bird.height/2, mouseX, mouseY);
				bullets.add(b);
				fireCounter = 0.0;
			}
			List<Bullet> deadBullets = new ArrayList<>();
			for (Bullet bullet : bullets) {
				bullet.update(delta);
				for (Pipe pipe : pipes) {
					if (pipe == null)
						continue;
					bullet.collide(pipe);
				}
				for (Bricks brick: bricks) {
					bullet.collide(brick);
				}
				if (!bullet.isAlive) {
					deadBullets.add(bullet);
				}
			}
			for (Bullet bullet : deadBullets) {
				bullets.remove(bullet);
			}

			cloudCounter += delta;
			if (cloudCounter >= nextCloud)
			{
				Cloud c = new Cloud(this);
				clouds.add(c);
				nextCloud = Math.random() * cloudRate;
				cloudCounter = 0.0;
			}
			List<Cloud> passedClouds = new ArrayList<>();
			for (Cloud cloud : clouds) {
				cloud.update(delta);
				if (cloud.passed) {
					passedClouds.add(cloud);
				}
			}
			for (Cloud cloud : passedClouds) {
				clouds.remove(cloud);
			}

			bird.update(delta);
			List<Bricks> deadBricks = new ArrayList<>();
			for (Bricks brick : bricks) {
				brick.update(delta);
				if (brick.health <= 0) {
					deadBricks.add(brick);
				}
			}
			for (Bricks brick : deadBricks) {
				bricks.remove(brick);
			}
			for (Bricks brick : bricks) {
				if (bird.collide(brick)) {
					running = !running;
				}
			}

			for (int i = 0; i < pipes.length; i++) {
				if (pipes[i] == null)
					continue;

				if (pipes[i].update(delta)) {
					score += 1;

					if (ramping && score % 10 == 0)
					{
						difficulty += 0.5;
						difficulty = Math.min(difficulty, 3.0);
					}

					if (score > highScore)
					{
						new Thread(() -> {
							highScore = score;
							running = true;
							try {
								File scoreFile = new File("score.txt");
								PrintWriter pw = new PrintWriter(scoreFile);
								pw.write(String.format("%d%n", highScore));
								pw.close();
							} catch (Exception ex) {
								System.out.printf("Error: failed to save high score to file!%n%s%n", ex.getMessage());
							}
						}).start();
					}

					playClip("score");
				}

				if (pipes[i].spawnable && pipes[i].xPos < 3 * Util.SCREEN_WIDTH / 4) {
					pipes[i].spawnable = false;
					int min = (int)(Util.SCREEN_HEIGHT / 4.0);
					int range = min * 2;
					int y = (int) (Math.random() * range) + min;
					boolean spawnBricks = Math.random() < brickRate;

					Pipe pipe = new Pipe(this, y, pipeWidth, pipeHeight, spawnBricks);
					pipes[pipeCount] = pipe;
					pipeCount++;
					if (pipeCount >= pipes.length)
						pipeCount = 0;
				}

				if (bird.collide(pipes[i])) {
					running = !running;
				}
			}
		}
	}
}
