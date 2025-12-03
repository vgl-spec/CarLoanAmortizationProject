package com.vismera.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database configuration and connection management.
 * Provides centralized database connectivity for the application.
 * 
 * @author Vismerá Inc.
 */
public class DatabaseConfig {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/car_loan_amortization_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Connection pool settings (for future enhancement)
    private static final int MAX_POOL_SIZE = 10;
    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    
    // Singleton connection for simple usage
    private static Connection sharedConnection = null;
    
    // Static initialization block to load the driver
    static {
        try {
            Class.forName(DB_DRIVER);
            LOGGER.info("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load MySQL JDBC Driver", e);
            throw new RuntimeException("MySQL JDBC Driver not found. Please add mysql-connector-java to the classpath.", e);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConfig() {
    }
    
    /**
     * Get a new database connection.
     * Each call creates a new connection - caller is responsible for closing it.
     * 
     * @return A new database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            throw e;
        }
    }
    
    /**
     * Get the shared connection (singleton pattern).
     * Use for simple operations. For transactions, use getConnection() instead.
     * 
     * @return The shared database connection
     * @throws SQLException if connection fails
     */
    public static Connection getSharedConnection() throws SQLException {
        if (sharedConnection == null || sharedConnection.isClosed()) {
            sharedConnection = getConnection();
        }
        return sharedConnection;
    }
    
    /**
     * Close the shared connection.
     */
    public static void closeSharedConnection() {
        closeConnection(sharedConnection);
        sharedConnection = null;
    }
    
    /**
     * Close a database connection safely.
     * 
     * @param conn The connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }
    
    /**
     * Close all database resources safely.
     * Use in finally blocks or try-with-resources alternative.
     * 
     * @param conn The connection to close
     * @param stmt The statement to close
     * @param rs The result set to close
     */
    public static void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        // Close ResultSet
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
            }
        }
        
        // Close Statement
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing Statement", e);
            }
        }
        
        // Close Connection
        closeConnection(conn);
    }
    
    /**
     * Test the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            boolean isValid = conn.isValid(5); // 5 second timeout
            if (isValid) {
                LOGGER.info("Database connection test successful");
            }
            return isValid;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection test failed", e);
            return false;
        } finally {
            closeConnection(conn);
        }
    }
    
    /**
     * Get the database URL.
     * 
     * @return The database URL
     */
    public static String getDbUrl() {
        return DB_URL;
    }
    
    /**
     * Get the database user.
     * 
     * @return The database username
     */
    public static String getDbUser() {
        return DB_USER;
    }
    
    /**
     * Get connection status message.
     * 
     * @return A status message describing the connection state
     */
    public static String getConnectionStatus() {
        if (testConnection()) {
            return "Connected to " + DB_URL;
        } else {
            return "Disconnected - Unable to connect to database";
        }
    }
    
    /**
     * Begin a transaction on the given connection.
     * 
     * @param conn The connection
     * @throws SQLException if setting auto-commit fails
     */
    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }
    
    /**
     * Commit a transaction on the given connection.
     * 
     * @param conn The connection
     * @throws SQLException if commit fails
     */
    public static void commitTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * Rollback a transaction on the given connection.
     * 
     * @param conn The connection
     */
    public static void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction", e);
            }
        }
    }
    
    /**
     * Execute a simple query to check if the database schema exists.
     * 
     * @return true if the schema is properly set up
     */
    public static boolean isSchemaInitialized() {
        String sql = "SELECT COUNT(*) FROM information_schema.tables " +
                     "WHERE table_schema = 'car_loan_amortization_db' " +
                     "AND table_name IN ('customers', 'cars', 'loans', 'amortization_rows', 'payments', 'admin_settings')";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int tableCount = rs.getInt(1);
                return tableCount >= 6; // All 6 main tables should exist
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error checking schema initialization", e);
            return false;
        }
    }
}
