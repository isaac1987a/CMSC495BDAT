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
    calculatePValue(): double pValue
    calculateBestFitLine(double[][] columns): double[] {m,b}
    calculateBestFitCurve()
    calculateR2()
 */
public class StaticMath {

    private double pValue;
    private double[] bestFitLine;
    private double bestFitCurve;
    private double r2;

    /*
    Calculate Mean of Target Column
     */
    public double calculateMean(double[] column) {
        double sum = 0.0;

        for (int i = 0; i < column.length; i++) {
            sum += column[i];
        }
        return ((double) sum / (double) column.length);
    }

    /*
    Calculate Median of Target Column
     */
    public double calculateMedian(double[] column) {
        Arrays.sort(column);

        if (column.length % 2 != 0) {
            return (double) column[column.length / 2];
        }
        return (double) (column[(column.length - 1) / 2]
                + column[column.length / 2]) / 2.0;
    }

    /*
    Calculate Mode of Target Column
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

    /*
    Calculate Standard Deviation of Target Column
     */
    public double calculateStdDeviation(double[] column) {
        double stdDevSum = 0.0;
        double mean = this.calculateMean(column);

        for (int i = 0; i < column.length; i++) {
            stdDevSum += Math.pow(column[i] - mean, 2.0);
        }
        return Math.sqrt(stdDevSum / column.length);
    }

    /*
    Calculate PValue of Target Column
     */
    public double calculatePValue(double[] column) {
        return pValue;
    }

    /*
    Calculate Best Fit Line of Target Columns
    Maximum 2 columns for X & Y
    Returns double[] containing m and c for y=mx+b
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

        bestFitLine[0] = m;
        bestFitLine[1] = b;

        return bestFitLine;
    }

    /*
    Calculate Best Fit Curve of Target Columns
     */
    public double calculateBestFitCurve(double[][] columns) {
        return bestFitCurve;
    }

    /*
    Calculate R2 of Target Columns
     */
    public double calculateR2(double[][] columns) {
        return r2;
    }

}
