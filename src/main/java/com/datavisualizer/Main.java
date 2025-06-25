package com.datavisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load landing page first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LandingView.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Data Visualizer - Welcome");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}