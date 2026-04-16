package com.datavisualizer.util;

import javafx.scene.paint.Color;
import javafx.scene.chart.XYChart;
import javafx.scene.Node;

public class ColorManager {
    
    public static final Color[] STANDARD_COLORS = {
        Color.rgb(52, 152, 219),    // Blue
        Color.rgb(231, 76, 60),     // Red
        Color.rgb(46, 204, 113),    // Green
        Color.rgb(241, 196, 15),    // Yellow
        Color.rgb(155, 89, 182),    // Purple
        Color.rgb(230, 126, 34),    // Orange
        Color.rgb(52, 73, 94),      // Dark Blue
        Color.rgb(149, 165, 166)    // Gray
    };
    
    public static final Color[] RADAR_COLORS = {
        Color.rgb(255, 107, 107),   // Bright Red
        Color.rgb(66, 165, 245),    // Bright Blue
        Color.rgb(76, 175, 80),     // Bright Green
        Color.rgb(255, 193, 7),     // Bright Yellow/Gold
        Color.rgb(171, 71, 188),    // Bright Purple
        Color.rgb(255, 152, 0),     // Bright Orange
        Color.rgb(233, 30, 99),     // Bright Pink
        Color.rgb(0, 188, 212)      // Bright Cyan
    };
    
    public static Color getStandardColor(int index) {
        return STANDARD_COLORS[index % STANDARD_COLORS.length];
    }
    
    public static Color getRadarColor(int index) {
        return RADAR_COLORS[index % RADAR_COLORS.length];
    }
    
    public static String toRGBString(Color color) {
        return String.format("rgb(%d, %d, %d)",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    public static String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    public static <X, Y> void applyColorToSeries(
            XYChart.Series<X, Y> series,
            Color color,
            boolean isLine) {
        
        String colorHex = toHexString(color);
        
        javafx.application.Platform.runLater(() -> {
            for (XYChart.Data<X, Y> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    if (isLine) {
                        node.setStyle("-fx-stroke: " + colorHex + "; -fx-stroke-width: 2;");
                    } else {
                        node.setStyle("-fx-fill: " + colorHex + ";");
                    }
                }
            }
        });
    }
    
    public static void applyColorToLineChartSeries(
            XYChart.Series<?, ?> series,
            Color color) {
        
        String colorHex = toHexString(color);
        
        javafx.application.Platform.runLater(() -> {
            Node seriesNode = series.getNode();
            if (seriesNode != null) {
                seriesNode.setStyle("-fx-stroke: " + colorHex + ";");
            }
            
            for (XYChart.Data<?, ?> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-fill: " + colorHex + "; -fx-stroke: " + colorHex + ";");
                }
            }
        });
    }
    
    public static void applyColorToScatterChartSeries(
            XYChart.Series<?, ?> series,
            Color color) {
        
        String colorHex = toHexString(color);
        
        javafx.application.Platform.runLater(() -> {
            for (XYChart.Data<?, ?> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-fill: " + colorHex + "; -fx-stroke: " + colorHex + "; -fx-stroke-width: 1.5;");
                }
            }
            
            Node seriesNode = series.getNode();
            if (seriesNode != null) {
                seriesNode.setStyle("-fx-stroke: " + colorHex + "; -fx-fill: " + colorHex + ";");
            }
        });
    }
    
    public static void applyColorToBarChartSeries(
            XYChart.Series<?, ?> series,
            Color color) {
        
        String colorHex = toHexString(color);
        
        javafx.application.Platform.runLater(() -> {
            for (XYChart.Data<?, ?> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: " + colorHex + ";");
                }
            }
        });
    }
}
