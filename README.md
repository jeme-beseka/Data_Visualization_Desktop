# Data Visualizer

A JavaFX desktop application for creating interactive data visualizations from CSV files or manual data entry.

## Features

- **Data Input Methods:**
  - Load CSV files with automatic parsing
  - Manual data entry with editable table interface
- **Chart Types:**
  - Line Chart (numeric/categorical X-axis support)
  - Bar Chart
  - Scatter Plot
  - Area Chart
  - Pie Chart
- **Export & Management:**
  - Save charts as PNG images
  - Reset functionality to clear all data
- **User Interface:**
  - Welcome landing page with feature overview
  - Intuitive main interface with real-time chart generation

## Requirements

- Java 17 or higher
- Maven 3.6 or higher

## Building and Running

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean compile
   ```
3. Run the application:
   ```bash
   mvn javafx:run
   ```

## Usage

### CSV Data
1. Click "Load CSV" to select and load a CSV file
2. Choose chart type, X-axis, and Y-axis columns
3. Click "Plot" to generate visualization
4. Use "Save Plot" to export as PNG

### Manual Data Entry
1. Click "Manual Input" to open data entry dialog
2. Set table dimensions and enter data
3. First row becomes column headers
4. Follow same plotting steps as CSV data

## Project Structure

```
src/main/java/com/datavisualizer/
├── Main.java                           # Application entry point
└── controller/
    ├── LandingController.java          # Landing page controller
    └── MainController.java             # Main application controller

src/main/resources/fxml/
├── LandingView.fxml                    # Welcome page layout
└── MainView.fxml                       # Main application layout
```

## Dependencies

- **JavaFX 17.0.2** - GUI framework and native charting
- **OpenCSV 5.7.1** - CSV file parsing
- **Maven** - Build tool and dependency management

## Architecture

Built using **MVC (Model-View-Controller)** pattern:
- **Model**: Data structures for CSV data and headers
- **View**: FXML files for UI layout
- **Controller**: Java classes handling user interactions and business logic