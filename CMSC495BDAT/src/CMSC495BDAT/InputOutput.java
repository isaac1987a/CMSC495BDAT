package CMSC495BDAT;


/*  File: InputOutput.java
    Author: Adam Rittermann
    Date: 28 February 2020
    Purpose: I/O class. Parses Chosen CSV file for Database Storage.
    Stores Current Database Name and retains a Search History.
    Exports Selected Database Data to File. */

 /* Public Methods
    parseFile(File file, String dbName): String[] parseInfo;
    setCurrentDatabase(String dbName): void;
    getCurrentDatabase(): String dbName;
    loadColumnNames(String dbName): ComboItemDualString[] loadColumnNames
    saveSearch(Vector<SearchOption> searchOptions, int id): void
    loadSearch(String dbName, int id): Vector<SearchOption> searchOptions
    loadFile(String dbName, String fileType): Object
    exportDB(double[][] valuesDatabase, File selectedFile): void
    updateSearchHistory(Vector<ComboItem> searchHistoryVector): void
    getSearchHistory(): Vector<ComboItem>
    removeSearch(int key): void
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import GUIObjects.ComboItem;
import GUIObjects.ComboItemDualString;
import GUIObjects.SearchOption;
import java.io.BufferedWriter;
import java.io.FileWriter;

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
            System.out.println("DB File Not Found");
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
        ArrayList<ComboItemDualString> contentsArray = 
                new ArrayList<ComboItemDualString>();

        String fileName = "Summary.csv";

        // Contents of Summary Info
        for (int i = 0; i < headers.length; i++) {
            contentsArray.add(new ComboItemDualString(headers[i], minValues[i] 
                    + "-" + maxValues[i]));
        }
        
        ComboItemDualString[] comboItemArray 
                = new ComboItemDualString[contentsArray.size()];
        comboItemArray = contentsArray.toArray(comboItemArray);
        this.createFile(dbName, fileName, comboItemArray);
    }

    /**
     * Loads any previously created CSV Summary file that contains column names
     * with min and max values
     *
     * @param dbName String Database Summary to be Loaded
     * @return ComboItemDualString[] of "Column Min-Max" values
     */
    public ComboItemDualString[] loadColumnNames(String dbName) {
        return (ComboItemDualString[]) loadFile(dbName, "Summary.csv");
    }

    /**
     * Save Search Options as SSV File for Future Reference
     *
     * @param searchOptions Vector<SearchOption>
     * @param id int search ID
     */
    public void saveSearch(Vector<SearchOption> searchOptions, int id) {

        String dbName = this.getCurrentDatabase();
        String fileType = id + ".ssv";
        this.createFile(dbName, fileType, searchOptions);
    }

    /**
     * Loads Past Search from SSV File
     *
     * @param dbName String Database Name
     * @param id int search ID
     * @return Vector<SearchOption> Object containing search options
     */
    public Vector<SearchOption> loadSearch(String dbName, int id) {
        Object obj = loadFile(dbName, id + ".ssv");
        if (obj == null) {
            return null;
        } else {
            return (Vector<SearchOption>) obj;
        }
    }

    /**
     * Support Method to Create Various Files in Database Directory
     *
     * @param dbName String Database Name
     * @param fileType String File Type
     * @param outputObject Object to be stored
     */
    private void createFile(String dbName, String fileType,
            Object outputObject) {
        try {
            FileOutputStream fs = new FileOutputStream(dbName + "\\" + fileType, false);
            ObjectOutputStream out = new ObjectOutputStream(fs);
            out.writeObject(outputObject);
            out.close();
            fs.close();
        } catch (IOException ioe) {
            System.out.println("ERROR: " + ioe);
        }
    }

    /**
     * Support Method to Load Data Files
     *
     * @param dbName String Database Name
     * @param fileType String File Type
     * @return Object Containing File Data
     */
    public Object loadFile(String dbName, String fileType) {
        Object obj = new Object();
        //Check if the file exists
        File file = new File(dbName + "\\" + fileType);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream fs = new FileInputStream(dbName + "\\" + fileType);
            ObjectInputStream in = new ObjectInputStream(fs);
            obj = in.readObject();
            in.close();
            fs.close();
        } catch (FileNotFoundException fne) {
            return null;
        } catch (IOException | ClassNotFoundException exc) {
            System.out.println("ERROR: " + exc);
        }
        return obj;
    }

    
    /**
     * Exports DB Data to CSV File
     * 
     * @param valuesDatabase double[][] containing DB Data
     * @param selectedFile File Selected Save File
     */
    public void exportDB(double[][] valuesDatabase, File selectedFile) {
        
        try {
            StringBuilder sb = new StringBuilder();
            
            for (double[] valuesDatabase1 : valuesDatabase) {
                for (int j = 0; j < valuesDatabase1.length; j++) {
                    sb.append(valuesDatabase1[j]+"");
                    if (j < valuesDatabase1.length - 1) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(
                    selectedFile.getAbsolutePath(), false));
            bw.write(sb.toString());
            bw.close();
        } catch (IOException ioe) {
            System.out.println("ERROR: " + ioe);
        }   
    }

    
    /**
     * Creates or Overrides the Persistent Search History File that includes
     * Historical Search Options and IDs for Current DB
     *
     * @param searchHistoryVector Vector of Search Options and Keys
     */
    public void updateSearchHistory(Vector<ComboItem> searchHistoryVector) {
        createFile(this.getCurrentDatabase(), "SearchHistory.shi", searchHistoryVector);
    }

    /**
     * Loads the Persistent Search History File for Current DB
     *
     * @return Vector Search History and IDs
     */
    public Vector<ComboItem> getSearchHistory() {
        Object obj = loadFile(this.getCurrentDatabase(), "SearchHistory.shi");
        if (obj == null) {
            return null;
        } else {
            try {
                return Vector.class.cast(obj);
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Deletes the Search History File Associated with the Search ID Key for the
     * Current DB
     *
     * @param key int Search ID
     */
    public void removeSearch(int key) {
        File file = new File(this.getCurrentDatabase() + "\\" + key + ".ssv");
        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }
    }
}
