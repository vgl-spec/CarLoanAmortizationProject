package com.vismera.controllers;

import com.vismera.dao.CarDAO;
import com.vismera.models.Car;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for managing car data.
 * Now integrated with database through CarDAO.
 * 
 * @author Vismerá Inc.
 */
public class CarController {
    
    private static final Logger LOGGER = Logger.getLogger(CarController.class.getName());
    
    private List<Car> cars;
    private static CarController instance;
    private final CarDAO carDAO;
    private boolean useDatabaseMode = false;

    private CarController() {
        cars = new ArrayList<>();
        carDAO = CarDAO.getInstance();
        // Try to load from database, fallback to hardcoded
        loadCars();
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
     * Enable database mode
     */
    public void enableDatabaseMode() {
        this.useDatabaseMode = true;
        loadCarsFromDatabase();
    }
    
    /**
     * Disable database mode (use hardcoded cars)
     */
    public void disableDatabaseMode() {
        this.useDatabaseMode = false;
        loadHardcodedCars();
    }
    
    /**
     * Set database mode on/off
     * @param useDatabase true to use database, false for hardcoded
     */
    public void setUseDatabase(boolean useDatabase) {
        if (useDatabase) {
            enableDatabaseMode();
        } else {
            disableDatabaseMode();
        }
    }
    
    /**
     * Check if database mode is enabled
     */
    public boolean isDatabaseModeEnabled() {
        return useDatabaseMode;
    }

    /**
     * Load car data (tries database first, falls back to hardcoded)
     */
    private void loadCars() {
        if (useDatabaseMode) {
            loadCarsFromDatabase();
        } else {
            // Try database first
            try {
                List<Car> dbCars = carDAO.findAll();
                if (!dbCars.isEmpty()) {
                    cars = dbCars;
                    useDatabaseMode = true;
                    LOGGER.info("Loaded " + cars.size() + " cars from database");
                    return;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load cars from database, using hardcoded data", e);
            }
            loadHardcodedCars();
        }
    }
    
    /**
     * Load cars from database
     */
    private void loadCarsFromDatabase() {
        try {
            cars = carDAO.findAll();
            LOGGER.info("Loaded " + cars.size() + " cars from database");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading cars from database", e);
            cars = new ArrayList<>();
        }
    }
    
    /**
     * Load hardcoded car data (fallback for demo/offline mode)
     */
    private void loadHardcodedCars() {
        cars.clear();
        
        // Sample cars matching the actual image files in resources/images
        cars.add(new Car(1, "Mercedes-Benz", "S-Class", 2024, "Luxury Sedan", 
                        "Silver", 24, 115000.00, "mercedes_sclass.png"));
        
        cars.add(new Car(2, "Porsche", "911 Carrera", 2024, "Sports Car", 
                        "Red", 20, 125000.00, "porsche_911.png"));
        
        cars.add(new Car(3, "BMW", "X5 M", 2024, "Luxury SUV", 
                        "Black", 22, 108000.00, "bmw_x5m.png"));
        
        // Additional cars
        cars.add(new Car(4, "Audi", "RS7", 2024, "Sports Car", 
                        "Gray", 21, 118000.00, "audi_rs7.png"));
        
        cars.add(new Car(5, "Lexus", "LS 500", 2024, "Luxury Sedan", 
                        "White", 25, 82000.00, "lexus_ls500.png"));
        
        cars.add(new Car(6, "Range Rover", "Sport", 2024, "Luxury SUV", 
                        "Green", 19, 95000.00, "rangerover_sport.png"));
        
        LOGGER.info("Loaded " + cars.size() + " hardcoded cars");
    }

    /**
     * Get all cars
     */
    public List<Car> getAllCars() {
        if (useDatabaseMode) {
            return carDAO.findAll();
        }
        return new ArrayList<>(cars);
    }
    
    /**
     * Get available cars (not in active loans)
     */
    public List<Car> getAvailableCars() {
        if (useDatabaseMode) {
            return carDAO.findAvailable();
        }
        return cars.stream()
            .filter(Car::isAvailable)
            .collect(Collectors.toList());
    }

    /**
     * Search cars by make, model, or category
     */
    public List<Car> searchCars(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCars();
        }
        
        if (useDatabaseMode) {
            return carDAO.searchCars(query);
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        return cars.stream()
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
        if (useDatabaseMode) {
            return carDAO.findById(id);
        }
        return cars.stream()
            .filter(car -> car.getId() == id)
            .findFirst()
            .orElse(null);
    }

    /**
     * Get cars filtered by category
     */
    public List<Car> getCarsByCategory(String category) {
        if (useDatabaseMode) {
            return carDAO.findByCategory(category);
        }
        return cars.stream()
            .filter(car -> car.getCategory() != null && car.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    /**
     * Get cars within a price range
     */
    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice) {
        return cars.stream()
            .filter(car -> car.getPrice() >= minPrice && car.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }

    /**
     * Get distinct categories
     */
    public List<String> getCategories() {
        if (useDatabaseMode) {
            return carDAO.getDistinctCategories();
        }
        return cars.stream()
            .map(Car::getCategory)
            .filter(cat -> cat != null)
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Get distinct years
     */
    public List<Integer> getDistinctYears() {
        if (useDatabaseMode) {
            return carDAO.getDistinctYears();
        }
        return cars.stream()
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
        
        int id = carDAO.insert(car);
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
        
        Car existing = carDAO.findById(car.getId());
        if (existing == null) {
            LOGGER.warning("Car not found: " + car.getId());
            return false;
        }
        
        boolean updated = carDAO.update(car);
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
        
        int id = carDAO.insert(car);
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
        return carDAO.delete(id);
    }
    
    /**
     * Check if car is in an active loan
     */
    public boolean isCarInActiveLoan(int carId) {
        if (useDatabaseMode) {
            return carDAO.isCarInActiveLoan(carId);
        }
        return false;
    }
    
    /**
     * Get car count
     */
    public int getCarCount() {
        if (useDatabaseMode) {
            return carDAO.getCount();
        }
        return cars.size();
    }
    
    /**
     * Get available car count
     */
    public int getAvailableCarCount() {
        if (useDatabaseMode) {
            return carDAO.getAvailableCount();
        }
        return (int) cars.stream().filter(Car::isAvailable).count();
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
        loadCars();
    }
}
