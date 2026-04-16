package com.datavisualizer.chart;

import javafx.scene.chart.Chart;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class HeatMapChart extends Chart {
    private List<String> xLabels = new ArrayList<>();
    private List<String> yLabels = new ArrayList<>();
    private double[][] data;
    private GridPane gridPane;
    
    public HeatMapChart() {
        gridPane = new GridPane();
        gridPane.setHgap(2);
        gridPane.setVgap(2);
        getChartChildren().add(gridPane);
    }
    
    public void setXLabels(List<String> labels) {
        this.xLabels = labels;
    }
    
    public void setYLabels(List<String> labels) {
        this.yLabels = labels;
    }
    
    public void setData(double[][] data) {
        this.data = data;
        drawChart();
    }
    
    @Override
    protected void layoutChartChildren(double top, double left, double width, double height) {
        if (gridPane != null) {
            gridPane.resizeRelocate(left + 50, top + 50, width - 100, height - 100);
        }
    }
    
    private void drawChart() {
        gridPane.getChildren().clear();
        
        if (data == null || data.length == 0) {
            return;
        }
        
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        
        for (double[] row : data) {
            for (double value : row) {
                if (value < minValue) minValue = value;
                if (value > maxValue) maxValue = value;
            }
        }
        
        double cellWidth = 60;
        double cellHeight = 40;
        
        for (int i = 0; i < xLabels.size(); i++) {
            Text label = new Text(xLabels.get(i));
            label.setStyle("-fx-font-size: 10px;");
            gridPane.add(label, i + 1, 0);
        }
        
        for (int i = 0; i < yLabels.size(); i++) {
            Text label = new Text(yLabels.get(i));
            label.setStyle("-fx-font-size: 10px;");
            gridPane.add(label, 0, i + 1);
        }
        
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                double value = data[row][col];
                double normalized = (value - minValue) / (maxValue - minValue);
                
                Color color = getHeatColor(normalized);
                
                Rectangle rect = new Rectangle(cellWidth, cellHeight);
                rect.setFill(color);
                rect.setStroke(Color.WHITE);
                rect.setStrokeWidth(1);
                
                Text valueText = new Text(String.format("%.1f", value));
                valueText.setFill(normalized > 0.5 ? Color.WHITE : Color.BLACK);
                valueText.setStyle("-fx-font-size: 10px;");
                
                StackPane cell = new StackPane(rect, valueText);
                Tooltip tooltip = new Tooltip(String.format("%s - %s: %.2f", 
                    yLabels.get(row), xLabels.get(col), value));
                Tooltip.install(cell, tooltip);
                
                gridPane.add(cell, col + 1, row + 1);
            }
        }
    }
    
    private Color getHeatColor(double value) {
        if (value < 0.25) {
            return Color.rgb(49, 130, 189);
        } else if (value < 0.5) {
            return Color.rgb(107, 174, 214);
        } else if (value < 0.75) {
            return Color.rgb(253, 174, 97);
        } else {
            return Color.rgb(215, 48, 39);
        }
    }
    
    public GridPane getChartPane() {
        return gridPane;
    }
}
