package com.datavisualizer.util;

import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.Node;

public class TooltipManager {
    
    public static <X, Y> void addTooltipToDataPoint(
            XYChart.Data<X, Y> data,
            String tooltipText) {
        
        Node node = data.getNode();
        if (node != null) {
            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.setStyle("-fx-font-size: 12px; -fx-padding: 8px;");
            Tooltip.install(node, tooltip);
            
            node.setOnMouseEntered(event -> {
                tooltip.show(node, event.getScreenX() + 10, event.getScreenY() + 10);
            });
            
            node.setOnMouseExited(event -> {
                tooltip.hide();
            });
        }
    }
    
    public static void addTooltipToNode(
            Node node,
            String tooltipText) {
        
        if (node != null) {
            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.setStyle("-fx-font-size: 12px; -fx-padding: 8px;");
            Tooltip.install(node, tooltip);
            
            node.setOnMouseEntered(event -> {
                tooltip.show(node, event.getScreenX() + 10, event.getScreenY() + 10);
            });
            
            node.setOnMouseExited(event -> {
                tooltip.hide();
            });
        }
    }
    
    public static void installTooltipOnSeries(
            XYChart.Series<?, ?> series,
            java.util.List<String> tooltipTexts) {
        
        for (int i = 0; i < series.getData().size() && i < tooltipTexts.size(); i++) {
            XYChart.Data<?, ?> data = series.getData().get(i);
            Node node = data.getNode();
            if (node != null) {
                addTooltipToNode(node, tooltipTexts.get(i));
            }
        }
    }
}
