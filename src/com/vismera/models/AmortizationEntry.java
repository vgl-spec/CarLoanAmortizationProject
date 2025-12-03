package com.vismera.models;

/**
 * Model class representing a single entry in the amortization schedule.
 * @author Vismerá Inc.
 */
public class AmortizationEntry {
    private int paymentNumber;
    private double payment;
    private double principal;
    private double interest;
    private double penalty;
    private double balance;
    private double totalPaid;

    public AmortizationEntry() {
    }

    public AmortizationEntry(int paymentNumber, double payment, double principal, 
                            double interest, double penalty, double balance, double totalPaid) {
        this.paymentNumber = paymentNumber;
        this.payment = payment;
        this.principal = principal;
        this.interest = interest;
        this.penalty = penalty;
        this.balance = balance;
        this.totalPaid = totalPaid;
    }

    // Getters and Setters
    public int getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(int paymentNumber) { this.paymentNumber = paymentNumber; }

    public double getPayment() { return payment; }
    public void setPayment(double payment) { this.payment = payment; }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }

    public double getInterest() { return interest; }
    public void setInterest(double interest) { this.interest = interest; }

    public double getPenalty() { return penalty; }
    public void setPenalty(double penalty) { this.penalty = penalty; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }

    /**
     * Returns the total payment for this period (payment + penalty)
     */
    public double getTotalPaymentWithPenalty() {
        return payment + penalty;
    }
}
