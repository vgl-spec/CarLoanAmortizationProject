package com.vismera.controllers;

import com.vismera.models.Car;
import com.vismera.storage.TextFileDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for managing car data.
 * Now integrated with text file database.
 * 
 * @author Vismerá Inc.
 */
public class CarController {
    
    private static final Logger LOGGER = Logger.getLogger(CarController.class.getName());
    
    private static CarController instance;
    private final TextFileDatabase database;

    private CarController() {
        database = TextFileDatabase.getInstance();
        LOGGER.info("CarController initialized with text file database");
    }

    /**
     * Get singleton instance
     */
    public static CarController getInstance() {
        if (instance == null) {
            instance = new CarController();
        }
        return instance;
    }
    
    /**
     * Enable database mode (kept for compatibility)
     */
    public void enableDatabaseMode() {
        // Text file database is always enabled
    }
    
    /**
     * Disable database mode (kept for compatibility)
     */
    public void disableDatabaseMode() {
        // Text file database is always enabled
    }
    
    /**
     * Set database mode on/off (kept for compatibility)
     */
    public void setUseDatabase(boolean useDatabase) {
        // Text file database is always enabled
    }
    
    /**
     * Check if database mode is enabled (always true now)
     */
    public boolean isDatabaseModeEnabled() {
        return true;
    }

    /**
     * Get all cars
     */
    public List<Car> getAllCars() {
        return database.getAllCars();
    }
    
    /**
     * Get available cars (not in active loans)
     */
    public List<Car> getAvailableCars() {
        return database.getAvailableCars();
    }

    /**
     * Search cars by make, model, or category
     */
    public List<Car> searchCars(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCars();
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        return database.getAllCars().stream()
            .filter(car -> 
                car.getMake().toLowerCase().contains(lowerQuery) ||
                car.getModel().toLowerCase().contains(lowerQuery) ||
                (car.getCategory() != null && car.getCategory().toLowerCase().contains(lowerQuery)) ||
                (car.getColor() != null && car.getColor().toLowerCase().contains(lowerQuery)) ||
                String.valueOf(car.getYear()).contains(lowerQuery)
            )
            .collect(Collectors.toList());
    }

    /**
     * Get a car by its ID
     */
    public Car getCarById(int id) {
        return database.getCarById(id);
    }

    /**
     * Get cars filtered by category
     */
    public List<Car> getCarsByCategory(String category) {
        return database.getAllCars().stream()
            .filter(car -> car.getCategory() != null && car.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    /**
     * Get cars within a price range
     */
    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice) {
        return database.getAllCars().stream()
            .filter(car -> car.getPrice() >= minPrice && car.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }

    /**
     * Get distinct categories
     */
    public List<String> getCategories() {
        return database.getAllCars().stream()
            .map(Car::getCategory)
            .filter(cat -> cat != null)
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Get distinct years
     */
    public List<Integer> getDistinctYears() {
        return database.getAllCars().stream()
            .map(Car::getYear)
            .distinct()
            .sorted((a, b) -> b - a)
            .collect(Collectors.toList());
    }

    // ==================== DATABASE CRUD OPERATIONS ====================
    
    /**
     * Add a new car to the database
     * @param car The car to add
     * @return The new car ID, or -1 if failed
     */
    public int addCar(Car car) {
        String validationError = validateCar(car);
        if (validationError != null) {
            LOGGER.warning("Car validation failed: " + validationError);
            return -1;
        }
        
        int id = database.addCar(car);
        if (id > 0) {
            LOGGER.info("Car added: " + car.getDisplayName() + " (ID: " + id + ")");
        }
        return id;
    }
    
    /**
     * Update an existing car
     * @param car The car to update
     * @return true if successful, false otherwise
     */
    public boolean updateCar(Car car) {
        String validationError = validateCar(car);
        if (validationError != null) {
            LOGGER.warning("Car validation failed: " + validationError);
            return false;
        }
        
        Car existing = database.getCarById(car.getId());
        if (existing == null) {
            LOGGER.warning("Car not found: " + car.getId());
            return false;
        }
        
        boolean updated = database.updateCar(car);
        if (updated) {
            LOGGER.info("Car updated: " + car.getDisplayName() + " (ID: " + car.getId() + ")");
        }
        return updated;
    }
    
    /**
     * Create a new car (database mode)
     * @param car The car to create
     * @return Success message or error message
     */
    public String createCar(Car car) {
        String validationError = validateCar(car);
        if (validationError != null) {
            return validationError;
        }
        
        int id = database.addCar(car);
        if (id > 0) {
            LOGGER.info("Car created: " + car.getDisplayName() + " (ID: " + id + ")");
            return "SUCCESS";
        } else {
            return "Failed to create car. Please try again.";
        }
    }
    
    /**
     * Delete a car (database mode)
     * @param id The car ID
     * @return true if successful
     */
    public boolean deleteCar(int id) {
        if (isCarInActiveLoan(id)) {
            LOGGER.warning("Cannot delete car in active loan: " + id);
            return false;
        }
        return database.deleteCar(id);
    }
    
    /**
     * Check if car is in an active loan
     */
    public boolean isCarInActiveLoan(int carId) {
        return database.isCarInActiveLoan(carId);
    }
    
    /**
     * Get car count
     */
    public int getCarCount() {
        return database.getAllCars().size();
    }
    
    /**
     * Get available car count
     */
    public int getAvailableCarCount() {
        return database.getAvailableCars().size();
    }
    
    /**
     * Validate car data
     */
    private String validateCar(Car car) {
        if (car == null) {
            return "Car data is required.";
        }
        
        if (car.getMake() == null || car.getMake().trim().isEmpty()) {
            return "Make is required.";
        }
        
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            return "Model is required.";
        }
        
        if (car.getYear() < 1900 || car.getYear() > 2100) {
            return "Year must be between 1900 and 2100.";
        }
        
        if (car.getPrice() <= 0) {
            return "Price must be greater than 0.";
        }
        
        return null;
    }

    /**
     * Refresh car data
     */
    public void refreshData() {
        // Data is always fresh from text file
        LOGGER.info("Car data refreshed");
    }
}
