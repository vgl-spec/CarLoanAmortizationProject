package com.vismera.models;

/**
 * Model class for loan comparison scenarios.
 * @author Vismerá Inc.
 */
public class LoanScenario {
    private String scenarioName;
    private double loanAmount;
    private double interestRate;
    private int termYears;
    private double monthlyPayment;
    private double totalInterest;
    private double totalCost;
    private boolean isBestDeal;

    public LoanScenario() {
    }

    public LoanScenario(String scenarioName, double loanAmount, double interestRate, int termYears) {
        this.scenarioName = scenarioName;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.termYears = termYears;
        calculateMetrics();
    }

    /**
     * Calculate monthly payment, total interest, and total cost
     */
    public void calculateMetrics() {
        if (loanAmount <= 0 || termYears <= 0) {
            monthlyPayment = 0;
            totalInterest = 0;
            totalCost = 0;
            return;
        }

        int totalMonths = termYears * 12;
        double monthlyRate = (interestRate / 100.0) / 12.0;

        if (monthlyRate == 0) {
            monthlyPayment = loanAmount / totalMonths;
            totalInterest = 0;
        } else {
            double numerator = monthlyRate * Math.pow(1 + monthlyRate, totalMonths);
            double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;
            monthlyPayment = loanAmount * (numerator / denominator);
            totalInterest = (monthlyPayment * totalMonths) - loanAmount;
        }

        totalCost = loanAmount + totalInterest;
    }

    // Getters and Setters
    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { 
        this.loanAmount = loanAmount; 
        calculateMetrics();
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { 
        this.interestRate = interestRate; 
        calculateMetrics();
    }

    public int getTermYears() { return termYears; }
    public void setTermYears(int termYears) { 
        this.termYears = termYears; 
        calculateMetrics();
    }

    public double getMonthlyPayment() { return monthlyPayment; }
    public double getTotalInterest() { return totalInterest; }
    public double getTotalCost() { return totalCost; }

    public boolean isBestDeal() { return isBestDeal; }
    public void setBestDeal(boolean bestDeal) { this.isBestDeal = bestDeal; }
}
