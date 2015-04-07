package client;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Kevok on 4/5/2015.
 */
public class DataStorage {
    private static HashMap<String,DataStorage> instances = new HashMap<String, DataStorage>();
    private Connection sqliteConnection;
    private DataStorage() {}
    public static DataStorage getInstance(String file) {
        if (instances.containsKey(file)) return instances.get(file);
        try {
            DataStorage temp = new DataStorage();
            temp.sqliteConnection = DriverManager.getConnection("jdbc:sqlite:"+file);
            instances.put(file,temp);
            System.out.println("Opened connection to "+file);
            return temp;
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public void close() {
        try {
            sqliteConnection.close();
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void writeData(LinkedList<DataPoint> data) {
        Statement tempStatement = null;
        String sql;
        try {
            tempStatement = sqliteConnection.createStatement();
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        sql =
            "CREATE TABLE data (\n" +
                "seconds NOT NULL," +
                "milliseconds NOT NULL,"+
                "magnitude NOT NULL," +
                "PRIMARY KEY(seconds, milliseconds)" +
                ") WITHOUT ROWID;";
        try {
            tempStatement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println(
                e.getClass().getName() +
                ": " +
                e.getMessage() +
                "\n" +
                sql
            );
        }

        sql = ";";
        for (DataPoint point : data) {
            sql += String.format(
                "\nINSERT INTO data VALUES(%d, %d, %f)",
                Math.floor(point.getMagnitude()),
                Math.floor(point.getMagnitude() * 1000) % 1000,
                Math.rint(point.getMagnitude() * 1000) / 1000
            );
        }
        try {
            tempStatement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println(
                e.getClass().getName() +
                    ": " +
                    e.getMessage() +
                    "\n" +
                    sql
            );
        }
        try {
            tempStatement.close();
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
