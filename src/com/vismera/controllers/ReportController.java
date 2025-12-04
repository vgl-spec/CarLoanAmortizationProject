package com.vismera.controllers;

import com.vismera.models.AmortizationRow;
import com.vismera.models.Loan;
import com.vismera.models.Payment;
import com.vismera.storage.TextFileDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for generating reports and analytics.
 * Now uses TextFileDatabase for storage.
 * 
 * @author Vismerá Inc.
 */
public class ReportController {
    
    private static final Logger LOGGER = Logger.getLogger(ReportController.class.getName());
    
    private static ReportController instance;
    
    private final TextFileDatabase database;
    
    private ReportController() {
        database = TextFileDatabase.getInstance();
    }
    
    /**
     * Get singleton instance
     */
    public static ReportController getInstance() {
        if (instance == null) {
            instance = new ReportController();
        }
        return instance;
    }
    
    /**
     * Get dashboard summary statistics
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Counts
        summary.put("totalCustomers", database.getAllCustomers().size());
        summary.put("totalCars", database.getAllCars().size());
        summary.put("availableCars", database.getAvailableCars().size());
        summary.put("totalLoans", database.getAllLoans().size());
        summary.put("activeLoans", database.getLoansByStatus(Loan.STATUS_ACTIVE).size());
        summary.put("closedLoans", database.getLoansByStatus(Loan.STATUS_CLOSED).size());
        
        // Financial
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        for (Loan loan : database.getLoansByStatus(Loan.STATUS_ACTIVE)) {
            if (loan.getTotalAmount() != null) {
                BigDecimal paid = database.getTotalPaidForLoan(loan.getId());
                totalOutstanding = totalOutstanding.add(loan.getTotalAmount().subtract(paid));
            }
        }
        summary.put("totalOutstanding", totalOutstanding);
        
        // Today's payments
        LocalDate today = LocalDate.now();
        BigDecimal todayPayments = database.getAllPayments().stream()
            .filter(p -> today.equals(p.getPaymentDate()))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("todayPayments", todayPayments);
        
        // This month's payments
        LocalDate monthStart = today.withDayOfMonth(1);
        BigDecimal monthPayments = database.getAllPayments().stream()
            .filter(p -> p.getPaymentDate() != null && 
                        !p.getPaymentDate().isBefore(monthStart) && 
                        !p.getPaymentDate().isAfter(today))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("monthPayments", monthPayments);
        
        return summary;
    }
    
    /**
     * Get loans report
     */
    public List<Map<String, Object>> getLoansReport(String status) {
        List<Map<String, Object>> report = new ArrayList<>();
        
        List<Loan> loans;
        if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
            loans = database.getLoansByStatus(status);
        } else {
            loans = database.getAllLoans();
        }
        
        // Add details to each loan
        for (Loan loan : loans) {
            loan.setCustomer(database.getCustomerById(loan.getCustomerId()));
            loan.setCar(database.getCarById(loan.getCarId()));
        }
        
        for (Loan loan : loans) {
            Map<String, Object> loanData = new HashMap<>();
            loanData.put("id", loan.getId());
            loanData.put("customer", loan.getCustomer() != null ? loan.getCustomer().getFullName() : "N/A");
            loanData.put("car", loan.getCar() != null ? loan.getCar().getDisplayName() : "N/A");
            loanData.put("principal", loan.getPrincipal());
            loanData.put("apr", loan.getApr());
            loanData.put("termMonths", loan.getTermMonths());
            loanData.put("monthlyPayment", loan.getMonthlyPayment());
            loanData.put("startDate", loan.getStartDate());
            loanData.put("status", loan.getStatus());
            
            // Payment info
            BigDecimal totalPaid = database.getTotalPaidForLoan(loan.getId());
            loanData.put("totalPaid", totalPaid);
            
            BigDecimal remaining = loan.getTotalAmount() != null ? 
                loan.getTotalAmount().subtract(totalPaid) : BigDecimal.ZERO;
            loanData.put("remainingBalance", remaining.max(BigDecimal.ZERO));
            
            List<AmortizationRow> amortRows = database.getAmortizationByLoanId(loan.getId());
            int paidPeriods = (int) amortRows.stream().filter(AmortizationRow::isPaid).count();
            int totalPeriods = amortRows.size();
            loanData.put("paidPeriods", paidPeriods);
            loanData.put("totalPeriods", totalPeriods);
            loanData.put("progressPercent", totalPeriods > 0 ? (paidPeriods * 100.0 / totalPeriods) : 0);
            
            report.add(loanData);
        }
        
