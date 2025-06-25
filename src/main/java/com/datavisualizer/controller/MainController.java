package com.datavisualizer.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class MainController {
    @FXML
    private void handleReset() {
        // Clear data
        csvData = null;
        headers = null;
        currentChart = null;
        
        // Clear combo boxes
        xAxisCombo.getItems().clear();
        yAxisCombo.getItems().clear();
        chartTypeCombo.getSelectionModel().selectFirst();
        
        // Clear chart container
        chartContainer.getChildren().clear();
        
        showAlert(Alert.AlertType.INFORMATION, "Reset", "Application reset successfully. All data and charts have been cleared.");
    }
    
    @FXML private Button loadButton;
    @FXML private Button manualInputButton;
    @FXML private Button resetButton;
    @FXML private ComboBox<String> chartTypeCombo;
    @FXML private ComboBox<String> xAxisCombo;
    @FXML private ComboBox<String> yAxisCombo;
    @FXML private StackPane chartContainer;
    
    private List<String[]> csvData;
    private String[] headers;
    private Chart currentChart;
    
    @FXML
    public void initialize() {
        chartTypeCombo.getItems().addAll(
            "Line Chart",
            "Bar Chart",
            "Scatter Plot",
            "Area Chart",
            "Pie Chart"
        );
        chartTypeCombo.getSelectionModel().selectFirst();
        
        // Set minimum size for the chart container
        chartContainer.setMinSize(600, 400);
        chartContainer.setStyle("-fx-background-color: white;");
    }
    
    //Upload CSV function
    @FXML
    private void handleLoadCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
        if (file != null) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> allData = reader.readAll();
                if (!allData.isEmpty()) {
                    headers = allData.get(0);
                    csvData = allData.subList(1, allData.size());
                    
                    // Clean headers (trim whitespace)
                    for (int i = 0; i < headers.length; i++) {
                        headers[i] = headers[i].trim();
                    }
                    
                    xAxisCombo.getItems().clear();
                    yAxisCombo.getItems().clear();
                    xAxisCombo.getItems().addAll(headers);
                    yAxisCombo.getItems().addAll(headers);
                    
                    xAxisCombo.getSelectionModel().selectFirst();
                    if (headers.length > 1) {
                        yAxisCombo.getSelectionModel().select(1);
                    }
                    
                    // Show success message
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "CSV file loaded successfully!\nRows: " + csvData.size() + 
                             "\nColumns: " + headers.length);
                }
            } catch (IOException | CsvException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load CSV file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    //Manual data entry code**
    @FXML
    private void handleManualInput() {
        ManualDataInputDialog dialog = new ManualDataInputDialog();
        dialog.showAndWait().ifPresent(result -> {
            if (result.getHeaders() != null && result.getData() != null && 
                !result.getHeaders().isEmpty() && !result.getData().isEmpty()) {
                
                // Convert to the format expected by the rest of the application
                headers = result.getHeaders().toArray(new String[0]);
                csvData = new ArrayList<>();
                
                for (List<String> row : result.getData()) {
                    csvData.add(row.toArray(new String[0]));
                }
                
                // Update combo boxes
                xAxisCombo.getItems().clear();
                yAxisCombo.getItems().clear();
                xAxisCombo.getItems().addAll(headers);
                yAxisCombo.getItems().addAll(headers);
                
                xAxisCombo.getSelectionModel().selectFirst();
                if (headers.length > 1) {
                    yAxisCombo.getSelectionModel().select(1);
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Data entered successfully!\nRows: " + csvData.size() + 
                         "\nColumns: " + headers.length);
            }
        });
    }
    
    // Method to  select graph and X and Y input**
    @FXML
    private void handlePlot() {
        if (csvData == null || xAxisCombo.getValue() == null || yAxisCombo.getValue() == null) {
            return;
        }
        
        int xIndex = getColumnIndex(xAxisCombo.getValue());
        int yIndex = getColumnIndex(yAxisCombo.getValue());
        
        if (xIndex == -1 || yIndex == -1) {
            return;
        }
        
        String chartType = chartTypeCombo.getValue();
        switch (chartType) {
            case "Line Chart":
                createLineChart(xIndex, yIndex);
                break;
            case "Bar Chart":
                createBarChart(xIndex, yIndex);
                break;
            case "Scatter Plot":
                createScatterPlot(xIndex, yIndex);
                break;
            case "Area Chart":
                createAreaChart(xIndex, yIndex);
                break;
            case "Pie Chart":
                createPieChart(xIndex, yIndex);
                break;
        }
    }
    
    @FXML
    private void handleSavePlot() {
        if (currentChart == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No chart to save. Please create a chart first.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        
        File file = fileChooser.showSaveDialog(chartContainer.getScene().getWindow());
        if (file != null) {
            try {
                WritableImage image = currentChart.snapshot(null, null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Chart saved successfully to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save chart: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private int getColumnIndex(String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean isNumericColumn(int columnIndex) {
        for (String[] row : csvData) {
            if (row.length > columnIndex) {
                try {
                    Double.parseDouble(row[columnIndex].trim());
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return true;
    }
    
    //Line chart Implementation
    private void createLineChart(int xIndex, int yIndex) {
        // Determine if x-axis should be numeric or categorical
        boolean xIsNumeric = isNumericColumn(xIndex);
        
        if (xIsNumeric) {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        double xValue = Double.parseDouble(row[xIndex].trim());
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                    } catch (NumberFormatException e) {
                        // Skip invalid data
                    }
                }
            }
            lineChart.getData().add(series);
            lineChart.setTitle("Line Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            lineChart.setCreateSymbols(true);
            lineChart.setLegendVisible(false);
            updateChartView(lineChart);
        } else {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        String xValue = row[xIndex].trim();
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                    } catch (NumberFormatException e) {
                        // Skip invalid data
                    }
                }
            }
            lineChart.getData().add(series);
            lineChart.setTitle("Line Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            lineChart.setCreateSymbols(true);
            lineChart.setLegendVisible(false);
            updateChartView(lineChart);
        }
    }
    
//Bar chart implementation
    private void createBarChart(int xIndex, int yIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(headers[xIndex]);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[yIndex]);
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Bar Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(headers[yIndex]);
        
        // Group data by x-axis values and sum y-values
        Map<String, Double> dataMap = new HashMap<>();
        for (String[] row : csvData) {
            if (row.length > Math.max(xIndex, yIndex)) {
                try {
                    String xValue = row[xIndex].trim();
                    double yValue = Double.parseDouble(row[yIndex].trim());
                    dataMap.merge(xValue, yValue, Double::sum);
                } catch (NumberFormatException e) {
                    // Skip invalid data
                }
            }
        }
        
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        
        updateChartView(barChart);
    }
    
    //Scatter Plot Implementation
    private void createScatterPlot(int xIndex, int yIndex) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(headers[xIndex]);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[yIndex]);
        
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Scatter Plot: " + headers[xIndex] + " vs " + headers[yIndex]);
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Points");
        
        for (String[] row : csvData) {
            if (row.length > Math.max(xIndex, yIndex)) {
                try {
                    double xValue = Double.parseDouble(row[xIndex].trim());
                    double yValue = Double.parseDouble(row[yIndex].trim());
                    series.getData().add(new XYChart.Data<>(xValue, yValue));
                } catch (NumberFormatException e) {
                    // Skip invalid data
                }
            }
        }
        
        scatterChart.getData().add(series);
        scatterChart.setLegendVisible(false);
        
        updateChartView(scatterChart);
    }
    
    //Area Chart Implementation
    private void createAreaChart(int xIndex, int yIndex) {
        // Determine if x-axis should be numeric or categorical
        boolean xIsNumeric = isNumericColumn(xIndex);
        
        if (xIsNumeric) {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            AreaChart<Number, Number> areaChart = new AreaChart<>(xAxis, yAxis);
            
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        double xValue = Double.parseDouble(row[xIndex].trim());
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                    } catch (NumberFormatException e) {
                        // Skip invalid data
                    }
                }
            }
            areaChart.getData().add(series);
            areaChart.setTitle("Area Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            updateChartView(areaChart);
        } else {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        String xValue = row[xIndex].trim();
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                    } catch (NumberFormatException e) {
                        // Skip invalid data
                    }
                }
            }
            areaChart.getData().add(series);
            areaChart.setTitle("Area Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            updateChartView(areaChart);
        }
    }
    
    //Pie Chart Implementation
    private void createPieChart(int xIndex, int yIndex) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Pie Chart: " + headers[xIndex] + " (Values: " + headers[yIndex] + ")");
        
        // Group data by x-axis values and sum y-values
        Map<String, Double> dataMap = new HashMap<>();
        for (String[] row : csvData) {
            if (row.length > Math.max(xIndex, yIndex)) {
                try {
                    String xValue = row[xIndex].trim();
                    double yValue = Double.parseDouble(row[yIndex].trim());
                    dataMap.merge(xValue, yValue, Double::sum);
                } catch (NumberFormatException e) {
                    // Skip invalid data
                }
            }
        }
        
        // Convert to pie chart data
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            if (entry.getValue() > 0) { // Only include positive values
                pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        
        // Configure pie chart
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(90);
        
        updateChartView(pieChart);
    }
    
    private void updateChartView(Chart chart) {
        currentChart = chart;
        
        // Configure chart appearance
        chart.setAnimated(true);
        chart.setPrefSize(chartContainer.getWidth(), chartContainer.getHeight());
        
        // Bind chart size to container size
        chart.prefWidthProperty().bind(chartContainer.widthProperty());
        chart.prefHeightProperty().bind(chartContainer.heightProperty());
        
        // Clear container and add new chart
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(chart);
    }
    
    // Inner class for manual data input result
    public static class DataInputResult {
        private List<String> headers;
        private List<List<String>> data;
        
        public DataInputResult(List<String> headers, List<List<String>> data) {
            this.headers = headers;
            this.data = data;
        }
        
        public List<String> getHeaders() { return headers; }
        public List<List<String>> getData() { return data; }
    }
    
    // Manual data input dialog class
    private static class ManualDataInputDialog extends Dialog<DataInputResult> {
        private TableView<ObservableList<String>> table;
        private TextField columnsField;
        private TextField rowsField;
        
        public ManualDataInputDialog() {
            setTitle("Manual Data Input");
            setHeaderText("Enter your data manually");
            
            // Create the dialog content
            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            
            // Size input section
            HBox sizeBox = new HBox(10);
            sizeBox.getChildren().addAll(
                new Label("Columns:"),
                columnsField = new TextField("3"),
                new Label("Rows:"),
                rowsField = new TextField("5"),
                createSizeButton()
            );
            
            columnsField.setPrefWidth(60);
            rowsField.setPrefWidth(60);
            
            // Table for data input
            table = new TableView<>();
            table.setEditable(true);
            table.setPrefHeight(300);
            
            content.getChildren().addAll(sizeBox, table);
            
            getDialogPane().setContent(content);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            // Initialize with default size
            createTable(3, 5);
            
            // Set result converter
            setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return extractData();
                }
                return null;
            });
        }
        
        private Button createSizeButton() {
            Button button = new Button("Create Table");
            button.setOnAction(e -> {
                try {
                    int cols = Integer.parseInt(columnsField.getText());
                    int rows = Integer.parseInt(rowsField.getText());
                    if (cols > 0 && rows > 0 && cols <= 20 && rows <= 100) {
                        createTable(cols, rows);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Invalid Input", 
                                "Please enter valid numbers (1-20 columns, 1-100 rows)");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numbers");
                }
            });
            return button;
        }
        
        private void createTable(int columns, int rows) {
            table.getColumns().clear();
            table.getItems().clear();
            
            // Create columns
            for (int i = 0; i < columns; i++) {
                final int colIndex = i;
                TableColumn<ObservableList<String>, String> column = 
                    new TableColumn<>("Column " + (i + 1));
                
                column.setCellValueFactory(param -> {
                    ObservableList<String> row = param.getValue();
                    if (colIndex < row.size()) {
                        return new javafx.beans.property.SimpleStringProperty(row.get(colIndex));
                    }
                    return new javafx.beans.property.SimpleStringProperty("");
                });
                
                column.setCellFactory(col -> new TableCell<ObservableList<String>, String>() {
                    private TextField textField;
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            if (textField == null) {
                                textField = new TextField();
                                textField.setOnAction(e -> commitEdit(textField.getText()));
                                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                                    if (!isNowFocused) {
                                        commitEdit(textField.getText());
                                    }
                                });
                            }
                            textField.setText(item == null ? "" : item);
                            setGraphic(textField);
                        }
                    }
                    
                    @Override
                    public void commitEdit(String newValue) {
                        super.commitEdit(newValue);
                        ObservableList<String> row = getTableView().getItems().get(getIndex());
                        if (colIndex < row.size()) {
                            row.set(colIndex, newValue);
                        }
                    }
                });
                
                column.setEditable(true);
                column.setPrefWidth(120);
                table.getColumns().add(column);
            }
            
            // Create rows
            for (int i = 0; i < rows; i++) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int j = 0; j < columns; j++) {
                    row.add(i == 0 ? "Header " + (j + 1) : "");
                }
                table.getItems().add(row);
            }
        }
        
        private DataInputResult extractData() {
            if (table.getItems().isEmpty()) {
                return null;
            }
            
            List<String> headers = new ArrayList<>();
            List<List<String>> data = new ArrayList<>();
            
            // Extract headers from first row
            ObservableList<String> headerRow = table.getItems().get(0);
            for (String header : headerRow) {
                headers.add(header == null ? "" : header.trim());
            }
            
            // Extract data from remaining rows
            for (int i = 1; i < table.getItems().size(); i++) {
                ObservableList<String> tableRow = table.getItems().get(i);
                List<String> dataRow = new ArrayList<>();
                for (String cell : tableRow) {
                    dataRow.add(cell == null ? "" : cell.trim());
                }
                // Only add non-empty rows
                if (dataRow.stream().anyMatch(cell -> !cell.isEmpty())) {
                    data.add(dataRow);
                }
            }
            
            return new DataInputResult(headers, data);
        }
        
        private void showAlert(Alert.AlertType type, String title, String message) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}