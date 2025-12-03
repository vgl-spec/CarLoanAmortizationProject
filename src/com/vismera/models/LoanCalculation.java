package com.vismera.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for loan calculation parameters and results.
 * Handles compound interest and penalty calculations.
 * @author Vismerá Inc.
 */
public class LoanCalculation {
    // Input fields
    private double carPrice;
    private double salesTaxRate; // as percentage (e.g., 8.0 for 8%)
    private double registrationFee;
    private double downPayment;
    private double tradeInValue;
    private double annualInterestRate; // as percentage
    private int loanTermYears;
    private String compoundingFrequency; // Monthly, Quarterly, Semi-Annually, Annually
    private double penaltyRate; // as percentage
    private int missedPayments;
    private double extraPaymentPerMonth;

    // Calculated results (cached)
    private double monthlyPayment;
    private double totalInterest;
    private double totalPenalties;
    private double totalAmountPaid;
    private List<AmortizationEntry> amortizationSchedule;

    public LoanCalculation() {
        this.compoundingFrequency = "Monthly";
        this.amortizationSchedule = new ArrayList<>();
    }

    // ==================== CALCULATION METHODS ====================

    /**
     * Calculate the total vehicle cost including taxes and fees
     */
    public double calculateTotalCost() {
        return carPrice + calculateTaxAmount() + registrationFee;
    }

    /**
     * Calculate the amount to be financed after down payment and trade-in
     */
    public double calculateAmountFinanced() {
        double totalCost = calculateTotalCost();
        double reductions = downPayment + tradeInValue;
        return Math.max(0, totalCost - reductions);
    }

    /**
     * Calculate the sales tax amount
     */
    public double calculateTaxAmount() {
        return carPrice * (salesTaxRate / 100.0);
    }

    /**
     * Get the compounding periods per year based on frequency
     */
    public int getCompoundingPeriodsPerYear() {
        switch (compoundingFrequency) {
            case "Annually": return 1;
            case "Semi-Annually": return 2;
            case "Quarterly": return 4;
            case "Monthly": 
            default: return 12;
        }
    }

    /**
     * Calculate the monthly payment using compound interest formula
     * M = P * [r(1+r)^n] / [(1+r)^n - 1]
     * Adjusted for compounding frequency
     */
    public double calculateMonthlyPayment() {
        double principal = calculateAmountFinanced();
        if (principal <= 0) return 0;

        int totalMonths = loanTermYears * 12;
        if (totalMonths <= 0) return 0;

        // Convert annual rate to effective monthly rate based on compounding frequency
        double annualRate = annualInterestRate / 100.0;
        int compoundingPeriods = getCompoundingPeriodsPerYear();
        
        // Effective annual rate considering compounding
        double effectiveAnnualRate = Math.pow(1 + (annualRate / compoundingPeriods), compoundingPeriods) - 1;
        
        // Convert to monthly rate
        double monthlyRate = Math.pow(1 + effectiveAnnualRate, 1.0 / 12.0) - 1;

        if (monthlyRate == 0) {
            return principal / totalMonths;
        }

        // Standard amortization formula
        double numerator = monthlyRate * Math.pow(1 + monthlyRate, totalMonths);
        double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;
        
        this.monthlyPayment = principal * (numerator / denominator);
        return this.monthlyPayment;
    }

    /**
     * Calculate total interest paid over the life of the loan
     */
    public double calculateTotalInterest() {
        if (amortizationSchedule.isEmpty()) {
            generateAmortizationSchedule();
        }
        return this.totalInterest;
    }

    /**
     * Calculate total penalties based on missed payments
     */
    public double calculateTotalPenalties() {
        if (amortizationSchedule.isEmpty()) {
            generateAmortizationSchedule();
        }
        return this.totalPenalties;
    }

