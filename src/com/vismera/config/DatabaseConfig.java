package com.vismera.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database configuration and connection management.
 * Uses H2 embedded database - no external server required.
 * Database file is stored in the 'data' folder next to the application.
 * 
 * @author Vismerá Inc.
 */
public class DatabaseConfig {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    
    // H2 Database connection parameters
    private static final String DB_NAME = "carloan_db";
    private static final String DB_FOLDER = "data";
    private static final String DB_URL;
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "org.h2.Driver";
    
    // Connection pool settings
    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    
    // Singleton connection for simple usage
    private static Connection sharedConnection = null;
    
    // Flag to track if schema has been initialized
    private static boolean schemaInitialized = false;
    
    // Static initialization block
    static {
        // Build database path relative to user home directory for portability
        String userHome = System.getProperty("user.home");
        String dbPath = userHome + File.separator + ".vismera" + File.separator + DB_FOLDER + File.separator + DB_NAME;
        
        // H2 URL with MySQL compatibility mode and auto-create
        DB_URL = "jdbc:h2:file:" + dbPath + ";MODE=MySQL;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
        
        try {
            Class.forName(DB_DRIVER);
            LOGGER.info("H2 JDBC Driver loaded successfully");
            
            // Create data directory if it doesn't exist
            File dataDir = new File(userHome + File.separator + ".vismera" + File.separator + DB_FOLDER);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                LOGGER.info("Created database directory: " + dataDir.getAbsolutePath());
            }
            
            // Initialize schema on first load
            initializeSchema();
            
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load H2 JDBC Driver", e);
            throw new RuntimeException("H2 JDBC Driver not found. Please add h2-*.jar to the classpath.", e);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConfig() {
    }
    
    /**
     * Initialize the database schema if not already done.
     * Creates all tables on first run.
     */
    private static void initializeSchema() {
        if (schemaInitialized) {
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if tables already exist
            if (tablesExist(conn)) {
                LOGGER.info("Database schema already initialized");
                schemaInitialized = true;
                return;
            }
            
            // Execute DDL to create tables
            executeDDL(conn);
            
            // Insert default data
            insertDefaultData(conn);
            
            schemaInitialized = true;
            LOGGER.info("Database schema initialized successfully");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database schema", e);
        }
    }
    
    /**
     * Check if tables already exist
     */
    private static boolean tablesExist(Connection conn) {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = 'CUSTOMERS'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error checking if tables exist", e);
        }
        return false;
    }
    
    /**
     * Execute DDL statements to create database schema
     */
    private static void executeDDL(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Customers table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    full_name VARCHAR(100) NOT NULL,
                    contact_number VARCHAR(20),
                    email VARCHAR(100),
                    address VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            LOGGER.info("Created customers table");
            
            // Cars table - note: "year" is quoted as it's a reserved word in H2
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cars (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    make VARCHAR(50) NOT NULL,
                    model VARCHAR(50) NOT NULL,
                    "year" INT NOT NULL,
                    price DECIMAL(15, 2) NOT NULL,
                    category VARCHAR(50),
                    color VARCHAR(30),
                    mpg INT,
                    image_path VARCHAR(255),
                    notes TEXT,
                    is_available BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            LOGGER.info("Created cars table");
            
            // Loans table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS loans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    customer_id INT NOT NULL,
                    car_id INT NOT NULL,
                    principal DECIMAL(15, 2) NOT NULL,
                    apr DECIMAL(5, 2) NOT NULL,
                    compounding VARCHAR(20) DEFAULT 'monthly',
                    term_months INT NOT NULL,
                    payment_frequency VARCHAR(20) DEFAULT 'monthly',
                    start_date DATE NOT NULL,
                    penalty_rate DECIMAL(5, 2) DEFAULT 0.00,
                    penalty_type VARCHAR(20) DEFAULT 'percent_per_month',
                    grace_period_days INT DEFAULT 5,
                    down_payment DECIMAL(15, 2) DEFAULT 0.00,
                    trade_in_value DECIMAL(15, 2) DEFAULT 0.00,
                    sales_tax_rate DECIMAL(5, 2) DEFAULT 0.00,
                    registration_fee DECIMAL(10, 2) DEFAULT 0.00,
                    monthly_payment DECIMAL(15, 2),
                    total_interest DECIMAL(15, 2),
                    total_amount DECIMAL(15, 2),
                    status VARCHAR(20) DEFAULT 'active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (customer_id) REFERENCES customers(id),
                    FOREIGN KEY (car_id) REFERENCES cars(id)
                )
            """);
            LOGGER.info("Created loans table");
            
            // Amortization rows table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS amortization_rows (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    loan_id INT NOT NULL,
                    period_index INT NOT NULL,
                    due_date DATE NOT NULL,
                    opening_balance DECIMAL(15, 2) NOT NULL,
                    scheduled_payment DECIMAL(15, 2) NOT NULL,
                    principal_paid DECIMAL(15, 2) NOT NULL,
                    interest_paid DECIMAL(15, 2) NOT NULL,
                    penalty_amount DECIMAL(15, 2) DEFAULT 0.00,
                    extra_payment DECIMAL(15, 2) DEFAULT 0.00,
                    closing_balance DECIMAL(15, 2) NOT NULL,
                    paid BOOLEAN DEFAULT FALSE,
                    paid_date DATE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE
                )
            """);
            LOGGER.info("Created amortization_rows table");
            
