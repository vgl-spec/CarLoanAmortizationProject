package com.vismera.controllers;

import com.vismera.dao.AmortizationRowDAO;
import com.vismera.dao.LoanDAO;
import com.vismera.dao.PaymentDAO;
import com.vismera.models.AmortizationRow;
import com.vismera.models.Loan;
import com.vismera.models.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for payment management and processing.
 * 
 * @author Vismerá Inc.
 */
public class PaymentController {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());
    
    private static PaymentController instance;
    
    private final PaymentDAO paymentDAO;
    private final LoanDAO loanDAO;
    private final AmortizationRowDAO amortizationRowDAO;
    
    private PaymentController() {
        paymentDAO = PaymentDAO.getInstance();
        loanDAO = LoanDAO.getInstance();
        amortizationRowDAO = AmortizationRowDAO.getInstance();
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
        int paymentId = paymentDAO.insert(payment);
        if (paymentId <= 0) {
            LOGGER.severe("Failed to record payment");
            return -1;
        }
        
        // Update amortization row if period specified
        if (payment.getAppliedToPeriod() != null) {
            amortizationRowDAO.markAsPaid(
                payment.getLoanId(), 
                payment.getAppliedToPeriod(), 
                payment.getPaymentDate()
            );
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
        return paymentDAO.update(payment);
    }
    
    /**
     * Delete a payment
     */
    public boolean deletePayment(int id) {
        return paymentDAO.delete(id);
    }
    
    /**
     * Get payment by ID
     */
    public Payment getPayment(int id) {
        return paymentDAO.findById(id);
    }
    
    /**
     * Get payments for a loan
     */
    public List<Payment> getPaymentsByLoan(int loanId) {
        return paymentDAO.findByLoanId(loanId);
    }
    
    /**
     * Get payments in date range
     */
    public List<Payment> getPaymentsByDateRange(LocalDate start, LocalDate end) {
        return paymentDAO.findByDateRange(start, end);
    }
    
    /**
     * Get all payments
     */
    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
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
        AmortizationRow row = amortizationRowDAO.findByLoanAndPeriod(loan.getId(), periodIndex);
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
                // Penalty rate is daily percentage
                penalty = scheduledPayment.multiply(penaltyRate)
                                         .multiply(BigDecimal.valueOf(daysLate))
                                         .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
                
            case "percent_per_month":
                // Penalty rate is monthly percentage, prorate by days
                double monthsLate = daysLate / 30.0;
                penalty = scheduledPayment.multiply(penaltyRate)
                                         .multiply(BigDecimal.valueOf(monthsLate))
                                         .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
                
            case "flat":
                // Flat penalty amount
                penalty = penaltyRate;
                break;
                
            default:
                // Default to monthly percentage
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
        
        summary.put("totalPaid", paymentDAO.getTotalPaidForLoan(loanId));
        summary.put("totalPenalties", paymentDAO.getPenaltiesPaidForLoan(loanId));
        
        Loan loan = loanDAO.findById(loanId);
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
        List<Loan> activeLoans = loanDAO.findByStatus(Loan.STATUS_ACTIVE);
        return activeLoans.stream()
            .filter(loan -> {
                List<AmortizationRow> overdue = amortizationRowDAO.findOverdueByLoanId(loan.getId());
                return !overdue.isEmpty();
            })
            .toList();
    }
    
    /**
     * Get total payments in date range
     */
    public BigDecimal getTotalPaymentsInRange(LocalDate start, LocalDate end) {
        return paymentDAO.getTotalInDateRange(start, end);
    }
    
    /**
     * Get last payment for a loan
     */
    public Payment getLastPayment(int loanId) {
        return paymentDAO.getLastPayment(loanId);
    }
    
    /**
     * Calculate penalty for a loan (for next unpaid period)
     */
    public BigDecimal calculatePenalty(int loanId) {
        Loan loan = loanDAO.findByIdWithDetails(loanId);
        if (loan == null) {
            return BigDecimal.ZERO;
        }
        
        AmortizationRow nextUnpaid = amortizationRowDAO.getNextUnpaidPeriod(loanId);
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
        AmortizationRow nextUnpaid = amortizationRowDAO.getNextUnpaidPeriod(loanId);
        if (nextUnpaid != null) {
            payment.setAppliedToPeriod(nextUnpaid.getPeriodIndex());
            
            // Calculate and apply penalty if any
            Loan loan = loanDAO.findById(loanId);
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
        return paymentDAO.findByDateRange(start, end);
    }
    
    /**
     * Get total paid for a loan
     */
    public BigDecimal getTotalPaidForLoan(int loanId) {
        return paymentDAO.getTotalPaidForLoan(loanId);
    }
    
    /**
     * Get payment count for a loan
     */
    public int getPaymentCountForLoan(int loanId) {
        return paymentDAO.getCountForLoan(loanId);
    }
    
    /**
     * Check if loan is fully paid and close it
     */
    private boolean checkAndCloseLoanIfPaid(int loanId) {
        Loan loan = loanDAO.findById(loanId);
        if (loan == null || loan.getTotalAmount() == null) {
            return false;
        }
        
        BigDecimal totalPaid = paymentDAO.getTotalPaidForLoan(loanId);
        
        if (totalPaid.compareTo(loan.getTotalAmount()) >= 0) {
            // Loan is fully paid, close it
            return loanDAO.updateStatus(loanId, Loan.STATUS_CLOSED);
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
        Loan loan = loanDAO.findById(payment.getLoanId());
        if (loan == null) {
            return "Loan not found.";
        }
        
        if (!loan.isActive()) {
            return "Loan is not active.";
        }
        
        return null;
    }
}
