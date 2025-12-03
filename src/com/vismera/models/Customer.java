package com.vismera.models;

import java.time.LocalDateTime;

/**
 * Entity class representing a customer in the car loan system.
 * Maps to the 'customers' table in the database.
 * 
 * @author Vismerá Inc.
 */
public class Customer {
    
    private int id;
    private String fullName;
    private String contactNumber;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor
     */
    public Customer() {
    }
    
    /**
     * Constructor for creating a new customer (without ID)
     */
    public Customer(String fullName, String contactNumber, String email, String address) {
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }
    
    /**
     * Full constructor with all fields
     */
    public Customer(int id, String fullName, String contactNumber, String email, 
                   String address, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.createdAt = createdAt;
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get display name for combo boxes and lists
     */
    public String getDisplayName() {
        return fullName + (contactNumber != null && !contactNumber.isEmpty() ? 
                          " (" + contactNumber + ")" : "");
    }
    
    /**
     * Check if customer has required fields
     */
    public boolean isValid() {
        return fullName != null && !fullName.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return fullName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return id == customer.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
