package com.vismera.views.admin;

import com.vismera.controllers.LoanControllerDB;
import com.vismera.models.Loan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

/**
 * Panel for managing loans.
 * 
 * @author Vismerá Inc.
 */
public class LoanManagementPanel extends JPanel {
    
    private final LoanControllerDB loanController;
    private final NumberFormat currencyFormat;
    
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> statusFilter;
    private JLabel recordCountLabel;
    
    public LoanManagementPanel() {
        loanController = LoanControllerDB.getInstance();
        currencyFormat = NumberFormat.getCurrencyInstance();
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Loan Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.setOpaque(false);
        
        statusFilter = new JComboBox<>(new String[]{"All Status", "Active", "Closed", "Defaulted"});
        statusFilter.addActionListener(e -> loadData());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        
        filterPanel.add(new JLabel("Status: "));
        filterPanel.add(statusFilter);
        filterPanel.add(refreshBtn);
        
        // Actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionsPanel.setOpaque(false);
        
        JButton newLoanBtn = createButton("New Loan", new Color(46, 204, 113));
        JButton viewBtn = createButton("View Details", new Color(52, 152, 219));
        JButton scheduleBtn = createButton("Amortization", new Color(155, 89, 182));
        JButton paymentBtn = createButton("Record Payment", new Color(241, 196, 15));
        JButton closeBtn = createButton("Close Loan", new Color(231, 76, 60));
        
        newLoanBtn.addActionListener(e -> showNewLoanDialog());
        viewBtn.addActionListener(e -> showLoanDetails());
        scheduleBtn.addActionListener(e -> showAmortizationSchedule());
        paymentBtn.addActionListener(e -> showPaymentDialog());
        closeBtn.addActionListener(e -> closeLoan());
        
        actionsPanel.add(newLoanBtn);
        actionsPanel.add(viewBtn);
        actionsPanel.add(scheduleBtn);
        actionsPanel.add(paymentBtn);
        actionsPanel.add(closeBtn);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(actionsPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(filterPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        String[] columns = {"ID", "Customer", "Car", "Principal", "APR%", "Term", "Monthly", "Start Date", "Status", "Balance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        loanTable = new JTable(tableModel);
        loanTable.setRowHeight(35);
        loanTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loanTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        loanTable.getTableHeader().setBackground(new Color(220, 220, 220));
        loanTable.getTableHeader().setForeground(Color.BLACK);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.setShowGrid(true);
        loanTable.setGridColor(new Color(230, 230, 230));
        
        // Column widths
        loanTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        loanTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        loanTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        loanTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        loanTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        loanTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        loanTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        loanTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        loanTable.getColumnModel().getColumn(8).setPreferredWidth(70);
        loanTable.getColumnModel().getColumn(9).setPreferredWidth(100);
        
        sorter = new TableRowSorter<>(tableModel);
        loanTable.setRowSorter(sorter);
        
        loanTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showLoanDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        recordCountLabel = new JLabel("0 records");
        recordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recordCountLabel.setForeground(new Color(127, 140, 141));
        
        panel.add(recordCountLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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
    
    public void loadData() {
        SwingWorker<List<Loan>, Void> worker = new SwingWorker<List<Loan>, Void>() {
            @Override
            protected List<Loan> doInBackground() {
                String status = (String) statusFilter.getSelectedItem();
                if (status == null || "All Status".equals(status)) {
                    return loanController.getAllLoansWithDetails();
                } else {
                    return loanController.getLoansByStatusWithDetails(status.toLowerCase());
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Loan> loans = get();
                    populateTable(loans);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(LoanManagementPanel.this,
                        "Error loading loans: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void populateTable(List<Loan> loans) {
        tableModel.setRowCount(0);
        
        for (Loan loan : loans) {
            String customerName = loan.getCustomer() != null ? loan.getCustomer().getFullName() : "N/A";
            String carName = loan.getCar() != null ? loan.getCar().getDisplayName() : "N/A";
            
            BigDecimal balance = loan.getTotalAmount();
            // In a real scenario, we'd calculate remaining balance from payments
            
            Object[] row = {
                loan.getId(),
                customerName,
                carName,
                currencyFormat.format(loan.getPrincipal()),
                loan.getApr() + "%",
                loan.getTermMonths() + " mo",
                currencyFormat.format(loan.getMonthlyPayment()),
                loan.getStartDate(),
                loan.getStatus().toUpperCase(),
                currencyFormat.format(balance)
            };
            tableModel.addRow(row);
        }
        
        recordCountLabel.setText(loans.size() + " loan" + (loans.size() != 1 ? "s" : ""));
    }
    
    private void showNewLoanDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        LoanFormDialog dialog = new LoanFormDialog((Frame) window, null);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }
    
    private void showLoanDetails() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a loan to view.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = loanTable.convertRowIndexToModel(selectedRow);
        int loanId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        Loan loan = loanController.getLoanWithDetails(loanId);
        if (loan == null) {
            JOptionPane.showMessageDialog(this,
                "Loan not found.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Window window = SwingUtilities.getWindowAncestor(this);
        LoanDetailsDialog dialog = new LoanDetailsDialog((Frame) window, loan);
        dialog.setVisible(true);
    }
    
    private void showAmortizationSchedule() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a loan to view schedule.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = loanTable.convertRowIndexToModel(selectedRow);
        int loanId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        Window window = SwingUtilities.getWindowAncestor(this);
        AmortizationScheduleDialog dialog = new AmortizationScheduleDialog((Frame) window, loanId);
        dialog.setVisible(true);
    }
    
    private void showPaymentDialog() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a loan to record payment.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = loanTable.convertRowIndexToModel(selectedRow);
        int loanId = (Integer) tableModel.getValueAt(modelRow, 0);
        String status = (String) tableModel.getValueAt(modelRow, 8);
        
        if (!"ACTIVE".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                "Can only record payments for active loans.",
                "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Window window = SwingUtilities.getWindowAncestor(this);
        PaymentRecordDialog dialog = new PaymentRecordDialog((Frame) window, loanId);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }
    
    private void closeLoan() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a loan to close.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = loanTable.convertRowIndexToModel(selectedRow);
        int loanId = (Integer) tableModel.getValueAt(modelRow, 0);
        String status = (String) tableModel.getValueAt(modelRow, 8);
        
        if (!"ACTIVE".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                "Loan is already " + status + ".",
                "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to close this loan?\n\n" +
            "This action will mark the loan as closed.",
            "Confirm Close Loan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean closed = loanController.closeLoan(loanId);
            if (closed) {
                loadData();
                JOptionPane.showMessageDialog(this,
                    "Loan closed successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to close loan.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadData();
    }
}
