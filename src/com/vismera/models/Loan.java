package com.vismera.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a loan agreement.
 * Maps to the 'loans' table in the database.
 * 
 * @author Vismerá Inc.
 */
public class Loan {
    
    private int id;
    private int customerId;
    private int carId;
    private BigDecimal principal;
    private BigDecimal apr; // Annual Percentage Rate as decimal (e.g., 6.5 for 6.5%)
    private String compounding; // monthly, quarterly, semi-annually, annually
    private int termMonths;
    private String paymentFrequency; // monthly
    private LocalDate startDate;
    private BigDecimal penaltyRate;
    private String penaltyType; // percent_per_day, percent_per_month, flat
    private int gracePeriodDays;
    private BigDecimal downPayment;
    private BigDecimal tradeInValue;
    private BigDecimal salesTaxRate;
    private BigDecimal registrationFee;
    private BigDecimal monthlyPayment;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    private String status; // active, closed, defaulted, archived
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Navigation properties (lazy loaded)
    private Customer customer;
    private Car car;
    
    // Status constants
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_CLOSED = "closed";
    public static final String STATUS_PAID_OFF = "paid_off";
    public static final String STATUS_DEFAULTED = "defaulted";
    public static final String STATUS_ARCHIVED = "archived";
    
    // Compounding frequency constants
    public static final String COMPOUNDING_MONTHLY = "monthly";
    public static final String COMPOUNDING_QUARTERLY = "quarterly";
    public static final String COMPOUNDING_SEMIANNUALLY = "semi-annually";
    public static final String COMPOUNDING_ANNUALLY = "annually";
    
    /**
     * Default constructor
     */
    public Loan() {
        this.compounding = COMPOUNDING_MONTHLY;
        this.paymentFrequency = "monthly";
        this.status = STATUS_ACTIVE;
        this.penaltyRate = BigDecimal.ZERO;
        this.penaltyType = "percent_per_month";
        this.gracePeriodDays = 5;
        this.downPayment = BigDecimal.ZERO;
        this.tradeInValue = BigDecimal.ZERO;
        this.salesTaxRate = BigDecimal.ZERO;
        this.registrationFee = BigDecimal.ZERO;
    }
    
    /**
     * Constructor for creating a new loan
     */
    public Loan(int customerId, int carId, BigDecimal principal, BigDecimal apr,
                int termMonths, LocalDate startDate) {
        this();
        this.customerId = customerId;
        this.carId = carId;
        this.principal = principal;
        this.apr = apr;
        this.termMonths = termMonths;
        this.startDate = startDate;
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public int getCarId() {
        return carId;
    }
    
    public void setCarId(int carId) {
        this.carId = carId;
    }
    
    public BigDecimal getPrincipal() {
        return principal;
    }
    
    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }
    
    public BigDecimal getApr() {
        return apr;
    }
    
    public void setApr(BigDecimal apr) {
        this.apr = apr;
    }
    
    public String getCompounding() {
        return compounding;
    }
    
    public void setCompounding(String compounding) {
        this.compounding = compounding;
    }
    
