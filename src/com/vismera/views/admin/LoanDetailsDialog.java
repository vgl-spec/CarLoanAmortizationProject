package com.vismera.views.admin;

import com.vismera.controllers.LoanControllerDB;
import com.vismera.controllers.PaymentController;
import com.vismera.models.Loan;
import com.vismera.models.Payment;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

/**
 * Dialog for displaying loan details.
 * 
 * @author Vismerá Inc.
 */
public class LoanDetailsDialog extends JDialog {
    
    private final Loan loan;
    private final NumberFormat currencyFormat;
    
    public LoanDetailsDialog(Frame owner, Loan loan) {
        super(owner, "Loan Details - #" + loan.getId(), true);
        this.loan = loan;
        this.currencyFormat = NumberFormat.getCurrencyInstance();
        initComponents();
    }
    
    private void initComponents() {
        setSize(600, 700);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Loan #" + loan.getId());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel statusLabel = new JLabel(loan.getStatus().toUpperCase());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        if (Loan.STATUS_ACTIVE.equals(loan.getStatus())) {
            statusLabel.setBackground(new Color(46, 204, 113));
            statusLabel.setForeground(Color.WHITE);
        } else if (Loan.STATUS_CLOSED.equals(loan.getStatus())) {
            statusLabel.setBackground(new Color(149, 165, 166));
            statusLabel.setForeground(Color.WHITE);
        } else {
            statusLabel.setBackground(new Color(231, 76, 60));
            statusLabel.setForeground(Color.WHITE);
        }
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        // Customer info
        detailsPanel.add(createSectionPanel("Customer Information", new String[][]{
            {"Name:", loan.getCustomer() != null ? loan.getCustomer().getFullName() : "N/A"},
            {"Phone:", loan.getCustomer() != null ? loan.getCustomer().getContactNumber() : "N/A"},
            {"Email:", loan.getCustomer() != null ? loan.getCustomer().getEmail() : "N/A"}
        }));
        
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Car info
        detailsPanel.add(createSectionPanel("Vehicle Information", new String[][]{
            {"Vehicle:", loan.getCar() != null ? loan.getCar().getDisplayName() : "N/A"},
            {"Price:", loan.getCar() != null ? currencyFormat.format(loan.getCar().getPriceBigDecimal()) : "N/A"}
        }));
        
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Loan terms
        detailsPanel.add(createSectionPanel("Loan Terms", new String[][]{
            {"Principal:", currencyFormat.format(loan.getPrincipal())},
            {"APR:", loan.getApr() + "%"},
            {"Term:", loan.getTermMonths() + " months"},
            {"Compounding:", loan.getCompounding()},
            {"Start Date:", loan.getStartDate() != null ? loan.getStartDate().toString() : "N/A"},
            {"Down Payment:", currencyFormat.format(loan.getDownPayment())},
            {"Trade-In Value:", currencyFormat.format(loan.getTradeInValue())},
            {"Sales Tax Rate:", loan.getSalesTaxRate() + "%"},
            {"Registration Fee:", currencyFormat.format(loan.getRegistrationFee())}
        }));
        
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Payment info
        detailsPanel.add(createSectionPanel("Payment Information", new String[][]{
            {"Monthly Payment:", currencyFormat.format(loan.getMonthlyPayment())},
            {"Total Interest:", currencyFormat.format(loan.getTotalInterest())},
            {"Total Amount:", currencyFormat.format(loan.getTotalAmount())},
            {"Penalty Rate:", loan.getPenaltyRate() + "% (" + loan.getPenaltyType() + ")"},
            {"Grace Period:", loan.getGracePeriodDays() + " days"}
        }));
        
        detailsPanel.add(Box.createVerticalStrut(15));
        
        // Payment summary
        PaymentController paymentController = PaymentController.getInstance();
        BigDecimal totalPaid = paymentController.getTotalPaidForLoan(loan.getId());
        BigDecimal remaining = loan.getTotalAmount().subtract(totalPaid);
        int paymentCount = paymentController.getPaymentCountForLoan(loan.getId());
        
        detailsPanel.add(createSectionPanel("Payment Summary", new String[][]{
            {"Total Paid:", currencyFormat.format(totalPaid)},
            {"Remaining Balance:", currencyFormat.format(remaining.max(BigDecimal.ZERO))},
            {"Payments Made:", String.valueOf(paymentCount)}
        }));
        
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton scheduleBtn = new JButton("View Schedule");
        scheduleBtn.addActionListener(e -> {
            dispose();
            AmortizationScheduleDialog dialog = new AmortizationScheduleDialog((Frame) getOwner(), loan.getId());
            dialog.setVisible(true);
        });
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(scheduleBtn);
        buttonPanel.add(closeBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createSectionPanel(String title, String[][] data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(52, 152, 219));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel dataPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        dataPanel.setOpaque(false);
        dataPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        for (String[] row : data) {
            JLabel labelLabel = new JLabel(row[0]);
            labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            labelLabel.setForeground(new Color(127, 140, 141));
            
            JLabel valueLabel = new JLabel(row[1]);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            dataPanel.add(labelLabel);
            dataPanel.add(valueLabel);
        }
        
        panel.add(dataPanel, BorderLayout.CENTER);
        
        return panel;
    }
}
