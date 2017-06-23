package de.haw.md.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

public class NetworkGuiMain extends Application {
	private Label label;
    @Override 
    public void init() {        
             label = new Label("Hello World"); 
     }

	@Override
	public void start(Stage stage) {
		StackPane root = new StackPane();
		root.getChildren().add(label);
		Scene scene = new Scene(root, 200, 200);
		stage.setTitle("Hello World Example");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
	}

	public static void main(String[] parameters) {
		launch(parameters);
	}

}
