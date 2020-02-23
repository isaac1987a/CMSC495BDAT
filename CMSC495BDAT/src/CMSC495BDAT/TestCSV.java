/*  File: TestCSV.java
    Author: Justin Rhodes
    Date: 18 February 2020
    Purpose: For testing purposes, quickly load a CSV file to supply data to other classes
    Version: 0.1
*/

/* Public Methods
    TestCSV()  
    getColumn(int col)
    getColumnName(int col)

   Public Properties
    String[] columnNames
    double[][] dataValues
    int numCols
    int numRows
*/
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/* Reference source: https://javainterviewpoint.com/how-to-read-and-parse-csv-file-in-java/ */
public class TestCSV {
  private static String filename = "heart2.csv";	// manually point to whatever file needed

  // keep vars public for easy testing
  public String[] columnNames = null;
  public double[][] dataValues = null;
  public int numCols = 0;
  public int numRows = 0;

  /** load the CSV */
  public TestCSV() {
    readData();
  }

  /** read the data from the CSV into the storage variables */
  private void readData() {
    Scanner scanner = null;
		try {
			//Get the scanner instance
			scanner = new Scanner(new File(filename));
      scanner.useDelimiter(",");

      // pull headers
      columnNames = scanner.nextLine().split(",");
      numCols = columnNames.length;

      // count rows of data
			while(scanner.hasNextLine()) {
        numRows++;
        scanner.nextLine();
      }
      scanner.close();

      // make a 2d double array
      dataValues = new double[numRows][numCols];

      // re-open to pull data into double array  [yes, inefficient, but quick and easy to code]
      scanner = new Scanner(new File(filename));
      scanner.useDelimiter(",");
      scanner.nextLine(); // skip headers
      for (int i=0; i < numRows; i++) {
        String[] data = scanner.nextLine().split(",");
        dataValues[i] = Arrays.stream(data).mapToDouble(Double::parseDouble).toArray();
      }

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
  }
  
  /** return a given column, 0-based */
  public double[] getColumn(int col) {
    double[] column = new double[numRows];
    for (int i=0; i < numRows; i++) {
      column[i] = dataValues[i][col];
    }
    return column;
  }

  /** return the name of a given column */
  public String getColumnName(int col) {
    return columnNames[col];
  }


  public static void main(String args[]) 	{
     TestCSV x = new TestCSV();
     
     System.out.println(Arrays.toString(x.getColumn(0)));
     System.out.println(x.getColumnName(0));
    }
}