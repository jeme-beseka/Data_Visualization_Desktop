package com.datavisualizer.util;

import javafx.scene.Scene;

public class ThemeManager {
    private static boolean isDarkMode = false;
    
    public static void applyTheme(Scene scene) {
        String cssResource = isDarkMode ? "/css/styles-dark.css" : "/css/styles.css";
        scene.getStylesheets().clear();
        scene.getStylesheets().add(ThemeManager.class.getResource(cssResource).toExternalForm());
    }
    
    public static void toggleTheme(Scene scene) {
        isDarkMode = !isDarkMode;
        applyTheme(scene);
    }
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static void setDarkMode(boolean dark, Scene scene) {
        isDarkMode = dark;
        applyTheme(scene);
    }
}
