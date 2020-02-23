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
    ScatterChartView(JFrame parentFrame, double[] yData, String colName)  
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


public class ScatterChartView extends AbstractChartView {

  private double[] xData = null;
  private double[] yData = null;
  private String colName = null;
  private double[] linearFitParams = null;

  /** Constructor that includes column names */
  public ScatterChartView(JFrame parentFrame, double[] yData, String colName) {
    super(parentFrame);
    this.yData = yData;
    this.xData = getIndexVector(yData.length); // give it a 1:n index
    this.colName = colName;

    // calculate a linear fit from the input data
    linearFitParams = StaticMath.calculateBestFitLine(combineVectors(xData, yData));

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
    XYChart chart = new XYChartBuilder().xAxisTitle("Row Number").yAxisTitle("Value").build();

    // Customize chart properties
    chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
    chart.getStyler().setLegendLayout(LegendLayout.Horizontal);
    chart.getStyler().setChartTitleVisible(false);
    chart.getStyler().setToolTipsEnabled(true);

    // Add primary data series
    XYSeries dataSeries = chart.addSeries(colName, xData, yData);
    dataSeries.setLineStyle(SeriesLines.NONE);
    dataSeries.setMarker(SeriesMarkers.CIRCLE);
    
    // Show the linear fit series too
    XYSeries linearSeries = chart.addSeries("Linear fit", xData, calculateFitLineValues());
    linearSeries.setLineColor(Color.RED);
    linearSeries.setLineStyle(SeriesLines.SOLID);
    linearSeries.setLineWidth(5);
    linearSeries.setMarker(SeriesMarkers.NONE);

    // add the chart into the swing panel
    JPanel chartPanel = new XChartPanel<XYChart>(chart);
    leftPane.add(chartPanel, BorderLayout.CENTER);
  }

  /** Set the info text on the right side panel */
  protected void setInfoText() {
    StringBuilder infoString = new StringBuilder();

    infoString.append("<h2>Data Statistics: " + colName + "</h2>");
    infoString.append(String.format("Mean: %.4f<br>", StaticMath.calculateMean(yData)));
    infoString.append(String.format("Median: %.4f<br>", StaticMath.calculateMedian(yData.clone())));
    infoString.append(String.format("Mode: %.4f<br>", StaticMath.calculateMode(yData)));
    infoString.append(String.format("Std dev: %.4f<br>", StaticMath.calculateStdDeviation(yData)));
    
    infoString.append("<h3>Linear Fit: y=mx+b</h3>");
    infoString.append(String.format(" m: %.4f<br>", linearFitParams[0]));
    infoString.append(String.format(" b: %.4f<br>", linearFitParams[1]));
    infoString.append(String.format(" R<sup>2</sup>: %.4f<br>", 
        StaticMath.calculateR2(combineVectors(xData, yData))));

    infoText.setText(infoString.toString());
  }

  /** Use the linear fit to calculate y values */
  private double[] calculateFitLineValues() {
    double[] y = new double[xData.length];
    double m = linearFitParams[0];
    double b = linearFitParams[1];
    for (int i=0; i < xData.length; i++) {
      y[i] = m * xData[i] + b;
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
    ScatterChartView test = new ScatterChartView(testFrame, csv.getColumn(0), csv.getColumnName(0));
    test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // kills app directly in this mode
	}

}
