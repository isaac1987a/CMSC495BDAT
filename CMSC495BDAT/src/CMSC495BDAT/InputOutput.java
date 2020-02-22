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
    loadColumnNames(String dbName): String[] dbSummary;
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
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
            fileReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));

            /* Get rid of UTF-8 BOM issue for bug #2. -sdr */
            fileReader.mark(1);
            if (fileReader.read() != 0xFEFF) {
                fileReader.reset();
            }

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
                double[] columns = Arrays.stream(line.split(DELIMITER))
                        .mapToDouble(Double::parseDouble).toArray();
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

        // Save Summary Data to CSV File
        this.saveColumnNames(dbName);

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
     * Creates New Directory for Each Loaded Database. Saves Current DBName to
     * "CurrentDB.txt" File within main Directory for Last State Load
     *
     * @param dbName Name of Database to be Stored
     */
    public void setCurrentDatabase(String dbName) {
        try {
            File file = new File(dbName);
            if (!file.exists()) {
                file.mkdir();
            }
            try (PrintWriter pw = new PrintWriter(dbName + "\\DBName.txt")) {
                pw.println(dbName);
            }
            try (PrintWriter pw = new PrintWriter("CurrentDB.txt")) {
                pw.println(dbName);
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("ERROR: " + fnf);
        }
    }

    /**
     * Reads Stored DBName from Text File for Last State Load. Creates Empty
     * CurrentDB.txt if no Database has been Loaded.
     *
     * @return dbName If File Exists, null if File Does Not Exist
     */
    public String getCurrentDatabase() {

        // Pull Previous DB Name from Text File if Exists
        try {
            fileReader = new BufferedReader(new FileReader("CurrentDB.txt"));
            String currentDB = fileReader.readLine();
            return currentDB;
        } catch (FileNotFoundException fnf) {
            System.out.println("ERROR: No Current Database. Creating File...");
            this.setCurrentDatabase("");
            return null;
        } catch (IOException ioe) {
            System.out.println("ERROR: " + ioe);
        }
        // Return Null if File not Found
        return null;
    }

    /**
     * Saves Summary Information to CSV File in DB Folder
     */
    private void saveColumnNames(String dbName) {
        // Create File at location DBName/DBNameSummary.csv
        try (PrintWriter pw = new PrintWriter(dbName + "\\" + dbName
                + "Summary.csv")) {
            pw.println("Column,Min,Max");
            for (int i = 0; i < headers.length; i++) {
                pw.println(headers[i] + "," + minValues[i] + "," + maxValues[i]);
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("ERROR: " + fnf);
        }
    }

    /**
     * Loads any previously created CSV Summary file that contains column names
     * with min and max values
     *
     * @param dbName String Database Summary to be Loaded
     * @return String[] of "Column Min-Max" values
     */
    public String[] loadColumnNames(String dbName) {

        // Using Arraylist because initial size is unknown
        ArrayList<String> list = new ArrayList<>();

        try {
            String line;

            File file = new File(dbName + "\\" + dbName + "Summary.csv");
            fileReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));

            /* Get rid of UTF-8 BOM issue for bug #2. -sdr */
            fileReader.mark(1);
            if (fileReader.read() != 0xFEFF) {
                fileReader.reset();
            }

            // Skip first line of headers
            fileReader.readLine();

            // First item is current DBName
            list.add(dbName);

            // Parse CSV Summary File, assumes Column Name, Min, Max (3 vars)
            while ((line = fileReader.readLine()) != null) {
                String[] tempArray = line.split(DELIMITER);
                String tempStr = tempArray[0] + " " + tempArray[1]
                        + "-" + tempArray[2];
                list.add(tempStr);
            }

        } catch (IOException ioe) {
            System.out.println("ERROR: " + ioe);
        }

        // Convert ArrayList back to String Array
        String[] dbSummary = new String[list.size()];
        list.toArray(dbSummary);
        return dbSummary;
    }

    /**
     * Saves Search as New Text File for Future Reference
     *
     * @param dbName String Database Name
     * @param searchName String Name of Search
     * @param searchInfo String Information contained in Search
     */
    public void saveSearch(String dbName, String searchName, String searchInfo) {
        try {
            try (PrintWriter pw = new PrintWriter(dbName + "\\"
                    + searchName + ".txt")) {
                pw.println(searchInfo);
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("ERROR: " + fnf);
        }
    }

    /**
     * Loads Search from Text File.
     *
     * @param dbName String Database Name
     * @param searchName String Name of Search
     * @return String thisSearch search to be loaded
     */
    public String[] loadSearch(String dbName, String searchName) {

        ArrayList<String> searchList = new ArrayList<>();

        // Pull Previous Search from Text File if Exists
        try {
            fileReader = new BufferedReader(new FileReader(dbName + "\\"
                    + searchName + ".txt"));
            String line;
            while ((line = fileReader.readLine()) != null) {
                searchList.add(line);
            }
            String[] searchInfo = new String[searchList.size()];
            searchList.toArray(searchInfo);
            return searchInfo;
        } catch (FileNotFoundException fnf) {
            System.out.println("ERROR: No search found with that name.");
            return null;
        } catch (IOException ioe) {
            System.out.println("ERROR: " + ioe);
        }
        // Return Null if File not Found
        return null;
    }

}
