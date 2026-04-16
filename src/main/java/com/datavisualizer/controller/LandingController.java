package com.datavisualizer.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class LandingController {
    @FXML private Button getStartedButton;
    
    @FXML
    private void handleGetStarted() {
        try {
            // Load the main application view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Get current stage and set new scene
            Stage stage = (Stage) getStartedButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("Data Visualizer");
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleButtonHover() {
        getStartedButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 40 15 40; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);");
    }
    
    @FXML
    private void handleButtonExit() {
        getStartedButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 40 15 40; -fx-background-radius: 8; -fx-cursor: hand;");
    }
}