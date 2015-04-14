import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Kevok on 4/5/2015.
 */
public class DataStorage {
    private static HashMap<String,DataStorage> instances = new HashMap<String, DataStorage>();
    private String location = "";
    private Connection sqliteConnection;
    private DataStorage() {}
    public static DataStorage getInstance(String file) {
        if (instances.containsKey(file)) return instances.get(file);
        String path = new File(file).getAbsolutePath();
        try {
            Class.forName("org.sqlite.JDBC");
            DataStorage temp = new DataStorage();
            String url = "jdbc:sqlite:"+path;
            temp.sqliteConnection = DriverManager.getConnection(url);
            instances.put(file,temp);
            temp.location = file;
            System.out.println("Opened connection to "+path);
            return temp;
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public void close() {
        try {
            sqliteConnection.close();
            instances.remove(location);
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void writeData(LinkedList<DataPoint> data) {
        String sql =
            "CREATE TABLE data (" +
                "seconds NOT NULL," +
                "milliseconds NOT NULL,"+
                "magnitude NOT NULL," +
                "PRIMARY KEY(seconds, milliseconds)" +
            ") WITHOUT ROWID;";
        for (DataPoint point : data) {
            sql += String.format(
                "\nINSERT INTO data VALUES(%d, %d, %f)",
                (int)Math.floor(point.getMagnitude()),
                (int)Math.floor(point.getMagnitude() * 1000) % 1000,
                Math.rint(point.getMagnitude() * 1000) / 1000
            );
        }
        try {
            Statement tempStatement = sqliteConnection.createStatement();
            tempStatement.executeUpdate(sql);
            tempStatement.close();
        } catch ( SQLException e ) {
            System.err.println(
                e.getClass().getName() +
                    ": " + e.getMessage() +
                    "\n" + sql
            );
        }
    }
}