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
import java.util.HashMap;
import java.util.Map;

public class JFXFlappy extends Application {

	private FXCanvas canvas;

	public Bird bird;

	public Pipe[] pipes = new Pipe[5];
	private int pipeCount = 1;
	private int pipeWidth;
	private int pipeHeight;
	public Image pipeImage;

	private final int numClouds = 21;
	public Image[] cloudImage = new Image[numClouds];
	private final int cloudCap = numClouds - 1;
	public Cloud[] clouds = new Cloud[cloudCap];
	private int cloudCount = 0;
	private final double cloudRate = 500.0;
	private double cloudIndex = 0.0;

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

		Group root = new Group(canvas);  // You can change this to Canvas later
		Scene scene = new Scene(root, Color.CYAN);

		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint(""); // Suppresses the hint entirely
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

			pipeWidth = (int)(Util.SCREEN_WIDTH / 16);
			pipeHeight = (int)(((double)pipeWidth / pipeImage.getWidth()) * pipeImage.getHeight());

			Pipe pipe = new Pipe(this, (int)(Util.SCREEN_HEIGHT / 2), pipeWidth, pipeHeight);
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
						canvas.cursor = Math.max(canvas.cursor - 1, 0);
					}
				}
				case KeyCode.DOWN -> {
					if (!running) {
						canvas.cursor = Math.min(canvas.cursor + 1, 6);
					}
				}
				case KeyCode.RIGHT -> {
					if (!running) {
						switch (canvas.cursor) {
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
						switch (canvas.cursor) {
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
						switch (canvas.cursor) {
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
				case MouseButton.PRIMARY -> {}//firing = true};
				case MouseButton.MIDDLE -> {}
				case MouseButton.SECONDARY -> {}
			}
		});

		canvas.setOnMouseReleased(e -> {
			switch (e.getButton()) {
				case MouseButton.PRIMARY -> {}//firing = false};
				case MouseButton.MIDDLE -> {}
				case MouseButton.SECONDARY -> {}
			}
		});

		canvas.setOnMouseMoved(e -> {
			//mouseX = (int) e.getX();
			//mouseY = (int) e.getY();
		});

		scene.setOnScroll(e -> {
			double deltaY = e.getDeltaY();	// >0 for up, <0 for down
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
			running = true;
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
		Pipe p = new Pipe(this, (int)(Util.SCREEN_HEIGHT / 2.0), pipeWidth, pipeHeight);
		pipes[0] = p;
		pipeCount = 1;
		score = 0;

		clouds = new Cloud[cloudCap];
		cloudCount = 0;

		running = true;
	}

	public void update(double delta) {
		if (running)
		{
			/*
			fireCounter -= 1;
			fireCounter = Math.max(fireCounter, 0);
			if (firing && fireCounter == 0)
			{
				// spawn new bullet and add to bullet array
				fireCounter = 10;
			}
			// for each bullet within drawable space:
			//   perform update
			//   check for collisions
			//   if collide():
			//     do something
			*/

			cloudIndex += Math.random() * delta;
			if (cloudIndex >= cloudRate)
			{
				cloudCount = (cloudCount + 1) % cloudCap;
				if (clouds[cloudCount] == null || clouds[cloudCount].passed)
				{
					Cloud c = new Cloud(this);
					clouds[cloudCount] = c;
				}
			}
			for (Cloud cloud : clouds) {
				if (cloud != null)
					cloud.update(delta);
			}

			bird.update(delta);
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
					Pipe pipe = new Pipe(this, y, pipeWidth, pipeHeight);
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
