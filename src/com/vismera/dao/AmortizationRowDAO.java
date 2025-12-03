package com.vismera.dao;

import com.vismera.config.DatabaseConfig;
import com.vismera.models.AmortizationRow;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for AmortizationRow entity.
 * Handles all database operations for amortization_rows table.
 * 
 * @author Vismerá Inc.
 */
public class AmortizationRowDAO {
    
    private static final Logger LOGGER = Logger.getLogger(AmortizationRowDAO.class.getName());
    
    private static AmortizationRowDAO instance;
    
    private AmortizationRowDAO() {
    }
    
    /**
     * Get singleton instance
     */
    public static AmortizationRowDAO getInstance() {
        if (instance == null) {
            instance = new AmortizationRowDAO();
        }
        return instance;
    }
    
    /**
     * Insert a new amortization row
     * @param row The row to insert
     * @return The generated row ID, or -1 if failed
     */
    public int insert(AmortizationRow row) {
        String sql = "INSERT INTO amortization_rows (loan_id, period_index, due_date, opening_balance, " +
                     "scheduled_payment, principal_paid, interest_paid, penalty_amount, extra_payment, " +
                     "closing_balance, is_paid, paid_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, row.getLoanId());
            stmt.setInt(2, row.getPeriodIndex());
            stmt.setDate(3, Date.valueOf(row.getDueDate()));
            stmt.setBigDecimal(4, row.getOpeningBalance());
            stmt.setBigDecimal(5, row.getScheduledPayment());
            stmt.setBigDecimal(6, row.getPrincipalPaid());
            stmt.setBigDecimal(7, row.getInterestPaid());
            stmt.setBigDecimal(8, row.getPenaltyAmount());
            stmt.setBigDecimal(9, row.getExtraPayment());
            stmt.setBigDecimal(10, row.getClosingBalance());
            stmt.setBoolean(11, row.isPaid());
            stmt.setDate(12, row.getPaidDate() != null ? Date.valueOf(row.getPaidDate()) : null);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        row.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting amortization row", e);
        }
        return -1;
    }
    
    /**
     * Batch insert multiple amortization rows (efficient for full schedule)
     * @param rows The list of rows to insert
     * @return true if all rows were inserted successfully
     */
    public boolean batchInsert(List<AmortizationRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return true;
        }
        
        String sql = "INSERT INTO amortization_rows (loan_id, period_index, due_date, opening_balance, " +
                     "scheduled_payment, principal_paid, interest_paid, penalty_amount, extra_payment, " +
                     "closing_balance, is_paid, paid_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            for (AmortizationRow row : rows) {
                stmt.setInt(1, row.getLoanId());
                stmt.setInt(2, row.getPeriodIndex());
                stmt.setDate(3, Date.valueOf(row.getDueDate()));
                stmt.setBigDecimal(4, row.getOpeningBalance());
                stmt.setBigDecimal(5, row.getScheduledPayment());
                stmt.setBigDecimal(6, row.getPrincipalPaid());
                stmt.setBigDecimal(7, row.getInterestPaid());
                stmt.setBigDecimal(8, row.getPenaltyAmount() != null ? row.getPenaltyAmount() : BigDecimal.ZERO);
                stmt.setBigDecimal(9, row.getExtraPayment() != null ? row.getExtraPayment() : BigDecimal.ZERO);
                stmt.setBigDecimal(10, row.getClosingBalance());
                stmt.setBoolean(11, row.isPaid());
                stmt.setDate(12, row.getPaidDate() != null ? Date.valueOf(row.getPaidDate()) : null);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            LOGGER.info("Batch inserted " + results.length + " amortization rows");
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error batch inserting amortization rows", e);
            DatabaseConfig.rollbackTransaction(conn);
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing resources", e);
            }
        }
    }
    
    /**
     * Find all amortization rows for a loan
     * @param loanId The loan ID
     * @return List of amortization rows ordered by period
     */
    public List<AmortizationRow> findByLoanId(int loanId) {
        List<AmortizationRow> rows = new ArrayList<>();
        String sql = "SELECT * FROM amortization_rows WHERE loan_id = ? ORDER BY period_index";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapResultSetToRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding amortization rows by loan ID: " + loanId, e);
        }
        return rows;
    }
    
    /**
     * Find specific amortization row by loan and period
     * @param loanId The loan ID
     * @param periodIndex The period index
     * @return The amortization row, or null if not found
     */
    public AmortizationRow findByLoanAndPeriod(int loanId, int periodIndex) {
        String sql = "SELECT * FROM amortization_rows WHERE loan_id = ? AND period_index = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            stmt.setInt(2, periodIndex);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding amortization row by loan and period", e);
        }
        return null;
    }
    
    /**
     * Find amortization row by ID
     * @param id The row ID
     * @return The amortization row, or null if not found
     */
    public AmortizationRow findById(int id) {
        String sql = "SELECT * FROM amortization_rows WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding amortization row by ID: " + id, e);
        }
        return null;
    }
    
    /**
     * Find unpaid amortization rows for a loan
     * @param loanId The loan ID
     * @return List of unpaid rows
     */
    public List<AmortizationRow> findUnpaidByLoanId(int loanId) {
        List<AmortizationRow> rows = new ArrayList<>();
        String sql = "SELECT * FROM amortization_rows WHERE loan_id = ? AND is_paid = FALSE ORDER BY period_index";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapResultSetToRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding unpaid amortization rows: " + loanId, e);
        }
        return rows;
    }
    
    /**
     * Find overdue amortization rows for a loan
     * @param loanId The loan ID
     * @return List of overdue rows
     */
    public List<AmortizationRow> findOverdueByLoanId(int loanId) {
        List<AmortizationRow> rows = new ArrayList<>();
        String sql = "SELECT * FROM amortization_rows WHERE loan_id = ? AND is_paid = FALSE AND due_date < CURDATE() ORDER BY period_index";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapResultSetToRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding overdue amortization rows: " + loanId, e);
        }
        return rows;
    }
    
    /**
     * Update an existing amortization row
     * @param row The row to update
     * @return true if successful
     */
    public boolean update(AmortizationRow row) {
        String sql = "UPDATE amortization_rows SET due_date = ?, opening_balance = ?, scheduled_payment = ?, " +
                     "principal_paid = ?, interest_paid = ?, penalty_amount = ?, extra_payment = ?, " +
                     "closing_balance = ?, is_paid = ?, paid_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(row.getDueDate()));
            stmt.setBigDecimal(2, row.getOpeningBalance());
            stmt.setBigDecimal(3, row.getScheduledPayment());
            stmt.setBigDecimal(4, row.getPrincipalPaid());
            stmt.setBigDecimal(5, row.getInterestPaid());
            stmt.setBigDecimal(6, row.getPenaltyAmount());
            stmt.setBigDecimal(7, row.getExtraPayment());
            stmt.setBigDecimal(8, row.getClosingBalance());
            stmt.setBoolean(9, row.isPaid());
            stmt.setDate(10, row.getPaidDate() != null ? Date.valueOf(row.getPaidDate()) : null);
            stmt.setInt(11, row.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating amortization row: " + row.getId(), e);
        }
        return false;
    }
    
    /**
     * Mark a period as paid
     * @param loanId The loan ID
     * @param periodIndex The period index
     * @param paidDate The date of payment
     * @return true if successful
     */
    public boolean markAsPaid(int loanId, int periodIndex, LocalDate paidDate) {
        String sql = "UPDATE amortization_rows SET is_paid = TRUE, paid_date = ? WHERE loan_id = ? AND period_index = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(paidDate));
            stmt.setInt(2, loanId);
            stmt.setInt(3, periodIndex);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking period as paid", e);
        }
        return false;
    }
    
    /**
     * Delete all amortization rows for a loan (used when regenerating schedule)
     * @param loanId The loan ID
     * @return true if successful
     */
    public boolean deleteByLoanId(int loanId) {
        String sql = "DELETE FROM amortization_rows WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            stmt.executeUpdate();
            LOGGER.info("Deleted amortization rows for loan: " + loanId);
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting amortization rows for loan: " + loanId, e);
        }
        return false;
    }
    
    /**
     * Get the last period index for a loan
     * @param loanId The loan ID
     * @return The last period index, or 0 if none
     */
    public int getLastPeriodIndex(int loanId) {
        String sql = "SELECT MAX(period_index) FROM amortization_rows WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting last period index for loan: " + loanId, e);
        }
        return 0;
    }
    
    /**
     * Get next unpaid period for a loan
     * @param loanId The loan ID
     * @return The next unpaid period row, or null if all paid
     */
    public AmortizationRow getNextUnpaidPeriod(int loanId) {
        String sql = "SELECT * FROM amortization_rows WHERE loan_id = ? AND is_paid = FALSE ORDER BY period_index LIMIT 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next unpaid period for loan: " + loanId, e);
        }
        return null;
    }
    
    /**
     * Get count of paid periods for a loan
     * @param loanId The loan ID
     * @return Number of paid periods
     */
    public int getPaidPeriodsCount(int loanId) {
        String sql = "SELECT COUNT(*) FROM amortization_rows WHERE loan_id = ? AND is_paid = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting paid periods count for loan: " + loanId, e);
        }
        return 0;
    }
    
    /**
     * Get total count of periods for a loan
     * @param loanId The loan ID
     * @return Total number of periods
     */
    public int getTotalPeriodsCount(int loanId) {
        String sql = "SELECT COUNT(*) FROM amortization_rows WHERE loan_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total periods count for loan: " + loanId, e);
        }
        return 0;
    }
    
    /**
     * Map ResultSet row to AmortizationRow object
     */
    private AmortizationRow mapResultSetToRow(ResultSet rs) throws SQLException {
        AmortizationRow row = new AmortizationRow();
        row.setId(rs.getInt("id"));
        row.setLoanId(rs.getInt("loan_id"));
        row.setPeriodIndex(rs.getInt("period_index"));
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            row.setDueDate(dueDate.toLocalDate());
        }
        
        row.setOpeningBalance(rs.getBigDecimal("opening_balance"));
        row.setScheduledPayment(rs.getBigDecimal("scheduled_payment"));
        row.setPrincipalPaid(rs.getBigDecimal("principal_paid"));
        row.setInterestPaid(rs.getBigDecimal("interest_paid"));
        row.setPenaltyAmount(rs.getBigDecimal("penalty_amount"));
        row.setExtraPayment(rs.getBigDecimal("extra_payment"));
        row.setClosingBalance(rs.getBigDecimal("closing_balance"));
        row.setPaid(rs.getBoolean("is_paid"));
        
        Date paidDate = rs.getDate("paid_date");
        if (paidDate != null) {
            row.setPaidDate(paidDate.toLocalDate());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            row.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return row;
    }
}
