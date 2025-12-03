package com.vismera.dao;

import com.vismera.config.DatabaseConfig;
import com.vismera.models.Car;
import com.vismera.models.Customer;
import com.vismera.models.Loan;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Loan entity.
 * Handles all database operations for loans table.
 * 
 * @author Vismerá Inc.
 */
public class LoanDAO {
    
    private static final Logger LOGGER = Logger.getLogger(LoanDAO.class.getName());
    
    private static LoanDAO instance;
    
    private LoanDAO() {
    }
    
    /**
     * Get singleton instance
     */
    public static LoanDAO getInstance() {
        if (instance == null) {
            instance = new LoanDAO();
        }
        return instance;
    }
    
    /**
     * Insert a new loan
     * @param loan The loan to insert
     * @return The generated loan ID, or -1 if failed
     */
    public int insert(Loan loan) {
        String sql = "INSERT INTO loans (customer_id, car_id, principal, apr, compounding, term_months, " +
                     "payment_frequency, start_date, penalty_rate, penalty_type, grace_period_days, " +
                     "down_payment, trade_in_value, sales_tax_rate, registration_fee, " +
                     "monthly_payment, total_interest, total_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, loan.getCustomerId());
            stmt.setInt(2, loan.getCarId());
            stmt.setBigDecimal(3, loan.getPrincipal());
            stmt.setBigDecimal(4, loan.getApr());
            stmt.setString(5, loan.getCompounding());
            stmt.setInt(6, loan.getTermMonths());
            stmt.setString(7, loan.getPaymentFrequency());
            stmt.setDate(8, Date.valueOf(loan.getStartDate()));
            stmt.setBigDecimal(9, loan.getPenaltyRate());
            stmt.setString(10, loan.getPenaltyType());
            stmt.setInt(11, loan.getGracePeriodDays());
            stmt.setBigDecimal(12, loan.getDownPayment());
            stmt.setBigDecimal(13, loan.getTradeInValue());
            stmt.setBigDecimal(14, loan.getSalesTaxRate());
            stmt.setBigDecimal(15, loan.getRegistrationFee());
            stmt.setBigDecimal(16, loan.getMonthlyPayment());
            stmt.setBigDecimal(17, loan.getTotalInterest());
            stmt.setBigDecimal(18, loan.getTotalAmount());
            stmt.setString(19, loan.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        loan.setId(id);
                        LOGGER.info("Loan inserted successfully with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting loan", e);
        }
        return -1;
    }
    
