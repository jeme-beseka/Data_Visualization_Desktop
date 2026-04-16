package com.datavisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set application icons (multiple sizes for better visibility)
        try {
            String iconPath = "/images/Data-Visual-Icon.png";
            Image icon32 = new Image(getClass().getResourceAsStream(iconPath), 32, 32, true, true);
            Image icon64 = new Image(getClass().getResourceAsStream(iconPath), 64, 64, true, true);
            Image icon128 = new Image(getClass().getResourceAsStream(iconPath), 128, 128, true, true);
            Image icon256 = new Image(getClass().getResourceAsStream(iconPath), 256, 256, true, true);
            Image icon512 = new Image(getClass().getResourceAsStream(iconPath), 512, 512, true, true);
            
            primaryStage.getIcons().addAll(icon32, icon64, icon128, icon256, icon512);
        } catch (Exception e) {
            System.err.println("Icon not found, continuing without icon: " + e.getMessage());
        }
        
        // Load landing page first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LandingView.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Data Visualizer");
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}