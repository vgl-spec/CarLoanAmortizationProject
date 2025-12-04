package com.vismera.controllers;

import com.vismera.models.AmortizationEntry;
import com.vismera.models.AmortizationRow;
import com.vismera.models.Loan;
import com.vismera.models.LoanCalculation;
import com.vismera.storage.TextFileDatabase;
import com.vismera.utils.CSVExporter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for loan calculations, management, and amortization.
 * Now uses TextFileDatabase for storage.
 * 
 * @author Vismerá Inc.
 */
public class LoanControllerDB {
    
    private static final Logger LOGGER = Logger.getLogger(LoanControllerDB.class.getName());
    
    private static LoanControllerDB instance;
    private LoanCalculation currentLoan;
    
    private final TextFileDatabase database;

    private LoanControllerDB() {
        database = TextFileDatabase.getInstance();
    }

    /**
     * Get singleton instance
     */
    public static LoanControllerDB getInstance() {
        if (instance == null) {
            instance = new LoanControllerDB();
        }
        return instance;
    }
    
    /**
     * Set database mode (for compatibility, always uses database)
     * @param useDatabase ignored, always uses database
     */
    public void setUseDatabase(boolean useDatabase) {
        // LoanControllerDB always uses database
        LOGGER.info("LoanControllerDB always uses text file database mode");
    }
    
    /**
     * Create a new loan (alias for createLoanWithSchedule)
     * @param loan The loan to create
     * @return The loan ID, or -1 if failed
     */
    public int createLoan(Loan loan) {
        return createLoanWithSchedule(loan);
    }
    
    /**
     * Close a loan (mark as paid off)
     * @param loanId The loan ID to close
     * @return true if successful
     */
    public boolean closeLoan(int loanId) {
        Loan loan = database.getLoanById(loanId);
        if (loan == null) {
            LOGGER.warning("Cannot close loan - not found: " + loanId);
            return false;
        }
        
        if (!Loan.STATUS_ACTIVE.equals(loan.getStatus())) {
            LOGGER.warning("Cannot close loan - not active: " + loanId);
            return false;
        }
        
        boolean updated = database.updateLoanStatus(loanId, Loan.STATUS_PAID_OFF);
        if (updated) {
            LOGGER.info("Loan closed (paid off): " + loanId);
        }
        return updated;
    }

    // ==================== LOAN CALCULATION METHODS (Original) ====================

    /**
     * Calculate loan with given parameters
     */
    public LoanCalculation calculateLoan(LoanCalculation loan) {
        this.currentLoan = loan;
        
        // Trigger all calculations
        loan.calculateMonthlyPayment();
        loan.generateAmortizationSchedule();
        
        return loan;
    }

    /**
     * Create a new loan calculation from input parameters
     */
    public LoanCalculation createLoanCalculation(
            double carPrice,
            double salesTaxRate,
            double registrationFee,
            double downPayment,
            double tradeInValue,
            double annualInterestRate,
            int loanTermYears,
            String compoundingFrequency,
            double penaltyRate,
            int missedPayments,
            double extraPayment) {
        
        LoanCalculation loan = new LoanCalculation();
        loan.setCarPrice(carPrice);
        loan.setSalesTaxRate(salesTaxRate);
        loan.setRegistrationFee(registrationFee);
        loan.setDownPayment(downPayment);
        loan.setTradeInValue(tradeInValue);
        loan.setAnnualInterestRate(annualInterestRate);
        loan.setLoanTermYears(loanTermYears);
        loan.setCompoundingFrequency(compoundingFrequency);
        loan.setPenaltyRate(penaltyRate);
        loan.setMissedPayments(missedPayments);
        loan.setExtraPaymentPerMonth(extraPayment);
        
        return calculateLoan(loan);
    }

    /**
     * Generate amortization schedule
     */
    public List<AmortizationEntry> generateAmortizationSchedule(LoanCalculation loan) {
        return loan.generateAmortizationSchedule();
    }

    /**
     * Export amortization schedule to CSV
     */
    public boolean exportToCSV(List<AmortizationEntry> schedule, String filename) {
        return CSVExporter.exportAmortizationSchedule(schedule, filename);
    }

    /**
     * Get the current loan calculation
     */
    public LoanCalculation getCurrentLoan() {
        return currentLoan;
    }

    /**
     * Clear current loan
     */
    public void clearCurrentLoan() {
        this.currentLoan = null;
    }

