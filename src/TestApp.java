package src;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TestApp extends Application {
	@Override
	public void start(Stage stage) {
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setTitle("JavaFX Flappy Bird");

		Pane root = new Pane();  // You can change this to Canvas later
		Scene scene = new Scene(root, Color.CYAN);

		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.show();
	}
}