    /**
     * Find loan by ID
     * @param id The loan ID
     * @return The loan, or null if not found
     */
    public Loan findById(int id) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding loan by ID: " + id, e);
        }
        return null;
    }
    
    /**
     * Find loan by ID with customer and car details
     * @param id The loan ID
     * @return The loan with navigation properties populated
     */
    public Loan findByIdWithDetails(int id) {
        String sql = "SELECT l.*, " +
                     "c.full_name, c.contact_number, c.email, c.address, c.created_at as customer_created, " +
                     "car.make, car.model, car.year as car_year, car.price as car_price, car.category, " +
                     "car.color, car.mpg, car.image_path, car.notes as car_notes, car.is_available, car.created_at as car_created " +
                     "FROM loans l " +
                     "JOIN customers c ON l.customer_id = c.id " +
                     "JOIN cars car ON l.car_id = car.id " +
                     "WHERE l.id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = mapResultSetToLoan(rs);
                    
                    // Map customer
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setContactNumber(rs.getString("contact_number"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    Timestamp customerCreated = rs.getTimestamp("customer_created");
                    if (customerCreated != null) {
                        customer.setCreatedAt(customerCreated.toLocalDateTime());
                    }
                    loan.setCustomer(customer);
                    
                    // Map car
                    Car car = new Car();
                    car.setId(rs.getInt("car_id"));
                    car.setMake(rs.getString("make"));
                    car.setModel(rs.getString("model"));
                    car.setYear(rs.getInt("car_year"));
                    car.setPriceBigDecimal(rs.getBigDecimal("car_price"));
                    car.setCategory(rs.getString("category"));
                    car.setColor(rs.getString("color"));
                    car.setMpg(rs.getInt("mpg"));
                    car.setImagePath(rs.getString("image_path"));
                    car.setNotes(rs.getString("car_notes"));
                    car.setAvailable(rs.getBoolean("is_available"));
                    Timestamp carCreated = rs.getTimestamp("car_created");
                    if (carCreated != null) {
                        car.setCreatedAt(carCreated.toLocalDateTime());
                    }
                    loan.setCar(car);
                    
                    return loan;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding loan by ID with details: " + id, e);
        }
        return null;
    }
    
    /**
     * Find all loans
     * @return List of all loans
     */
    public List<Loan> findAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all loans", e);
        }
        return loans;
    }
    
    /**
     * Find all loans with customer and car details
     * @return List of all loans with details
     */
    public List<Loan> findAllWithDetails() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, " +
                     "c.full_name, c.contact_number, c.email, " +
                     "car.make, car.model, car.year as car_year, car.price as car_price " +
                     "FROM loans l " +
                     "JOIN customers c ON l.customer_id = c.id " +
                     "JOIN cars car ON l.car_id = car.id " +
                     "ORDER BY l.created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                
                // Map basic customer info
                Customer customer = new Customer();
                customer.setId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setContactNumber(rs.getString("contact_number"));
                customer.setEmail(rs.getString("email"));
                loan.setCustomer(customer);
                
                // Map basic car info
                Car car = new Car();
                car.setId(rs.getInt("car_id"));
                car.setMake(rs.getString("make"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("car_year"));
                car.setPriceBigDecimal(rs.getBigDecimal("car_price"));
                loan.setCar(car);
                
                loans.add(loan);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all loans with details", e);
        }
        return loans;
    }
    
    /**
     * Find loans by customer ID
     * @param customerId The customer ID
     * @return List of loans for that customer
     */
    public List<Loan> findByCustomerId(int customerId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE customer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding loans by customer ID: " + customerId, e);
        }
        return loans;
    }
    
    /**
     * Find loans by status
     * @param status The loan status
     * @return List of loans with that status
     */
    public List<Loan> findByStatus(String status) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding loans by status: " + status, e);
        }
        return loans;
    }
    
    /**
     * Get active loans with details
     * @return List of active loans
     */
    public List<Loan> getActiveLoans() {
        return findByStatusWithDetails(Loan.STATUS_ACTIVE);
    }
    
    /**
     * Find loans by status with customer and car details
     * @param status The loan status
     * @return List of loans with details
     */
    public List<Loan> findByStatusWithDetails(String status) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, " +
                     "c.full_name, c.contact_number, c.email, " +
                     "car.make, car.model, car.year as car_year, car.price as car_price " +
                     "FROM loans l " +
                     "JOIN customers c ON l.customer_id = c.id " +
                     "JOIN cars car ON l.car_id = car.id " +
                     "WHERE l.status = ? " +
                     "ORDER BY l.created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = mapResultSetToLoan(rs);
                    
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setContactNumber(rs.getString("contact_number"));
                    customer.setEmail(rs.getString("email"));
                    loan.setCustomer(customer);
                    
                    Car car = new Car();
                    car.setId(rs.getInt("car_id"));
                    car.setMake(rs.getString("make"));
                    car.setModel(rs.getString("model"));
                    car.setYear(rs.getInt("car_year"));
                    car.setPriceBigDecimal(rs.getBigDecimal("car_price"));
                    loan.setCar(car);
                    
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding loans by status with details: " + status, e);
        }
        return loans;
    }
    
    /**
     * Update an existing loan
     * @param loan The loan to update
     * @return true if successful
     */
    public boolean update(Loan loan) {
        String sql = "UPDATE loans SET customer_id = ?, car_id = ?, principal = ?, apr = ?, compounding = ?, " +
                     "term_months = ?, payment_frequency = ?, start_date = ?, penalty_rate = ?, penalty_type = ?, " +
                     "grace_period_days = ?, down_payment = ?, trade_in_value = ?, sales_tax_rate = ?, " +
                     "registration_fee = ?, monthly_payment = ?, total_interest = ?, total_amount = ?, status = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loan.getCustomerId());
            stmt.setInt(2, loan.getCarId());
            stmt.setBigDecimal(3, loan.getPrincipal());
            stmt.setBigDecimal(4, loan.getApr());
            stmt.setString(5, loan.getCompounding());
            stmt.setInt(6, loan.getTermMonths());
            stmt.setString(7, loan.getPaymentFrequency());
            stmt.setDate(8, Date.valueOf(loan.getStartDate()));
            stmt.setBigDecimal(9, loan.getPenaltyRate());
            stmt.setString(10, loan.getPenaltyType());
            stmt.setInt(11, loan.getGracePeriodDays());
            stmt.setBigDecimal(12, loan.getDownPayment());
            stmt.setBigDecimal(13, loan.getTradeInValue());
            stmt.setBigDecimal(14, loan.getSalesTaxRate());
            stmt.setBigDecimal(15, loan.getRegistrationFee());
            stmt.setBigDecimal(16, loan.getMonthlyPayment());
            stmt.setBigDecimal(17, loan.getTotalInterest());
            stmt.setBigDecimal(18, loan.getTotalAmount());
            stmt.setString(19, loan.getStatus());
            stmt.setInt(20, loan.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Loan updated successfully: " + loan.getId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating loan: " + loan.getId(), e);
        }
        return false;
    }
    
    /**
     * Update loan status
     * @param loanId The loan ID
     * @param newStatus The new status
     * @return true if successful
     */
    public boolean updateStatus(int loanId, String newStatus) {
        String sql = "UPDATE loans SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, loanId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Loan status updated: " + loanId + " -> " + newStatus);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating loan status: " + loanId, e);
        }
        return false;
    }
    
    /**
     * Delete a loan by ID
     * Note: This will cascade delete amortization rows
     * @param id The loan ID
     * @return true if successful
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Loan deleted successfully: " + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting loan: " + id, e);
        }
        return false;
    }
    
    /**
     * Get total outstanding balance for all active loans
     * @return Total outstanding amount
     */
    public BigDecimal getTotalOutstanding() {
        String sql = "SELECT COALESCE(SUM(total_amount - " +
                     "(SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payments.loan_id = loans.id)), 0) " +
                     "FROM loans WHERE status = 'active'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total outstanding", e);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get count of loans by status
     * @param status The status to count
     * @return Loan count
     */
    public int getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting loan count by status: " + status, e);
        }
        return 0;
    }
    
    /**
     * Get total count of loans
     * @return Total loan count
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM loans";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total loan count", e);
        }
        return 0;
    }
    
    /**
     * Map ResultSet row to Loan object
     */
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setCustomerId(rs.getInt("customer_id"));
        loan.setCarId(rs.getInt("car_id"));
        loan.setPrincipal(rs.getBigDecimal("principal"));
        loan.setApr(rs.getBigDecimal("apr"));
        loan.setCompounding(rs.getString("compounding"));
        loan.setTermMonths(rs.getInt("term_months"));
        loan.setPaymentFrequency(rs.getString("payment_frequency"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            loan.setStartDate(startDate.toLocalDate());
        }
        
        loan.setPenaltyRate(rs.getBigDecimal("penalty_rate"));
        loan.setPenaltyType(rs.getString("penalty_type"));
        loan.setGracePeriodDays(rs.getInt("grace_period_days"));
        loan.setDownPayment(rs.getBigDecimal("down_payment"));
        loan.setTradeInValue(rs.getBigDecimal("trade_in_value"));
        loan.setSalesTaxRate(rs.getBigDecimal("sales_tax_rate"));
        loan.setRegistrationFee(rs.getBigDecimal("registration_fee"));
        loan.setMonthlyPayment(rs.getBigDecimal("monthly_payment"));
        loan.setTotalInterest(rs.getBigDecimal("total_interest"));
        loan.setTotalAmount(rs.getBigDecimal("total_amount"));
        loan.setStatus(rs.getString("status"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            loan.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            loan.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return loan;
    }
}
