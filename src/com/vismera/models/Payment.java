package com.vismera.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a payment made against a loan.
 * Maps to the 'payments' table in the database.
 * 
 * @author Vismerá Inc.
 */
public class Payment {
    
    private int id;
    private int loanId;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private Integer appliedToPeriod;
    private String type; // regular, partial, advance, late, full
    private BigDecimal penaltyApplied;
    private BigDecimal principalApplied;
    private BigDecimal interestApplied;
    private String note;
    private String recordedBy;
    private LocalDateTime recordedAt;
    
    // Navigation property
    private Loan loan;
    
    // Payment type constants
    public static final String TYPE_REGULAR = "regular";
    public static final String TYPE_PARTIAL = "partial";
    public static final String TYPE_ADVANCE = "advance";
    public static final String TYPE_LATE = "late";
    public static final String TYPE_FULL = "full";
    public static final String TYPE_EXTRA = "extra";
    public static final String TYPE_EARLY_PAYOFF = "early_payoff";
    public static final String TYPE_PENALTY_ONLY = "penalty_only";
    
    /**
     * Default constructor
     */
    public Payment() {
        this.type = TYPE_REGULAR;
        this.penaltyApplied = BigDecimal.ZERO;
        this.principalApplied = BigDecimal.ZERO;
        this.interestApplied = BigDecimal.ZERO;
    }
    
    /**
     * Constructor for creating a new payment
     */
    public Payment(int loanId, LocalDate paymentDate, BigDecimal amount, String type) {
        this();
        this.loanId = loanId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.type = type;
    }
    
    /**
     * Full constructor with all fields
     */
    public Payment(int id, int loanId, LocalDate paymentDate, BigDecimal amount,
                  Integer appliedToPeriod, String type, BigDecimal penaltyApplied,
                  BigDecimal principalApplied, BigDecimal interestApplied,
                  String note, String recordedBy, LocalDateTime recordedAt) {
        this.id = id;
        this.loanId = loanId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.appliedToPeriod = appliedToPeriod;
        this.type = type;
        this.penaltyApplied = penaltyApplied != null ? penaltyApplied : BigDecimal.ZERO;
        this.principalApplied = principalApplied != null ? principalApplied : BigDecimal.ZERO;
        this.interestApplied = interestApplied != null ? interestApplied : BigDecimal.ZERO;
        this.note = note;
        this.recordedBy = recordedBy;
        this.recordedAt = recordedAt;
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
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Integer getAppliedToPeriod() {
        return appliedToPeriod;
    }
    
    public void setAppliedToPeriod(Integer appliedToPeriod) {
        this.appliedToPeriod = appliedToPeriod;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getPenaltyApplied() {
        return penaltyApplied;
    }
    
    public void setPenaltyApplied(BigDecimal penaltyApplied) {
        this.penaltyApplied = penaltyApplied;
    }
    
    public BigDecimal getPrincipalApplied() {
        return principalApplied;
    }
    
    public void setPrincipalApplied(BigDecimal principalApplied) {
        this.principalApplied = principalApplied;
    }
    
    public BigDecimal getInterestApplied() {
        return interestApplied;
    }
    
    public void setInterestApplied(BigDecimal interestApplied) {
        this.interestApplied = interestApplied;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public String getRecordedBy() {
        return recordedBy;
    }
    
    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }
    
    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
    
    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
    
    public Loan getLoan() {
        return loan;
    }
    
    public void setLoan(Loan loan) {
        this.loan = loan;
        if (loan != null) {
            this.loanId = loan.getId();
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Check if this is a late payment
     */
    public boolean isLatePayment() {
        return TYPE_LATE.equals(type) || 
               (penaltyApplied != null && penaltyApplied.compareTo(BigDecimal.ZERO) > 0);
    }
    
    /**
     * Check if this is a full payoff
     */
    public boolean isFullPayoff() {
        return TYPE_FULL.equals(type);
    }
    
    /**
     * Get payment summary
     */
    public String getPaymentSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Payment #").append(id);
        sb.append(" - ").append(paymentDate);
        sb.append(" - $").append(amount);
        sb.append(" (").append(type).append(")");
        if (penaltyApplied != null && penaltyApplied.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" [Penalty: $").append(penaltyApplied).append("]");
        }
        return sb.toString();
    }
    
    /**
     * Get type display name
     */
    public String getTypeDisplayName() {
        if (type == null) return "Regular";
        switch (type.toLowerCase()) {
            case TYPE_REGULAR: return "Regular";
            case TYPE_PARTIAL: return "Partial";
            case TYPE_ADVANCE: return "Advance";
            case TYPE_LATE: return "Late";
            case TYPE_FULL: return "Full Payoff";
            case TYPE_EXTRA: return "Extra";
            default: return type;
        }
    }
    
    /**
     * Check if payment has required fields
     */
    public boolean isValid() {
        return loanId > 0 &&
               paymentDate != null &&
               amount != null && amount.compareTo(BigDecimal.ZERO) > 0 &&
               type != null && !type.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return getPaymentSummary();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Payment payment = (Payment) obj;
        return id == payment.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
