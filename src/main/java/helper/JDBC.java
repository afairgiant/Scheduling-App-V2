package helper;

import java.sql.*;

public class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static final String password = "Passw0rd!"; // Password
    public static Connection conn = null;  // Connection Interface
    private static PreparedStatement preparedStatement;
    private static ResultSet result;
    /**
     * Starts the connection to the database
     */
    public static Connection startConnection() {
        try {
            Class.forName(driver); // Locate Driver
            conn = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            //connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return conn;
    }
    /**
     * Method to get the current connection
     * @return connection
     */
    public static Connection getConnection(){
        return conn;
    }
    /**
     * Close connection to the database
     */
    public static void closeConnection() {
        try {
            conn.close();
            //System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }
    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        preparedStatement = conn.prepareStatement(sqlStatement);
    }
    /**
     * Returns a PreparedStatement object.
     * @return PreparedStatement object.
     */
    public static PreparedStatement getPreparedStatement(){
        return preparedStatement;
    }
    /**
     * Executes a prepared statement and returns a ResultSet
     * @return the ResultSet obtained from executing the prepared statement
     * @throws SQLException if a database access error occurs or the SQL statement does not return a ResultSet object.
     */
    public static ResultSet executePreparedStatement() throws SQLException {
        result = preparedStatement.executeQuery();
        return result;
    }
}
