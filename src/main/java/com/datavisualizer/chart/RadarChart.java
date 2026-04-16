package com.datavisualizer.chart;

import javafx.scene.chart.Chart;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import com.datavisualizer.util.ColorManager;

import java.util.ArrayList;
import java.util.List;

public class RadarChart extends Chart {
    private ObservableList<Data> data = FXCollections.observableArrayList();
    private List<String> categories = new ArrayList<>();
    private Pane chartPane;
    private double centerX;
    private double centerY;
    private double radius;
    
    public static class Data {
        private String name;
        private List<Double> values;
        
        public Data(String name, List<Double> values) {
            this.name = name;
            this.values = values;
        }
        
        public String getName() { return name; }
        public List<Double> getValues() { return values; }
    }
    
    public RadarChart() {
        chartPane = new Pane();
        chartPane.setMinSize(400, 400);
    }
    
    public void setData(ObservableList<Data> data) {
        this.data = data;
        drawChart();
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    @Override
    protected void layoutChartChildren(double top, double left, double width, double height) {
        if (chartPane != null) {
            chartPane.resizeRelocate(left, top, width, height);
            drawChart();
        }
    }
    
    private void drawChart() {
        chartPane.getChildren().clear();
        
        if (categories.isEmpty() || data.isEmpty()) {
            return;
        }
        
        double width = chartPane.getWidth() > 0 ? chartPane.getWidth() : 400;
        double height = chartPane.getHeight() > 0 ? chartPane.getHeight() : 400;
        
        centerX = width / 2;
        centerY = height / 2;
        radius = Math.min(width, height) / 2 - 60;
        
        drawAxes();
        drawData();
        drawLabels();
    }
    
    private void drawAxes() {
        int numAxes = categories.size();
        double angleStep = 2 * Math.PI / numAxes;
        
        for (int i = 0; i < 5; i++) {
            double r = radius * (i + 1) / 5;
            Polygon polygon = new Polygon();
            
            for (int j = 0; j < numAxes; j++) {
                double angle = j * angleStep - Math.PI / 2;
                double x = centerX + r * Math.cos(angle);
                double y = centerY + r * Math.sin(angle);
                polygon.getPoints().addAll(x, y);
            }
            
            polygon.setFill(Color.TRANSPARENT);
            polygon.setStroke(Color.LIGHTGRAY);
            polygon.setStrokeWidth(1);
            chartPane.getChildren().add(polygon);
        }
        
        for (int i = 0; i < numAxes; i++) {
            double angle = i * angleStep - Math.PI / 2;
            Line line = new Line(
                centerX, centerY,
                centerX + radius * Math.cos(angle),
                centerY + radius * Math.sin(angle)
            );
            line.setStroke(Color.LIGHTGRAY);
            line.setStrokeWidth(1);
            chartPane.getChildren().add(line);
        }
    }
    
    private void drawData() {
        int numAxes = categories.size();
        double angleStep = 2 * Math.PI / numAxes;
        
        int colorIndex = 0;
        for (Data series : data) {
            Polygon polygon = new Polygon();
            
            double maxValue = series.getValues().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            if (maxValue == 0) maxValue = 1.0;
            
            List<Double> pointXCoords = new ArrayList<>();
            List<Double> pointYCoords = new ArrayList<>();
            
            for (int i = 0; i < numAxes && i < series.getValues().size(); i++) {
                double angle = i * angleStep - Math.PI / 2;
                double value = series.getValues().get(i);
                double normalizedValue = value / maxValue;
                double r = radius * normalizedValue;
                
                double x = centerX + r * Math.cos(angle);
                double y = centerY + r * Math.sin(angle);
                polygon.getPoints().addAll(x, y);
                
                pointXCoords.add(x);
                pointYCoords.add(y);
            }
            
            Color baseColor = ColorManager.getRadarColor(colorIndex);
            Color fillColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0.4);
            Color strokeColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0.8);
            
            polygon.setFill(fillColor);
            polygon.setStroke(strokeColor);
            polygon.setStrokeWidth(2.5);
            
            chartPane.getChildren().add(polygon);
            
            // Create invisible circles at each data point with individual tooltips
            for (int i = 0; i < pointXCoords.size(); i++) {
                Circle pointCircle = new Circle(pointXCoords.get(i), pointYCoords.get(i), 6);
                pointCircle.setFill(Color.TRANSPARENT);
                pointCircle.setStroke(Color.TRANSPARENT);
                
                String tooltipText = String.format("%s\n%s: %.2f", 
                    series.getName(), 
                    categories.get(i), 
                    series.getValues().get(i));
                
                Tooltip tooltip = new Tooltip(tooltipText);
                Tooltip.install(pointCircle, tooltip);
                
                chartPane.getChildren().add(pointCircle);
            }
            
            colorIndex++;
        }
    }
    
    private void drawLabels() {
        int numAxes = categories.size();
        double angleStep = 2 * Math.PI / numAxes;
        
        for (int i = 0; i < numAxes; i++) {
            double angle = i * angleStep - Math.PI / 2;
            double labelRadius = radius + 30;
            double x = centerX + labelRadius * Math.cos(angle);
            double y = centerY + labelRadius * Math.sin(angle);
            
            Text label = new Text(categories.get(i));
            label.setX(x - label.getLayoutBounds().getWidth() / 2);
            label.setY(y + label.getLayoutBounds().getHeight() / 4);
            chartPane.getChildren().add(label);
        }
    }
    
    public Pane getChartPane() {
        return chartPane;
    }
}
