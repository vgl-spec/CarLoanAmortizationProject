# Database Setup Tutorial for Apache NetBeans
## Car Loan Amortization System with MySQL

### Prepared for: Vismerá Inc.

---

## Table of Contents
1. [Prerequisites](#1-prerequisites)
2. [Installing MySQL](#2-installing-mysql)
3. [Setting Up the Database](#3-setting-up-the-database)
4. [Downloading MySQL Connector J](#4-downloading-mysql-connector-j)
5. [Configuring NetBeans Project](#5-configuring-netbeans-project)
6. [Testing the Connection](#6-testing-the-connection)
7. [Troubleshooting](#7-troubleshooting)

---

## 1. Prerequisites

Before starting, ensure you have:

| Requirement | Version | Notes |
|-------------|---------|-------|
| **Apache NetBeans** | 17+ | IDE for Java development |
| **Java JDK** | 17+ | Java Development Kit |
| **MySQL Server** | 8.0+ | Database server |
| **MySQL Workbench** | 8.0+ | (Optional) Database GUI tool |

---

## 2. Installing MySQL

### Step 2.1: Download MySQL

1. Visit the official MySQL download page:
   ```
   https://dev.mysql.com/downloads/mysql/
   ```

2. Select your operating system:
   - **Windows**: Choose "Windows (x86, 64-bit), MSI Installer"
   - **macOS**: Choose "macOS 14 (ARM, 64-bit), DMG Archive"
   - **Linux**: Choose appropriate package for your distribution

3. Click "Download" (you can skip the login by clicking "No thanks, just start my download")

### Step 2.2: Install MySQL (Windows)

1. Run the downloaded MSI installer
2. Choose **"Custom"** installation type
3. Select these components:
   - ✅ MySQL Server 8.0.x
   - ✅ MySQL Workbench 8.0.x
   - ✅ Connector/J 9.x (or download separately)
4. Click **Next** through the installation
5. **Configuration:**
   - Type: Development Computer
   - Port: 3306 (default)
   - Authentication: Use Strong Password Encryption
6. **Set Root Password:**
   - Enter a strong password (remember this!)
   - Example: `YourSecurePassword123!`
7. Complete the installation

### Step 2.3: Verify MySQL Installation

Open **Command Prompt** or **PowerShell** and run:

```powershell
mysql --version
```

Expected output:
```
mysql  Ver 8.0.xx for Win64 on x86_64 (MySQL Community Server - GPL)
```

---

## 3. Setting Up the Database

### Step 3.1: Open MySQL Command Line or Workbench

**Option A: Command Line**
```powershell
mysql -u root -p
```
Enter your root password when prompted.

**Option B: MySQL Workbench**
1. Open MySQL Workbench
2. Click on "Local instance MySQL80"
3. Enter your root password

### Step 3.2: Create the Database

Run the following SQL commands:

```sql
-- Create the database
CREATE DATABASE IF NOT EXISTS car_loan_amortization_db;

-- Use the database
USE car_loan_amortization_db;
```

### Step 3.3: Run the Schema Script

The project includes a schema file at:
```
src/com/vismera/db/db_schema.sql
```

**Option A: Command Line**
```powershell
mysql -u root -p car_loan_amortization_db < "path\to\db_schema.sql"
```

**Option B: MySQL Workbench**
1. Open MySQL Workbench
2. Connect to your local instance
3. Go to **File → Open SQL Script**
4. Navigate to `src/com/vismera/db/db_schema.sql`
5. Click the **lightning bolt** icon to execute

### Step 3.4: Verify Tables Created

Run this query to see all tables:

```sql
SHOW TABLES;
```

Expected output:
```
+--------------------------------------+
| Tables_in_car_loan_amortization_db   |
+--------------------------------------+
| admin_settings                       |
| amortization_rows                    |
| cars                                 |
| customers                            |
| loans                                |
| payments                             |
+--------------------------------------+
```

### Step 3.5: Verify Sample Data (Optional)

```sql
-- Check customers
SELECT * FROM customers;

-- Check cars
SELECT * FROM cars;

-- Check admin settings
SELECT * FROM admin_settings;
```

---

## 4. Downloading MySQL Connector J

The MySQL Connector/J is the JDBC driver that allows Java to communicate with MySQL.

### Step 4.1: Download the Connector

1. Visit:
   ```
   https://dev.mysql.com/downloads/connector/j/
   ```

2. Select:
   - **Operating System**: Platform Independent
   - **Version**: 9.5.0 (or latest)

3. Download the **ZIP Archive** (Platform Independent)

4. Extract the ZIP file to a permanent location:
   ```
   C:\mysql-connector-j-9.5.0\
   ```

### Step 4.2: Locate the JAR File

After extraction, you'll find:
```
C:\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar
```

This JAR file will be added to your NetBeans project.

---

## 5. Configuring NetBeans Project

### Step 5.1: Open Your Project

1. Open Apache NetBeans
2. Go to **File → Open Project**
3. Navigate to your project folder:
   ```
   CarLoanAmortizationProject
   ```
4. Click **Open Project**

### Step 5.2: Add MySQL Connector to Project Libraries

**Method A: Project Properties (Recommended)**

1. Right-click on your project name in the **Projects** panel
2. Select **Properties**
3. In the left panel, click **Libraries**
4. Click the **+ (Add JAR/Folder)** button
5. Navigate to your MySQL Connector JAR:
   ```
   C:\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar
   ```
6. Click **Open**
7. Click **OK** to save

**Method B: Drag and Drop**

1. In the **Projects** panel, expand your project
2. Locate the **Libraries** folder
3. Drag the `mysql-connector-j-9.5.0.jar` file directly onto the **Libraries** folder

### Step 5.3: Verify Library Added

Expand the **Libraries** folder in your project. You should see:
```
Libraries
├── JDK 17
├── mysql-connector-j-9.5.0.jar  ← New
└── [other libraries]
```

### Step 5.4: Configure Database Connection

Open `src/com/vismera/db/DatabaseConfig.java` and update with your credentials:

```java
package com.vismera.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/car_loan_amortization_db";
    private static final String USER = "root";
    private static final String PASSWORD = "YourPasswordHere";  // ← Update this!
    
    // Connection instance
    private static Connection connection = null;
    
    /**
     * Get database connection (singleton pattern)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
```

**Important:** Replace `YourPasswordHere` with your actual MySQL root password.

---

## 6. Testing the Connection

### Step 6.1: Create a Test Class

Create a new Java class to test the database connection:

**File: `src/com/vismera/db/DatabaseTest.java`**

```java
package com.vismera.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...\n");
        
        try {
            // Get connection
            Connection conn = DatabaseConfig.getConnection();
            System.out.println("✓ Connection successful!");
            
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM cars");
            
            if (rs.next()) {
                int carCount = rs.getInt("count");
                System.out.println("✓ Query successful! Found " + carCount + " cars in database.");
            }
            
            // Clean up
            rs.close();
            stmt.close();
            DatabaseConfig.closeConnection();
            
            System.out.println("\n✓ All tests passed! Database is ready to use.");
            
        } catch (Exception e) {
            System.out.println("✗ Connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### Step 6.2: Run the Test

1. Right-click on `DatabaseTest.java`
2. Select **Run File** (or press **Shift + F6**)
3. Check the Output window

**Expected Output:**
```
Testing Database Connection...

✓ Connection successful!
✓ Query successful! Found 5 cars in database.

✓ All tests passed! Database is ready to use.
```

---

## 7. Troubleshooting

### Common Issues and Solutions

#### Issue 1: "No suitable driver found"

**Error:**
```
java.sql.SQLException: No suitable driver found for jdbc:mysql://localhost:3306/...
```

**Solution:**
1. Ensure MySQL Connector JAR is added to project libraries
2. Make sure the JAR path is correct
3. Clean and rebuild the project:
   - **Build → Clean and Build Project**

---

#### Issue 2: "Access denied for user 'root'@'localhost'"

**Error:**
```
java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

**Solution:**
1. Check your password in `DatabaseConfig.java`
2. Ensure MySQL server is running
3. Reset MySQL root password if needed:
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'NewPassword123!';
   FLUSH PRIVILEGES;
   ```

---

#### Issue 3: "Communications link failure"

**Error:**
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Solution:**
1. Ensure MySQL server is running:
   ```powershell
   net start MySQL80
   ```
2. Check that port 3306 is not blocked by firewall
3. Verify connection URL is correct

---

#### Issue 4: "Unknown database 'car_loan_amortization_db'"

**Error:**
```
java.sql.SQLException: Unknown database 'car_loan_amortization_db'
```

**Solution:**
1. Create the database first:
   ```sql
   CREATE DATABASE car_loan_amortization_db;
   ```
2. Run the schema script

---

#### Issue 5: MySQL Server Not Starting

**Windows Services Check:**
1. Press `Win + R`, type `services.msc`
2. Find "MySQL80" in the list
3. Right-click → **Start**

**Command Line:**
```powershell
net start MySQL80
```

---

### Verifying MySQL is Running

```powershell
# Check MySQL status
Get-Service -Name "MySQL*"

# Or using netstat
netstat -an | findstr 3306
```

---

## Quick Reference Card

### Connection Settings

| Setting | Value |
|---------|-------|
| Host | `localhost` |
| Port | `3306` |
| Database | `car_loan_amortization_db` |
| Username | `root` |
| JDBC URL | `jdbc:mysql://localhost:3306/car_loan_amortization_db` |

### Key Files

| File | Location | Purpose |
|------|----------|---------|
| `db_schema.sql` | `src/com/vismera/db/` | Database schema |
| `DatabaseConfig.java` | `src/com/vismera/db/` | Connection config |
| `mysql-connector-j-9.5.0.jar` | Project Libraries | JDBC driver |

### Useful MySQL Commands

```sql
-- Show all databases
SHOW DATABASES;

-- Use specific database
USE car_loan_amortization_db;

-- Show all tables
SHOW TABLES;

-- Describe table structure
DESCRIBE customers;

-- Count records
SELECT COUNT(*) FROM loans;

-- View recent customers
SELECT * FROM customers ORDER BY created_at DESC LIMIT 5;
```

---

## Next Steps

After completing the database setup:

1. ✅ Run `DatabaseTest.java` to verify connection
2. ✅ Build the project: **Build → Clean and Build**
3. ✅ Run the application: **Run → Run Project**
4. ✅ Access Admin panel to manage data

---

*Tutorial prepared for Vismerá Inc. Car Loan Amortization System*
*Version 1.0 | December 2025*
