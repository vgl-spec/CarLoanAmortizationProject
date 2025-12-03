package com.vismera.dao;

import com.vismera.config.DatabaseConfig;
import com.vismera.models.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Customer entity.
 * Handles all database operations for customers table.
 * 
 * @author Vismerá Inc.
 */
public class CustomerDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());
    
    private static CustomerDAO instance;
    
    private CustomerDAO() {
    }
    
    /**
     * Get singleton instance
     */
    public static CustomerDAO getInstance() {
        if (instance == null) {
            instance = new CustomerDAO();
        }
        return instance;
    }
    
    /**
     * Insert a new customer
     * @param customer The customer to insert
     * @return The generated customer ID, or -1 if failed
     */
    public int insert(Customer customer) {
        String sql = "INSERT INTO customers (full_name, contact_number, email, address) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getContactNumber());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        customer.setId(id);
                        LOGGER.info("Customer inserted successfully with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting customer", e);
        }
        return -1;
    }
    
    /**
     * Find customer by ID
     * @param id The customer ID
     * @return The customer, or null if not found
     */
    public Customer findById(int id) {
        String sql = "SELECT id, full_name, contact_number, email, address, created_at FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by ID: " + id, e);
        }
        return null;
    }
    
    /**
     * Find all customers
     * @return List of all customers
     */
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, full_name, contact_number, email, address, created_at FROM customers ORDER BY full_name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all customers", e);
        }
        return customers;
    }
    
    /**
     * Search customers by name
     * @param name The name to search for
     * @return List of matching customers
     */
    public List<Customer> searchByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, full_name, contact_number, email, address, created_at " +
                     "FROM customers WHERE full_name LIKE ? ORDER BY full_name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching customers by name: " + name, e);
        }
        return customers;
    }
    
    /**
     * Search customers by any field
     * @param query The search query
     * @return List of matching customers
     */
    public List<Customer> search(String query) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, full_name, contact_number, email, address, created_at " +
                     "FROM customers WHERE full_name LIKE ? OR contact_number LIKE ? OR email LIKE ? " +
                     "ORDER BY full_name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching customers: " + query, e);
        }
        return customers;
    }
    
    /**
     * Update an existing customer
     * @param customer The customer to update
     * @return true if successful
     */
    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET full_name = ?, contact_number = ?, email = ?, address = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getContactNumber());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getAddress());
            stmt.setInt(5, customer.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Customer updated successfully: " + customer.getId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating customer: " + customer.getId(), e);
        }
        return false;
    }
    
    /**
     * Delete a customer by ID
     * @param id The customer ID
     * @return true if successful
     */
    public boolean delete(int id) {
        // First check if customer has loans
        if (hasActiveLoans(id)) {
            LOGGER.warning("Cannot delete customer with active loans: " + id);
            return false;
        }
        
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Customer deleted successfully: " + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting customer: " + id, e);
        }
        return false;
    }
    
    /**
     * Check if customer has active loans
     * @param customerId The customer ID
     * @return true if customer has active loans
     */
    public boolean hasActiveLoans(int customerId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE customer_id = ? AND status = 'active'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking active loans for customer: " + customerId, e);
        }
        return false;
    }
    
    /**
     * Check if customer has any loans (active or closed)
     * @param customerId The customer ID
     * @return true if customer has any loans
     */
    public boolean hasLoans(int customerId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking loans for customer: " + customerId, e);
        }
        return false;
    }
    
    /**
     * Get total number of customers
     * @return Customer count
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM customers";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting customer count", e);
        }
        return 0;
    }
    
    /**
     * Map ResultSet row to Customer object
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setContactNumber(rs.getString("contact_number"));
        customer.setEmail(rs.getString("email"));
        customer.setAddress(rs.getString("address"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            customer.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return customer;
    }
}
