package com.vismera.dao;

import com.vismera.config.DatabaseConfig;
import com.vismera.models.Car;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Car entity.
 * Handles all database operations for cars table.
 * 
 * @author Vismerá Inc.
 */
public class CarDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CarDAO.class.getName());
    
    private static CarDAO instance;
    
    private CarDAO() {
    }
    
    /**
     * Get singleton instance
     */
    public static CarDAO getInstance() {
        if (instance == null) {
            instance = new CarDAO();
        }
        return instance;
    }
    
    /**
     * Insert a new car
     * @param car The car to insert
     * @return The generated car ID, or -1 if failed
     */
    public int insert(Car car) {
        String sql = "INSERT INTO cars (make, model, year, price, category, color, mpg, image_path, notes, is_available) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setBigDecimal(4, car.getPriceBigDecimal());
            stmt.setString(5, car.getCategory());
            stmt.setString(6, car.getColor());
            stmt.setInt(7, car.getMpg());
            stmt.setString(8, car.getImagePath());
            stmt.setString(9, car.getNotes());
            stmt.setBoolean(10, car.isAvailable());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        car.setId(id);
                        LOGGER.info("Car inserted successfully with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting car", e);
        }
        return -1;
    }
    
    /**
     * Find car by ID
     * @param id The car ID
     * @return The car, or null if not found
     */
    public Car findById(int id) {
        String sql = "SELECT id, make, model, year, price, category, color, mpg, image_path, notes, is_available, created_at " +
                     "FROM cars WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCar(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding car by ID: " + id, e);
        }
        return null;
    }
    
    /**
     * Find all cars
     * @return List of all cars
     */
    public List<Car> findAll() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, make, model, year, price, category, color, mpg, image_path, notes, is_available, created_at " +
                     "FROM cars ORDER BY make, model, year DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all cars", e);
        }
        return cars;
    }
    
    /**
     * Find available cars (not in active loans)
     * @return List of available cars
     */
    public List<Car> findAvailable() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT c.id, c.make, c.model, c.year, c.price, c.category, c.color, c.mpg, " +
                     "c.image_path, c.notes, c.is_available, c.created_at " +
                     "FROM cars c " +
                     "WHERE c.is_available = TRUE " +
                     "AND c.id NOT IN (SELECT car_id FROM loans WHERE status = 'active') " +
                     "ORDER BY c.make, c.model, c.year DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding available cars", e);
        }
        return cars;
    }
    
    /**
     * Search cars by make, model, or year
     * @param query The search query
     * @return List of matching cars
     */
    public List<Car> searchCars(String query) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, make, model, year, price, category, color, mpg, image_path, notes, is_available, created_at " +
                     "FROM cars WHERE make LIKE ? OR model LIKE ? OR CAST(year AS CHAR) LIKE ? OR category LIKE ? " +
                     "ORDER BY make, model, year DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapResultSetToCar(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching cars: " + query, e);
        }
        return cars;
    }
    
    /**
     * Find cars by year
     * @param year The year to filter by
     * @return List of cars from that year
     */
    public List<Car> findByYear(int year) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, make, model, year, price, category, color, mpg, image_path, notes, is_available, created_at " +
                     "FROM cars WHERE year = ? ORDER BY make, model";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapResultSetToCar(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding cars by year: " + year, e);
        }
        return cars;
    }
    
    /**
     * Find cars by category
     * @param category The category to filter by
     * @return List of cars in that category
     */
    public List<Car> findByCategory(String category) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT id, make, model, year, price, category, color, mpg, image_path, notes, is_available, created_at " +
                     "FROM cars WHERE category = ? ORDER BY make, model, year DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapResultSetToCar(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding cars by category: " + category, e);
        }
        return cars;
    }
    
    /**
     * Update an existing car
     * @param car The car to update
     * @return true if successful
     */
    public boolean update(Car car) {
        String sql = "UPDATE cars SET make = ?, model = ?, year = ?, price = ?, category = ?, " +
                     "color = ?, mpg = ?, image_path = ?, notes = ?, is_available = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setBigDecimal(4, car.getPriceBigDecimal());
            stmt.setString(5, car.getCategory());
            stmt.setString(6, car.getColor());
            stmt.setInt(7, car.getMpg());
            stmt.setString(8, car.getImagePath());
            stmt.setString(9, car.getNotes());
            stmt.setBoolean(10, car.isAvailable());
            stmt.setInt(11, car.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Car updated successfully: " + car.getId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating car: " + car.getId(), e);
        }
        return false;
    }
    
    /**
     * Delete a car by ID
     * @param id The car ID
     * @return true if successful
     */
    public boolean delete(int id) {
        // First check if car is in active loan
        if (isCarInActiveLoan(id)) {
            LOGGER.warning("Cannot delete car in active loan: " + id);
            return false;
        }
        
        String sql = "DELETE FROM cars WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Car deleted successfully: " + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting car: " + id, e);
        }
        return false;
    }
    
    /**
     * Check if car is in an active loan
     * @param carId The car ID
     * @return true if car is in active loan
     */
    public boolean isCarInActiveLoan(int carId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE car_id = ? AND status = 'active'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, carId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking active loan for car: " + carId, e);
        }
        return false;
    }
    
    /**
     * Get total number of cars
     * @return Car count
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM cars";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting car count", e);
        }
        return 0;
    }
    
    /**
     * Get count of available cars
     * @return Available car count
     */
    public int getAvailableCount() {
        String sql = "SELECT COUNT(*) FROM cars c " +
                     "WHERE c.is_available = TRUE " +
                     "AND c.id NOT IN (SELECT car_id FROM loans WHERE status = 'active')";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting available car count", e);
        }
        return 0;
    }
    
    /**
     * Get distinct years from inventory
     * @return List of years
     */
    public List<Integer> getDistinctYears() {
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT year FROM cars ORDER BY year DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting distinct years", e);
        }
        return years;
    }
    
    /**
     * Get distinct categories from inventory
     * @return List of categories
     */
    public List<String> getDistinctCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM cars WHERE category IS NOT NULL ORDER BY category";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting distinct categories", e);
        }
        return categories;
    }
    
    /**
     * Map ResultSet row to Car object
     */
    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setMake(rs.getString("make"));
        car.setModel(rs.getString("model"));
        car.setYear(rs.getInt("year"));
        
        BigDecimal price = rs.getBigDecimal("price");
        car.setPriceBigDecimal(price != null ? price : BigDecimal.ZERO);
        
        car.setCategory(rs.getString("category"));
        car.setColor(rs.getString("color"));
        car.setMpg(rs.getInt("mpg"));
        car.setImagePath(rs.getString("image_path"));
        car.setNotes(rs.getString("notes"));
        car.setAvailable(rs.getBoolean("is_available"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            car.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return car;
    }
}
