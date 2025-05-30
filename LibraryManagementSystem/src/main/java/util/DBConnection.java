package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1100";

    public static Connection getConnection() throws SQLException {
        System.out.println("Connected to DB");
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}
