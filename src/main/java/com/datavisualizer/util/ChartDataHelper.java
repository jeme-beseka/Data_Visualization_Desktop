package com.datavisualizer.util;

import java.util.*;

public class ChartDataHelper {
    
    public static class SeriesData {
        public String seriesName;
        public List<DataPoint> points;
        
        public SeriesData(String seriesName) {
            this.seriesName = seriesName;
            this.points = new ArrayList<>();
        }
    }
    
    public static class DataPoint {
        public String inputName;
        public String metricName;
        public double xValue;
        public double yValue;
        public String xLabel;
        
        public DataPoint(String inputName, String metricName, double xValue, double yValue) {
            this.inputName = inputName;
            this.metricName = metricName;
            this.xValue = xValue;
            this.yValue = yValue;
        }
        
        public DataPoint(String inputName, String metricName, String xLabel, double yValue) {
            this.inputName = inputName;
            this.metricName = metricName;
            this.xLabel = xLabel;
            this.yValue = yValue;
        }
        
        public String getTooltipText() {
            if (xLabel != null) {
                return String.format("%s: %s: %.2f", inputName, metricName, yValue);
            }
            return String.format("%s: %s: %.2f", inputName, metricName, yValue);
        }
    }
    
    public static List<SeriesData> extractMultiSeriesData(
            List<String[]> csvData,
            String[] headers,
            int inputColumnIndex,
            int metricColumnIndex,
            int valueColumnIndex) {
        
        Map<String, SeriesData> seriesMap = new LinkedHashMap<>();
        
        for (String[] row : csvData) {
            if (row.length > Math.max(Math.max(inputColumnIndex, metricColumnIndex), valueColumnIndex)) {
                try {
                    String inputName = row[inputColumnIndex].trim();
                    String metricName = row[metricColumnIndex].trim();
                    double value = Double.parseDouble(row[valueColumnIndex].trim());
                    
                    SeriesData series = seriesMap.computeIfAbsent(inputName, k -> new SeriesData(inputName));
                    series.points.add(new DataPoint(inputName, metricName, metricName, value));
                } catch (NumberFormatException e) {
                }
            }
        }
        
        return new ArrayList<>(seriesMap.values());
    }
    
    public static List<SeriesData> extractMultiSeriesDataNumericX(
            List<String[]> csvData,
            String[] headers,
            int inputColumnIndex,
            int xColumnIndex,
            int yColumnIndex) {
        
        Map<String, SeriesData> seriesMap = new LinkedHashMap<>();
        
        for (String[] row : csvData) {
            if (row.length > Math.max(Math.max(inputColumnIndex, xColumnIndex), yColumnIndex)) {
                try {
                    String inputName = row[inputColumnIndex].trim();
                    double xValue = Double.parseDouble(row[xColumnIndex].trim());
                    double yValue = Double.parseDouble(row[yColumnIndex].trim());
                    
                    SeriesData series = seriesMap.computeIfAbsent(inputName, k -> new SeriesData(inputName));
                    series.points.add(new DataPoint(inputName, headers[xColumnIndex], xValue, yValue));
                } catch (NumberFormatException e) {
                }
            }
        }
        
        return new ArrayList<>(seriesMap.values());
    }
    
    public static List<String> getUniqueInputNames(List<String[]> csvData, int inputColumnIndex) {
        List<String> names = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        
        for (String[] row : csvData) {
            if (row.length > inputColumnIndex) {
                String name = row[inputColumnIndex].trim();
                if (seen.add(name)) {
                    names.add(name);
                }
            }
        }
        
        return names;
    }
    
    public static List<SeriesData> filterSeriesByInputNames(
            List<SeriesData> allSeries,
            List<String> selectedInputNames) {
        
        List<SeriesData> filtered = new ArrayList<>();
        Set<String> selectedSet = new HashSet<>(selectedInputNames);
        
        for (SeriesData series : allSeries) {
            if (selectedSet.contains(series.seriesName)) {
                filtered.add(series);
            }
        }
        
        return filtered;
    }
}