    /**
     * Calculate quick estimate for comparison (simple interest)
     */
    public double[] quickCalculate(double principal, double annualRate, int years) {
        int totalMonths = years * 12;
        double monthlyRate = (annualRate / 100.0) / 12.0;
        
        double monthlyPayment;
        if (monthlyRate == 0) {
            monthlyPayment = principal / totalMonths;
        } else {
            double numerator = monthlyRate * Math.pow(1 + monthlyRate, totalMonths);
            double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;
            monthlyPayment = principal * (numerator / denominator);
        }
        
        double totalPaid = monthlyPayment * totalMonths;
        double totalInterest = totalPaid - principal;
        
        return new double[] { monthlyPayment, totalInterest, totalPaid };
    }

    // ==================== DATABASE LOAN MANAGEMENT ====================

    /**
     * Create a loan with amortization schedule in database
     * @param loan The loan to create
     * @return The loan ID, or -1 if failed
     */
    public int createLoanWithSchedule(Loan loan) {
        // Calculate payments if not already set
        if (loan.getMonthlyPayment() == null) {
            loan.calculateMonthlyPayment();
        }
        loan.calculateTotalAmount();
        loan.calculateTotalInterest();
        
        // Insert loan
        int loanId = database.insertLoan(loan);
        if (loanId <= 0) {
            LOGGER.severe("Failed to create loan");
            return -1;
        }
        
        loan.setId(loanId);
        
        // Generate and insert amortization schedule
        List<AmortizationRow> schedule = generateAmortizationRows(loan);
        database.insertAmortizationRows(schedule);
        
        LOGGER.info("Created loan #" + loanId + " with " + schedule.size() + " amortization rows");
        return loanId;
    }
    
    /**
     * Generate amortization rows for a loan entity
     */
    public List<AmortizationRow> generateAmortizationRows(Loan loan) {
        List<AmortizationRow> rows = new ArrayList<>();
        
        BigDecimal principal = loan.getPrincipal();
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            return rows;
        }
        
        int termMonths = loan.getTermMonths();
        BigDecimal monthlyPayment = loan.getMonthlyPayment();
        if (monthlyPayment == null) {
            monthlyPayment = loan.calculateMonthlyPayment();
        }
        
        // Calculate monthly interest rate
        double annualRate = loan.getApr().doubleValue() / 100.0;
        int compoundingPeriods = loan.getCompoundingPeriodsPerYear();
        double effectiveAnnualRate = Math.pow(1 + (annualRate / compoundingPeriods), compoundingPeriods) - 1;
        double monthlyRate = Math.pow(1 + effectiveAnnualRate, 1.0 / 12.0) - 1;
        
        BigDecimal balance = principal;
        LocalDate dueDate = loan.getStartDate();
        
        for (int period = 1; period <= termMonths; period++) {
            dueDate = loan.getStartDate().plusMonths(period);
            
            BigDecimal openingBalance = balance;
            
            // Calculate interest for this period
            BigDecimal interestPaid = balance.multiply(BigDecimal.valueOf(monthlyRate))
                                            .setScale(2, RoundingMode.HALF_UP);
            
            // Calculate principal for this period
            BigDecimal principalPaid = monthlyPayment.subtract(interestPaid)
                                                     .setScale(2, RoundingMode.HALF_UP);
            
            // Handle final payment adjustment
            if (period == termMonths) {
                principalPaid = balance;
                monthlyPayment = principalPaid.add(interestPaid);
            } else if (principalPaid.compareTo(balance) > 0) {
                principalPaid = balance;
                monthlyPayment = principalPaid.add(interestPaid);
            }
            
            balance = balance.subtract(principalPaid);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                balance = BigDecimal.ZERO;
            }
            
