package CMSC495BDAT;
/*  File: AbstractChartView.java
    Author: Justin Rhodes
    Date: 20 February 2020
    Purpose: Abstract chart view - build a standard split-pane GUI stucture for chart display
    Version: 0.1
*/

/* Public Methods
    AbstractChartView(JFrame parentFrame) 
*/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.Format;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

abstract class AbstractChartView extends JFrame {

  protected JFrame parentFrame;

  protected JPanel rightPane = new JPanel();
  protected JPanel leftPane = new JPanel();
  protected JEditorPane infoText = new JEditorPane();

  /** Constructor that includes column names */
  public AbstractChartView(JFrame parentFrame) {
    this.parentFrame = parentFrame;

    buildInterface(true);

    // show the finished window
    setVisible(true);
  }

  /** Build out the main panel and table display */
  private void buildInterface(Boolean debugMode) {
    // set up main window design
    setSize(800, 600);
    setMinimumSize(new Dimension(500, 300));
    setLocationRelativeTo(parentFrame);
    setLayout(new BorderLayout());

    // left side - chart space
    leftPane.setLayout(new BorderLayout());

    // right side - text info
    rightPane.setLayout(new BorderLayout());
    infoText.setEditable(false);
    infoText.setContentType("text/html");
    infoText.setText("<h2>Data Statistics</h2>");
    JScrollPane infoScrollPane = new JScrollPane(infoText);
    rightPane.add(infoScrollPane, BorderLayout.CENTER);

    // create the split pane
    JSplitPane centralPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
    centralPane.setResizeWeight(0.85D);  // defines the default split between the panes
    centralPane.setOneTouchExpandable(true);

    // add split pane to main panel
    add(centralPane, BorderLayout.CENTER);
  }

  /** Draw a sample chart - override this in extended charts classes */
  protected abstract void addChart();

  /** Draw a sample chart - override this in extended charts classes */
  protected abstract void setInfoText();

  /** generate a simple 1 to n vector [1, 2, 3, ..., n] */
  protected static double[] getIndexVector(int numRows) {
    double[] index = new double[numRows];
    for (int i=1; i <= numRows; i++) {
      index[i-1] = i;
    }
    return index;
  }

  /** zip an x and y vector together into an xy array */
  protected static double[][] combineVectors(double[] x, double[] y) {
    double[][] combined = new double[x.length][2];
    for (int i=0; i < x.length; i++) {
      combined[i][0] = x[i];
      combined[i][1] = y[i];
    }
    // sort the x,y values just in case - makes plotting better
    Arrays.sort(combined, new Comparator<double[]>() {
      public int compare(double[] a, double[] b) {
          return Double.compare(a[0], b[0]);
      }
    });

    return combined;
  }

}
