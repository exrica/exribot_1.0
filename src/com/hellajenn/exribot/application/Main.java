package com.hellajenn.exribot.application;
	
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private static Controller controller;
	private static Parent root;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.load((getClass().getResource("MainView.fxml")));
			Scene scene = new Scene(root);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("ExriBot");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		controller.closeApplication();
	}
	
	public static void setController(Controller c) {
		controller = c;
	}
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Parent getRoot() {
		return root;
	}
	
	

}
