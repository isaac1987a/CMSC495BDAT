package CMSC495BDAT;

/* File:    SqlDatabase.java
   Author:  Steven Rutter
   Date:    6 February 2020
   Purpose: Provide a class for interacting with SQLite3. */

/* Public methods:
       SqlDatabase()
       int createDatabase(String name, String[] columns)
       int insertDatabase(double[] values)
       String[] getColumnDatabase()
       double[] getValuesDatabase(String column, Vector<SQLParameters> params)
       double[][] getValuesDatabase(Vector<SQLParameters> params)
       ArrayList<ArrayList<Double>> exportDatabase()
       ArrayList<Double> getValuesAllDatabase(String column)
       ArrayList<Double> getValuesRangeDatabase(String column, double lower,
               double upper)
       ArrayList<Double> getValuesLessDatabase(String column, double value,
               boolean inclusive)
       ArrayList<Double> getValuesGreaterDatabase(String column, double value,
               boolean inclusive)
       String[] listDatabase()
       String currentDatabase()
       int selectDatabase(String name)
       int deleteDatabase(String name) */

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.System;
import java.nio.file.*; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import GUIObjects.SQLParameters;

public class SqlDatabase
{
    private String currentDatabaseName;

    /* SqlDatabase - constructor. */
    public SqlDatabase()
    {
        this.currentDatabaseName = "";
    }
    
    /* createDatabase - create a new database with specified columns.
     * 
     * @param name The name of the database
     * @param columns Column names
     * @return 1 if error, else 0
     */
    public int createDatabase(String name, String[] columns)
    {
        String url = "jdbc:sqlite:" + name + File.separator + name + ".db";

        String sql = "CREATE TABLE IF NOT EXISTS dataset (";

        for (int i = 0; i < columns.length; i++) {
            sql += columns[i] + " double";
            if (i < columns.length - 1)
                sql += ", ";
        }

        sql += ");";
        
        File dir = new File(name);
        
        if (!dir.exists())
        	dir.mkdirs();

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 1;
        }

        this.currentDatabaseName = name;

