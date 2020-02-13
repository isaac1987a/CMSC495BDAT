package CMSC495BDAT;
/*  File: InputOutput.java
    Author: Adam Rittermann
    Date: 10 February 2020
    Purpose:  CSV Parser. Passes values to SQL for storage. Returns
            Min/Max values for each column. Stores current DB Name
            for Last State Load */

 /* Public Methods
    parseFile(File file, String dbName): String[] parseInfo;
    getCurrentDatabase(): String dbName;
    setCurrentDatabase(String dbName): void;
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class InputOutput {

    // parseFile Variables
    private BufferedReader fileReader; 
    private final String DELIMITER = ",";
    private String[] parseInfo;
    private String[] headers;
    private int count;

    // Storage Arrays for Min/Max
    private double[] minValues;
    private double[] maxValues;

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

            // Save Current Chosen Database Name in File for Last State Load
            this.setCurrentDatabase(dbName);

            // Create Database Instance
            SqlDatabase db = new SqlDatabase();
            db.createDatabase(dbName, headers);

            /*
            Convert Additional Rows to Double values and insert into Database.
            Record Min & Max for Each Column of Data
             */
            while ((line = fileReader.readLine()) != null) {
                double[] columns = Arrays.stream(line.split(DELIMITER)).mapToDouble(Double::parseDouble).toArray();
                db.insertDatabase(columns);
                this.getMinAndMax(columns);
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
    private void getMinAndMax(double[] columns) {

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

    /**
     * Saves DBName to a Text File for Last State Load
     *
     * @param dbName Name of Database to be Stored
     */
    public void setCurrentDatabase(String dbName) {
        try (PrintWriter pw = new PrintWriter("DBName.txt")) {
            pw.println(dbName);
        } catch (FileNotFoundException fnf) {
            System.out.println("ERROR: " + fnf);
        }
    }

    /**
     * Reads Stored DBName from Text File for Last State Load. Creates Empty
     * DBName.txt if no Database has been Loaded.
     *
     * @return dbName If File Exists, null if File Does Not Exist
     */
    public String getCurrentDatabase() {

        // Pull Previous DB Name from Text File if Exists
        try {
            fileReader = new BufferedReader(new FileReader("DBName.txt"));
            String dbName = fileReader.readLine();
            return dbName;
        } catch (FileNotFoundException fnf) {
            System.out.println("ERROR: DBName.txt not found. Creating File...");
            this.setCurrentDatabase("");
            return null;
        } catch (IOException ioe) {
            System.out.println("ERROR: " + ioe);
        }
        // Return Null if File not Found
        return null;
    }
}
