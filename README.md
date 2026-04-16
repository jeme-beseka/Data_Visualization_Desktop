# King Data Visualizer v2.0

A modern JavaFX desktop application for creating interactive data visualizations from CSV files or manual data entry.

## ✨ Features

### 📥 Data Input Methods
- **CSV File Import** - Load and parse CSV files automatically
- **Manual Data Entry** - Enter data directly in an editable table interface
- **Data Preview** - View loaded data in a preview panel with summary statistics

### 📊 Chart Types (10+ Supported)

#### Basic Charts
- **Line Chart** - Show trends and changes over time or continuous data
  - Supports numeric or categorical X-axis
  - Multi-series support with color-coded lines
  - Interactive tooltips on hover

- **Bar Chart** - Grouped and aggregated data visualization
  - Categorical X-axis with numeric Y-axis
  - Automatic data aggregation
  - Interactive tooltips

- **Scatter Plot** - Correlation and distribution analysis
  - Numeric X and Y axes
  - Multi-series support with distinct point colors
  - Interactive tooltips

- **Area Chart** - Cumulative data trends
  - Stacked area visualization
  - Shows cumulative changes over time
  - Interactive tooltips

- **Pie Chart** - Proportional data representation
  - Shows percentage distribution
  - Interactive tooltips with values
  - Supports multiple data series

#### Advanced Charts
- **Histogram** - Distribution and frequency analysis
  - Automatic binning algorithm (10 bins)
  - Shows frequency distribution
  - Single numeric column input

- **Box Plot** - Statistical distribution with quartiles
  - Displays min, Q1, median, Q3, max
  - Shows outliers and distribution shape
  - Single numeric column input

- **Bubble Chart** - Three-dimensional data visualization
  - Uses X and Y values with size encoding
  - Dynamic bubble sizing
  - Interactive tooltips

- **Heatmap** - Matrix visualization with color coding
  - Color gradient from blue (low) to red (high)
  - Shows patterns in data matrices
  - Supports large datasets

- **Radar Chart** - Multi-attribute comparison (spider chart)
  - Compares multiple attributes across entities
  - Two modes:
    - Standard: Compare all entities
    - vs Average: Compare individual entity against group average
  - High-contrast color palette for easy differentiation

### 🎯 Advanced Features

#### Multi-Series Support
- Display multiple data series on a single chart
- Each series gets a unique, consistent color
- User selection dialog to choose which series to display
- "Select All" and "Deselect All" buttons for convenience
- Default: All series pre-selected

#### Interactive Tooltips
- **Available on all chart types** - Hover over any data point to see detailed information
- **Automatic positioning** - Tooltips appear with brief delay and disappear on mouse away
- **Consistent formatting** - Values formatted to 2 decimal places with clear labels

**Tooltip Formats by Chart Type:**
- **Line/Scatter/Area Charts**: `[Series Name]: [X Value]: [Y Value]`
- **Bar Chart**: `[Category]: [Value]`
- **Pie Chart**: `[Category]: [Percentage]`
- **Histogram**: `Bin: [Start] - [End], Frequency: [Count]`
- **Box Plot**: `[Statistic]: [Value]` (Min, Q1, Median, Q3, Max)
- **Bubble Chart**: `[X Axis]: [Value], [Y Axis]: [Value]`
- **Radar Chart**: Shows series name with the specific attribute being hovered over
  ```
  PlayerA
  Speed: 85.00
  ```
- **Heatmap**: `[Row]: [Column]: [Value]`

#### Color Management
- **ColorManager Utility** - Centralized color management
  - Standard 8-color palette for general charts
  - Radar-specific 8-color palette with high contrast
  - Consistent color application across all chart types
  - Colors synchronized between chart series and legend

#### UI & Visual Enhancements
- **Modern Landing Page**
  - Gradient background (purple to pink)
  - Feature showcase with 2x2 grid layout
  - Professional typography and spacing
  - Application logo display
  - Smooth button hover effects

- **Professional Main Interface**
  - Organized toolbar with logical button grouping
  - File operations section (Load CSV, Manual Input)
  - Chart configuration section (Chart Type, X-Axis, Y-Axis)
  - Export and settings section (Save Plot, Theme Toggle)
  - Clean, professional color scheme

