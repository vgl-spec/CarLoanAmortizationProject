package com.vismera.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...\n");
        
        try {
            // Get connection
            Connection conn = DatabaseConfig.getConnection();
            System.out.println("Connection successful!");
            
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM cars");
            
            if (rs.next()) {
                int carCount = rs.getInt("count");
                System.out.println("Query successful! Found " + carCount + " cars in database.");
            }
            
            // Clean up
            rs.close();
            stmt.close();
            
            System.out.println("\nAll tests passed! Database is ready to use.");
            
        } catch (Exception e) {
            System.out.println("Connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}