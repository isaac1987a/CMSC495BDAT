 package CMSC495BDAT;
/*  File: HistogramChartView.java
    Author: Justin Rhodes
    Date: 23 February 2020
    Purpose: Show a histogram plot
    Version: 0.1
*/

// build note: (adjust accordingly)
// javac -classpath ".;.\xchart-3.6.1\xchart-3.6.1.jar" .\*ChartView.java
// java -classpath ".;.\xchart-3.6.1\xchart-3.6.1.jar" HistogramChartView

/* Public Methods
    HistogramChartView(JFrame parentFrame, double[] yData, String colName)  
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.lang.StringBuilder;
import java.text.Format;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import GUIObjects.SearchOption;

import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.CategoryStyler;
import org.knowm.xchart.style.Styler.LegendLayout;


public class HistogramChartView extends AbstractChartView {

  private double[] xData = null;
  private double[] yData = null;
  private String colName = null;

  private double[] bins;
  private double binSize = 1;
  private double[] histogram;

  /** Constructor that includes column names */
  public HistogramChartView(JFrame parentFrame, double[] yData, SearchOption searchOption) {
    super(parentFrame);
    this.yData = yData;
    this.xData = getIndexVector(yData.length); // give it a 1:n index
    this.colName = searchOption.getColumn();
    
    calculateHistogram();
    
    setTitle("Histogram Plot");
    addChart();
    setInfoText();
    // force swing to redraw the panel...
    repaint();
    revalidate();
  }

  /** Draw a scatter plot chart with linear fit */
  protected void addChart() {
   
    // set up category chart
    CategoryChart chart = new CategoryChartBuilder()
        .xAxisTitle("Value")
        .yAxisTitle("Count")
        .build();

    // customize chart properties
    chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
    chart.getStyler().setLegendLayout(LegendLayout.Vertical);
    chart.getStyler().setChartTitleVisible(false);
    // chart.getStyler().setXAxisMaxLabelCount(25);
    chart.getStyler().setXAxisDecimalPattern("0");
    chart.getStyler().setAvailableSpaceFill(.8);
    chart.getStyler().setOverlapped(true);
    chart.getStyler().setToolTipsEnabled(true);

    // add histogram series
    chart.addSeries(colName, bins, histogram);

    // add normal distribution curve
    CategorySeries normalSeries = chart.addSeries("Normal distribution", bins, getNormalCurve());
    normalSeries.setYAxisGroup(1); // put on second axes
    normalSeries.setChartCategorySeriesRenderStyle(CategorySeriesRenderStyle.Line);
    chart.getStyler().setYAxisGroupPosition(1, Styler.YAxisPosition.Right);
    chart.setYAxisGroupTitle(1, "Percentage");

    // add the chart into the swing panel
    JPanel chartPanel = new XChartPanel<CategoryChart>(chart);
    leftPane.add(chartPanel, BorderLayout.CENTER);
  }

  /** Set the info text on the right side panel */
  protected void setInfoText() {
    StringBuilder infoString = new StringBuilder();

    infoString.append("<h2>Data Statistics: " + colName + "</h2>");
    infoString.append(String.format("Minimum: %.4f<br>", Arrays.stream(yData).min().getAsDouble()));
    infoString.append(String.format("Maximum: %.4f<br>", Arrays.stream(yData).max().getAsDouble()));
    infoString.append(String.format("Mean: %.4f<br>", StaticMath.calculateMean(yData)));
    infoString.append(String.format("Median: %.4f<br>", StaticMath.calculateMedian(yData.clone())));
    infoString.append(String.format("Mode: %.4f<br>", StaticMath.calculateMode(yData)));
    infoString.append(String.format("Std dev: %.4f<br>", StaticMath.calculateStdDeviation(yData)));

    infoText.setText(infoString.toString());
  }

  /** Use the linear fit to calculate y values */
  private void calculateHistogram() {

    // figure out our histogram bins
    Double dataMin = Math.floor(Arrays.stream(yData).min().getAsDouble());
    Double dataMax = Math.ceil(Arrays.stream(yData).max().getAsDouble());
    Double numBins = Math.ceil((dataMax-dataMin)/binSize) + 1;

    // set up histogram array
    histogram  = new double[numBins.intValue()]; 
    Arrays.fill(histogram, 0); // initialize to 0s

    // calculate and store bin markers. bin = [start, stop)
    bins = new double[histogram.length];
    for (int bin=0; bin < histogram.length; bin++) {
      bins[bin] = dataMin + binSize*(bin);
    }

    // sort data values into their bins
    for (int i=0; i < yData.length; i++) {
      for (int bin=0; bin < histogram.length; bin++) {
        Double binStart = bins[bin]; 
        Double binStop = binStart + binSize; // calculating it prevents array overflow from bin+1
        if ( (yData[i] >= binStart) && (yData[i] < binStop)) {
          histogram[bin]++;
          break;
        }
      }
    }
  }

  private double[] getNormalCurve() {
    double[] normal = new double[bins.length];
    for (int i=0; i < normal.length; i++) {
      double mean = StaticMath.calculateMean(yData);
      double std = StaticMath.calculateStdDeviation(yData);
      // Adam's formula, multipled by 100 to get %
      normal[i] = 100 * 1 / (std * Math.sqrt(2*Math.PI)) * Math.exp(-1*((bins[i]-mean)*(bins[i]-mean)/(2*std*std)));
    }
    return normal;
  }


  /** Testing: show chart */
	/*public static void main(String[] args) {

    JFrame testFrame = new JFrame();
    testFrame.setTitle("TEST launcher: HistogramChartView");
    testFrame.setSize(400, 300);
    testFrame.setMinimumSize(new Dimension(400, 300));
    testFrame.setLocationRelativeTo(null);
    testFrame.setLayout(new BorderLayout());
    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    testFrame.setVisible(true);

    TestCSV csv = new TestCSV();
    HistogramChartView test = new HistogramChartView(testFrame, csv.getColumn(2), csv.getColumnName(0));
    test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // kills app directly in this mode
	}*/

}
