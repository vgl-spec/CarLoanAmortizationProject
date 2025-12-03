-- ============================================================
-- Car Loan Amortization Database Schema
-- Client: Vismerá Inc.
-- Database: car_loan_amortization_db
-- MySQL 8.0+
-- ============================================================

-- Create database
CREATE DATABASE IF NOT EXISTS car_loan_amortization_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE car_loan_amortization_db;

-- ============================================================
-- DROP EXISTING TABLES (for clean reinstall)
-- ============================================================
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS amortization_rows;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS admin_settings;

-- ============================================================
-- CUSTOMERS TABLE
-- Stores customer information for loan agreements
-- ============================================================
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(200) NOT NULL,
    contact_number VARCHAR(30),
    email VARCHAR(150),
    address VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_customer_name (full_name),
    INDEX idx_customer_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- CARS TABLE
-- Stores car inventory available for financing
-- ============================================================
CREATE TABLE cars (
    id INT PRIMARY KEY AUTO_INCREMENT,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    category VARCHAR(50),
    color VARCHAR(30),
    mpg INT,
    image_path VARCHAR(255),
    notes TEXT,
    is_available BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_car_make (make),
    INDEX idx_car_year (year),
    INDEX idx_car_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- LOANS TABLE
-- Stores loan agreements linking customers and cars
-- ============================================================
CREATE TABLE loans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    car_id INT NOT NULL,
    principal DECIMAL(14,2) NOT NULL,
    apr DECIMAL(7,4) NOT NULL,
    compounding VARCHAR(20) NOT NULL DEFAULT 'monthly',
    term_months INT NOT NULL,
    payment_frequency VARCHAR(20) NOT NULL DEFAULT 'monthly',
    start_date DATE NOT NULL,
    penalty_rate DECIMAL(9,6) DEFAULT 0,
    penalty_type VARCHAR(30) DEFAULT 'percent_per_month',
    grace_period_days INT DEFAULT 0,
    down_payment DECIMAL(14,2) DEFAULT 0,
    trade_in_value DECIMAL(14,2) DEFAULT 0,
    sales_tax_rate DECIMAL(5,2) DEFAULT 0,
    registration_fee DECIMAL(10,2) DEFAULT 0,
    monthly_payment DECIMAL(14,2),
    total_interest DECIMAL(14,2),
    total_amount DECIMAL(14,2),
    status VARCHAR(20) DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE RESTRICT,
    
    INDEX idx_loan_status (status),
    INDEX idx_loan_customer (customer_id),
    INDEX idx_loan_car (car_id),
    INDEX idx_loan_start_date (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- AMORTIZATION_ROWS TABLE
-- Stores calculated payment schedule for each loan
-- ============================================================
CREATE TABLE amortization_rows (
    id INT PRIMARY KEY AUTO_INCREMENT,
    loan_id INT NOT NULL,
    period_index INT NOT NULL,
    due_date DATE NOT NULL,
    opening_balance DECIMAL(14,2) NOT NULL,
    scheduled_payment DECIMAL(14,2) NOT NULL,
    principal_paid DECIMAL(14,2) NOT NULL,
    interest_paid DECIMAL(14,2) NOT NULL,
    penalty_amount DECIMAL(14,2) DEFAULT 0,
    extra_payment DECIMAL(14,2) DEFAULT 0,
    closing_balance DECIMAL(14,2) NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    paid_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    
    INDEX idx_amort_loan_period (loan_id, period_index),
    UNIQUE KEY unique_loan_period (loan_id, period_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- PAYMENTS TABLE
-- Tracks actual payments made against loans
-- ============================================================
CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    loan_id INT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    applied_to_period INT,
    type VARCHAR(20) NOT NULL DEFAULT 'regular',
    penalty_applied DECIMAL(14,2) DEFAULT 0,
    principal_applied DECIMAL(14,2) DEFAULT 0,
    interest_applied DECIMAL(14,2) DEFAULT 0,
    note VARCHAR(255),
    recorded_by VARCHAR(100),
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE RESTRICT,
    
    INDEX idx_payment_loan (loan_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_payment_loan_date (loan_id, payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- ADMIN_SETTINGS TABLE
-- Stores system configuration and default values
-- ============================================================
CREATE TABLE admin_settings (
    key_name VARCHAR(100) PRIMARY KEY,
    value_text TEXT,
    description VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- SEED DATA - Default Settings
-- ============================================================
INSERT INTO admin_settings (key_name, value_text, description) VALUES 
    ('company_name', 'Vismerá Inc.', 'Company name displayed in reports'),
    ('default_apr', '6.5', 'Default annual percentage rate'),
    ('default_penalty_rate', '2.0', 'Default penalty rate for late payments'),
    ('default_grace_period', '5', 'Default grace period in days'),
    ('default_sales_tax_rate', '8.0', 'Default sales tax rate'),
    ('default_registration_fee', '500.00', 'Default vehicle registration fee'),
    ('default_compounding', 'monthly', 'Default interest compounding frequency'),
    ('currency_symbol', '$', 'Currency symbol for display'),
    ('date_format', 'MM/dd/yyyy', 'Date format pattern'),
    ('app_version', '1.0.0', 'Application version');

-- ============================================================
-- SEED DATA - Sample Cars
-- ============================================================
INSERT INTO cars (make, model, year, price, category, color, mpg, notes) VALUES 
    ('Mercedes-Benz', 'S-Class', 2024, 115000.00, 'Luxury Sedan', 'Silver', 24, 'Flagship luxury sedan with advanced technology'),
    ('Porsche', '911 Carrera', 2024, 125000.00, 'Sports Car', 'Red', 20, 'Iconic sports car with exceptional performance'),
    ('BMW', 'X5 M', 2024, 108000.00, 'Luxury SUV', 'Black', 22, 'High-performance luxury SUV'),
    ('Audi', 'RS7', 2024, 118000.00, 'Sports Car', 'Gray', 21, 'Sporty four-door coupe with powerful engine'),
    ('Lexus', 'LS 500', 2024, 82000.00, 'Luxury Sedan', 'White', 25, 'Japanese luxury with exceptional comfort'),
    ('Range Rover', 'Sport', 2024, 95000.00, 'Luxury SUV', 'Green', 19, 'Premium British SUV with off-road capability');

-- ============================================================
-- SEED DATA - Sample Customer
-- ============================================================
INSERT INTO customers (full_name, contact_number, email, address) VALUES 
    ('John Doe', '(555) 123-4567', 'john.doe@email.com', '123 Main Street, City, State 12345'),
    ('Jane Smith', '(555) 234-5678', 'jane.smith@email.com', '456 Oak Avenue, City, State 12345'),
    ('Robert Johnson', '(555) 345-6789', 'robert.j@email.com', '789 Pine Road, City, State 12345');

-- ============================================================
-- VIEWS (Optional - for reporting)
-- ============================================================

-- Active Loans Summary View
CREATE OR REPLACE VIEW v_active_loans AS
SELECT 
    l.id AS loan_id,
    c.full_name AS customer_name,
    CONCAT(ca.year, ' ', ca.make, ' ', ca.model) AS car_name,
    l.principal,
    l.apr,
    l.term_months,
    l.monthly_payment,
    l.start_date,
    l.status,
    (SELECT COALESCE(SUM(p.amount), 0) FROM payments p WHERE p.loan_id = l.id) AS total_paid,
    l.total_amount - (SELECT COALESCE(SUM(p.amount), 0) FROM payments p WHERE p.loan_id = l.id) AS remaining_balance
FROM loans l
JOIN customers c ON l.customer_id = c.id
JOIN cars ca ON l.car_id = ca.id
WHERE l.status = 'active';

-- Payment History View
CREATE OR REPLACE VIEW v_payment_history AS
SELECT 
    p.id AS payment_id,
    p.loan_id,
    c.full_name AS customer_name,
    p.payment_date,
    p.amount,
    p.type,
    p.penalty_applied,
    p.note,
    p.recorded_by,
    p.recorded_at
FROM payments p
JOIN loans l ON p.loan_id = l.id
JOIN customers c ON l.customer_id = c.id
ORDER BY p.payment_date DESC;

-- ============================================================
-- STORED PROCEDURES (Optional - for complex operations)
-- ============================================================

DELIMITER //

-- Procedure to calculate remaining balance for a loan
CREATE PROCEDURE sp_get_loan_balance(IN p_loan_id INT, OUT p_balance DECIMAL(14,2))
BEGIN
    DECLARE total_paid DECIMAL(14,2);
    DECLARE total_amount DECIMAL(14,2);
    
    SELECT COALESCE(SUM(amount), 0) INTO total_paid
    FROM payments WHERE loan_id = p_loan_id;
    
    SELECT total_amount INTO total_amount
    FROM loans WHERE id = p_loan_id;
    
    SET p_balance = total_amount - total_paid;
END //

-- Procedure to close a fully paid loan
CREATE PROCEDURE sp_close_loan_if_paid(IN p_loan_id INT)
BEGIN
    DECLARE total_paid DECIMAL(14,2);
    DECLARE total_amount DECIMAL(14,2);
    
    SELECT COALESCE(SUM(amount), 0) INTO total_paid
    FROM payments WHERE loan_id = p_loan_id;
    
    SELECT total_amount INTO total_amount
    FROM loans WHERE id = p_loan_id;
    
    IF total_paid >= total_amount THEN
        UPDATE loans SET status = 'closed' WHERE id = p_loan_id;
    END IF;
END //

DELIMITER ;

-- ============================================================
-- GRANTS (for production - adjust username as needed)
-- ============================================================
-- CREATE USER IF NOT EXISTS 'vismera_app'@'localhost' IDENTIFIED BY 'secure_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON car_loan_amortization_db.* TO 'vismera_app'@'localhost';
-- FLUSH PRIVILEGES;
