package com.datavisualizer.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.datavisualizer.util.StatisticsCalculator;
import com.datavisualizer.util.ThemeManager;
import com.datavisualizer.util.ChartDataHelper;
import com.datavisualizer.util.TooltipManager;
import com.datavisualizer.util.ColorManager;
import com.datavisualizer.chart.RadarChart;
import com.datavisualizer.chart.HeatMapChart;
import com.datavisualizer.component.ChartLegend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.ChoiceDialog;
import java.util.Optional;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;

public class MainController {
    @FXML private Button loadButton;
    @FXML private Button manualInputButton;
    @FXML private Button resetButton;
    @FXML private Button plotButton;
    @FXML private Button saveButton;
    @FXML private Button themeButton;
    @FXML private ComboBox<String> chartTypeCombo;
    @FXML private ComboBox<String> xAxisCombo;
    @FXML private ComboBox<String> yAxisCombo;
    @FXML private StackPane chartContainer;
    @FXML private Label dataRowsLabel;
    @FXML private Label dataColumnsLabel;
    @FXML private Label dataStatusLabel;
    @FXML private TableView<ObservableList<String>> dataPreviewTable;
    @FXML private VBox statsContainer;
    @FXML private Label statusLabel;
    @FXML private ColorPicker chartColorPicker;
    @FXML private VBox legendContainer;
    @FXML private ScrollPane legendScrollPane;
    @FXML private VBox legendContent;
    
    private List<String[]> csvData;
    private String[] headers;
    private Chart currentChart;
    private Color chartColor = Color.rgb(52, 152, 219);
    
