package com.vismera.views.admin;

import com.vismera.controllers.ReportController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Map;

/**
 * Dashboard panel showing system overview and statistics.
 * 
 * @author Vismerá Inc.
 */
public class DashboardPanel extends JPanel {
    
    private final ReportController reportController;
    private final NumberFormat currencyFormat;
    
    // Stat cards
    private JLabel totalCustomersLabel;
    private JLabel totalCarsLabel;
    private JLabel availableCarsLabel;
    private JLabel totalLoansLabel;
    private JLabel activeLoansLabel;
    private JLabel closedLoansLabel;
    private JLabel totalOutstandingLabel;
    private JLabel todayPaymentsLabel;
    private JLabel monthPaymentsLabel;
    
    public DashboardPanel() {
        reportController = ReportController.getInstance();
        currencyFormat = NumberFormat.getCurrencyInstance();
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Row 1: Customer, Cars stats
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;
        contentPanel.add(createCustomerStatsCard(), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(createCarStatsCard(), gbc);
        
        gbc.gridx = 2;
        contentPanel.add(createLoanStatsCard(), gbc);
        
        // Row 2: Financial stats
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        contentPanel.add(createFinancialStatsCard(), gbc);
        
        // Row 3: Quick Actions
        gbc.gridy = 2; gbc.weighty = 1;
        contentPanel.add(createQuickActionsCard(), gbc);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createCustomerStatsCard() {
        JPanel card = createCard("Customers");
        card.setLayout(new GridLayout(1, 1));
        
        totalCustomersLabel = createStatLabel("0", "Total Customers");
        card.add(totalCustomersLabel);
        
        return card;
    }
    
    private JPanel createCarStatsCard() {
        JPanel card = createCard("Car Inventory");
        card.setLayout(new GridLayout(1, 2, 10, 0));
        
        totalCarsLabel = createStatLabel("0", "Total Cars");
        availableCarsLabel = createStatLabel("0", "Available");
        
        card.add(totalCarsLabel);
        card.add(availableCarsLabel);
        
        return card;
    }
    
    private JPanel createLoanStatsCard() {
        JPanel card = createCard("Loans");
        card.setLayout(new GridLayout(1, 3, 10, 0));
        
        totalLoansLabel = createStatLabel("0", "Total");
        activeLoansLabel = createStatLabel("0", "Active");
        closedLoansLabel = createStatLabel("0", "Closed");
        
        card.add(totalLoansLabel);
        card.add(activeLoansLabel);
        card.add(closedLoansLabel);
        
        return card;
    }
    
    private JPanel createFinancialStatsCard() {
        JPanel card = createCard("Financial Overview");
        card.setLayout(new GridLayout(1, 3, 20, 0));
        
        totalOutstandingLabel = createStatLabel("$0.00", "Total Outstanding");
        todayPaymentsLabel = createStatLabel("$0.00", "Today's Payments");
        monthPaymentsLabel = createStatLabel("$0.00", "This Month");
        
        card.add(createStatPanel(totalOutstandingLabel, new Color(231, 76, 60)));
        card.add(createStatPanel(todayPaymentsLabel, new Color(39, 174, 96)));
        card.add(createStatPanel(monthPaymentsLabel, new Color(52, 152, 219)));
        
        return card;
    }
    
    private JPanel createQuickActionsCard() {
        JPanel card = createCard("Quick Actions");
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        
        JButton newCustomerBtn = createActionButton("New Customer", new Color(52, 152, 219));
        JButton newCarBtn = createActionButton("Add Car", new Color(46, 204, 113));
        JButton newLoanBtn = createActionButton("Create Loan", new Color(155, 89, 182));
        JButton recordPaymentBtn = createActionButton("Record Payment", new Color(241, 196, 15));
        JButton viewReportsBtn = createActionButton("View Reports", new Color(52, 73, 94));
        
        newCustomerBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof AdminMainFrame) {
                CustomerFormDialog dialog = new CustomerFormDialog((AdminMainFrame) window, null);
                dialog.setVisible(true);
            }
        });
        
        newLoanBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof AdminMainFrame) {
                LoanFormDialog dialog = new LoanFormDialog((AdminMainFrame) window, null);
                dialog.setVisible(true);
            }
        });
        
        recordPaymentBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof AdminMainFrame) {
                PaymentRecordDialog dialog = new PaymentRecordDialog((AdminMainFrame) window);
                dialog.setVisible(true);
            }
        });
        
        viewReportsBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof AdminMainFrame) {
                ((AdminMainFrame) window).navigateToTab(5); // Reports tab
            }
        });
        
        card.add(newCustomerBtn);
        card.add(newCarBtn);
        card.add(newLoanBtn);
        card.add(recordPaymentBtn);
        card.add(viewReportsBtn);
        
        return card;
    }
    
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)
            )
        ));
        return card;
    }
    
    private JLabel createStatLabel(String value, String description) {
        JLabel label = new JLabel("<html><center><span style='font-size:24px;font-weight:bold;'>" + 
            value + "</span><br><span style='font-size:10px;color:#7f8c8d;'>" + 
            description + "</span></center></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    private JPanel createStatPanel(JLabel statLabel, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, accentColor));
        panel.add(statLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadData() {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() {
                return reportController.getDashboardSummary();
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> summary = get();
                    updateStats(summary);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void updateStats(Map<String, Object> summary) {
        int totalCustomers = (Integer) summary.getOrDefault("totalCustomers", 0);
        int totalCars = (Integer) summary.getOrDefault("totalCars", 0);
        int availableCars = (Integer) summary.getOrDefault("availableCars", 0);
        int totalLoans = (Integer) summary.getOrDefault("totalLoans", 0);
        int activeLoans = (Integer) summary.getOrDefault("activeLoans", 0);
        int closedLoans = (Integer) summary.getOrDefault("closedLoans", 0);
        
        BigDecimal totalOutstanding = (BigDecimal) summary.getOrDefault("totalOutstanding", BigDecimal.ZERO);
        BigDecimal todayPayments = (BigDecimal) summary.getOrDefault("todayPayments", BigDecimal.ZERO);
        BigDecimal monthPayments = (BigDecimal) summary.getOrDefault("monthPayments", BigDecimal.ZERO);
        
        updateStatLabel(totalCustomersLabel, String.valueOf(totalCustomers), "Total Customers");
        updateStatLabel(totalCarsLabel, String.valueOf(totalCars), "Total Cars");
        updateStatLabel(availableCarsLabel, String.valueOf(availableCars), "Available");
        updateStatLabel(totalLoansLabel, String.valueOf(totalLoans), "Total");
        updateStatLabel(activeLoansLabel, String.valueOf(activeLoans), "Active");
        updateStatLabel(closedLoansLabel, String.valueOf(closedLoans), "Closed");
        updateStatLabel(totalOutstandingLabel, currencyFormat.format(totalOutstanding), "Total Outstanding");
        updateStatLabel(todayPaymentsLabel, currencyFormat.format(todayPayments), "Today's Payments");
        updateStatLabel(monthPaymentsLabel, currencyFormat.format(monthPayments), "This Month");
    }
    
    private void updateStatLabel(JLabel label, String value, String description) {
        label.setText("<html><center><span style='font-size:24px;font-weight:bold;'>" + 
            value + "</span><br><span style='font-size:10px;color:#7f8c8d;'>" + 
            description + "</span></center></html>");
    }
    
    public void refreshData() {
        loadData();
    }
}
