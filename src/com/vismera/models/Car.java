package com.vismera.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a car available for loan financing.
 * Maps to the 'cars' table in the database.
 * 
 * @author Vismerá Inc.
 */
public class Car {
    
    private int id;
    private String make;
    private String model;
    private int year;
    private BigDecimal price;
    private String category; // Sports Car, Luxury Sedan, Luxury SUV
    private String color;
    private int mpg;
    private String imagePath;
    private String notes;
    private boolean available;
    private LocalDateTime createdAt;

    /**
     * Default constructor
     */
    public Car() {
        this.available = true;
    }

    /**
     * Constructor for UI compatibility (with double price)
     */
    public Car(int id, String make, String model, int year, String category, 
               String color, int mpg, double price, String imagePath) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.category = category;
        this.color = color;
        this.mpg = mpg;
        this.price = BigDecimal.valueOf(price);
        this.imagePath = imagePath;
        this.available = true;
    }
    
    /**
     * Constructor for database operations (with BigDecimal price)
     */
    public Car(int id, String make, String model, int year, BigDecimal price,
               String category, String color, int mpg, String imagePath, 
               String notes, boolean available, LocalDateTime createdAt) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.category = category;
        this.color = color;
        this.mpg = mpg;
        this.imagePath = imagePath;
        this.notes = notes;
        this.available = available;
        this.createdAt = createdAt;
    }
    
    /**
     * Constructor for creating new car (without ID)
     */
    public Car(String make, String model, int year, BigDecimal price, String notes) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.notes = notes;
        this.available = true;
    }

    // ==================== GETTERS AND SETTERS ====================
    
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public String getMake() { 
        return make; 
    }
    
    public void setMake(String make) { 
        this.make = make; 
    }

    public String getModel() { 
        return model; 
    }
    
    public void setModel(String model) { 
        this.model = model; 
    }

    public int getYear() { 
        return year; 
    }
    
    public void setYear(int year) { 
        this.year = year; 
    }

    public String getCategory() { 
        return category; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }

    public String getColor() { 
        return color; 
    }
    
    public void setColor(String color) { 
        this.color = color; 
    }

    public int getMpg() { 
        return mpg; 
    }
    
    public void setMpg(int mpg) { 
        this.mpg = mpg; 
    }

    /**
     * Get price as double (for backward compatibility)
     */
    public double getPrice() { 
        return price != null ? price.doubleValue() : 0.0; 
    }
    
    /**
     * Set price from double (for backward compatibility)
     */
    public void setPrice(double price) { 
        this.price = BigDecimal.valueOf(price); 
    }
    
    /**
     * Get price as BigDecimal (for precise calculations)
     */
    public BigDecimal getPriceBigDecimal() {
        return price;
    }
    
    /**
     * Set price as BigDecimal (for precise calculations)
     */
    public void setPriceBigDecimal(BigDecimal price) {
        this.price = price;
    }

    public String getImagePath() { 
        return imagePath; 
    }
    
    public void setImagePath(String imagePath) { 
        this.imagePath = imagePath; 
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Get full name for display
     */
    public String getFullName() {
        return make + " " + model;
    }
    
    /**
     * Get display name with year
     */
    public String getDisplayName() {
        return year + " " + make + " " + model;
    }
    
    /**
     * Check if car has required fields
     */
    public boolean isValid() {
        return make != null && !make.trim().isEmpty() &&
               model != null && !model.trim().isEmpty() &&
               year > 1900 && year <= 2100 &&
               price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return year + " " + make + " " + model;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Car car = (Car) obj;
        return id == car.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
