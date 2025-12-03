package com.vismera.dao;

import com.vismera.config.DatabaseConfig;
import com.vismera.models.Payment;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Payment entity.
 * Handles all database operations for payments table.
 * 
 * @author Vismerá Inc.
 */
public class PaymentDAO {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());
    
    private static PaymentDAO instance;
    
    private PaymentDAO() {
    }
    
    /**
     * Get singleton instance
     */
    public static PaymentDAO getInstance() {
        if (instance == null) {
            instance = new PaymentDAO();
        }
        return instance;
    }
    
    /**
     * Insert a new payment
     * @param payment The payment to insert
     * @return The generated payment ID, or -1 if failed
     */
    public int insert(Payment payment) {
        String sql = "INSERT INTO payments (loan_id, payment_date, amount, applied_to_period, type, " +
                     "penalty_applied, principal_applied, interest_applied, note, recorded_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getLoanId());
            stmt.setDate(2, Date.valueOf(payment.getPaymentDate()));
            stmt.setBigDecimal(3, payment.getAmount());
            
            if (payment.getAppliedToPeriod() != null) {
                stmt.setInt(4, payment.getAppliedToPeriod());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setString(5, payment.getType());
            stmt.setBigDecimal(6, payment.getPenaltyApplied());
            stmt.setBigDecimal(7, payment.getPrincipalApplied());
            stmt.setBigDecimal(8, payment.getInterestApplied());
            stmt.setString(9, payment.getNote());
            stmt.setString(10, payment.getRecordedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        payment.setId(id);
                        LOGGER.info("Payment inserted successfully with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting payment", e);
        }
        return -1;
    }
    
    /**
     * Find payment by ID
     * @param id The payment ID
     * @return The payment, or null if not found
     */
    public Payment findById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding payment by ID: " + id, e);
        }
        return null;
    }
    
    /**
     * Find all payments for a loan
     * @param loanId The loan ID
     * @return List of payments for that loan
     */
    public List<Payment> findByLoanId(int loanId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE loan_id = ? ORDER BY payment_date DESC, id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding payments by loan ID: " + loanId, e);
        }
        return payments;
    }
    
    /**
     * Find payments within a date range
     * @param start Start date
     * @param end End date
     * @return List of payments in that range
     */
    public List<Payment> findByDateRange(LocalDate start, LocalDate end) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE payment_date BETWEEN ? AND ? ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding payments by date range", e);
        }
        return payments;
    }
    
    /**
     * Find all payments
     * @return List of all payments
     */
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC, id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all payments", e);
        }
        return payments;
    }
    
    /**
     * Get total amount paid for a loan
     * @param loanId The loan ID
     * @return Total paid amount
     */
    public BigDecimal getTotalPaidForLoan(int loanId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total paid for loan: " + loanId, e);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get total penalties paid for a loan
     * @param loanId The loan ID
     * @return Total penalties paid
     */
    public BigDecimal getPenaltiesPaidForLoan(int loanId) {
        String sql = "SELECT COALESCE(SUM(penalty_applied), 0) FROM payments WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting penalties paid for loan: " + loanId, e);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get the last payment for a loan
     * @param loanId The loan ID
     * @return The most recent payment, or null if none
     */
    public Payment getLastPayment(int loanId) {
        String sql = "SELECT * FROM payments WHERE loan_id = ? ORDER BY payment_date DESC, id DESC LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting last payment for loan: " + loanId, e);
        }
        return null;
    }
    
    /**
     * Update an existing payment
     * @param payment The payment to update
     * @return true if successful
     */
    public boolean update(Payment payment) {
        String sql = "UPDATE payments SET payment_date = ?, amount = ?, applied_to_period = ?, type = ?, " +
                     "penalty_applied = ?, principal_applied = ?, interest_applied = ?, note = ?, recorded_by = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(payment.getPaymentDate()));
            stmt.setBigDecimal(2, payment.getAmount());
            
            if (payment.getAppliedToPeriod() != null) {
                stmt.setInt(3, payment.getAppliedToPeriod());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, payment.getType());
            stmt.setBigDecimal(5, payment.getPenaltyApplied());
            stmt.setBigDecimal(6, payment.getPrincipalApplied());
            stmt.setBigDecimal(7, payment.getInterestApplied());
            stmt.setString(8, payment.getNote());
            stmt.setString(9, payment.getRecordedBy());
            stmt.setInt(10, payment.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Payment updated successfully: " + payment.getId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating payment: " + payment.getId(), e);
        }
        return false;
    }
    
    /**
     * Delete a payment by ID
     * @param id The payment ID
     * @return true if successful
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Payment deleted successfully: " + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting payment: " + id, e);
        }
        return false;
    }
    
    /**
     * Get count of payments for a loan
     * @param loanId The loan ID
     * @return Payment count
     */
    public int getCountForLoan(int loanId) {
        String sql = "SELECT COUNT(*) FROM payments WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payment count for loan: " + loanId, e);
        }
        return 0;
    }
    
    /**
     * Get total payments in a date range
     * @param start Start date
     * @param end End date
     * @return Total amount
     */
    public BigDecimal getTotalInDateRange(LocalDate start, LocalDate end) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total payments in date range", e);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Map ResultSet row to Payment object
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setLoanId(rs.getInt("loan_id"));
        
        Date paymentDate = rs.getDate("payment_date");
        if (paymentDate != null) {
            payment.setPaymentDate(paymentDate.toLocalDate());
        }
        
        payment.setAmount(rs.getBigDecimal("amount"));
        
        int appliedToPeriod = rs.getInt("applied_to_period");
        if (!rs.wasNull()) {
            payment.setAppliedToPeriod(appliedToPeriod);
        }
        
        payment.setType(rs.getString("type"));
        payment.setPenaltyApplied(rs.getBigDecimal("penalty_applied"));
        payment.setPrincipalApplied(rs.getBigDecimal("principal_applied"));
        payment.setInterestApplied(rs.getBigDecimal("interest_applied"));
        payment.setNote(rs.getString("note"));
        payment.setRecordedBy(rs.getString("recorded_by"));
        
        Timestamp recordedAt = rs.getTimestamp("recorded_at");
        if (recordedAt != null) {
            payment.setRecordedAt(recordedAt.toLocalDateTime());
        }
        
        return payment;
    }
}
