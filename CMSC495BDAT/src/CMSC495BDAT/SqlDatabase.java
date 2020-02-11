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
        Returns 0 if there are no errors, else returns 1. */
    public int createDatabase(String name, String[] columns)
    {
        String url = "jdbc:sqlite:" + name + ".db";

        String sql = "CREATE TABLE IF NOT EXISTS dataset (";

        for (int i = 0; i < columns.length; i++) {
            sql += columns[i] + " double";
            if (i < columns.length - 1)
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

        this.currentDatabaseName = name;

        return 0;
    }

    /* insertDatabase - insert a row into active database with specified values.
        Returns 0 if there are no errors, else returns 1. */
    public int insertDatabase(double[] values)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";

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

    /* getColumnsDatabase - return column names for active database. */
    public String[] getColumnDatabase()
    {
        String[] columns = {};

        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";

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
    
    /* getValuesDatabase - get all values with provided params. */
    double[][] getValuesDatabase(Vector<SQLParameters> params)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";
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
        		
        		if (i < params.size() - 1)
        			sql += " AND ";
        		else
        			sql += ";";
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

    /* getValuesDatabase - get all values for column with provided params. */
    double[] getValuesDatabase(String column, Vector<SQLParameters> params)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";
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
        		
        		if (i < params.size() - 1)
        			sql += " AND ";
        		else
        			sql += ";";
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

    /* getValuesAllDatabase - returns all values for column in database. */
    ArrayList<Double> getValuesAllDatabase(String column)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";
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

    /* exportDatabase - return two-dimensional array list of entire dataset. */
    ArrayList<ArrayList<Double>> exportDatabase()
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";
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
        the specified range (inclusive). */
    ArrayList<Double> getValuesRangeDatabase(String column, double lower,
            double upper)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";
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
        are less than specified value with boolean inclusive flag. */
    ArrayList<Double> getValuesLessDatabase(String column, double value, boolean inclusive)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";
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
        are greater than specified value with boolean inclusive flag. */
    ArrayList<Double> getValuesGreaterDatabase(String column, double value, boolean inclusive)
    {
        String url = "jdbc:sqlite:" + currentDatabaseName + ".db";
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

    /* listDatabase - returns a list of all databases. */
    String[] listDatabase()
    {
        String[] names;
        File directory = new File(".");

        FilenameFilter filter = new FilenameFilter(){
             public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".db"));
             }
        };

        names = directory.list(filter);

        for (int i = 0; i < names.length; i++)
            names[i] = names[i].substring(0, names[i].lastIndexOf('.'));

        return names;
    }
    
    String currentDatabase()
    {
    	return this.currentDatabaseName;
    }

    /* selectDatabase - selects active database as specified. Returns 0 if
        successful, else returns 1. */
    int selectDatabase(String name)
    {
        File dbFile = new File(name + ".db");

        if (dbFile.exists()) {
            this.currentDatabaseName = name;
            return 0;
        } else {
            return 1;
        }
    }

    /* deleteDatabase - deletes active database as specified. Returns 0 if
        successful, else returns 1. */
    int deleteDatabase(String name)
    {
        try {
            Files.deleteIfExists(Paths.get(name + ".db"));
        } catch(IOException e) {
            return 1;
        }

        return 0;
    }
}
