package com.vismera.controllers;

import com.vismera.models.Car;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing car data.
 * @author Vismerá Inc.
 */
public class CarController {
    
    private List<Car> cars;
    private static CarController instance;

    private CarController() {
        cars = new ArrayList<>();
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
     * Load car data (hardcoded for demo, could be from file/database)
     */
    private void loadCars() {
        cars.clear();
        
        // Sample cars matching the design reference
        cars.add(new Car(1, "Mercedes-Benz", "S-Class", 2024, "Luxury Sedan", 
                        "Silver", 24, 115000.00, "/resources/images/mercedes_sclass.png"));
        
        cars.add(new Car(2, "Porsche", "911 Carrera", 2024, "Sports Car", 
                        "Red", 20, 125000.00, "/resources/images/porsche_911.png"));
        
        cars.add(new Car(3, "BMW", "X5 M", 2024, "Luxury SUV", 
                        "Black", 22, 108000.00, "/resources/images/bmw_x5m.png"));
        
        // Additional cars
        cars.add(new Car(4, "Audi", "RS7", 2024, "Sports Car", 
                        "Gray", 21, 118000.00, "/resources/images/audi_rs7.png"));
        
        cars.add(new Car(5, "Lexus", "LS 500", 2024, "Luxury Sedan", 
                        "White", 25, 82000.00, "/resources/images/lexus_ls500.png"));
        
        cars.add(new Car(6, "Range Rover", "Sport", 2024, "Luxury SUV", 
                        "Green", 19, 95000.00, "/resources/images/rangerover_sport.png"));
    }

    /**
     * Get all cars
     */
    public List<Car> getAllCars() {
        return new ArrayList<>(cars);
    }

    /**
     * Search cars by make, model, or category
     */
    public List<Car> searchCars(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCars();
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        return cars.stream()
            .filter(car -> 
                car.getMake().toLowerCase().contains(lowerQuery) ||
                car.getModel().toLowerCase().contains(lowerQuery) ||
                car.getCategory().toLowerCase().contains(lowerQuery) ||
                car.getColor().toLowerCase().contains(lowerQuery) ||
                String.valueOf(car.getYear()).contains(lowerQuery)
            )
            .collect(Collectors.toList());
    }

    /**
     * Get a car by its ID
     */
    public Car getCarById(int id) {
        return cars.stream()
            .filter(car -> car.getId() == id)
            .findFirst()
            .orElse(null);
    }

    /**
     * Get cars filtered by category
     */
    public List<Car> getCarsByCategory(String category) {
        return cars.stream()
            .filter(car -> car.getCategory().equalsIgnoreCase(category))
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
        return cars.stream()
            .map(Car::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Refresh car data
     */
    public void refreshData() {
        loadCars();
    }
}
