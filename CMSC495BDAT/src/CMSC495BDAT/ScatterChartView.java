package CMSC495BDAT;
/*  File: ScatterChartView.java
    Author: Justin Rhodes
    Date: 21 February 2020
    Purpose: Show a scatter plot with linear fit line for a given data vector
    Version: 0.1
*/

// build note: (adjust accordingly)
// javac -classpath ".;.\xchart-3.6.1\xchart-3.6.1.jar" .\*ChartView.java
// java -classpath ".;.\xchart-3.6.1\xchart-3.6.1.jar" ScatterChartView

/* Public Methods
    ScatterChartView(JFrame parentFrame, double[] yData, String yName, SearchOption searchOption)
    ScatterChartView(JFrame parentFrame, double[] xData, String xName, 
                                         double[] yData, String yName, SearchOption searchOption)
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
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.Styler.LegendLayout;

import GUIObjects.SearchOption;
import CMSC495BDAT.CurveOptionEnum;

public class ScatterChartView extends AbstractChartView {

  private double[] xData = null;
  private double[] yData = null;
  private String xName = "";
  private String yName = "";
  private Boolean crossPlot = null;
  private double[] fitParams = null;

  private CurveOptionEnum curveOption = CurveOptionEnum.Linear;
  private Color displayColor = Color.GRAY;

  /** Constructor for single variable view */
  public ScatterChartView(JFrame parentFrame, double[] yData, String yName,
                                              SearchOption searchOption) {
    super(parentFrame);
    this.crossPlot = false; // mark this as a single variable plot
    this.yData = yData;
    this.xData = getIndexVector(yData.length);
    this.yName = yName;
    this.xName = "Row Number";
    this.curveOption = searchOption.getOption();
    this.displayColor = searchOption.getColor1();

    constructChart();
  }

  /** Constructor for 2 variable view */
  public ScatterChartView(JFrame parentFrame, double[] xData, String xName, 
                                              double[] yData, String yName, 
                                              SearchOption searchOption) {
    super(parentFrame);
    this.crossPlot = true; // mark this as a single variable plot
    this.yData = yData;
    this.xData = xData;
    this.yName = yName;
    this.xName = xName;
    this.curveOption = searchOption.getOption();
    this.displayColor = searchOption.getColor1();

    constructChart();
  }

  private void constructChart() {
    // calculate a linear fit from the input data
    fitParams = StaticMath.calculateBestFitLine(combineVectors(xData, yData));

    setTitle("Scatter Plot");
    addChart();
    setInfoText();
    // force swing to redraw the panel...
    repaint();
    revalidate();
  }

  /** Draw a scatter plot chart with linear fit */
  protected void addChart() {
    // set up a basic XY/scatter chart
    XYChart chart = new XYChartBuilder()
        .xAxisTitle(xName)
        .yAxisTitle(yName)
        .build();

    // Customize chart properties
    chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
    chart.getStyler().setLegendLayout(LegendLayout.Horizontal);
    chart.getStyler().setChartTitleVisible(false);
    chart.getStyler().setToolTipsEnabled(true);

    // Add primary data series
    XYSeries dataSeries = chart.addSeries("Data values", xData, yData);
    dataSeries.setLineStyle(SeriesLines.NONE);
    dataSeries.setMarker(SeriesMarkers.CIRCLE);
    dataSeries.setMarkerColor(displayColor);
    
    // Show the linear fit series too
    XYSeries fitSeries = getDataFitSeries(chart);
    fitSeries.setLineColor(Color.RED);
    fitSeries.setLineStyle(SeriesLines.SOLID);
    fitSeries.setLineWidth(5);
    fitSeries.setMarker(SeriesMarkers.SQUARE);

    // add the chart into the swing panel
    JPanel chartPanel = new XChartPanel<XYChart>(chart);
    leftPane.add(chartPanel, BorderLayout.CENTER);
  }

  /** build approriate fit series for the chart */
  private XYSeries getDataFitSeries(XYChart chart) {
    XYSeries series = null;

    switch(curveOption) {
      case Exponential:
        fitParams = StaticMath.calculateExponentialCurve(combineVectors(xData, yData));
        series = chart.addSeries("Exponential fit", xData, calculateExponetialFitLineValues());
        break;
      case Power:
        fitParams = StaticMath.calculatePowerCurve(combineVectors(xData, yData));
        series = chart.addSeries("Power fit", xData, calculatePowerFitLineValues());
        break;
      case Logarithimic:
        fitParams = StaticMath.calculateLogCurve(combineVectors(xData, yData));
        series = chart.addSeries("Logarithimic fit", xData, calculateLogFitLineValues());
        break;
      case Linear: // fall through for default
      default: 
        fitParams = StaticMath.calculateBestFitLine(combineVectors(xData, yData));
        series = chart.addSeries("Linear fit", xData, calculateLinearFitLineValues());
        break;
    }
    return series;
  }

  /** Set the info text on the right side panel */
  protected void setInfoText() {
    StringBuilder infoString = new StringBuilder();

    // show first data set info if we have a 2-var view
    if (crossPlot == true) {
      infoString.append("<h2>Variable: " + xName + "</h2>");
      infoString.append(String.format("Mean: %.4f<br>", StaticMath.calculateMean(xData)));
      infoString.append(String.format("Median: %.4f<br>", StaticMath.calculateMedian(xData.clone())));
      infoString.append(String.format("Mode: %.4f<br>", StaticMath.calculateMode(xData)));
      infoString.append(String.format("Std dev: %.4f<br>", StaticMath.calculateStdDeviation(xData)));
    }

    infoString.append("<h2>Variable: " + yName + "</h2>");
    infoString.append(String.format("Mean: %.4f<br>", StaticMath.calculateMean(yData)));
    infoString.append(String.format("Median: %.4f<br>", StaticMath.calculateMedian(yData.clone())));
    infoString.append(String.format("Mode: %.4f<br>", StaticMath.calculateMode(yData)));
    infoString.append(String.format("Std dev: %.4f<br>", StaticMath.calculateStdDeviation(yData)));

    // show stats based on chosen curve
    switch(curveOption) {
      case Exponential:
        infoString.append("<h2>Exponential Fit: y = ar^x</h2>");
        infoString.append(String.format(" a: %.4f<br>", fitParams[0]));
        infoString.append(String.format(" r: %.4f<br>", fitParams[1]));
        break;
      case Power:
        infoString.append("<h2>Power Fit: y = ax^r</h2>");
        infoString.append(String.format(" a: %.4f<br>", fitParams[0]));
        infoString.append(String.format(" r: %.4f<br>", fitParams[1]));
        break;
      case Logarithimic:
        infoString.append("<h2>Logarithimic Fit: y = a*ln(x) + r</h2>");
        infoString.append(String.format(" a: %.4f<br>", fitParams[0]));
        infoString.append(String.format(" r: %.4f<br>", fitParams[1]));
        break;
      case Linear: // fall through for default
      default: 
        infoString.append("<h2>Linear Fit: y = mx + b</h2>");
        infoString.append(String.format(" m: %.4f<br>", fitParams[0]));
        infoString.append(String.format(" b: %.4f<br>", fitParams[1]));
        infoString.append(String.format(" R<sup>2</sup>: %.4f<br>", 
            StaticMath.calculateR2(combineVectors(xData, yData))));
        break;
    }

    infoText.setText(infoString.toString());
  }

  /** Use the linear fit to calculate y values */
  private double[] calculateLinearFitLineValues() {
    double[] y = new double[xData.length];
    double m = fitParams[0];
    double b = fitParams[1];
    for (int i=0; i < xData.length; i++) {
      y[i] = m * xData[i] + b;  // y = mx + b
    }
    return y;
  }

  /** Use the exponential fit to calculate y values */
  private double[] calculateExponetialFitLineValues() {
    double[] y = new double[xData.length];
    double a = fitParams[0];
    double r = fitParams[1];
    for (int i=0; i < xData.length; i++) {
      y[i] =a * Math.pow(r, xData[i]); // y = ar^x
    }
    return y;
  }

  /** Use the power fit to calculate y values */
  private double[] calculatePowerFitLineValues() {
    double[] y = new double[xData.length];
    double a = fitParams[0];
    double r = fitParams[1];
    for (int i=0; i < xData.length; i++) {
      y[i] = a * Math.pow(xData[i], r); // y = ax^r
    }
    return y;
  }

  /** Use the log fit to calculate y values */
  private double[] calculateLogFitLineValues() {
    double[] y = new double[xData.length];
    double a = fitParams[0];
    double r = fitParams[1];
    for (int i=0; i < xData.length; i++) {
      y[i] = a * Math.log(xData[i]) + r; // y = a*ln(x) + r
    }
    return y;
  }


  /** Testing: show chart */
	public static void main(String[] args) {

    JFrame testFrame = new JFrame();
    testFrame.setTitle("TEST launcher: ScatterChartView");
    testFrame.setSize(400, 300);
    testFrame.setMinimumSize(new Dimension(400, 300));
    testFrame.setLocationRelativeTo(null);
    testFrame.setLayout(new BorderLayout());
    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    testFrame.setVisible(true);

    TestCSV csv = new TestCSV();
    // ScatterChartView test = new ScatterChartView(testFrame, csv.getColumn(0), csv.getColumnName(0));
    // test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // kills app directly in this mode

    ScatterChartView test2 = new ScatterChartView(testFrame, csv.getColumn(0), csv.getColumnName(0),
                                                             csv.getColumn(4), csv.getColumnName(4),
                                                             CurveOptionEnum.Power);
    test2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // kills app directly in this mode
	}

}