            AmortizationRow row = new AmortizationRow(
                loan.getId(),
                period,
                dueDate,
                openingBalance,
                monthlyPayment,
                principalPaid,
                interestPaid,
                balance
            );
            rows.add(row);
        }
        
        return rows;
    }
    
    /**
     * Update a loan
     */
    public boolean updateLoan(Loan loan) {
        return database.updateLoan(loan);
    }
    
    /**
     * Delete a loan
     */
    public boolean deleteLoan(int id) {
        return database.deleteLoan(id);
    }
    
    /**
     * Get loan by ID with customer and car details
     */
    public Loan getLoanWithDetails(int id) {
        return database.getLoanWithDetails(id);
    }
    
    /**
     * Get loan by ID
     */
    public Loan getLoanById(int id) {
        return database.getLoanById(id);
    }
    
    /**
     * Get all loans
     */
    public List<Loan> getAllLoans() {
        return database.getAllLoans();
    }
    
    /**
     * Get all loans with details
     */
    public List<Loan> getAllLoansWithDetails() {
        return database.getAllLoansWithDetails();
    }
    
    /**
     * Get loans by customer
     */
    public List<Loan> getLoansByCustomer(int customerId) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : database.getAllLoans()) {
            if (loan.getCustomerId() == customerId) {
                result.add(loan);
            }
        }
        return result;
    }
    
    /**
     * Get loans by status
     */
    public List<Loan> getLoansByStatus(String status) {
        return database.getLoansByStatus(status);
    }
    
    /**
     * Get loans by status with customer and car details
     */
    public List<Loan> getLoansByStatusWithDetails(String status) {
        List<Loan> loans = database.getLoansByStatus(status);
        for (Loan loan : loans) {
            loan.setCustomer(database.getCustomerById(loan.getCustomerId()));
            loan.setCar(database.getCarById(loan.getCarId()));
        }
        return loans;
    }
    
    /**
     * Get active loans with details
     */
    public List<Loan> getActiveLoans() {
        return getLoansByStatusWithDetails(Loan.STATUS_ACTIVE);
    }
    
    /**
     * Change loan status
     */
    public boolean changeLoanStatus(int loanId, String newStatus) {
        return database.updateLoanStatus(loanId, newStatus);
    }
    
    /**
     * Recalculate amortization for a loan
     */
    public boolean recalculateAmortization(int loanId) {
        Loan loan = database.getLoanById(loanId);
        if (loan == null) {
            return false;
        }
        
        // Delete existing amortization rows
        database.deleteAmortizationByLoanId(loanId);
        
        // Regenerate
        loan.calculateMonthlyPayment();
        List<AmortizationRow> schedule = generateAmortizationRows(loan);
        database.insertAmortizationRows(schedule);
        return true;
    }
    
    /**
     * Get amortization schedule for a loan
     */
    public List<AmortizationRow> getAmortizationSchedule(int loanId) {
        return database.getAmortizationByLoanId(loanId);
    }
    
    /**
     * Get remaining balance for a loan
     */
    public BigDecimal getRemainingBalance(int loanId) {
        BigDecimal totalPaid = database.getTotalPaidForLoan(loanId);
        Loan loan = database.getLoanById(loanId);
        if (loan == null || loan.getTotalAmount() == null) {
            return BigDecimal.ZERO;
        }
        return loan.getTotalAmount().subtract(totalPaid);
    }
    
    /**
     * Get loan summary statistics
     */
    public Map<String, Object> getLoanSummary(int loanId) {
        Map<String, Object> summary = new HashMap<>();
        
        Loan loan = database.getLoanWithDetails(loanId);
        if (loan == null) {
            return summary;
        }
        
        BigDecimal totalPaid = database.getTotalPaidForLoan(loanId);
        List<AmortizationRow> amortRows = database.getAmortizationByLoanId(loanId);
        int paidPeriods = 0;
        int totalPeriods = amortRows.size();
        for (AmortizationRow row : amortRows) {
            if (row.isPaid()) paidPeriods++;
        }
        
        summary.put("loan", loan);
        summary.put("totalPaid", totalPaid);
        summary.put("penaltiesPaid", BigDecimal.ZERO); // Simplified
        summary.put("remainingBalance", getRemainingBalance(loanId));
        summary.put("paymentCount", database.getPaymentsByLoanId(loanId).size());
        summary.put("paidPeriods", paidPeriods);
        summary.put("totalPeriods", totalPeriods);
        summary.put("progressPercent", totalPeriods > 0 ? (paidPeriods * 100.0 / totalPeriods) : 0);
        
        return summary;
    }
    
    /**
     * Get total outstanding balance for all active loans
     */
    public BigDecimal getTotalOutstanding() {
        BigDecimal total = BigDecimal.ZERO;
        for (Loan loan : database.getLoansByStatus(Loan.STATUS_ACTIVE)) {
            if (loan.getTotalAmount() != null) {
                BigDecimal paid = database.getTotalPaidForLoan(loan.getId());
                total = total.add(loan.getTotalAmount().subtract(paid));
            }
        }
        return total;
    }
    
    /**
     * Get active loan count
     */
    public int getActiveLoanCount() {
        return database.getLoansByStatus(Loan.STATUS_ACTIVE).size();
    }
    
    /**
     * Get total loan count
     */
    public int getTotalLoanCount() {
        return database.getAllLoans().size();
    }
}
