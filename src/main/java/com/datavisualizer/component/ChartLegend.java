package com.datavisualizer.component;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import java.util.List;
import java.util.ArrayList;

public class ChartLegend extends VBox {
    private List<LegendItem> items = new ArrayList<>();
    private static final Color[] DEFAULT_COLORS = {
        Color.rgb(52, 152, 219),   // Blue
        Color.rgb(231, 76, 60),    // Red
        Color.rgb(46, 204, 113),   // Green
        Color.rgb(241, 196, 15),   // Yellow
        Color.rgb(155, 89, 182),   // Purple
        Color.rgb(230, 126, 34),   // Orange
        Color.rgb(52, 73, 94),     // Dark Blue
        Color.rgb(149, 165, 166)   // Gray
    };
    
    public ChartLegend() {
        setSpacing(8);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }
    
    public void addLegendItem(String label, Color color) {
        LegendItem item = new LegendItem(label, color);
        items.add(item);
        getChildren().add(item);
    }
    
    public void addLegendItems(List<String> labels) {
        clear();
        for (int i = 0; i < labels.size(); i++) {
            Color color = DEFAULT_COLORS[i % DEFAULT_COLORS.length];
            addLegendItem(labels.get(i), color);
        }
    }
    
    public void clear() {
        items.clear();
        getChildren().clear();
    }
    
    public Color getColor(int index) {
        if (index < DEFAULT_COLORS.length) {
            return DEFAULT_COLORS[index];
        }
        return DEFAULT_COLORS[index % DEFAULT_COLORS.length];
    }
    
    public static class LegendItem extends HBox {
        private Label label;
        private Rectangle colorBox;
        
        public LegendItem(String labelText, Color color) {
            setSpacing(8);
            setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            // Color swatch
            colorBox = new Rectangle(12, 12);
            colorBox.setFill(color);
            colorBox.setStroke(Color.gray(0.8));
            colorBox.setStrokeWidth(1);
            
            // Label
            label = new Label(labelText);
            label.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
            
            getChildren().addAll(colorBox, label);
        }
        
        public void setColor(Color color) {
            colorBox.setFill(color);
        }
        
        public void setLabel(String labelText) {
            label.setText(labelText);
        }
    }
}
