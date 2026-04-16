package com.datavisualizer.util;

import java.util.List;

public class StatisticsCalculator {
    
    public static class ColumnStats {
        public String columnName;
        public int count;
        public double min;
        public double max;
        public double mean;
        public double median;
        public boolean isNumeric;
        
        public ColumnStats(String columnName) {
            this.columnName = columnName;
        }
    }
    
    public static ColumnStats calculateStats(String columnName, List<String[]> data, int columnIndex) {
        ColumnStats stats = new ColumnStats(columnName);
        stats.count = data.size();
        
        List<Double> numericValues = new java.util.ArrayList<>();
        
        for (String[] row : data) {
            if (row.length > columnIndex) {
                try {
                    double value = Double.parseDouble(row[columnIndex].trim());
                    numericValues.add(value);
                } catch (NumberFormatException e) {
                    // Not numeric
                }
            }
        }
        
        if (numericValues.isEmpty()) {
            stats.isNumeric = false;
            return stats;
        }
        
        stats.isNumeric = true;
        stats.min = numericValues.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        stats.max = numericValues.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        stats.mean = numericValues.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        
        // Calculate median
        java.util.Collections.sort(numericValues);
        if (numericValues.size() % 2 == 0) {
            stats.median = (numericValues.get(numericValues.size() / 2 - 1) + 
                           numericValues.get(numericValues.size() / 2)) / 2.0;
        } else {
            stats.median = numericValues.get(numericValues.size() / 2);
        }
        
        return stats;
    }
}