    /**
     * Generate the complete amortization schedule with penalties
     */
    public List<AmortizationEntry> generateAmortizationSchedule() {
        amortizationSchedule = new ArrayList<>();
        
        double principal = calculateAmountFinanced();
        if (principal <= 0) return amortizationSchedule;

        int totalMonths = loanTermYears * 12;
        double payment = calculateMonthlyPayment();
        
        // Adjust payment for extra payments
        double adjustedPayment = payment + extraPaymentPerMonth;
        
        // Calculate monthly interest rate (using effective rate from compounding)
        double annualRate = annualInterestRate / 100.0;
        int compoundingPeriods = getCompoundingPeriodsPerYear();
        double effectiveAnnualRate = Math.pow(1 + (annualRate / compoundingPeriods), compoundingPeriods) - 1;
        double monthlyRate = Math.pow(1 + effectiveAnnualRate, 1.0 / 12.0) - 1;
        
        double balance = principal;
        double cumulativePaid = 0;
        double cumulativeInterest = 0;
        double cumulativePenalties = 0;
        
        int paymentNum = 1;
        
        while (balance > 0.01 && paymentNum <= totalMonths + missedPayments) {
            double interestPayment = balance * monthlyRate;
            double penalty = 0;
            double principalPayment;
            double currentPayment;
            
            // Check if this is a missed payment
            if (missedPayments > 0 && paymentNum <= missedPayments) {
                // Missed payment - only interest accrues, penalty applies
                penalty = balance * (penaltyRate / 100.0);
                principalPayment = 0;
                currentPayment = 0; // No payment made
                balance += interestPayment; // Interest capitalizes
                cumulativePenalties += penalty;
            } else {
                // Normal payment
                currentPayment = Math.min(adjustedPayment, balance + interestPayment);
                principalPayment = currentPayment - interestPayment;
                
                if (principalPayment < 0) {
                    principalPayment = 0;
                }
                
                balance -= principalPayment;
                if (balance < 0) balance = 0;
            }
            
            cumulativeInterest += interestPayment;
            cumulativePaid += currentPayment + penalty;
            
            AmortizationEntry entry = new AmortizationEntry(
                paymentNum,
                currentPayment,
                principalPayment,
                interestPayment,
                penalty,
                balance,
                cumulativePaid
            );
            
            amortizationSchedule.add(entry);
            paymentNum++;
            
            // Safety check to prevent infinite loops
            if (paymentNum > totalMonths * 2) break;
        }
        
        this.totalInterest = cumulativeInterest;
        this.totalPenalties = cumulativePenalties;
        this.totalAmountPaid = cumulativePaid;
        
        return amortizationSchedule;
    }

    /**
     * Get total amount paid including principal, interest, and penalties
     */
    public double getTotalAmountPaid() {
        if (amortizationSchedule.isEmpty()) {
            generateAmortizationSchedule();
        }
        return totalAmountPaid;
    }

    // ==================== GETTERS AND SETTERS ====================

    public double getCarPrice() { return carPrice; }
    public void setCarPrice(double carPrice) { this.carPrice = carPrice; }

    public double getSalesTaxRate() { return salesTaxRate; }
    public void setSalesTaxRate(double salesTaxRate) { this.salesTaxRate = salesTaxRate; }

    public double getRegistrationFee() { return registrationFee; }
    public void setRegistrationFee(double registrationFee) { this.registrationFee = registrationFee; }

    public double getDownPayment() { return downPayment; }
    public void setDownPayment(double downPayment) { this.downPayment = downPayment; }

    public double getTradeInValue() { return tradeInValue; }
    public void setTradeInValue(double tradeInValue) { this.tradeInValue = tradeInValue; }

    public double getAnnualInterestRate() { return annualInterestRate; }
    public void setAnnualInterestRate(double annualInterestRate) { this.annualInterestRate = annualInterestRate; }

    public int getLoanTermYears() { return loanTermYears; }
    public void setLoanTermYears(int loanTermYears) { this.loanTermYears = loanTermYears; }

    public String getCompoundingFrequency() { return compoundingFrequency; }
    public void setCompoundingFrequency(String compoundingFrequency) { this.compoundingFrequency = compoundingFrequency; }

    public double getPenaltyRate() { return penaltyRate; }
    public void setPenaltyRate(double penaltyRate) { this.penaltyRate = penaltyRate; }

    public int getMissedPayments() { return missedPayments; }
    public void setMissedPayments(int missedPayments) { this.missedPayments = missedPayments; }

    public double getExtraPaymentPerMonth() { return extraPaymentPerMonth; }
    public void setExtraPaymentPerMonth(double extraPaymentPerMonth) { this.extraPaymentPerMonth = extraPaymentPerMonth; }

    public double getMonthlyPayment() { return monthlyPayment; }
    public double getTotalInterest() { return totalInterest; }
    public double getTotalPenalties() { return totalPenalties; }
    public List<AmortizationEntry> getAmortizationSchedule() { return amortizationSchedule; }
}