            // Payments table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS payments (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    loan_id INT NOT NULL,
                    payment_date DATE NOT NULL,
                    amount DECIMAL(15, 2) NOT NULL,
                    applied_to_period INT,
                    type VARCHAR(20) DEFAULT 'regular',
                    penalty_applied DECIMAL(15, 2) DEFAULT 0.00,
                    principal_applied DECIMAL(15, 2) DEFAULT 0.00,
                    interest_applied DECIMAL(15, 2) DEFAULT 0.00,
                    note TEXT,
                    recorded_by VARCHAR(100),
                    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE
                )
            """);
            LOGGER.info("Created payments table");
            
            // Admin settings table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS admin_settings (
                    key_name VARCHAR(50) PRIMARY KEY,
                    value_text VARCHAR(255),
                    description VARCHAR(255),
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            LOGGER.info("Created admin_settings table");
            
            // Create indexes
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_loans_customer ON loans(customer_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_loans_car ON loans(car_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_amort_loan ON amortization_rows(loan_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_payments_loan ON payments(loan_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_payments_date ON payments(payment_date)");
            LOGGER.info("Created indexes");
            
        }
        LOGGER.info("DDL statements executed successfully");
    }
    
    /**
     * Insert default data into tables
     */
    private static void insertDefaultData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Insert default admin settings with PHP currency
            stmt.execute("""
                INSERT INTO admin_settings (key_name, value_text, description) VALUES
                ('company_name', 'Vismerá Inc.', 'Company name for reports'),
                ('default_apr', '6.5', 'Default annual percentage rate'),
                ('default_term_months', '60', 'Default loan term in months'),
                ('default_penalty_rate', '2.0', 'Default penalty rate'),
                ('default_penalty_type', 'percent_per_month', 'Default penalty calculation type'),
                ('default_grace_period', '5', 'Default grace period in days'),
                ('default_sales_tax_rate', '12.0', 'Default sales tax rate (VAT)'),
                ('default_registration_fee', '25000.00', 'Default registration fee'),
                ('default_compounding', 'monthly', 'Default compounding frequency'),
                ('currency_symbol', '₱', 'Currency symbol for display (Philippine Peso)'),
                ('date_format', 'MM/dd/yyyy', 'Date format for display'),
                ('app_version', '3.0.0', 'Application version')
            """);
            LOGGER.info("Inserted admin settings");
            
            // Insert sample cars with PHP prices
            stmt.execute("""
                INSERT INTO cars (make, model, "year", price, category, color, mpg, image_path, is_available) VALUES
                ('Mercedes-Benz', 'S-Class', 2024, 8500000.00, 'Luxury Sedan', 'Silver', 24, 'mercedes_sclass.png', TRUE),
                ('Porsche', '911 Carrera', 2024, 12500000.00, 'Sports Car', 'Red', 20, 'porsche_911.png', TRUE),
                ('BMW', 'X5 M', 2024, 7800000.00, 'Luxury SUV', 'Black', 22, 'bmw_x5m.png', TRUE),
                ('Audi', 'RS7', 2024, 9200000.00, 'Sports Car', 'Gray', 21, 'audi_rs7.png', TRUE),
                ('Lexus', 'LS 500', 2024, 6500000.00, 'Luxury Sedan', 'White', 25, 'lexus_ls500.png', TRUE),
                ('Range Rover', 'Sport', 2024, 8900000.00, 'Luxury SUV', 'Green', 19, 'rangerover_sport.png', TRUE)
            """);
            LOGGER.info("Inserted sample cars");
            
            // Insert Filipino customers with Philippine phone numbers (+63)
            stmt.execute("""
                INSERT INTO customers (full_name, contact_number, email, address) VALUES
                ('Juan Carlos Dela Cruz', '+63 917 123 4567', 'juan.delacruz@email.com', '123 Rizal Avenue, Makati City, Metro Manila'),
                ('Maria Santos Garcia', '+63 918 234 5678', 'maria.garcia@email.com', '456 EDSA, Quezon City, Metro Manila'),
                ('Jose Andres Reyes', '+63 919 345 6789', 'jose.reyes@email.com', '789 Ayala Avenue, BGC, Taguig City'),
                ('Ana Patricia Villanueva', '+63 920 456 7890', 'ana.villanueva@email.com', '321 Session Road, Baguio City, Benguet'),
                ('Roberto Miguel Fernandez', '+63 921 567 8901', 'roberto.fernandez@email.com', '555 Colon Street, Cebu City, Cebu'),
                ('Carmen Lucia Bautista', '+63 922 678 9012', 'carmen.bautista@email.com', '777 Bonifacio Drive, Davao City, Davao del Sur'),
                ('Antonio Luis Gonzales', '+63 923 789 0123', 'antonio.gonzales@email.com', '888 Gen. Luna Street, Iloilo City, Iloilo'),
                ('Isabella Marie Cruz', '+63 928 890 1234', 'isabella.cruz@email.com', '999 Magsaysay Boulevard, Pasay City, Metro Manila')
            """);
            LOGGER.info("Inserted Filipino customers");
            
            // Insert sample loans (3 active, 1 closed)
            stmt.execute("""
                INSERT INTO loans (customer_id, car_id, principal, apr, compounding, term_months, payment_frequency,
                    start_date, penalty_rate, penalty_type, grace_period_days, down_payment, trade_in_value,
                    sales_tax_rate, registration_fee, monthly_payment, total_interest, total_amount, status) VALUES
                (1, 1, 7650000.00, 6.5, 'monthly', 60, 'monthly', '2024-01-15', 2.0, 'percent_per_month', 5, 
                    850000.00, 0.00, 12.0, 25000.00, 149523.45, 1321407.00, 8971407.00, 'active'),
                (2, 3, 7020000.00, 7.0, 'monthly', 48, 'monthly', '2024-03-01', 2.0, 'percent_per_month', 5,
                    780000.00, 0.00, 12.0, 25000.00, 168125.50, 1070024.00, 8090024.00, 'active'),
                (3, 5, 5850000.00, 5.5, 'monthly', 36, 'monthly', '2024-06-01', 2.0, 'percent_per_month', 5,
                    650000.00, 0.00, 12.0, 25000.00, 176543.21, 503555.56, 6353555.56, 'active'),
                (4, 2, 11250000.00, 6.0, 'monthly', 60, 'monthly', '2023-01-01', 2.0, 'percent_per_month', 5,
                    1250000.00, 0.00, 12.0, 25000.00, 217500.00, 1800000.00, 13050000.00, 'closed')
            """);
            LOGGER.info("Inserted sample loans");
            
            // Insert sample payments for active loans
            stmt.execute("""
                INSERT INTO payments (loan_id, payment_date, amount, applied_to_period, type, 
                    penalty_applied, principal_applied, interest_applied, note, recorded_by) VALUES
                (1, '2024-02-15', 149523.45, 1, 'regular', 0.00, 108073.45, 41450.00, 'First payment', 'System'),
                (1, '2024-03-15', 149523.45, 2, 'regular', 0.00, 108658.84, 40864.61, 'On-time payment', 'System'),
                (1, '2024-04-15', 149523.45, 3, 'regular', 0.00, 109247.82, 40275.63, 'On-time payment', 'System'),
                (1, '2024-05-15', 149523.45, 4, 'regular', 0.00, 109840.41, 39683.04, 'On-time payment', 'System'),
                (1, '2024-06-15', 149523.45, 5, 'regular', 0.00, 110436.63, 39086.82, 'On-time payment', 'System'),
                (2, '2024-04-01', 168125.50, 1, 'regular', 0.00, 127210.50, 40915.00, 'First payment', 'System'),
                (2, '2024-05-01', 168125.50, 2, 'regular', 0.00, 127952.81, 40172.69, 'On-time payment', 'System'),
                (2, '2024-06-01', 168125.50, 3, 'regular', 0.00, 128699.87, 39425.63, 'On-time payment', 'System'),
                (3, '2024-07-01', 176543.21, 1, 'regular', 0.00, 149668.21, 26875.00, 'First payment', 'System'),
                (3, '2024-08-01', 176543.21, 2, 'regular', 0.00, 150354.43, 26188.78, 'On-time payment', 'System')
            """);
            LOGGER.info("Inserted sample payments");
            
            // Insert amortization rows for loan 1 (first 6 months)
            stmt.execute("""
                INSERT INTO amortization_rows (loan_id, period_index, due_date, opening_balance, scheduled_payment,
                    principal_paid, interest_paid, penalty_amount, extra_payment, closing_balance, paid, paid_date) VALUES
                (1, 1, '2024-02-15', 7650000.00, 149523.45, 108073.45, 41450.00, 0.00, 0.00, 7541926.55, TRUE, '2024-02-15'),
                (1, 2, '2024-03-15', 7541926.55, 149523.45, 108658.84, 40864.61, 0.00, 0.00, 7433267.71, TRUE, '2024-03-15'),
                (1, 3, '2024-04-15', 7433267.71, 149523.45, 109247.82, 40275.63, 0.00, 0.00, 7324019.89, TRUE, '2024-04-15'),
                (1, 4, '2024-05-15', 7324019.89, 149523.45, 109840.41, 39683.04, 0.00, 0.00, 7214179.48, TRUE, '2024-05-15'),
                (1, 5, '2024-06-15', 7214179.48, 149523.45, 110436.63, 39086.82, 0.00, 0.00, 7103742.85, TRUE, '2024-06-15'),
                (1, 6, '2024-07-15', 7103742.85, 149523.45, 111036.51, 38486.94, 0.00, 0.00, 6992706.34, FALSE, NULL),
                (1, 7, '2024-08-15', 6992706.34, 149523.45, 111640.07, 37883.38, 0.00, 0.00, 6881066.27, FALSE, NULL),
                (1, 8, '2024-09-15', 6881066.27, 149523.45, 112247.33, 37276.12, 0.00, 0.00, 6768818.94, FALSE, NULL)
            """);
            LOGGER.info("Inserted amortization rows for loan 1");
            
            // Insert amortization rows for loan 2 (first 4 months)
            stmt.execute("""
                INSERT INTO amortization_rows (loan_id, period_index, due_date, opening_balance, scheduled_payment,
                    principal_paid, interest_paid, penalty_amount, extra_payment, closing_balance, paid, paid_date) VALUES
                (2, 1, '2024-04-01', 7020000.00, 168125.50, 127210.50, 40915.00, 0.00, 0.00, 6892789.50, TRUE, '2024-04-01'),
                (2, 2, '2024-05-01', 6892789.50, 168125.50, 127952.81, 40172.69, 0.00, 0.00, 6764836.69, TRUE, '2024-05-01'),
                (2, 3, '2024-06-01', 6764836.69, 168125.50, 128699.87, 39425.63, 0.00, 0.00, 6636136.82, TRUE, '2024-06-01'),
                (2, 4, '2024-07-01', 6636136.82, 168125.50, 129451.74, 38673.76, 0.00, 0.00, 6506685.08, FALSE, NULL)
            """);
            LOGGER.info("Inserted amortization rows for loan 2");
            
            // Insert amortization rows for loan 3 (first 3 months)
            stmt.execute("""
                INSERT INTO amortization_rows (loan_id, period_index, due_date, opening_balance, scheduled_payment,
                    principal_paid, interest_paid, penalty_amount, extra_payment, closing_balance, paid, paid_date) VALUES
                (3, 1, '2024-07-01', 5850000.00, 176543.21, 149668.21, 26875.00, 0.00, 0.00, 5700331.79, TRUE, '2024-07-01'),
                (3, 2, '2024-08-01', 5700331.79, 176543.21, 150354.43, 26188.78, 0.00, 0.00, 5549977.36, TRUE, '2024-08-01'),
                (3, 3, '2024-09-01', 5549977.36, 176543.21, 151044.26, 25498.95, 0.00, 0.00, 5398933.10, FALSE, NULL)
            """);
            LOGGER.info("Inserted amortization rows for loan 3");
            
        }
        LOGGER.info("Default data inserted successfully");
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
            return "Connected to embedded H2 database";
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
        return schemaInitialized && tablesExist();
    }
    
    /**
     * Check if tables exist using a new connection
     */
    private static boolean tablesExist() {
        try (Connection conn = getConnection()) {
            return tablesExist(conn);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Get the database file location
     * @return Path to the database files
     */
    public static String getDatabaseLocation() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + ".vismera" + File.separator + DB_FOLDER;
    }
    
    /**
     * Reset the database (drop all tables and recreate)
     * WARNING: This will delete all data!
     */
    public static void resetDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Drop tables in reverse order (due to foreign keys)
            stmt.execute("DROP TABLE IF EXISTS payments");
            stmt.execute("DROP TABLE IF EXISTS amortization_rows");
            stmt.execute("DROP TABLE IF EXISTS loans");
            stmt.execute("DROP TABLE IF EXISTS cars");
            stmt.execute("DROP TABLE IF EXISTS customers");
            stmt.execute("DROP TABLE IF EXISTS admin_settings");
            
            schemaInitialized = false;
            
            // Recreate schema
            initializeSchema();
            
            LOGGER.info("Database reset successfully");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error resetting database", e);
        }
    }
}