- **Data Preview Panel**
  - Left sidebar showing:
    - Data summary (row count, column count, status)
    - Data preview table (first 5 rows)
    - Statistics section (min, max, mean, median for numeric columns)
  - Responsive and scrollable

#### Theme Support
- **Dark Mode** - Toggle between light and dark themes
  - Comprehensive dark theme CSS
  - Applies to all UI elements including charts
  - Smooth theme switching

#### Additional Features
- **Summary Statistics** - Min, max, mean, and median calculations for numeric columns
- **Status Bar** - Real-time feedback on application actions
- **Keyboard Shortcuts**:
  - `Ctrl+O` - Open CSV file
  - `Ctrl+S` - Save plot
  - `Ctrl+R` - Reset application
- **Export Charts** - Save visualizations as high-quality PNG images
- **Responsive Design** - Adapts to different window sizes
- **Professional Icons** - Multiple icon sizes (16x16, 32x32, 64x64, 128x128) for taskbar and window title

## 🚀 Quick Start Guide

### First Launch
1. Run `King Data Visualizer.exe` (or use `mvn javafx:run` for development)
2. You'll see the welcome screen with feature overview
3. Click **"Get Started →"** to enter the main application

### Creating Your First Chart

#### From CSV File
1. Click **"📁 Load CSV"** button
2. Select a CSV file from your computer
3. View the data preview in the left panel
4. Select **X-Axis** column (e.g., "Month")
5. Select **Y-Axis** column (e.g., "Sales")
6. Choose a **Chart Type** (Line, Bar, Scatter, Area, Pie, etc.)
7. Click **"📈 Plot"** to generate the chart

#### From Manual Entry
1. Click **"✏️ Manual Input"** button
2. Enter number of columns and rows
3. Click **"Create Table"**
4. Fill in your data (first row = headers)
5. Click **OK**
6. Follow steps 4-7 from CSV method above

### Using Multi-Series Charts
1. Load data with multiple input columns (e.g., Player A, Player B, Player C)
2. Select X-axis and Y-axis as usual
3. Choose a chart type that supports multi-series (Line, Scatter, Bar)
4. A selection dialog will appear
5. Choose which series to display (or use "Select All")
6. Click **"📈 Plot"** to generate the chart

### Hovering for Information
- Hover over any data point to see detailed information
- Tooltip shows: `[Series Name]: [Metric]: [Value]`
- Works on all chart types

### Saving Your Work
1. After creating a chart, click **"💾 Save Plot"**
2. Choose a location and filename
3. Chart is saved as a PNG image

### Switching Themes
- Click **"🌙 Theme"** button to toggle between light and dark modes
- Theme preference is applied to entire application

## 📋 Requirements

- **Java 17 or higher**
- **Maven 3.6 or higher**
- **Windows 10/11** (for .exe distribution)

## 🏗️ Technical Implementation

### Architecture Overview

#### Utility Classes
- **ColorManager** - Centralized color management for all chart types
  - Standard color palette (8 colors)
  - Radar-specific color palette (8 bright colors)
  - Methods to apply colors to different chart types
  - Color conversion utilities (RGB, Hex)

- **ChartDataHelper** - Multi-series data extraction and management
  - Extracts data for multiple series from CSV
  - Creates data points with proper formatting
  - Handles categorical and numeric data

- **TooltipManager** - Interactive tooltip management
  - Installs tooltips on chart series
  - Formats tooltip text
  - Handles hover events

- **SeriesSelectionDialog** - User selection interface
  - Multi-select checkboxes for series
  - "Select All" and "Deselect All" buttons
  - Modal dialog for user interaction

- **StatisticsCalculator** - Statistical analysis
  - Calculates min, max, mean, median
  - Identifies numeric vs categorical columns
  - Provides column statistics

- **ThemeManager** - Theme management
  - Light and dark theme CSS
  - Dynamic theme switching
  - Applies to all UI components

#### Key Components
- **ChartLegend** - Legend display component
  - Color-coded legend items
  - Displays series names and colors
  - Responsive layout

- **RadarChart** - Custom radar chart implementation
  - Multi-series support
  - High-contrast color palette
  - Customizable sizing and styling

- **HeatMapChart** - Custom heatmap implementation
  - Color gradient visualization
  - Matrix data display
  - Customizable color ranges

