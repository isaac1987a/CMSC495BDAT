package CMSC495BDAT;
/*  File: TabularView.java
    Author: Justin Rhodes
    Date: 7 February 2020
    Purpose: Display a data set in a spreadsheet-style table
    Version: 0.1 - basic table display
*/

/* Public Methods
    TabularView(JFrame parentFrame, double[][] dataValues, String[] columnNames)  
    TabularView(JFrame parentFrame, double[][] dataValues)
*/

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.text.Format;

import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;


public class TabularView extends JFrame {

  private double[][] dataValues;
  private String[] columnNames;
  private JFrame parentFrame;
  private int[] indexLimits;

  /** Constructor without column names */
  public TabularView(JFrame parentFrame, double[][] dataValues) {
    this(parentFrame, dataValues, makeColumnNumberNames(dataValues[0].length));
  }

  /** Constructor that includes column names */
  public TabularView(JFrame parentFrame, double[][] dataValues, String[] columnNames) {
    this.parentFrame = parentFrame;
    this.dataValues = dataValues;
    this.columnNames = columnNames;

    buildInterface(true);

    // show the finished window
    setVisible(true);
  }

  /** Make a string array of one-up numbers for column headers */
  private static String[] makeColumnNumberNames(int numColumns) {
    String[] columnNames = new String[numColumns];
    for (int i = 0; i < numColumns; i++) {
      columnNames[i] = String.valueOf(i+1);
    }
    return columnNames;
  }

  /** Build out the main panel and table display */
  private void buildInterface(Boolean debugMode) {
    // set up main window design
    setTitle("Table View");
    setSize(800, 600);
    setMinimumSize(new Dimension(400, 300));
    setLocationRelativeTo(parentFrame);
    setLayout(new BorderLayout());

    // build the table
    TabularViewModel tableModel = new TabularViewModel(dataValues, columnNames);
    JTable tableView = new JTable(tableModel);
    // JTable tableView = new JTable(dataValues, columnNames);
    tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    JScrollPane scrollPane = new JScrollPane(tableView, 
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(scrollPane);
  }

  /** Internal class to drive the table data model */
  private class TabularViewModel extends AbstractTableModel {

    private double[][] dataValues;
    private String[] columnNames;

    TabularViewModel(double[][] dataValues, String[] columnNames) {
      this.dataValues = dataValues;
      this.columnNames = columnNames;
    }

    // public void setData(double[][] dataValues){
    //   this.dataValues = dataValues;
    // }

    // public void setColumnNames(String[] columnNames) {
    //   this.columnNames = columnNames;
    // }

    public int getRowCount() {
      return dataValues.length;
    }
    
    public int getColumnCount() {
      return dataValues[0].length+1;
    }

    public String getColumnName(int column) {
      if (column == 0) {
        return "Row";
      } else {
        return columnNames[column-1];
      }
    }
  
    public Object getValueAt(int row, int column){
      if (column == 0) {
        return row+1;
      } else {
        return dataValues[row][column-1];
      }
    }
  
  }


  /** Display panel with fake data for testing purposes. */
	public static void main(String[] args) {
    System.out.println("TEST MODE: Launch TabularView window with test data");

    JFrame testFrame = new JFrame();
    testFrame.setTitle("TEST launcher: TabularView");
    testFrame.setSize(400, 300);
    testFrame.setMinimumSize(new Dimension(400, 300));
    testFrame.setLocationRelativeTo(null);
    testFrame.setLayout(new BorderLayout());
    testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    testFrame.setVisible(true);

    double[][] testData = new double[10000][100];
    Random randomGenerator = new Random(1);
    for (int i = 0; i < testData.length; i++) {
      for (int j = 0; j < testData[i].length; j++) {
        testData[i][j] = randomGenerator.nextDouble()*100;
      }
    }
    // System.out.println( Arrays.deepToString(testData) );

    TabularView myView = new TabularView(testFrame, testData);
    
	}

}



