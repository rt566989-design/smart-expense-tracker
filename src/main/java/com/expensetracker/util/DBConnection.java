package com.expensetracker.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class to manage JDBC database connections.
 * Reads configuration from db.properties if available, with resilient defaults.
 */
public class DBConnection {

    private static String url = "jdbc:mysql://localhost:3306/expense_tracker_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static String username = "root";
    private static String password = "root";
    private static String driver = "com.mysql.cj.jdbc.Driver";

    static {
        loadProperties();
        loadDriver();
    }

    private static void loadProperties() {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);

                if (prop.getProperty("db.url") != null) {
                    url = prop.getProperty("db.url").trim();
                }
                if (prop.getProperty("db.username") != null) {
                    username = prop.getProperty("db.username").trim();
                }
                if (prop.getProperty("db.password") != null) {
                    password = prop.getProperty("db.password").trim();
                }
                if (prop.getProperty("db.driver") != null) {
                    driver = prop.getProperty("db.driver").trim();
                }
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Notice: db.properties could not be loaded, using defaults: " + e.getMessage());
        }
    }

    private static void loadDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            // Fallback to legacy MySQL driver if newer one is not found
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("[DBConnection] ERROR: MySQL JDBC Driver not found in classpath!");
            }
        }
    }

    /**
     * Obtains a new connection to the MySQL database.
     * @return Connection instance
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Safely closes a ResultSet without throwing unchecked exceptions.
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Safely closes a Statement / PreparedStatement without throwing unchecked exceptions.
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Safely closes a Connection without throwing unchecked exceptions.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Convenience method to close all JDBC resources together.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }

    /**
     * Convenience method to close connection and statement.
     */
    public static void close(Connection conn, Statement stmt) {
        close(stmt);
        close(conn);
    }
}