#### File Structure
```
src/main/java/com/datavisualizer/
├── Main.java                          # Application entry point
├── controller/
│   ├── MainController.java            # Main UI controller
│   ├── LandingController.java         # Landing page controller
│   └── SeriesSelectionDialog.java     # Series selection dialog
├── util/
│   ├── ColorManager.java              # Color management
│   ├── ChartDataHelper.java           # Data extraction
│   ├── TooltipManager.java            # Tooltip management
│   ├── StatisticsCalculator.java      # Statistics
│   └── ThemeManager.java              # Theme management
├── component/
│   └── ChartLegend.java               # Legend component
└── chart/
    ├── RadarChart.java                # Radar chart
    └── HeatMapChart.java              # Heatmap chart

src/main/resources/
├── fxml/
│   ├── MainView.fxml                  # Main UI layout
│   └── LandingView.fxml               # Landing page layout
├── css/
│   ├── styles.css                     # Light theme
│   └── styles-dark.css                # Dark theme
└── images/
    └── Data-Visual-Icon.png           # Application icon
```

### Color Palettes

#### Standard Colors (for Line, Scatter, Bar, Area, etc.)
```
Blue        (52, 152, 219)
Red         (231, 76, 60)
Green       (46, 204, 113)
Yellow      (241, 196, 15)
Purple      (155, 89, 182)
Orange      (230, 126, 34)
Dark Blue   (52, 73, 94)
Gray        (149, 165, 166)
```

#### Radar Colors (for Radar Charts)
```
Bright Red      (255, 107, 107)
Bright Blue     (66, 165, 245)
Bright Green    (76, 175, 80)
Bright Yellow   (255, 193, 7)
Bright Purple   (171, 71, 188)
Bright Orange   (255, 152, 0)
Bright Pink     (233, 30, 99)
Bright Cyan     (0, 188, 212)
```

## 🔨 Building and Running

### Development Mode
```bash
# Clone the repository
git clone <repository-url>
cd Data_Visualization_Desktop

# Build the project
mvn clean compile

# Run the application
mvn javafx:run
```

### Production Build
For detailed build instructions including creating a Windows .exe executable, see [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md).

Quick build:
```bash
mvn clean package
```

## 📝 Sample Data

Sample CSV files are provided in the `sample-data/` directory:
- `radar-chart-sample.csv` - Player statistics for radar charts
- `histogram-sample.csv` - Distribution data
- `box-plot-sample.csv` - Statistical data
- `bubble-chart-sample.csv` - Multi-dimensional data
- `heatmap-sample.csv` - Matrix data

## 🎨 Customization

### Adding Custom Colors
Edit the `ColorManager.java` file to modify color palettes:
```java
public static final Color[] STANDARD_COLORS = {
    Color.rgb(52, 152, 219),   // Blue
    // ... add more colors
};
```

### Changing Themes
Modify CSS files in `src/main/resources/css/`:
- `styles.css` - Light theme
- `styles-dark.css` - Dark theme

### Customizing Chart Appearance
Edit chart creation methods in `MainController.java` to adjust:
- Chart titles and labels
- Axis ranges and formatting
- Legend positioning
- Color schemes

## 📈 Version History

### v2.0 (Current)
- Modern UI redesign with landing page
- Data preview panel with statistics
- Dark mode support
- Keyboard shortcuts
- Enhanced status bar
- Professional icon integration

### v3.0
- Added 6 new chart types (Histogram, Box Plot, Bubble, Heatmap, Radar, Radar vs Average)
- Multi-series support for Line and Scatter charts
- Interactive tooltips on all chart types
- User selection dialog for series

### v3.1
- Dark theme fixes and improvements
- Chart color customization
- Enhanced theme application across all UI elements

### v3.2
- Chart legend system
- Radar chart enhancements
- Color synchronization between legend and charts

### v3.3 (Latest)
- Multi-series implementation for all applicable charts
- ColorManager utility for centralized color management
- Legend display removal (functionality preserved)
- Application icon size enhancement
- Landing page logo display
- Color synchronization fixes

## 🤝 Contributing

To contribute to this project:
1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## 📄 License

This project is provided as-is for educational purposes.

## 📞 Support

For issues, questions, or suggestions, please refer to the documentation files or contact the development team.

---

**Last Updated:** April 16, 2026  
**Version:** 3.3  
**Status:** Production Ready
