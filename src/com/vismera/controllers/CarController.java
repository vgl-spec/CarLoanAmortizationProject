package com.vismera.controllers;

import com.vismera.models.Car;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing car data.
 * Uses in-memory sample data for the calculator.
 * 
 * @author Vismerá Inc.
 */
public class CarController {
    
    private static CarController instance;
    private List<Car> cars;

    private CarController() {
        initializeSampleData();
    }

    /**
     * Initialize sample car data for the calculator
     */
    private void initializeSampleData() {
        cars = new ArrayList<>(Arrays.asList(
            // Sedans
            new Car(1, "Toyota", "Camry", 2024, "Sedan", "Midnight Black", 35, 1850000.00, ""),
            new Car(2, "Honda", "Civic", 2024, "Sedan", "Pearl White", 38, 1350000.00, ""),
            new Car(3, "Mazda", "3", 2024, "Sedan", "Soul Red", 34, 1450000.00, ""),
            new Car(4, "Hyundai", "Elantra", 2024, "Sedan", "Electric Blue", 36, 1198000.00, ""),
            new Car(5, "Nissan", "Altima", 2024, "Sedan", "Gun Metallic", 32, 1680000.00, ""),
            
            // SUVs
            new Car(6, "Toyota", "Fortuner", 2024, "SUV", "Super White", 28, 2096000.00, ""),
            new Car(7, "Ford", "Everest", 2024, "SUV", "Absolute Black", 26, 2299000.00, ""),
            new Car(8, "Mitsubishi", "Montero Sport", 2024, "SUV", "Jet Black", 27, 2150000.00, ""),
            new Car(9, "Honda", "CR-V", 2024, "SUV", "Modern Steel", 30, 2198000.00, ""),
            new Car(10, "Mazda", "CX-5", 2024, "SUV", "Machine Grey", 29, 1850000.00, ""),
            
            // Pickup Trucks
            new Car(11, "Toyota", "Hilux", 2024, "Pickup", "Emotional Red", 25, 1885000.00, ""),
            new Car(12, "Ford", "Ranger", 2024, "Pickup", "Lightning Blue", 24, 1698000.00, ""),
            new Car(13, "Nissan", "Navara", 2024, "Pickup", "Brilliant Silver", 26, 1579000.00, ""),
            new Car(14, "Mitsubishi", "Strada", 2024, "Pickup", "Quartz White", 25, 1505000.00, ""),
            new Car(15, "Isuzu", "D-Max", 2024, "Pickup", "Onyx Black", 23, 1625000.00, ""),
            
            // Luxury
            new Car(16, "BMW", "3 Series", 2024, "Luxury", "Alpine White", 30, 3590000.00, ""),
            new Car(17, "Mercedes-Benz", "C-Class", 2024, "Luxury", "Obsidian Black", 29, 3890000.00, ""),
            new Car(18, "Audi", "A4", 2024, "Luxury", "Mythos Black", 31, 3490000.00, ""),
            new Car(19, "Lexus", "IS", 2024, "Luxury", "Sonic Chrome", 28, 3280000.00, ""),
            new Car(20, "Volvo", "S60", 2024, "Luxury", "Crystal White", 27, 3150000.00, ""),
            
            // Hatchbacks
            new Car(21, "Toyota", "Yaris", 2024, "Hatchback", "Citrus Mica", 42, 1096000.00, ""),
            new Car(22, "Honda", "Jazz", 2024, "Hatchback", "Platinum White", 40, 1115000.00, ""),
            new Car(23, "Mazda", "2", 2024, "Hatchback", "Deep Crystal Blue", 39, 1070000.00, ""),
            new Car(24, "Suzuki", "Swift", 2024, "Hatchback", "Burning Red", 43, 914000.00, ""),
            new Car(25, "Kia", "Picanto", 2024, "Hatchback", "Clear White", 45, 780000.00, ""),
            
            // MPVs/Vans
            new Car(26, "Toyota", "Innova", 2024, "MPV", "Silver Metallic", 31, 1550000.00, ""),
            new Car(27, "Honda", "Odyssey", 2024, "MPV", "Platinum Pearl", 28, 2850000.00, ""),
            new Car(28, "Mitsubishi", "Xpander", 2024, "MPV", "Quartz White", 33, 1188000.00, ""),
            new Car(29, "Suzuki", "Ertiga", 2024, "MPV", "Magma Grey", 35, 998000.00, ""),
            new Car(30, "Nissan", "Livina", 2024, "MPV", "Brilliant Silver", 34, 1049000.00, "")
        ));
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
     * Get all cars
     */
    public List<Car> getAllCars() {
        return new ArrayList<>(cars);
    }
    
    /**
     * Get available cars (all cars are available in calculator mode)
     */
    public List<Car> getAvailableCars() {
        return getAllCars();
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
        return cars.stream()
            .filter(car -> car.getId() == id)
            .findFirst()
            .orElse(null);
    }

    /**
     * Get cars filtered by category
     */
    public List<Car> getCarsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("All")) {
            return getAllCars();
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
        return cars.stream()
            .map(Car::getCategory)
            .filter(cat -> cat != null)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Get distinct years
     */
    public List<Integer> getDistinctYears() {
        return cars.stream()
            .map(Car::getYear)
            .distinct()
            .sorted((a, b) -> b - a)
            .collect(Collectors.toList());
    }

    /**
     * Get car count
     */
    public int getCarCount() {
        return cars.size();
    }
    
    /**
     * Get available car count
     */
    public int getAvailableCarCount() {
        return cars.size();
    }

    /**
     * Refresh car data (no-op for in-memory data)
     */
    public void refreshData() {
        // Data is always fresh in memory
    }
}