    public int getTermMonths() {
        return termMonths;
    }
    
    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }
    
    public String getPaymentFrequency() {
        return paymentFrequency;
    }
    
    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public BigDecimal getPenaltyRate() {
        return penaltyRate;
    }
    
    public void setPenaltyRate(BigDecimal penaltyRate) {
        this.penaltyRate = penaltyRate;
    }
    
    public String getPenaltyType() {
        return penaltyType;
    }
    
    public void setPenaltyType(String penaltyType) {
        this.penaltyType = penaltyType;
    }
    
    public int getGracePeriodDays() {
        return gracePeriodDays;
    }
    
    public void setGracePeriodDays(int gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }
    
    public BigDecimal getDownPayment() {
        return downPayment;
    }
    
    public void setDownPayment(BigDecimal downPayment) {
        this.downPayment = downPayment;
    }
    
    public BigDecimal getTradeInValue() {
        return tradeInValue;
    }
    
    public void setTradeInValue(BigDecimal tradeInValue) {
        this.tradeInValue = tradeInValue;
    }
    
    public BigDecimal getSalesTaxRate() {
        return salesTaxRate;
    }
    
    public void setSalesTaxRate(BigDecimal salesTaxRate) {
        this.salesTaxRate = salesTaxRate;
    }
    
    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }
    
    public void setRegistrationFee(BigDecimal registrationFee) {
        this.registrationFee = registrationFee;
    }
    
    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }
    
    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
    
    public BigDecimal getTotalInterest() {
        return totalInterest;
    }
    
    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }
    
    public Car getCar() {
        return car;
    }
    
    public void setCar(Car car) {
        this.car = car;
        if (car != null) {
            this.carId = car.getId();
        }
    }
    
    // ==================== CALCULATION METHODS ====================
    
    /**
     * Get compounding periods per year
     */
    public int getCompoundingPeriodsPerYear() {
        if (compounding == null) return 12;
        switch (compounding.toLowerCase()) {
            case "annually": return 1;
            case "semi-annually": return 2;
            case "quarterly": return 4;
            case "monthly":
            default: return 12;
        }
    }
    
    /**
     * Calculate the monthly payment using compound interest formula
     * M = P * [r(1+r)^n] / [(1+r)^n - 1]
     */
    public BigDecimal calculateMonthlyPayment() {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (termMonths <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Convert APR to decimal
        double annualRate = apr.doubleValue() / 100.0;
        int compoundingPeriods = getCompoundingPeriodsPerYear();
        
        // Effective annual rate considering compounding
        double effectiveAnnualRate = Math.pow(1 + (annualRate / compoundingPeriods), compoundingPeriods) - 1;
        
        // Convert to monthly rate
        double monthlyRate = Math.pow(1 + effectiveAnnualRate, 1.0 / 12.0) - 1;
        
        if (monthlyRate == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }
        
        double principalValue = principal.doubleValue();
        double numerator = monthlyRate * Math.pow(1 + monthlyRate, termMonths);
        double denominator = Math.pow(1 + monthlyRate, termMonths) - 1;
        double payment = principalValue * (numerator / denominator);
        
        this.monthlyPayment = BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
        return this.monthlyPayment;
    }
    
    /**
     * Calculate total amount to be paid
     */
    public BigDecimal calculateTotalAmount() {
        if (monthlyPayment == null) {
            calculateMonthlyPayment();
        }
        this.totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(termMonths))
                                         .setScale(2, RoundingMode.HALF_UP);
        return this.totalAmount;
    }
    
    /**
     * Calculate total interest
     */
    public BigDecimal calculateTotalInterest() {
        if (totalAmount == null) {
            calculateTotalAmount();
        }
        this.totalInterest = totalAmount.subtract(principal).setScale(2, RoundingMode.HALF_UP);
        return this.totalInterest;
    }
    
    /**
     * Get term in years
     */
    public int getTermYears() {
        return termMonths / 12;
    }
    
    /**
     * Set term from years
     */
    public void setTermYears(int years) {
        this.termMonths = years * 12;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Check if loan is active
     */
    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
    
    /**
     * Check if loan is closed
     */
    public boolean isClosed() {
        return STATUS_CLOSED.equals(status);
    }
    
    /**
     * Get loan display info
     */
    public String getLoanDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Loan #").append(id);
        if (customer != null) {
            sb.append(" - ").append(customer.getFullName());
        }
        if (car != null) {
            sb.append(" (").append(car.getDisplayName()).append(")");
        }
        return sb.toString();
    }
    
    /**
     * Get loan end date
     */
    public LocalDate getEndDate() {
        if (startDate == null) return null;
        return startDate.plusMonths(termMonths);
    }
    
    /**
     * Check if loan has required fields
     */
    public boolean isValid() {
        return customerId > 0 &&
               carId > 0 &&
               principal != null && principal.compareTo(BigDecimal.ZERO) > 0 &&
               apr != null && apr.compareTo(BigDecimal.ZERO) >= 0 &&
               termMonths > 0 &&
               startDate != null;
    }
    
    @Override
    public String toString() {
        return "Loan #" + id + " (" + status + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Loan loan = (Loan) obj;
        return id == loan.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
