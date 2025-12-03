package com.vismera.views.admin;

import com.vismera.controllers.PaymentController;
import com.vismera.controllers.LoanControllerDB;
import com.vismera.models.Loan;
import com.vismera.models.Payment;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for recording loan payments.
 * 
 * @author Vismerá Inc.
 */
public class PaymentRecordDialog extends JDialog {
    
    private boolean saved = false;
    private Integer preSelectedLoanId;
    private final NumberFormat currencyFormat;
    
    private JComboBox<LoanItem> loanCombo;
    private JTextField amountField;
    private JSpinner paymentDateSpinner;
    private JComboBox<String> paymentTypeCombo;
    private JTextArea noteArea;
    private JLabel loanDetailsLabel;
    private JLabel penaltyWarningLabel;
    
    public PaymentRecordDialog(Frame owner) {
        this(owner, null);
    }
    
    public PaymentRecordDialog(Frame owner, Integer loanId) {
        super(owner, "Record Payment", true);
        this.preSelectedLoanId = loanId;
        this.currencyFormat = NumberFormat.getCurrencyInstance();
        initComponents();
        loadLoans();
    }
    
    private void initComponents() {
        setSize(500, 450);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Record Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Loan selection
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Select Loan:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        loanCombo = new JComboBox<>();
        loanCombo.addActionListener(e -> updateLoanDetails());
        formPanel.add(loanCombo, gbc);
        
        // Loan details display
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        loanDetailsLabel = new JLabel(" ");
        loanDetailsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        loanDetailsLabel.setForeground(new Color(127, 140, 141));
        formPanel.add(loanDetailsLabel, gbc);
        gbc.gridwidth = 1;
        
        // Payment amount
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Amount:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        amountField = new JTextField(20);
        formPanel.add(amountField, gbc);
        
        // Payment date
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Payment Date:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        paymentDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(paymentDateSpinner, "yyyy-MM-dd");
        paymentDateSpinner.setEditor(dateEditor);
        formPanel.add(paymentDateSpinner, gbc);
        
        // Payment type
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Payment Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        paymentTypeCombo = new JComboBox<>(new String[]{
            Payment.TYPE_REGULAR, 
            Payment.TYPE_EXTRA, 
            Payment.TYPE_EARLY_PAYOFF,
            Payment.TYPE_PENALTY_ONLY
        });
        formPanel.add(paymentTypeCombo, gbc);
        
        // Note
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Note:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1; gbc.weighty = 1;
        noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        formPanel.add(noteScroll, gbc);
        
        // Penalty warning
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        penaltyWarningLabel = new JLabel(" ");
        penaltyWarningLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        penaltyWarningLabel.setForeground(new Color(231, 76, 60));
        formPanel.add(penaltyWarningLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton applyMonthlyBtn = new JButton("Apply Monthly");
        applyMonthlyBtn.addActionListener(e -> applyMonthlyPayment());
        
        JButton saveBtn = new JButton("Record Payment");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> save());
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(applyMonthlyBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
    
    private void loadLoans() {
        LoanControllerDB loanController = LoanControllerDB.getInstance();
        List<Loan> activeLoans = loanController.getActiveLoans();
        
        loanCombo.addItem(new LoanItem(null, "-- Select Loan --"));
        
        for (Loan loan : activeLoans) {
            String display = String.format("Loan #%d - %s (%s)", 
                loan.getId(),
                loan.getCustomer() != null ? loan.getCustomer().getFullName() : "Unknown",
                loan.getCar() != null ? loan.getCar().getDisplayName() : "Unknown");
            loanCombo.addItem(new LoanItem(loan, display));
        }
        
        // Pre-select if provided
        if (preSelectedLoanId != null) {
            for (int i = 0; i < loanCombo.getItemCount(); i++) {
                LoanItem item = loanCombo.getItemAt(i);
                if (item.loan != null && item.loan.getId() == preSelectedLoanId) {
                    loanCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void updateLoanDetails() {
        LoanItem item = (LoanItem) loanCombo.getSelectedItem();
        if (item == null || item.loan == null) {
            loanDetailsLabel.setText(" ");
            penaltyWarningLabel.setText(" ");
            return;
        }
        
        Loan loan = item.loan;
        String details = String.format("Monthly: %s | Principal: %s | APR: %s%%",
            currencyFormat.format(loan.getMonthlyPayment()),
            currencyFormat.format(loan.getPrincipal()),
            loan.getApr());
        loanDetailsLabel.setText(details);
        
        // Check for late payment penalty
        PaymentController paymentController = PaymentController.getInstance();
        BigDecimal penalty = paymentController.calculatePenalty(loan.getId());
        if (penalty.compareTo(BigDecimal.ZERO) > 0) {
            penaltyWarningLabel.setText("⚠ Late payment penalty: " + currencyFormat.format(penalty));
        } else {
            penaltyWarningLabel.setText(" ");
        }
    }
    
    private void applyMonthlyPayment() {
        LoanItem item = (LoanItem) loanCombo.getSelectedItem();
        if (item != null && item.loan != null) {
            amountField.setText(item.loan.getMonthlyPayment().toPlainString());
        }
    }
    
    private void save() {
        LoanItem loanItem = (LoanItem) loanCombo.getSelectedItem();
        if (loanItem == null || loanItem.loan == null) {
            JOptionPane.showMessageDialog(this, "Please select a loan.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String amountStr = amountField.getText().trim();
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.replace(",", "").replace("$", ""));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }
        
        java.util.Date date = (java.util.Date) paymentDateSpinner.getValue();
        LocalDate paymentDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        
        String type = (String) paymentTypeCombo.getSelectedItem();
        String note = noteArea.getText().trim();
        
        // Record payment
        PaymentController paymentController = PaymentController.getInstance();
        boolean success = paymentController.recordPayment(
            loanItem.loan.getId(),
            amount,
            paymentDate,
            type,
            note,
            "Admin" // recorded by
        );
        
        if (success) {
            saved = true;
            JOptionPane.showMessageDialog(this,
                "Payment recorded successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to record payment.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    private static class LoanItem {
        Loan loan;
        String display;
        
        LoanItem(Loan loan, String display) {
            this.loan = loan;
            this.display = display;
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
}
