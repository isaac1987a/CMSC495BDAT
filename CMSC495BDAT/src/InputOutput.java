/*  File: InputOutput.java
    Author: Adam Rittermann
    Date: 7 February 2020
    Purpose:  CSV Parser. Passes values to SQL for storage. Returns String[]
            containing "Column Min-Max" for each column. */

/* Public Methods
    parseFile(File file, String dbName);
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class InputOutput {

    // parseFile Variables
    private String[] parseInfo;
    private BufferedReader fileReader;
    private final String DELIMITER = ",";
    private String[] headers;
    private int count;

    // Storage Arrays for Min/Max
    private double[] minValues;
    private double[] maxValues;

    // CSV Array
    /**
     * Parses the chosen CSV file, identifies min & max for each column, and
     * sends data to the SQL Database
     *
     * @param file Chosen Input File as CSV
     * @param dbName String containing DB Name
     * @return String[] parseInfo that includes DBName followed by "Column Name,
     * Min, Max".
     */
    public String[] parseFile(File file, String dbName) {

        try {
            String line;
            count = 0;
            fileReader = new BufferedReader(new FileReader(file));

            // Create Database Instance
            SqlDatabase db = new SqlDatabase();

            // Read First Row of CSV as Headers
            line = fileReader.readLine();
            headers = line.split(DELIMITER);
            minValues = new double[headers.length];
            maxValues = new double[headers.length];

            // Declare Size of Return String Array + 1 For DBName
            parseInfo = new String[headers.length + 1];
            parseInfo[count] = dbName;
            count++;

            // Declare Starting Values for Min and Max Arrays
            for (int i = 0; i < headers.length; i++) {
                minValues[i] = Double.MAX_VALUE;
                maxValues[i] = 0;

            }

            // Create Database Instance
            db.createDatabase(dbName, headers);

            /*
            Convert Additional Rows to Double values and insert into Database.
            Record Min & Max for Each Column of Data
             */
            while ((line = fileReader.readLine()) != null) {
                double[] columns = Arrays.stream(line.split(DELIMITER)).mapToDouble(Double::parseDouble).toArray();
                db.insertDatabase(columns);
                calculateMinAndMax(columns);
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File Not Found.");
        } catch (IOException ioe) {
            System.out.println("ERROR: " + ioe);
        }
        
        for (int i = 0; i < headers.length; i++) {
            parseInfo[count] = headers[i] + " " + minValues[i] + "-" + maxValues[i];
            count++;
        }

        
        /* --------------- FOR TESTING --------------- */
        System.out.println("Min Values: " + Arrays.toString(minValues));
        System.out.println("Max Values: " + Arrays.toString(maxValues));
        System.out.println(Arrays.toString(parseInfo));
        /* --------------- FOR TESTING --------------- */

        return parseInfo;
    }

    /**
     * Records Min & Max for each Column in CSV File
     *
     * @param columns double[] containing current row of values
     */
    private void calculateMinAndMax(double[] columns) {

        for (int i = 0; i < columns.length; i++) {

            // Check Max Value
            double currentValue = columns[i];
            // Check Max Value
            if (currentValue > maxValues[i]) {
                maxValues[i] = currentValue;
            }

            // Check Min Value
            if (currentValue < minValues[i]) {
                minValues[i] = currentValue;
            }
        }
    }
}
