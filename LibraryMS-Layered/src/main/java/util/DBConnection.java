package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/library_ms_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1100";


    public static Connection getConnection() throws SQLException {
        try {
            // Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Get the connection
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure it's in your classpath.");
            throw new SQLException("JDBC Driver not found", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void beginTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }

    public static void commitTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.commit();
        }
    }

    public static void rollbackTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.rollback();
        }
    }

    public static void finalizeTransaction(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error finalizing connection: " + e.getMessage());
            }
        }
    }
}
