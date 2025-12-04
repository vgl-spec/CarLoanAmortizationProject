package com.vismera.controllers;

import com.vismera.models.AmortizationRow;
import com.vismera.models.Loan;
import com.vismera.models.Payment;
import com.vismera.storage.TextFileDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for payment management and processing.
 * Now uses TextFileDatabase for storage.
 * 
 * @author Vismerá Inc.
 */
public class PaymentController {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());
    
    private static PaymentController instance;
    
    private final TextFileDatabase database;
    
    private PaymentController() {
        database = TextFileDatabase.getInstance();
    }
    
    /**
     * Get singleton instance
     */
    public static PaymentController getInstance() {
        if (instance == null) {
            instance = new PaymentController();
        }
        return instance;
    }
    
    /**
     * Record a payment
     * @param payment The payment to record
     * @return The payment ID, or -1 if failed
     */
    public int recordPayment(Payment payment) {
        // Validate payment
        String error = validatePayment(payment);
        if (error != null) {
            LOGGER.warning("Payment validation failed: " + error);
            return -1;
        }
        
        // Insert payment
        int paymentId = database.insertPayment(payment);
        if (paymentId <= 0) {
            LOGGER.severe("Failed to record payment");
            return -1;
        }
        
        // Update amortization row if period specified
        if (payment.getAppliedToPeriod() != null) {
            List<AmortizationRow> rows = database.getAmortizationByLoanId(payment.getLoanId());
            for (AmortizationRow row : rows) {
                if (row.getPeriodIndex() == payment.getAppliedToPeriod()) {
                    row.setPaid(true);
                    row.setPaidDate(payment.getPaymentDate());
                    database.updateAmortizationRow(row);
                    break;
                }
            }
        }
        
        // Check if loan is fully paid
        checkAndCloseLoanIfPaid(payment.getLoanId());
        
        LOGGER.info("Payment #" + paymentId + " recorded for loan #" + payment.getLoanId());
        return paymentId;
    }
    
    /**
     * Update a payment
     */
    public boolean updatePayment(Payment payment) {
        // Simple update - just record the new version
        return database.insertPayment(payment) > 0;
    }
    
    /**
     * Delete a payment
     */
    public boolean deletePayment(int id) {
        // For simplicity, we don't delete payments in text file mode
        return false;
    }
    
    /**
     * Get payment by ID
     */
    public Payment getPayment(int id) {
        return database.getAllPayments().stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get payments for a loan
     */
    public List<Payment> getPaymentsByLoan(int loanId) {
        return database.getPaymentsByLoanId(loanId);
    }
    
    /**
     * Get payments in date range
     */
    public List<Payment> getPaymentsByDateRange(LocalDate start, LocalDate end) {
        return database.getAllPayments().stream()
            .filter(p -> p.getPaymentDate() != null &&
                        !p.getPaymentDate().isBefore(start) &&
                        !p.getPaymentDate().isAfter(end))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all payments
     */
    public List<Payment> getAllPayments() {
        return database.getAllPayments();
    }
    
    /**
     * Calculate penalty for late payment
     * @param loan The loan
     * @param paymentDate The date of payment
     * @param periodIndex The period being paid
     * @return The penalty amount
     */
    public BigDecimal calculatePenalty(Loan loan, LocalDate paymentDate, int periodIndex) {
        if (loan == null || loan.getPenaltyRate() == null || 
            loan.getPenaltyRate().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Get the amortization row for this period
        List<AmortizationRow> rows = database.getAmortizationByLoanId(loan.getId());
        AmortizationRow row = null;
        for (AmortizationRow r : rows) {
            if (r.getPeriodIndex() == periodIndex) {
                row = r;
                break;
            }
        }
        
        if (row == null || row.getDueDate() == null) {
            return BigDecimal.ZERO;
        }
        
        // Check if payment is late (after due date + grace period)
        LocalDate gracePeriodEnd = row.getDueDate().plusDays(loan.getGracePeriodDays());
        if (!paymentDate.isAfter(gracePeriodEnd)) {
            return BigDecimal.ZERO;
        }
        
        // Calculate days late
        long daysLate = ChronoUnit.DAYS.between(gracePeriodEnd, paymentDate);
        
        BigDecimal penaltyRate = loan.getPenaltyRate();
        BigDecimal scheduledPayment = row.getScheduledPayment();
        BigDecimal penalty = BigDecimal.ZERO;
        
        String penaltyType = loan.getPenaltyType();
        if (penaltyType == null) penaltyType = "percent_per_month";
        
        switch (penaltyType.toLowerCase()) {
            case "percent_per_day":
                penalty = scheduledPayment.multiply(penaltyRate)
                                         .multiply(BigDecimal.valueOf(daysLate))
                                         .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
                
            case "percent_per_month":
                double monthsLate = daysLate / 30.0;
                penalty = scheduledPayment.multiply(penaltyRate)
                                         .multiply(BigDecimal.valueOf(monthsLate))
                                         .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
                
            case "flat":
                penalty = penaltyRate;
                break;
                
            default:
                monthsLate = daysLate / 30.0;
                penalty = scheduledPayment.multiply(penaltyRate)
                                         .multiply(BigDecimal.valueOf(monthsLate))
                                         .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        return penalty.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Apply payment to loan (update balances)
     */
    public boolean applyPaymentToLoan(int loanId, Payment payment) {
        // This is called after payment is recorded
        // Additional logic to update loan status if needed
        return checkAndCloseLoanIfPaid(loanId);
    }
    
    /**
     * Get payment summary for a loan
     */
    public Map<String, BigDecimal> getPaymentSummary(int loanId) {
        Map<String, BigDecimal> summary = new HashMap<>();
        
        summary.put("totalPaid", database.getTotalPaidForLoan(loanId));
        summary.put("totalPenalties", BigDecimal.ZERO); // Simplified
        
        Loan loan = database.getLoanById(loanId);
        if (loan != null && loan.getTotalAmount() != null) {
            BigDecimal remaining = loan.getTotalAmount().subtract(summary.get("totalPaid"));
            summary.put("remainingBalance", remaining.max(BigDecimal.ZERO));
        } else {
            summary.put("remainingBalance", BigDecimal.ZERO);
        }
        
        return summary;
    }
    
    /**
     * Get overdue loans
     */
    public List<Loan> getOverdueLoans() {
        List<Loan> activeLoans = database.getLoansByStatus(Loan.STATUS_ACTIVE);
        LocalDate today = LocalDate.now();
        return activeLoans.stream()
            .filter(loan -> {
                List<AmortizationRow> rows = database.getAmortizationByLoanId(loan.getId());
                return rows.stream().anyMatch(row -> 
                    !row.isPaid() && row.getDueDate() != null && row.getDueDate().isBefore(today)
                );
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get total payments in date range
     */
    public BigDecimal getTotalPaymentsInRange(LocalDate start, LocalDate end) {
        return getPaymentsByDateRange(start, end).stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Get last payment for a loan
     */
    public Payment getLastPayment(int loanId) {
        List<Payment> payments = database.getPaymentsByLoanId(loanId);
        return payments.isEmpty() ? null : payments.get(payments.size() - 1);
    }
    
    /**
     * Calculate penalty for a loan (for next unpaid period)
     */
    public BigDecimal calculatePenalty(int loanId) {
        Loan loan = database.getLoanWithDetails(loanId);
        if (loan == null) {
            return BigDecimal.ZERO;
        }
        
        AmortizationRow nextUnpaid = database.getNextUnpaidRow(loanId);
        if (nextUnpaid == null) {
            return BigDecimal.ZERO;
        }
        
        return calculatePenalty(loan, LocalDate.now(), nextUnpaid.getPeriodIndex());
    }
    
    /**
     * Record a payment with simplified parameters
     */
    public boolean recordPayment(int loanId, BigDecimal amount, LocalDate paymentDate, 
                                  String type, String note, String recordedBy) {
        Payment payment = new Payment();
        payment.setLoanId(loanId);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setType(type != null ? type : Payment.TYPE_REGULAR);
        payment.setNote(note);
        payment.setRecordedBy(recordedBy);
        payment.setPenaltyApplied(BigDecimal.ZERO);
        
        // Get next unpaid period
        AmortizationRow nextUnpaid = database.getNextUnpaidRow(loanId);
        if (nextUnpaid != null) {
            payment.setAppliedToPeriod(nextUnpaid.getPeriodIndex());
            
            // Calculate and apply penalty if any
            Loan loan = database.getLoanById(loanId);
            if (loan != null) {
                BigDecimal penalty = calculatePenalty(loan, paymentDate, nextUnpaid.getPeriodIndex());
                payment.setPenaltyApplied(penalty);
            }
            
            // Split payment into principal and interest based on schedule
            payment.setPrincipalApplied(nextUnpaid.getPrincipalPaid());
            payment.setInterestApplied(nextUnpaid.getInterestPaid());
        }
        
        return recordPayment(payment) > 0;
    }
    
    /**
     * Get payments in date range
     */
    public List<Payment> getPaymentsInDateRange(LocalDate start, LocalDate end) {
        return getPaymentsByDateRange(start, end);
    }
    
    /**
     * Get total paid for a loan
     */
    public BigDecimal getTotalPaidForLoan(int loanId) {
        return database.getTotalPaidForLoan(loanId);
    }
    
    /**
     * Get payment count for a loan
     */
    public int getPaymentCountForLoan(int loanId) {
        return database.getPaymentsByLoanId(loanId).size();
    }
    
    /**
     * Check if loan is fully paid and close it
     */
    private boolean checkAndCloseLoanIfPaid(int loanId) {
        Loan loan = database.getLoanById(loanId);
        if (loan == null || loan.getTotalAmount() == null) {
            return false;
        }
        
        BigDecimal totalPaid = database.getTotalPaidForLoan(loanId);
        
        if (totalPaid.compareTo(loan.getTotalAmount()) >= 0) {
            // Loan is fully paid, close it
            return database.updateLoanStatus(loanId, Loan.STATUS_CLOSED);
        }
        
        return false;
    }
    
    /**
     * Validate payment data
     */
    private String validatePayment(Payment payment) {
        if (payment == null) {
            return "Payment data is required.";
        }
        
        if (payment.getLoanId() <= 0) {
            return "Valid loan ID is required.";
        }
        
        if (payment.getPaymentDate() == null) {
            return "Payment date is required.";
        }
        
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "Payment amount must be greater than 0.";
        }
        
        // Verify loan exists
        Loan loan = database.getLoanById(payment.getLoanId());
        if (loan == null) {
            return "Loan not found.";
        }
        
        if (!loan.isActive()) {
            return "Loan is not active.";
        }
        
        return null;
    }
}