        return 0;
    }

    /* insertDatabase - insert a row into active database with specified values.
     * 
     * @param values The values to be inserted as a new row
     * @return 1 if error, else 0
     */
    public int insertDatabase(double[] values)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";

        String sql = "INSERT INTO dataset VALUES (";

        for (int i = 0; i < values.length; i++) {
            sql += values[i];
            if (i < values.length - 1)
                sql += ", ";
        }

        sql += ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 1;
        }

        return 0;
    }

    /* getColumnsDatabase - return column names for active database.
     * 
     * @return Column names
     */
    public String[] getColumnDatabase()
    {
        String[] columns = {};

        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";

        try {
            Connection conn = DriverManager.getConnection(url);

            String sql = "SELECT group_concat(name, '|') ";
            sql += "FROM pragma_table_info('dataset');";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);     
            
            while (rs.next())
                columns = rs.getString(1).split("\\|", -1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } 

        return columns;
    }
    
    /* getValuesDatabase - get all values with provided params.
     * 
     * @param params Vector of SQLParameters for SELECT
     * @return Matching values from database matching params
     */
    double[][] getValuesDatabase(Vector<SQLParameters> params)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";
        ArrayList<ArrayList<Double>> retValues = new ArrayList<>();
        String[] columns = getColumnDatabase();
        String sql = "SELECT * FROM dataset WHERE ";
        
        for (int i = 0; i < params.size(); i++) {
        	SQLParameters param = params.get(i);

        	if (param.valid) {
        		sql += param.columnName + " ";
        		
        		switch (param.operator) {
        			case ">":
        				sql += "> " + param.value;
        				break;
        			case ">=":
        				sql += ">= " + param.value;
        				break;
        			case "=":
        				sql += "= " + param.value;
        				break;
        			case "<=":
        				sql += "<= " + param.value;
        				break;
        			case "<":
        				sql += "< " + param.value;
        				break;
        		}

        		if (i < params.size() - 1) {
        			if (params.get(i + 1).valid) {
            			sql += " AND ";
        			}
        		}
        	} else {
        		if ((i < params.size() - 1) && (params.get(i + 1).valid)) {
        			sql += " AND ";
        		}
        	}
        }
                	
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int i = 0;

            while (rs.next()) {
                retValues.add(new ArrayList<Double>());

                for (int j = 0; j < columns.length; j++)
                    retValues.get(i).add(rs.getDouble(columns[j]));

                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        double[][] values = new double[retValues.size()][];

        for (int i = 0; i < retValues.size(); i++) {
        	ArrayList<Double> retRow = retValues.get(i);
        	double[] retRowArray = new double[retRow.size()];
        	
        	for (int j = 0; j < retRow.size(); j++) {
        		retRowArray[j] = retRow.get(j);
        	}
        	
        	values[i] = retRowArray;
        }
        
        return values;	
    }

    /* getValuesDatabase - get all values for column with provided params.
     * 
     * @param column Column name
     * @param params Vector of SQLParamter parameters for SELECT
     * @return Matching values from the database
     */
    double[] getValuesDatabase(String column, Vector<SQLParameters> params)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";
        ArrayList<Double> retValues = new ArrayList<Double>();
        String sql = "SELECT " + column + " FROM dataset WHERE ";
        
        for (int i = 0; i < params.size(); i++) {
        	SQLParameters param = params.get(i);
        	if (param.valid) {
        		sql += param.columnName + " ";
        		
        		switch (param.operator) {
        			case ">":
        				sql += "> " + param.value;
        				break;
        			case ">=":
        				sql += ">= " + param.value;
        				break;
        			case "=":
        				sql += "= " + param.value;
        				break;
        			case "<=":
        				sql += "<= " + param.value;
        				break;
        			case "<":
        				sql += "< " + param.value;
        				break;
        		}

        		if (i < params.size() - 1) {
        			if (params.get(i + 1).valid) {
            			sql += " AND ";
        			}
        		}
        	} else {
        		if ((i < params.size() - 1) && (params.get(i + 1).valid)) {
        			sql += " AND ";
        		}
        	}
        }
        	
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                retValues.add(rs.getDouble(column));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        double[] values = new double[retValues.size()];

        for (int i = 0; i < retValues.size(); i++)
        	values[i] = retValues.get(i);
        
        return values;
    }

    /* getValuesAllDatabase - returns all values for column in database.
     * 
     * @param column Column name
     * @return All values from the active database
     */
    ArrayList<Double> getValuesAllDatabase(String column)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";
        ArrayList<Double> values = new ArrayList<Double>();
        String sql = "SELECT " + column + " FROM dataset;";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
                values.add(rs.getDouble(column));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return values;
    }

    /* exportDatabase - return two-dimensional array list of entire dataset.
     * 
     * @return Entire database as a 2D ArrayList
     */
    ArrayList<ArrayList<Double>> exportDatabase()
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";
        String sql = "SELECT * FROM dataset;";
        ArrayList<ArrayList<Double>> values = new ArrayList<>();
        String[] columns = getColumnDatabase();

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int i = 0;

            while (rs.next()) {
                values.add(new ArrayList<Double>());

                for (int j = 0; j < columns.length; j++)
                    values.get(i).add(rs.getDouble(columns[j]));

                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return values;
    }

    /* getValuesRangeDatabase - returns all values for column in database within
     * the specified range (inclusive).
     * 
     * @param column Column name
     * @param lower Lower limit for value (inclusive)
     * @param upper Upper limit for value (inclusive)
     * @return All values in database for column, within range
     */
    ArrayList<Double> getValuesRangeDatabase(String column, double lower,
            double upper)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";
        ArrayList<Double> values = new ArrayList<Double>();

        String sql = "SELECT " + column + " FROM dataset ";
        sql += "WHERE " + column + " >= " + lower;
        sql += " AND " + column + " <= " + upper;

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
                values.add(rs.getDouble(column));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return values;
    }

    /* getValuesLessDatabase - returns all values for column in database which
     * are less than specified value with boolean inclusive flag.
     * 
     * @param column Column name
     * @param value Less than value
     * @param Boolean flag to indicate inclusive
     * @return All values for column in the active database less than value
     */
    ArrayList<Double> getValuesLessDatabase(String column, double value, boolean inclusive)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";
        ArrayList<Double> values = new ArrayList<Double>();

        String sql = "SELECT " + column + " FROM dataset ";

        if (inclusive)
            sql += "WHERE " + column + " <= " + value;
        else
            sql += "WHERE " + column + " < " + value;

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
                values.add(rs.getDouble(column));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return values;
    }

    /* getValuesGreaterDatabase - returns all values for column in database which
        are greater than specified value with boolean inclusive flag.
     * 
     * @param column Column name
     * @param value Greater than value
     * @param inclusive Flag indicating inclusive
     * @return 1 if error, else 0
     */
    ArrayList<Double> getValuesGreaterDatabase(String column, double value, boolean inclusive)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + File.separator +
        		currentDatabaseName + ".db";
        ArrayList<Double> values = new ArrayList<Double>();

        String sql = "SELECT " + column + " FROM dataset ";

        if (inclusive)
            sql += "WHERE " + column + " >= " + value;
        else
            sql += "WHERE " + column + " > " + value;

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
                values.add(rs.getDouble(column));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return values;
    }

    /* listDatabase - returns a list of all databases found in subdirectories.
     * 
     * @return Array of database names
     */
    String[] listDatabase()
    {
        String[] names;
        ArrayList<String> namesList = new ArrayList<String>();
        File directory = new File(".");
        
        try {
			Files.walk(Paths.get("."))
			.filter(Files::isRegularFile)
			.forEach((f)->{
			    String file = f.toString();
			    if( file.endsWith(".db"))
			        namesList.add(file);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        names = new String[namesList.size()];
        
        for (int i = 0; i < namesList.size(); i++) {
            names[i] = namesList.get(i).substring(2, namesList.get(i).lastIndexOf(File.separator));
        }

        return names;
    }
    
    /* currentDatabase - get name of the active database.
     * 
     * @return The active database name
     */
    String currentDatabase()
    {
    	return this.currentDatabaseName;
    }

    /* selectDatabase - selects active database as specified. Returns 0 if
     * successful, else returns 1.
     * 
     * @param name Name of database
     * @return 1 if error, else 0
     */
    int selectDatabase(String name)
    {
        File dbFile = new File(name + File.separator + name + ".db");

        if (dbFile.exists()) {
            this.currentDatabaseName = name;
            return 0;
        } else {
            return 1;
        }
    }

    /* deleteDatabase - deletes active database as specified. Returns 0 if
     * successful, else returns 1.
     * 
     * @param name The name of the database to delete
     * @return 1 if error, else 0
     */
    int deleteDatabase(String name)
    {
        try {
            Files.deleteIfExists(Paths.get(name + File.separator + name + ".db"));
        } catch(IOException e) {
            return 1;
        }

        return 0;
    }
}