        return report;
    }
    
    /**
     * Get payments report for date range
     */
    public List<Map<String, Object>> getPaymentsReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> report = new ArrayList<>();
        
        List<Payment> payments = database.getAllPayments().stream()
            .filter(p -> p.getPaymentDate() != null &&
                        !p.getPaymentDate().isBefore(startDate) &&
                        !p.getPaymentDate().isAfter(endDate))
            .collect(Collectors.toList());
        
        for (Payment payment : payments) {
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("id", payment.getId());
            paymentData.put("loanId", payment.getLoanId());
            paymentData.put("paymentDate", payment.getPaymentDate());
            paymentData.put("amount", payment.getAmount());
            paymentData.put("type", payment.getTypeDisplayName());
            paymentData.put("penaltyApplied", payment.getPenaltyApplied());
            paymentData.put("note", payment.getNote());
            
            // Get loan and customer info
            Loan loan = database.getLoanWithDetails(payment.getLoanId());
            if (loan != null) {
                paymentData.put("customerName", loan.getCustomer() != null ? 
                    loan.getCustomer().getFullName() : "N/A");
                paymentData.put("carName", loan.getCar() != null ? 
                    loan.getCar().getDisplayName() : "N/A");
            }
            
            report.add(paymentData);
        }
        
        return report;
    }
    
    /**
     * Get overdue loans report
     */
    public List<Map<String, Object>> getOverdueLoansReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        
        List<Loan> activeLoans = database.getLoansByStatus(Loan.STATUS_ACTIVE);
        LocalDate today = LocalDate.now();
        
        for (Loan loan : activeLoans) {
            loan.setCustomer(database.getCustomerById(loan.getCustomerId()));
            loan.setCar(database.getCarById(loan.getCarId()));
            
            List<AmortizationRow> overdueRows = database.getAmortizationByLoanId(loan.getId()).stream()
                .filter(row -> !row.isPaid() && row.getDueDate() != null && row.getDueDate().isBefore(today))
                .collect(Collectors.toList());
            
            if (!overdueRows.isEmpty()) {
                Map<String, Object> overdueData = new HashMap<>();
                overdueData.put("loanId", loan.getId());
                overdueData.put("customer", loan.getCustomer() != null ? 
                    loan.getCustomer().getFullName() : "N/A");
                overdueData.put("customerPhone", loan.getCustomer() != null ? 
                    loan.getCustomer().getContactNumber() : "N/A");
                overdueData.put("car", loan.getCar() != null ? 
                    loan.getCar().getDisplayName() : "N/A");
                overdueData.put("overdueCount", overdueRows.size());
                
                // Total overdue amount
                BigDecimal overdueAmount = overdueRows.stream()
                    .map(row -> row.getScheduledPayment())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                overdueData.put("overdueAmount", overdueAmount);
                
                // Oldest overdue date
                overdueData.put("oldestDueDate", overdueRows.get(0).getDueDate());
                
                report.add(overdueData);
            }
        }
        
        return report;
    }
    
    /**
     * Get monthly summary report
     */
    public Map<String, Object> getMonthlySummary(int year, int month) {
        Map<String, Object> summary = new HashMap<>();
        
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        
        // Total payments received
        List<Payment> payments = database.getAllPayments().stream()
            .filter(p -> p.getPaymentDate() != null &&
                        !p.getPaymentDate().isBefore(monthStart) &&
                        !p.getPaymentDate().isAfter(monthEnd))
            .collect(Collectors.toList());
        
        BigDecimal totalPayments = payments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalPayments", totalPayments);
        
        // Number of payments
        summary.put("paymentCount", payments.size());
        
        // Penalties collected
        BigDecimal totalPenalties = payments.stream()
            .map(p -> p.getPenaltyApplied() != null ? p.getPenaltyApplied() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalPenalties", totalPenalties);
        
        summary.put("monthName", monthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        
        return summary;
    }
    
    /**
     * Get customer loans summary
     */
    public Map<String, Object> getCustomerLoansSummary(int customerId) {
        Map<String, Object> summary = new HashMap<>();
        
        List<Loan> customerLoans = database.getAllLoans().stream()
            .filter(l -> l.getCustomerId() == customerId)
            .collect(Collectors.toList());
        
        summary.put("totalLoans", customerLoans.size());
        
        long activeCount = customerLoans.stream()
            .filter(loan -> Loan.STATUS_ACTIVE.equals(loan.getStatus()))
            .count();
        summary.put("activeLoans", activeCount);
        
        long closedCount = customerLoans.stream()
            .filter(loan -> Loan.STATUS_CLOSED.equals(loan.getStatus()))
            .count();
        summary.put("closedLoans", closedCount);
        
        BigDecimal totalPrincipal = customerLoans.stream()
            .map(Loan::getPrincipal)
            .filter(p -> p != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalPrincipal", totalPrincipal);
        
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        for (Loan loan : customerLoans) {
            if (Loan.STATUS_ACTIVE.equals(loan.getStatus()) && loan.getTotalAmount() != null) {
                BigDecimal paid = database.getTotalPaidForLoan(loan.getId());
                totalOutstanding = totalOutstanding.add(loan.getTotalAmount().subtract(paid));
            }
        }
        summary.put("totalOutstanding", totalOutstanding.max(BigDecimal.ZERO));
        
        return summary;
    }
    
    /**
     * Export report to CSV format (returns data structure for CSV exporter)
     */
    public List<String[]> exportLoansReportToCSV(String status) {
        List<String[]> csvData = new ArrayList<>();
        
        // Header row
        csvData.add(new String[] {
            "Loan ID", "Customer", "Car", "Principal", "APR%", "Term (Months)", 
            "Monthly Payment", "Start Date", "Status", "Total Paid", "Remaining Balance", "Progress %"
        });
        
        // Data rows
        List<Map<String, Object>> report = getLoansReport(status);
        for (Map<String, Object> row : report) {
            csvData.add(new String[] {
                String.valueOf(row.get("id")),
                String.valueOf(row.get("customer")),
                String.valueOf(row.get("car")),
                String.valueOf(row.get("principal")),
                String.valueOf(row.get("apr")),
                String.valueOf(row.get("termMonths")),
                String.valueOf(row.get("monthlyPayment")),
                row.get("startDate") != null ? row.get("startDate").toString() : "",
                String.valueOf(row.get("status")),
                String.valueOf(row.get("totalPaid")),
                String.valueOf(row.get("remainingBalance")),
                String.format("%.1f", row.get("progressPercent"))
            });
        }
        
        return csvData;
    }
}
