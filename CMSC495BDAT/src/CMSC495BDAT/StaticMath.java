package CMSC495BDAT;

/*  File: InputOutput.java
    Author: Adam Rittermann
    Date: 17 February 2020
    Purpose:  Statistics Calculations */
import java.util.Arrays;

/* Public Methods
    calculateMean(double[] column): double mean
    calculateMedian(double[] column): double median
    calculateMode(double[] column): double mode
    calculateStdDeviation(double[] column): double standard deviation
    calculatePValue() -- NOT IMPLEMENTED
    calculateBestFitLine(double[][] columns): double[] {m,b}
    calculateBestFitCurve() -- NOT IMPLEMENTED
    calculateR2(double[][] columns): double r2
 */
public class StaticMath {

    private double pValue;
    private double bestFitCurve;

    /**
     * Calculate Mean of Target Column Values
     *
     * @param column double[] column data
     * @return double mean
     */
    public double calculateMean(double[] column) {
        double sum = 0.0;

        for (int i = 0; i < column.length; i++) {
            sum += column[i];
        }
        return ((double) sum / (double) column.length);
    }

    /**
     * Calculate Median of Target Column Values
     * 
     * @param column double[] column data
     * @return double median
     */
    public double calculateMedian(double[] column) {
        Arrays.sort(column);

        if (column.length % 2 != 0) {
            return (double) column[column.length / 2];
        }
        return (double) (column[(column.length - 1) / 2]
                + column[column.length / 2]) / 2.0;
    }

    /**
     * Calculate Mode of Target Column
     * 
     * @param column double[] column data
     * @return double mode
     */
    public double calculateMode(double[] column) {
        int maxCount = 0;
        double mode = 0.0;

        for (int i = 0; i < column.length; i++) {
            int count = 0;
            for (int j = 0; j < column.length; j++) {
                if (column[j] == column[i]) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                mode = column[i];
            }
        }
        return mode;
    }

    /**
     * Calculate Standard Deviation of Target Column
     * 
     * @param column double[] column data
     * @return double standard deviation
     */
    public double calculateStdDeviation(double[] column) {
        double stdDevSum = 0.0;
        double mean = this.calculateMean(column);

        for (int i = 0; i < column.length; i++) {
            stdDevSum += Math.pow(column[i] - mean, 2.0);
        }
        return Math.sqrt(stdDevSum / column.length);
    }

    /**
     * -- NOT IMPLEMENTED
     * Calculate PValue of Target Column
     * 
     * @param column double[] column data
     * @return double pValue
     * -- NOT IMPLEMENTED
     */
    public double calculatePValue(double[] column) {
        return pValue;
    }

    /**
     * Calculate Best Fit Line of Target Columns
     * Maximum 2 columns for X & Y
     * 
     * @params columns double[][] column x and y
     * @return double[] m and c for linear function y=mx+b
     */
    public double[] calculateBestFitLine(double[][] columns) {
        double m, b;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (double[] row : columns) {
            sumX += row[0];
            sumY += row[1];
            sumXY += row[0] * row[1];
            sumX2 += Math.pow(row[0], 2);
        }

        m = ((columns.length * sumXY) - (sumX * sumY))
                / ((columns.length * sumX2) - (Math.pow(sumX, 2)));
        b = (sumY - m * sumX) / columns.length;

        double[] bestFitLine = new double[2];
        bestFitLine[0] = m;
        bestFitLine[1] = b;

        return bestFitLine;
    }

    /**
     * -- NOT IMPLEMENTED
     * Calculate Best Fit Curve of Target Columns
     * 
     * @params columns double[][] columns x and y
     * @return double bestFitCurve
     * -- NOT IMPLEMENTED
     */
    public double calculateBestFitCurve(double[][] columns) {
        return bestFitCurve;
    }

    /**
     * Calculate R-Squared Value for Linear Regression
     *
     * @param columns double[][] x and y values for calculation
     * @return double R-squared value
     */
    public double calculateR2(double[][] columns) {
        double r2 = 0.0;
        double[] vars = this.calculateBestFitLine(columns);
        double slope = vars[0];
        double intercept = vars[1];
        double sumY = 0;
        double[] x = new double[columns.length];
        double[] y = new double[columns.length];

        // Find sum of Y
        for (double[] row : columns) {
            sumY += row[1];
        }

        // Find average of Y
        double meanY = sumY / columns.length;

        // Sum of Squares Y
        double ssY = 0.0;
        for (int i = 0; i < columns.length; i++) {
            ssY += (columns[i][1] - meanY) * (columns[i][1] - meanY);
        }

        // Regression Sum of Squares Y
        double ssr = 0.0;   
        for (int i = 0; i < columns.length; i++) {
            double fit = slope * columns[i][0] + intercept;
            ssr += (fit - meanY) * (fit - meanY);
        }

        // Calculate R-Squared
        r2 = ssr / ssY;

        return r2;
    }
}
