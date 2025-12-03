package com.vismera.controllers;

import com.vismera.dao.CustomerDAO;
import com.vismera.models.Customer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for customer management operations.
 * Provides business logic layer between UI and DAO.
 * 
 * @author Vismerá Inc.
 */
public class CustomerController {
    
    private static final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());
    
    private static CustomerController instance;
    private final CustomerDAO customerDAO;
    
    private CustomerController() {
        this.customerDAO = CustomerDAO.getInstance();
    }
    
    /**
     * Get singleton instance
     */
    public static CustomerController getInstance() {
        if (instance == null) {
            instance = new CustomerController();
        }
        return instance;
    }
    
    /**
     * Add a new customer to the database
     * @param customer The customer to add
     * @return The new customer ID, or -1 if failed
     */
    public int addCustomer(Customer customer) {
        String validationError = validateCustomer(customer);
        if (validationError != null) {
            LOGGER.warning("Customer validation failed: " + validationError);
            return -1;
        }
        
        int id = customerDAO.insert(customer);
        if (id > 0) {
            LOGGER.info("Customer added: " + customer.getFullName() + " (ID: " + id + ")");
        }
        return id;
    }
    
    /**
     * Validate email format
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Simple email regex pattern
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    
    /**
     * Get a customer by ID (alias for getCustomer)
     * @param id The customer ID
     * @return The customer, or null if not found
     */
    public Customer getCustomerById(int id) {
        return getCustomer(id);
    }
    
    /**
     * Create a new customer with validation
     * @param customer The customer to create
     * @return Success message or error message
     */
    public String createCustomer(Customer customer) {
        // Validate customer
        String validationError = validateCustomer(customer);
        if (validationError != null) {
            return validationError;
        }
        
        // Attempt to insert
        int id = customerDAO.insert(customer);
        if (id > 0) {
            LOGGER.info("Customer created: " + customer.getFullName() + " (ID: " + id + ")");
            return "SUCCESS";
        } else {
            return "Failed to create customer. Please try again.";
        }
    }
    
    /**
     * Update an existing customer
     * @param customer The customer to update
     * @return true if successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        String validationError = validateCustomer(customer);
        if (validationError != null) {
            LOGGER.warning("Customer validation failed: " + validationError);
            return false;
        }
        
        Customer existing = customerDAO.findById(customer.getId());
        if (existing == null) {
            LOGGER.warning("Customer not found: " + customer.getId());
            return false;
        }
        
        boolean updated = customerDAO.update(customer);
        if (updated) {
            LOGGER.info("Customer updated: " + customer.getFullName() + " (ID: " + customer.getId() + ")");
        }
        return updated;
    }
    
    /**
     * Update an existing customer with validation (returns message)
     * @param customer The customer to update
     * @return Success message or error message
     */
    public String updateCustomerWithMessage(Customer customer) {
        // Validate customer
        String validationError = validateCustomer(customer);
        if (validationError != null) {
            return validationError;
        }
        
        // Check if customer exists
        Customer existing = customerDAO.findById(customer.getId());
        if (existing == null) {
            return "Customer not found.";
        }
        
        // Attempt to update
        if (customerDAO.update(customer)) {
            LOGGER.info("Customer updated: " + customer.getFullName() + " (ID: " + customer.getId() + ")");
            return "SUCCESS";
        } else {
            return "Failed to update customer. Please try again.";
        }
    }
    
    /**
     * Delete a customer by ID
     * @param id The customer ID
     * @return true if successful
     */
    public boolean deleteCustomer(int id) {
        if (!canDeleteCustomer(id)) {
            LOGGER.warning("Cannot delete customer with active loans: " + id);
            return false;
        }
        
        return customerDAO.delete(id);
    }
    
    /**
     * Get a customer by ID
     * @param id The customer ID
     * @return The customer, or null if not found
     */
    public Customer getCustomer(int id) {
        return customerDAO.findById(id);
    }
    
    /**
     * Get all customers
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
    
    /**
     * Search customers by query
     * @param query The search query
     * @return List of matching customers
     */
    public List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDAO.search(query);
    }
    
    /**
     * Check if a customer can be deleted (no active loans)
     * @param customerId The customer ID
     * @return true if can be deleted
     */
    public boolean canDeleteCustomer(int customerId) {
        return !customerDAO.hasActiveLoans(customerId);
    }
    
    /**
     * Check if customer has any loans
     * @param customerId The customer ID
     * @return true if has loans
     */
    public boolean hasLoans(int customerId) {
        return customerDAO.hasLoans(customerId);
    }
    
    /**
     * Get total number of customers
     * @return Customer count
     */
    public int getCustomerCount() {
        return customerDAO.getCount();
    }
    
    /**
     * Validate customer data
     * @param customer The customer to validate
     * @return Error message or null if valid
     */
    private String validateCustomer(Customer customer) {
        if (customer == null) {
            return "Customer data is required.";
        }
        
        if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
            return "Full name is required.";
        }
        
        if (customer.getFullName().trim().length() < 2) {
            return "Full name must be at least 2 characters.";
        }
        
        if (customer.getFullName().trim().length() > 200) {
            return "Full name must be less than 200 characters.";
        }
        
        // Validate email format if provided
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            if (!isValidEmail(customer.getEmail())) {
                return "Invalid email format.";
            }
        }
        
        // Validate contact number format if provided
        if (customer.getContactNumber() != null && !customer.getContactNumber().trim().isEmpty()) {
            if (customer.getContactNumber().trim().length() > 30) {
                return "Contact number must be less than 30 characters.";
            }
        }
        
        return null; // Valid
    }
}
