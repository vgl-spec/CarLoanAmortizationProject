package com.vismera.controllers;

import com.vismera.dao.AmortizationRowDAO;
import com.vismera.dao.CarDAO;
import com.vismera.dao.CustomerDAO;
import com.vismera.dao.LoanDAO;
import com.vismera.dao.PaymentDAO;
import com.vismera.models.Loan;
import com.vismera.models.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller for generating reports and analytics.
 * 
 * @author Vismerá Inc.
 */
public class ReportController {
    
    private static final Logger LOGGER = Logger.getLogger(ReportController.class.getName());
    
    private static ReportController instance;
    
    private final LoanDAO loanDAO;
    private final PaymentDAO paymentDAO;
    private final CustomerDAO customerDAO;
    private final CarDAO carDAO;
    private final AmortizationRowDAO amortizationRowDAO;
    
    private ReportController() {
        loanDAO = LoanDAO.getInstance();
        paymentDAO = PaymentDAO.getInstance();
        customerDAO = CustomerDAO.getInstance();
        carDAO = CarDAO.getInstance();
        amortizationRowDAO = AmortizationRowDAO.getInstance();
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
        summary.put("totalCustomers", customerDAO.getCount());
        summary.put("totalCars", carDAO.getCount());
        summary.put("availableCars", carDAO.getAvailableCount());
        summary.put("totalLoans", loanDAO.getCount());
        summary.put("activeLoans", loanDAO.getCountByStatus(Loan.STATUS_ACTIVE));
        summary.put("closedLoans", loanDAO.getCountByStatus(Loan.STATUS_CLOSED));
        
        // Financial
        summary.put("totalOutstanding", loanDAO.getTotalOutstanding());
        
        // Today's payments
        LocalDate today = LocalDate.now();
        summary.put("todayPayments", paymentDAO.getTotalInDateRange(today, today));
        
        // This month's payments
        LocalDate monthStart = today.withDayOfMonth(1);
        summary.put("monthPayments", paymentDAO.getTotalInDateRange(monthStart, today));
        
        return summary;
    }
    
    /**
     * Get loans report
     */
    public List<Map<String, Object>> getLoansReport(String status) {
        List<Map<String, Object>> report = new ArrayList<>();
        
        List<Loan> loans;
        if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
            loans = loanDAO.findByStatusWithDetails(status);
        } else {
            loans = loanDAO.findAllWithDetails();
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
            BigDecimal totalPaid = paymentDAO.getTotalPaidForLoan(loan.getId());
            loanData.put("totalPaid", totalPaid);
            
            BigDecimal remaining = loan.getTotalAmount() != null ? 
                loan.getTotalAmount().subtract(totalPaid) : BigDecimal.ZERO;
            loanData.put("remainingBalance", remaining.max(BigDecimal.ZERO));
            
            int paidPeriods = amortizationRowDAO.getPaidPeriodsCount(loan.getId());
            int totalPeriods = amortizationRowDAO.getTotalPeriodsCount(loan.getId());
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
        
        List<Payment> payments = paymentDAO.findByDateRange(startDate, endDate);
        
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
            Loan loan = loanDAO.findByIdWithDetails(payment.getLoanId());
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
        
        List<Loan> activeLoans = loanDAO.getActiveLoans();
        
        for (Loan loan : activeLoans) {
            var overdueRows = amortizationRowDAO.findOverdueByLoanId(loan.getId());
            
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
        BigDecimal totalPayments = paymentDAO.getTotalInDateRange(monthStart, monthEnd);
        summary.put("totalPayments", totalPayments);
        
        // Number of payments
        List<Payment> payments = paymentDAO.findByDateRange(monthStart, monthEnd);
        summary.put("paymentCount", payments.size());
        
        // Penalties collected
        BigDecimal totalPenalties = payments.stream()
            .map(p -> p.getPenaltyApplied() != null ? p.getPenaltyApplied() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalPenalties", totalPenalties);
        
        // New loans in month (approximation based on created_at)
        // This would need a query by created_at range
        summary.put("monthName", monthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        
        return summary;
    }
    
    /**
     * Get customer loans summary
     */
    public Map<String, Object> getCustomerLoansSummary(int customerId) {
        Map<String, Object> summary = new HashMap<>();
        
        List<Loan> customerLoans = loanDAO.findByCustomerId(customerId);
        
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
                BigDecimal paid = paymentDAO.getTotalPaidForLoan(loan.getId());
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
