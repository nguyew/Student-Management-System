package com.studentmanagement.database;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "/database.properties";
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER_CLASS;
    
    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
               setDefaultConfiguration();
               return;
            }
            
            props.load(input);
            URL = props.getProperty("database.url");
            USERNAME = props.getProperty("database.username");
            PASSWORD = props.getProperty("database.password");
            DRIVER_CLASS = props.getProperty("database.driver");  
            
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            setDefaultConfiguration();
        }
    }

    private static void setDefaultConfiguration() {
        DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        URL = "jdbc:sqlserver://localhost:1433;databaseName=StudentManagementDB;encrypt=false;trustServerCertificate=true";
        USERNAME = "SA";
        PASSWORD = "12345";
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found", e);
        }
    }
    
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    public static void closePreparedStatement(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
    }
    
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }
    
    public static void closeAll(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        closeResultSet(rs);
        closePreparedStatement(pstmt);
        closeConnection(conn);
    }
    
    public static void printConfiguration() {
        System.out.println("=== Database Configuration ===");
        System.out.println("Driver: " + DRIVER_CLASS);
        System.out.println("URL: " + URL);
        System.out.println("Username: " + USERNAME);
        System.out.println("Password: " + (PASSWORD != null ? "***" : "null"));
    }
     
     
}
