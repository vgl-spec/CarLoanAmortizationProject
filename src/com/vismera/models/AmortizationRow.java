package com.vismera.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a single row in the amortization schedule.
 * Maps to the 'amortization_rows' table in the database.
 * 
 * @author Vismerá Inc.
 */
public class AmortizationRow {
    
    private int id;
    private int loanId;
    private int periodIndex;
    private LocalDate dueDate;
    private BigDecimal openingBalance;
    private BigDecimal scheduledPayment;
    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private BigDecimal penaltyAmount;
    private BigDecimal extraPayment;
    private BigDecimal closingBalance;
    private boolean paid;
    private LocalDate paidDate;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor
     */
    public AmortizationRow() {
        this.penaltyAmount = BigDecimal.ZERO;
        this.extraPayment = BigDecimal.ZERO;
        this.paid = false;
    }
    
    /**
     * Constructor for creating a new amortization row
     */
    public AmortizationRow(int loanId, int periodIndex, LocalDate dueDate,
                          BigDecimal openingBalance, BigDecimal scheduledPayment,
                          BigDecimal principalPaid, BigDecimal interestPaid,
                          BigDecimal closingBalance) {
        this();
        this.loanId = loanId;
        this.periodIndex = periodIndex;
        this.dueDate = dueDate;
        this.openingBalance = openingBalance;
        this.scheduledPayment = scheduledPayment;
        this.principalPaid = principalPaid;
        this.interestPaid = interestPaid;
        this.closingBalance = closingBalance;
    }
    
    /**
     * Full constructor with all fields
     */
    public AmortizationRow(int id, int loanId, int periodIndex, LocalDate dueDate,
                          BigDecimal openingBalance, BigDecimal scheduledPayment,
                          BigDecimal principalPaid, BigDecimal interestPaid,
                          BigDecimal penaltyAmount, BigDecimal extraPayment,
                          BigDecimal closingBalance, boolean paid, LocalDate paidDate,
                          LocalDateTime createdAt) {
        this.id = id;
        this.loanId = loanId;
        this.periodIndex = periodIndex;
        this.dueDate = dueDate;
        this.openingBalance = openingBalance;
        this.scheduledPayment = scheduledPayment;
        this.principalPaid = principalPaid;
        this.interestPaid = interestPaid;
        this.penaltyAmount = penaltyAmount != null ? penaltyAmount : BigDecimal.ZERO;
        this.extraPayment = extraPayment != null ? extraPayment : BigDecimal.ZERO;
        this.closingBalance = closingBalance;
        this.paid = paid;
        this.paidDate = paidDate;
        this.createdAt = createdAt;
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getLoanId() {
        return loanId;
    }
    
    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }
    
    public int getPeriodIndex() {
        return periodIndex;
    }
    
    public void setPeriodIndex(int periodIndex) {
        this.periodIndex = periodIndex;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }
    
    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }
    
    public BigDecimal getScheduledPayment() {
        return scheduledPayment;
    }
    
    public void setScheduledPayment(BigDecimal scheduledPayment) {
        this.scheduledPayment = scheduledPayment;
    }
    
    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }
    
    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }
    
    public BigDecimal getInterestPaid() {
        return interestPaid;
    }
    
    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }
    
    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }
    
    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
    
    public BigDecimal getExtraPayment() {
        return extraPayment;
    }
    
    public void setExtraPayment(BigDecimal extraPayment) {
        this.extraPayment = extraPayment;
    }
    
    public BigDecimal getClosingBalance() {
        return closingBalance;
    }
    
    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }
    
    public boolean isPaid() {
        return paid;
    }
    
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    
    public LocalDate getPaidDate() {
        return paidDate;
    }
    
    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get total payment for this period (scheduled + penalty + extra)
     */
    public BigDecimal getTotalPayment() {
        BigDecimal total = scheduledPayment != null ? scheduledPayment : BigDecimal.ZERO;
        if (penaltyAmount != null) {
            total = total.add(penaltyAmount);
        }
        if (extraPayment != null) {
            total = total.add(extraPayment);
        }
        return total;
    }
    
    /**
     * Check if this period is overdue
     */
    public boolean isOverdue() {
        if (paid) return false;
        if (dueDate == null) return false;
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * Get days overdue
     */
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    /**
     * Get payment number (1-based period index for display)
     */
    public int getPaymentNumber() {
        return periodIndex;
    }
    
    /**
     * Get payment as double (for backward compatibility)
     */
    public double getPayment() {
        return scheduledPayment != null ? scheduledPayment.doubleValue() : 0.0;
    }
    
    /**
     * Get principal as double (for backward compatibility)
     */
    public double getPrincipal() {
        return principalPaid != null ? principalPaid.doubleValue() : 0.0;
    }
    
    /**
     * Get interest as double (for backward compatibility)
     */
    public double getInterest() {
        return interestPaid != null ? interestPaid.doubleValue() : 0.0;
    }
    
    /**
     * Get penalty as double (for backward compatibility)
     */
    public double getPenalty() {
        return penaltyAmount != null ? penaltyAmount.doubleValue() : 0.0;
    }
    
    /**
     * Get balance as double (for backward compatibility)
     */
    public double getBalance() {
        return closingBalance != null ? closingBalance.doubleValue() : 0.0;
    }
    
    /**
     * Get total paid as double (for backward compatibility)
     */
    public double getTotalPaid() {
        return getTotalPayment().doubleValue();
    }
    
    @Override
    public String toString() {
        return "Period " + periodIndex + " - Due: " + dueDate + 
               " - Payment: $" + (scheduledPayment != null ? scheduledPayment : "0.00");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AmortizationRow row = (AmortizationRow) obj;
        return id == row.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
