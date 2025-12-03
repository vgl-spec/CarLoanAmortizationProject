package com.vismera.controllers;

import com.vismera.models.AmortizationEntry;
import com.vismera.models.LoanCalculation;
import com.vismera.utils.CSVExporter;
import java.util.List;

/**
 * Controller for loan calculations and amortization.
 * @author Vismerá Inc.
 */
public class LoanController {
    
    private static LoanController instance;
    private LoanCalculation currentLoan;

    private LoanController() {
    }

    /**
     * Get singleton instance
     */
    public static LoanController getInstance() {
        if (instance == null) {
            instance = new LoanController();
        }
        return instance;
    }

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
}