    @FXML
    public void initialize() {
        chartTypeCombo.getItems().addAll(
            "Line Chart",
            "Bar Chart",
            "Scatter Plot",
            "Area Chart",
            "Pie Chart",
            "Histogram",
            "Box Plot",
            "Bubble Chart",
            "Heatmap",
            "Radar Chart",
            "Radar Chart (vs Average)"
        );
        chartTypeCombo.getSelectionModel().selectFirst();
        
        chartContainer.setMinSize(600, 400);
        
        // Initialize color picker if present
        if (chartColorPicker != null) {
            chartColorPicker.setValue(chartColor);
            chartColorPicker.setOnAction(e -> {
                chartColor = chartColorPicker.getValue();
                updateStatus("Chart color changed");
            });
        }
        
        // Add keyboard shortcuts after scene is loaded
        chartContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyboardShortcuts);
                // Apply theme to chart container
                updateChartContainerTheme();
            }
        });
    }
    
    private void handleKeyboardShortcuts(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case O:
                    handleLoadCSV();
                    event.consume();
                    break;
                case S:
                    handleSavePlot();
                    event.consume();
                    break;
                case R:
                    handleReset();
                    event.consume();
                    break;
                default:
                    break;
            }
        }
    }
    
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
                    
                    updateDataPreview();
                    updateStatistics();
                    updateStatus("CSV loaded: " + csvData.size() + " rows, " + headers.length + " columns");
                    
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
    
    @FXML
    private void handleManualInput() {
        ManualDataInputDialog dialog = new ManualDataInputDialog();
        dialog.showAndWait().ifPresent(result -> {
            if (result.getHeaders() != null && result.getData() != null && 
                !result.getHeaders().isEmpty() && !result.getData().isEmpty()) {
                
                headers = result.getHeaders().toArray(new String[0]);
                csvData = new ArrayList<>();
                
                for (List<String> row : result.getData()) {
                    csvData.add(row.toArray(new String[0]));
                }
                
                xAxisCombo.getItems().clear();
                yAxisCombo.getItems().clear();
                xAxisCombo.getItems().addAll(headers);
                yAxisCombo.getItems().addAll(headers);
                
                xAxisCombo.getSelectionModel().selectFirst();
                if (headers.length > 1) {
                    yAxisCombo.getSelectionModel().select(1);
                }
                
                updateDataPreview();
                updateStatistics();
                updateStatus("Manual data entered: " + csvData.size() + " rows, " + headers.length + " columns");
                
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Data entered successfully!\nRows: " + csvData.size() + 
                         "\nColumns: " + headers.length);
            }
        });
    }
    
    @FXML
    private void handlePlot() {
        if (csvData == null || xAxisCombo.getValue() == null || yAxisCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please load data and select axes first.");
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
            case "Histogram":
                createHistogram(yIndex);
                break;
            case "Box Plot":
                createBoxPlot(yIndex);
                break;
            case "Bubble Chart":
                createBubbleChart(xIndex, yIndex);
                break;
            case "Heatmap":
                createHeatmap();
                break;
            case "Radar Chart":
                createRadarChart();
                break;
            case "Radar Chart (vs Average)":
                createRadarChartVsAverage();
                break;
        }
        updateStatus("Chart generated: " + chartType);
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
                updateStatus("Chart saved to: " + file.getName());
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Chart saved successfully to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save chart: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleThemeToggle() {
        Scene scene = chartContainer.getScene();
        ThemeManager.toggleTheme(scene);
        updateChartContainerTheme();
        updateStatus("Theme toggled");
    }
    
    private void updateChartContainerTheme() {
        if (ThemeManager.isDarkMode()) {
            chartContainer.setStyle("-fx-background-color: #2d2d2d;");
            legendContainer.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #404040; -fx-border-width: 0 0 0 1;");
        } else {
            chartContainer.setStyle("-fx-background-color: white;");
            legendContainer.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 0 1;");
        }
    }
    
    private void clearLegend() {
        legendContent.getChildren().clear();
    }
    
    private void addLegendItem(String label, Color color) {
        ChartLegend.LegendItem item = new ChartLegend.LegendItem(label, color);
        legendContent.getChildren().add(item);
    }
    
    private void createLegendForRadarChart(List<String> playerNames) {
        clearLegend();
        for (int i = 0; i < playerNames.size(); i++) {
            Color color = ColorManager.getRadarColor(i);
            addLegendItem(playerNames.get(i), color);
        }
    }
    
    private void createLegendForRadarVsAverage(String playerName) {
        clearLegend();
        addLegendItem(playerName, ColorManager.getRadarColor(0));
        addLegendItem("Average", ColorManager.getRadarColor(1));
    }
    
    private String toRGBCode(Color color) {
        return String.format("rgb(%d, %d, %d)",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    private void applyChartColor(Chart chart) {
        String colorString = toRGBCode(chartColor);
        chart.lookup(".chart-series-line").setStyle("-fx-stroke: " + colorString + ";");
        for (Node node : chart.lookupAll(".chart-bar")) {
            node.setStyle("-fx-bar-fill: " + colorString + ";");
        }
        for (Node node : chart.lookupAll(".chart-line-symbol")) {
            node.setStyle("-fx-background-color: " + colorString + ", white;");
        }
        for (Node node : chart.lookupAll(".chart-area-symbol")) {
            node.setStyle("-fx-background-color: " + colorString + ", white;");
        }
    }
    
    @FXML
    private void handleReset() {
        csvData = null;
        headers = null;
        currentChart = null;
        
        xAxisCombo.getItems().clear();
        yAxisCombo.getItems().clear();
        chartTypeCombo.getSelectionModel().selectFirst();
        
        chartContainer.getChildren().clear();
        dataPreviewTable.getColumns().clear();
        dataPreviewTable.getItems().clear();
        statsContainer.getChildren().clear();
        
        dataRowsLabel.setText("Rows: 0");
        dataColumnsLabel.setText("Columns: 0");
        dataStatusLabel.setText("Status: No data loaded");
        dataStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        
        updateStatus("Application reset");
        
        showAlert(Alert.AlertType.INFORMATION, "Reset", "Application reset successfully. All data and charts have been cleared.");
    }
    
    private void updateDataPreview() {
        if (csvData == null || headers == null) return;
        
        dataPreviewTable.getColumns().clear();
        dataPreviewTable.getItems().clear();
        
        for (int i = 0; i < headers.length; i++) {
            final int colIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);
            column.setCellValueFactory(param -> {
                ObservableList<String> row = param.getValue();
                if (colIndex < row.size()) {
                    return new javafx.beans.property.SimpleStringProperty(row.get(colIndex));
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
            column.setPrefWidth(100);
            dataPreviewTable.getColumns().add(column);
        }
        
        int previewRows = Math.min(5, csvData.size());
        for (int i = 0; i < previewRows; i++) {
            String[] row = csvData.get(i);
            ObservableList<String> rowData = FXCollections.observableArrayList();
            for (String cell : row) {
                rowData.add(cell);
            }
            dataPreviewTable.getItems().add(rowData);
        }
        
        dataRowsLabel.setText("Rows: " + csvData.size());
        dataColumnsLabel.setText("Columns: " + headers.length);
        dataStatusLabel.setText("Status: Data loaded");
        dataStatusLabel.setStyle("-fx-text-fill: #27ae60;");
    }
    
    private void updateStatistics() {
        if (csvData == null || headers == null) return;
        
        statsContainer.getChildren().clear();
        
        for (int i = 0; i < headers.length; i++) {
            StatisticsCalculator.ColumnStats stats = StatisticsCalculator.calculateStats(headers[i], csvData, i);
            
            VBox statBox = new VBox(4);
            statBox.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0; -fx-padding: 8 0 8 0;");
            
            Label nameLabel = new Label(stats.columnName);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
            statBox.getChildren().add(nameLabel);
            
            if (stats.isNumeric) {
                Label minLabel = new Label(String.format("Min: %.2f", stats.min));
                Label maxLabel = new Label(String.format("Max: %.2f", stats.max));
                Label meanLabel = new Label(String.format("Mean: %.2f", stats.mean));
                
                minLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                maxLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                meanLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                
                statBox.getChildren().addAll(minLabel, maxLabel, meanLabel);
            } else {
                Label typeLabel = new Label("Type: Categorical");
                typeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                statBox.getChildren().add(typeLabel);
            }
            
            statsContainer.getChildren().add(statBox);
        }
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
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
    
    private void createLineChart(int xIndex, int yIndex) {
        if (headers.length < 3) {
            createLineChartSingleSeries(xIndex, yIndex);
            return;
        }
        
        List<String> inputNames = ChartDataHelper.getUniqueInputNames(csvData, 0);
        
        if (inputNames.size() <= 1) {
            createLineChartSingleSeries(xIndex, yIndex);
            return;
        }
        
        SeriesSelectionDialog dialog = new SeriesSelectionDialog(inputNames);
        Optional<List<String>> result = dialog.showAndWait();
        
        if (result.isEmpty() || result.get().isEmpty()) {
            return;
        }
        
        List<String> selectedInputs = result.get();
        createLineChartMultiSeries(xIndex, yIndex, selectedInputs);
    }
    
    private void createLineChartSingleSeries(int xIndex, int yIndex) {
        boolean xIsNumeric = isNumericColumn(xIndex);
        
        if (xIsNumeric) {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            List<String> tooltips = new ArrayList<>();
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        double xValue = Double.parseDouble(row[xIndex].trim());
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                        tooltips.add(String.format("%s: %.2f", headers[yIndex], yValue));
                    } catch (NumberFormatException e) {
                    }
                }
            }
            lineChart.getData().add(series);
            lineChart.setTitle("Line Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            lineChart.setCreateSymbols(true);
            lineChart.setLegendVisible(false);
            
            javafx.application.Platform.runLater(() -> {
                TooltipManager.installTooltipOnSeries(series, tooltips);
            });
            
            updateChartView(lineChart);
        } else {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            List<String> tooltips = new ArrayList<>();
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        String xValue = row[xIndex].trim();
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                        tooltips.add(String.format("%s: %.2f", headers[yIndex], yValue));
                    } catch (NumberFormatException e) {
                    }
                }
            }
            lineChart.getData().add(series);
            lineChart.setTitle("Line Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            lineChart.setCreateSymbols(true);
            lineChart.setLegendVisible(false);
            
            javafx.application.Platform.runLater(() -> {
                TooltipManager.installTooltipOnSeries(series, tooltips);
            });
            
            updateChartView(lineChart);
        }
    }
    
    private void createLineChartMultiSeries(int xIndex, int yIndex, List<String> selectedInputs) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(headers[xIndex]);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[yIndex]);
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        
        Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();
        Map<String, List<String>> tooltipsMap = new HashMap<>();
        Map<String, Color> colorMap = new HashMap<>();
        
        int colorIndex = 0;
        for (String inputName : selectedInputs) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(inputName);
            seriesMap.put(inputName, series);
            tooltipsMap.put(inputName, new ArrayList<>());
            
            Color seriesColor = ColorManager.getStandardColor(colorIndex);
            colorMap.put(inputName, seriesColor);
            addLegendItem(inputName, seriesColor);
            colorIndex++;
        }
        
        for (String[] row : csvData) {
            if (row.length > 0) {
                String inputName = row[0].trim();
                if (selectedInputs.contains(inputName) && row.length > Math.max(xIndex, yIndex)) {
                    try {
                        String xValue = row[xIndex].trim();
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        
                        XYChart.Series<String, Number> series = seriesMap.get(inputName);
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                        tooltipsMap.get(inputName).add(String.format("%s: %s: %.2f", inputName, headers[yIndex], yValue));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        
        for (XYChart.Series<String, Number> series : seriesMap.values()) {
            lineChart.getData().add(series);
        }
        
        lineChart.setTitle("Line Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
        lineChart.setCreateSymbols(true);
        lineChart.setLegendVisible(true);
        
        javafx.application.Platform.runLater(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                javafx.application.Platform.runLater(() -> {
                    for (String inputName : selectedInputs) {
                        XYChart.Series<String, Number> series = seriesMap.get(inputName);
                        Color color = colorMap.get(inputName);
                        ColorManager.applyColorToLineChartSeries(series, color);
                        TooltipManager.installTooltipOnSeries(series, tooltipsMap.get(inputName));
                    }
                });
            }).start();
        });
        
        updateChartView(lineChart);
    }
    
    private void createBarChart(int xIndex, int yIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(headers[xIndex]);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[yIndex]);
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Bar Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(headers[yIndex]);
        List<String> tooltips = new ArrayList<>();
        
        Map<String, Double> dataMap = new HashMap<>();
        for (String[] row : csvData) {
            if (row.length > Math.max(xIndex, yIndex)) {
                try {
                    String xValue = row[xIndex].trim();
                    double yValue = Double.parseDouble(row[yIndex].trim());
                    dataMap.merge(xValue, yValue, Double::sum);
                } catch (NumberFormatException e) {
                }
            }
        }
        
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            tooltips.add(String.format("%s: %.2f", entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        
        javafx.application.Platform.runLater(() -> {
            Color barColor = ColorManager.getStandardColor(0);
            ColorManager.applyColorToBarChartSeries(series, barColor);
            TooltipManager.installTooltipOnSeries(series, tooltips);
        });
        
        updateChartView(barChart);
    }
    
    private void createScatterPlot(int xIndex, int yIndex) {
        if (headers.length < 3) {
            createScatterPlotSingleSeries(xIndex, yIndex);
            return;
        }
        
        List<String> inputNames = ChartDataHelper.getUniqueInputNames(csvData, 0);
        
        if (inputNames.size() <= 1) {
            createScatterPlotSingleSeries(xIndex, yIndex);
            return;
        }
        
        SeriesSelectionDialog dialog = new SeriesSelectionDialog(inputNames);
        Optional<List<String>> result = dialog.showAndWait();
        
        if (result.isEmpty() || result.get().isEmpty()) {
            return;
        }
        
        List<String> selectedInputs = result.get();
        createScatterPlotMultiSeries(xIndex, yIndex, selectedInputs);
    }
    
    private void createScatterPlotSingleSeries(int xIndex, int yIndex) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(headers[xIndex]);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[yIndex]);
        
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Scatter Plot: " + headers[xIndex] + " vs " + headers[yIndex]);
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Points");
        List<String> tooltips = new ArrayList<>();
        
        for (String[] row : csvData) {
            if (row.length > Math.max(xIndex, yIndex)) {
                try {
                    double xValue = Double.parseDouble(row[xIndex].trim());
                    double yValue = Double.parseDouble(row[yIndex].trim());
                    series.getData().add(new XYChart.Data<>(xValue, yValue));
                    tooltips.add(String.format("%s: %.2f, %s: %.2f", headers[xIndex], xValue, headers[yIndex], yValue));
                } catch (NumberFormatException e) {
                }
            }
        }
        
        scatterChart.getData().add(series);
        scatterChart.setLegendVisible(false);
        
        javafx.application.Platform.runLater(() -> {
            TooltipManager.installTooltipOnSeries(series, tooltips);
        });
        
        updateChartView(scatterChart);
    }
    
    private void createScatterPlotMultiSeries(int xIndex, int yIndex, List<String> selectedInputs) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(headers[xIndex]);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[yIndex]);
        
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Scatter Plot: " + headers[xIndex] + " vs " + headers[yIndex]);
        
        Map<String, XYChart.Series<Number, Number>> seriesMap = new LinkedHashMap<>();
        Map<String, List<String>> tooltipsMap = new HashMap<>();
        Map<String, Color> colorMap = new HashMap<>();
        
        int colorIndex = 0;
        for (String inputName : selectedInputs) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(inputName);
            seriesMap.put(inputName, series);
            tooltipsMap.put(inputName, new ArrayList<>());
            
            Color seriesColor = ColorManager.getStandardColor(colorIndex);
            colorMap.put(inputName, seriesColor);
            addLegendItem(inputName, seriesColor);
            colorIndex++;
        }
        
        for (String[] row : csvData) {
            if (row.length > 0) {
                String inputName = row[0].trim();
                if (selectedInputs.contains(inputName) && row.length > Math.max(xIndex, yIndex)) {
                    try {
                        double xValue = Double.parseDouble(row[xIndex].trim());
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        
                        XYChart.Series<Number, Number> series = seriesMap.get(inputName);
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                        tooltipsMap.get(inputName).add(String.format("%s: %s: %.2f, %s: %.2f", inputName, headers[xIndex], xValue, headers[yIndex], yValue));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        
        for (XYChart.Series<Number, Number> series : seriesMap.values()) {
            scatterChart.getData().add(series);
        }
        
        scatterChart.setLegendVisible(true);
        
        javafx.application.Platform.runLater(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                javafx.application.Platform.runLater(() -> {
                    for (String inputName : selectedInputs) {
                        XYChart.Series<Number, Number> series = seriesMap.get(inputName);
                        Color color = colorMap.get(inputName);
                        ColorManager.applyColorToScatterChartSeries(series, color);
                        TooltipManager.installTooltipOnSeries(series, tooltipsMap.get(inputName));
                    }
                });
            }).start();
        });
        
        updateChartView(scatterChart);
    }
    
    private void createAreaChart(int xIndex, int yIndex) {
        boolean xIsNumeric = isNumericColumn(xIndex);
        
        if (xIsNumeric) {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            AreaChart<Number, Number> areaChart = new AreaChart<>(xAxis, yAxis);
            
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            List<String> tooltips = new ArrayList<>();
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        double xValue = Double.parseDouble(row[xIndex].trim());
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                        tooltips.add(String.format("%s: %.2f, %s: %.2f", headers[xIndex], xValue, headers[yIndex], yValue));
                    } catch (NumberFormatException e) {
                    }
                }
            }
            areaChart.getData().add(series);
            areaChart.setTitle("Area Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            
            javafx.application.Platform.runLater(() -> {
                TooltipManager.installTooltipOnSeries(series, tooltips);
            });
            
            updateChartView(areaChart);
        } else {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(headers[xIndex]);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(headers[yIndex]);
            AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(headers[yIndex]);
            List<String> tooltips = new ArrayList<>();
            
            for (String[] row : csvData) {
                if (row.length > Math.max(xIndex, yIndex)) {
                    try {
                        String xValue = row[xIndex].trim();
                        double yValue = Double.parseDouble(row[yIndex].trim());
                        series.getData().add(new XYChart.Data<>(xValue, yValue));
                        tooltips.add(String.format("%s: %.2f", headers[yIndex], yValue));
                    } catch (NumberFormatException e) {
                    }
                }
            }
            areaChart.getData().add(series);
            areaChart.setTitle("Area Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
            
            javafx.application.Platform.runLater(() -> {
                TooltipManager.installTooltipOnSeries(series, tooltips);
            });
            
            updateChartView(areaChart);
        }
    }
    
    private void createPieChart(int xIndex, int yIndex) {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Pie Chart: " + headers[xIndex] + " (Values: " + headers[yIndex] + ")");
        
        Map<String, Double> dataMap = new HashMap<>();
        for (String[] row : csvData) {
            if (row.length > Math.max(xIndex, yIndex)) {
                try {
                    String xValue = row[xIndex].trim();
                    double yValue = Double.parseDouble(row[yIndex].trim());
                    dataMap.merge(xValue, yValue, Double::sum);
                } catch (NumberFormatException e) {
                }
            }
        }
        
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            if (entry.getValue() > 0) {
                pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }
        
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(90);
        
        javafx.application.Platform.runLater(() -> {
            for (PieChart.Data data : pieChart.getData()) {
                String tooltipText = String.format("%s: %.2f", data.getName(), data.getPieValue());
                TooltipManager.addTooltipToNode(data.getNode(), tooltipText);
            }
        });
        
        updateChartView(pieChart);
    }
    
    private void createHistogram(int columnIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Bins");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequency");
        
        BarChart<String, Number> histogram = new BarChart<>(xAxis, yAxis);
        histogram.setTitle("Histogram: " + headers[columnIndex]);
        
        List<Double> values = new ArrayList<>();
        for (String[] row : csvData) {
            if (row.length > columnIndex) {
                try {
                    values.add(Double.parseDouble(row[columnIndex].trim()));
                } catch (NumberFormatException e) {
                }
            }
        }
        
        if (values.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No numeric data found for histogram.");
            return;
        }
        
        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(100);
        int numBins = 10;
        double binWidth = (max - min) / numBins;
        
        Map<Integer, Integer> bins = new HashMap<>();
        for (double value : values) {
            int binIndex = (int) ((value - min) / binWidth);
            if (binIndex >= numBins) binIndex = numBins - 1;
            bins.merge(binIndex, 1, Integer::sum);
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Frequency");
        List<String> tooltips = new ArrayList<>();
        
        for (int i = 0; i < numBins; i++) {
            double binStart = min + i * binWidth;
            int frequency = bins.getOrDefault(i, 0);
            String binLabel = String.format("%.2f", binStart);
            series.getData().add(new XYChart.Data<>(binLabel, frequency));
            tooltips.add(String.format("Bin: %.2f - %.2f, Frequency: %d", binStart, binStart + binWidth, frequency));
        }
        
        histogram.getData().add(series);
        histogram.setLegendVisible(false);
        histogram.setCategoryGap(0);
        histogram.setBarGap(0);
        
        javafx.application.Platform.runLater(() -> {
            TooltipManager.installTooltipOnSeries(series, tooltips);
        });
        
        updateChartView(histogram);
    }
    
    private void createBoxPlot(int columnIndex) {
        List<Double> values = new ArrayList<>();
        for (String[] row : csvData) {
            if (row.length > columnIndex) {
                try {
                    values.add(Double.parseDouble(row[columnIndex].trim()));
                } catch (NumberFormatException e) {
                }
            }
        }
        
        if (values.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No numeric data found for box plot.");
            return;
        }
        
        java.util.Collections.sort(values);
        
        double min = values.get(0);
        double max = values.get(values.size() - 1);
        double q1 = values.get(values.size() / 4);
        double median = values.get(values.size() / 2);
        double q3 = values.get(3 * values.size() / 4);
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[columnIndex]);
        
        BarChart<String, Number> boxPlot = new BarChart<>(xAxis, yAxis);
        boxPlot.setTitle("Box Plot: " + headers[columnIndex]);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Statistics");
        List<String> tooltips = new ArrayList<>();
        
        series.getData().add(new XYChart.Data<>("Min", min));
        tooltips.add(String.format("Min: %.2f", min));
        series.getData().add(new XYChart.Data<>("Q1", q1));
        tooltips.add(String.format("Q1: %.2f", q1));
        series.getData().add(new XYChart.Data<>("Median", median));
        tooltips.add(String.format("Median: %.2f", median));
        series.getData().add(new XYChart.Data<>("Q3", q3));
        tooltips.add(String.format("Q3: %.2f", q3));
        series.getData().add(new XYChart.Data<>("Max", max));
        tooltips.add(String.format("Max: %.2f", max));
        
        boxPlot.getData().add(series);
        boxPlot.setLegendVisible(false);
        
        javafx.application.Platform.runLater(() -> {
            TooltipManager.installTooltipOnSeries(series, tooltips);
        });
        
        updateChartView(boxPlot);
    }
    
    private void createBubbleChart(int xIndex, int yIndex) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(headers[xIndex]);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(headers[yIndex]);
        
        BubbleChart<Number, Number> bubbleChart = new BubbleChart<>(xAxis, yAxis);
        bubbleChart.setTitle("Bubble Chart: " + headers[xIndex] + " vs " + headers[yIndex]);
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Points");
        List<String> tooltips = new ArrayList<>();
        
        for (String[] row : csvData) {
            if (row.length > Math.max(xIndex, yIndex)) {
                try {
                    double xValue = Double.parseDouble(row[xIndex].trim());
                    double yValue = Double.parseDouble(row[yIndex].trim());
                    double bubbleSize = Math.abs(yValue) / 10;
                    series.getData().add(new XYChart.Data<>(xValue, yValue, bubbleSize));
                    tooltips.add(String.format("%s: %.2f, %s: %.2f", headers[xIndex], xValue, headers[yIndex], yValue));
                } catch (NumberFormatException e) {
                }
            }
        }
        
        bubbleChart.getData().add(series);
        bubbleChart.setLegendVisible(false);
        
        javafx.application.Platform.runLater(() -> {
            TooltipManager.installTooltipOnSeries(series, tooltips);
        });
        
        updateChartView(bubbleChart);
    }
    
    private void createHeatmap() {
        if (csvData.size() < 2 || headers.length < 2) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Heatmap requires at least 2 rows and 2 columns of numeric data.");
            return;
        }
        
        List<String> xLabels = new ArrayList<>();
        List<String> yLabels = new ArrayList<>();
        
        for (int i = 1; i < headers.length; i++) {
            xLabels.add(headers[i]);
        }
        
        for (int i = 0; i < Math.min(csvData.size(), 10); i++) {
            yLabels.add(csvData.get(i)[0]);
        }
        
        double[][] data = new double[yLabels.size()][xLabels.size()];
        
        for (int row = 0; row < yLabels.size(); row++) {
            for (int col = 0; col < xLabels.size(); col++) {
                try {
                    data[row][col] = Double.parseDouble(csvData.get(row)[col + 1].trim());
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    data[row][col] = 0;
                }
            }
        }
        
        HeatMapChart heatmap = new HeatMapChart();
        heatmap.setXLabels(xLabels);
        heatmap.setYLabels(yLabels);
        heatmap.setData(data);
        heatmap.setTitle("Heatmap");
        
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(heatmap.getChartPane());
    }
    
    private void createRadarChart() {
        if (csvData.size() < 1 || headers.length < 3) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Radar chart requires at least 1 row and 3 numeric columns.");
            return;
        }
        
        List<String> categories = new ArrayList<>();
        for (int i = 1; i < headers.length; i++) {
            categories.add(headers[i]);
        }
        
        ObservableList<RadarChart.Data> radarData = FXCollections.observableArrayList();
        
        for (int rowIndex = 0; rowIndex < Math.min(csvData.size(), 5); rowIndex++) {
            String[] row = csvData.get(rowIndex);
            List<Double> values = new ArrayList<>();
            
            for (int col = 1; col < row.length; col++) {
                try {
                    values.add(Double.parseDouble(row[col].trim()));
                } catch (NumberFormatException e) {
                    values.add(0.0);
                }
            }
            
            radarData.add(new RadarChart.Data(row[0], values));
        }
        
        RadarChart radarChart = new RadarChart();
        radarChart.setCategories(categories);
        radarChart.setData(radarData);
        radarChart.setTitle("Radar Chart");
        
        // Create legend
        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < Math.min(csvData.size(), 5); i++) {
            playerNames.add(csvData.get(i)[0]);
        }
        createLegendForRadarChart(playerNames);
        
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(radarChart.getChartPane());
    }
    
    private void createRadarChartVsAverage() {
        if (csvData.size() < 2 || headers.length < 3) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Radar chart (vs Average) requires at least 2 rows and 3 numeric columns.");
            return;
        }
        
        List<String> categories = new ArrayList<>();
        for (int i = 1; i < headers.length; i++) {
            categories.add(headers[i]);
        }
        
        // Calculate average values for each category
        List<Double> averageValues = new ArrayList<>();
        for (int col = 1; col < headers.length; col++) {
            double sum = 0;
            int count = 0;
            for (String[] row : csvData) {
                if (col < row.length) {
                    try {
                        sum += Double.parseDouble(row[col].trim());
                        count++;
                    } catch (NumberFormatException e) {
                        // Skip invalid values
                    }
                }
            }
            averageValues.add(count > 0 ? sum / count : 0.0);
        }
        
        // Create a dialog to select which player to compare
        List<String> playerNames = new ArrayList<>();
        for (String[] row : csvData) {
            if (row.length > 0) {
                playerNames.add(row[0]);
            }
        }
        
        String selectedPlayer = showPlayerSelectionDialog(playerNames);
        if (selectedPlayer == null) {
            return; // User cancelled
        }
        
        // Find the selected player's data
        List<Double> playerValues = new ArrayList<>();
        for (String[] row : csvData) {
            if (row[0].equals(selectedPlayer)) {
                for (int col = 1; col < Math.min(row.length, headers.length); col++) {
                    try {
                        playerValues.add(Double.parseDouble(row[col].trim()));
                    } catch (NumberFormatException e) {
                        playerValues.add(0.0);
                    }
                }
                break;
            }
        }
        
        // Create radar chart with player vs average
        ObservableList<RadarChart.Data> radarData = FXCollections.observableArrayList();
        radarData.add(new RadarChart.Data(selectedPlayer, playerValues));
        radarData.add(new RadarChart.Data("Average", averageValues));
        
        RadarChart radarChart = new RadarChart();
        radarChart.setCategories(categories);
        radarChart.setData(radarData);
        radarChart.setTitle("Radar Chart: " + selectedPlayer + " vs Average");
        
        // Create legend
        createLegendForRadarVsAverage(selectedPlayer);
        
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(radarChart.getChartPane());
    }
    
    private String showPlayerSelectionDialog(List<String> playerNames) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(playerNames.get(0), playerNames);
        dialog.setTitle("Select Player");
        dialog.setHeaderText("Select a player to compare against the average");
        dialog.setContentText("Player:");
        
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    
    private void updateChartView(Chart chart) {
        currentChart = chart;
        
        chart.setAnimated(true);
        chart.setPrefSize(chartContainer.getWidth(), chartContainer.getHeight());
        
        chart.prefWidthProperty().bind(chartContainer.widthProperty());
        chart.prefHeightProperty().bind(chartContainer.heightProperty());
        
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(chart);
        
        // Apply custom color after chart is added to scene
        javafx.application.Platform.runLater(() -> {
            applyChartColor(chart);
        });
    }
    
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
    
    private static class ManualDataInputDialog extends Dialog<DataInputResult> {
        private TableView<ObservableList<String>> table;
        private TextField columnsField;
        private TextField rowsField;
        
        public ManualDataInputDialog() {
            setTitle("Manual Data Input");
            setHeaderText("Enter your data manually");
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            
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
            
            table = new TableView<>();
            table.setEditable(true);
            table.setPrefHeight(300);
            
            content.getChildren().addAll(sizeBox, table);
            
            getDialogPane().setContent(content);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            createTable(3, 5);
            
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
            
            ObservableList<String> headerRow = table.getItems().get(0);
            for (String header : headerRow) {
                headers.add(header == null ? "" : header.trim());
            }
            
            for (int i = 1; i < table.getItems().size(); i++) {
                ObservableList<String> tableRow = table.getItems().get(i);
                List<String> dataRow = new ArrayList<>();
                for (String cell : tableRow) {
                    dataRow.add(cell == null ? "" : cell.trim());
                }
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
