package com.vismera.views;

import com.vismera.controllers.LoanController;
import com.vismera.models.Car;
import com.vismera.models.LoanCalculation;
import com.vismera.utils.FormatUtils;
import com.vismera.utils.UIStyler;
import com.vismera.utils.ValidationUtils;
import java.awt.*;
import javax.swing.*;

/**
 * Calculate Panel - Page 2: Loan Calculator
 * @author Vismerá Inc.
 */
public class CalculatePanel extends JPanel {
    
    private MainFrame parentFrame;
    private Car selectedCar;
    
    // Input fields
    private JTextField carPriceField;
    private JTextField salesTaxField;
    private JTextField registrationFeeField;
    private JTextField downPaymentField;
    private JTextField tradeInValueField;
    private JTextField annualInterestRateField;
    private JTextField loanTermYearsField;
    private JComboBox<String> compoundingFrequencyCombo;
    private JTextField penaltyRateField;
    private JTextField missedPaymentsField;
    private JTextField extraPaymentField;
    
    // Summary labels
    private JLabel totalCostLabel;
    private JLabel amountFinancedLabel;
    private JLabel downPlusTradeLabel;
    private JLabel taxAmountLabel;
    
    // Selected car panel
    private JPanel selectedCarPanel;
    private JLabel carNameLabel;
    private JLabel carPriceLabel;
    
    public CalculatePanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyler.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        // Selected car info panel (top)
        selectedCarPanel = createSelectedCarPanel();
        contentPanel.add(selectedCarPanel, BorderLayout.NORTH);
        
        // Form panel (center)
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        formPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        // Left column - Inputs
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        leftColumn.add(createVehicleCostSection());
        leftColumn.add(Box.createVerticalStrut(15));
        leftColumn.add(createDownPaymentSection());
        leftColumn.add(Box.createVerticalStrut(15));
        leftColumn.add(createLoanTermsSection());
        
        // Right column - Summary & Calculate
        JPanel rightColumn = new JPanel(new BorderLayout(0, 15));
        rightColumn.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        rightColumn.add(createSummarySection(), BorderLayout.NORTH);
        rightColumn.add(createPenaltySection(), BorderLayout.CENTER);
        rightColumn.add(createCalculateSection(), BorderLayout.SOUTH);
        
        formPanel.add(leftColumn);
        formPanel.add(rightColumn);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UIStyler.BACKGROUND_LIGHT);
        scrollPane.getViewport().setBackground(UIStyler.BACKGROUND_LIGHT);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Add field listeners to update summary
        addFieldListeners();
    }
    
    private JPanel createSelectedCarPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Car icon placeholder
        JLabel iconLabel = new JLabel("🚗");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        
        // Car info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        carNameLabel = new JLabel("No car selected");
        carNameLabel.setFont(UIStyler.HEADER_FONT);
        carNameLabel.setForeground(UIStyler.TEXT_DARK);
        
        carPriceLabel = new JLabel("Please select a car from the Cars tab");
        carPriceLabel.setFont(UIStyler.BODY_FONT);
        carPriceLabel.setForeground(UIStyler.TEXT_SECONDARY);
        
        infoPanel.add(carNameLabel);
        infoPanel.add(carPriceLabel);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createVehicleCostSection() {
        JPanel panel = UIStyler.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel header = new JLabel("VEHICLE COST");
        header.setFont(UIStyler.SUBHEADER_FONT);
        header.setForeground(UIStyler.TEXT_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(header);
        panel.add(Box.createVerticalStrut(15));
        
        carPriceField = createLabeledField(panel, "Car Price ($)", "0.00");
        salesTaxField = createLabeledField(panel, "Sales Tax Rate (%)", "8.0");
        registrationFeeField = createLabeledField(panel, "Registration Fee ($)", "500.00");
        
        return panel;
    }
    
    private JPanel createDownPaymentSection() {
        JPanel panel = UIStyler.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel header = new JLabel("DOWN PAYMENT & TRADE-IN");
        header.setFont(UIStyler.SUBHEADER_FONT);
        header.setForeground(UIStyler.TEXT_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(header);
        panel.add(Box.createVerticalStrut(15));
        
        downPaymentField = createLabeledField(panel, "Down Payment ($)", "0.00");
        tradeInValueField = createLabeledField(panel, "Trade-In Value ($)", "0.00");
        
        return panel;
    }
    
    private JPanel createLoanTermsSection() {
        JPanel panel = UIStyler.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel header = new JLabel("LOAN TERMS");
        header.setFont(UIStyler.SUBHEADER_FONT);
        header.setForeground(UIStyler.TEXT_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(header);
        panel.add(Box.createVerticalStrut(15));
        
        annualInterestRateField = createLabeledField(panel, "Annual Interest Rate (%)", "6.5");
        loanTermYearsField = createLabeledField(panel, "Loan Term (Years)", "5");
        
        // Compounding frequency dropdown
        JPanel comboPanel = new JPanel(new BorderLayout(10, 5));
        comboPanel.setOpaque(false);
        comboPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel comboLabel = new JLabel("Compounding Frequency");
        UIStyler.styleLabel(comboLabel);
        
        String[] frequencies = {"Monthly", "Quarterly", "Semi-Annually", "Annually"};
        compoundingFrequencyCombo = new JComboBox<>(frequencies);
        compoundingFrequencyCombo.setFont(UIStyler.BODY_FONT);
        compoundingFrequencyCombo.setPreferredSize(new Dimension(200, 35));
        
        comboPanel.add(comboLabel, BorderLayout.NORTH);
        comboPanel.add(compoundingFrequencyCombo, BorderLayout.CENTER);
        
        panel.add(comboPanel);
        panel.add(Box.createVerticalStrut(10));
        
        return panel;
    }
    
    private JPanel createPenaltySection() {
        JPanel panel = UIStyler.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel header = new JLabel("PENALTY & EXTRA PAYMENTS");
        header.setFont(UIStyler.SUBHEADER_FONT);
        header.setForeground(UIStyler.TEXT_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(header);
        panel.add(Box.createVerticalStrut(15));
        
        penaltyRateField = createLabeledField(panel, "Penalty Rate (%)", "2.0");
        missedPaymentsField = createLabeledField(panel, "Simulated Missed Payments", "0");
        extraPaymentField = createLabeledField(panel, "Extra Payment Per Month ($)", "0.00");
        
        return panel;
    }
    
    private JPanel createSummarySection() {
        JPanel panel = UIStyler.createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel header = new JLabel("LOAN SUMMARY");
        header.setFont(UIStyler.SUBHEADER_FONT);
        header.setForeground(UIStyler.TEXT_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(header);
        panel.add(Box.createVerticalStrut(15));
        
        totalCostLabel = createSummaryRow(panel, "Total Vehicle Cost:", "$0.00");
        taxAmountLabel = createSummaryRow(panel, "Tax Amount:", "$0.00");
        downPlusTradeLabel = createSummaryRow(panel, "Down Payment + Trade-In:", "$0.00");
        amountFinancedLabel = createSummaryRow(panel, "Amount Financed:", "$0.00");
        amountFinancedLabel.setForeground(UIStyler.PRIMARY_BLUE);
        amountFinancedLabel.setFont(UIStyler.HEADER_FONT);
        
        return panel;
    }
    
    private JPanel createCalculateSection() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        JButton calculateButton = new JButton("Calculate Loan");
        UIStyler.stylePrimaryButton(calculateButton);
        calculateButton.setPreferredSize(new Dimension(200, 50));
        calculateButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        calculateButton.addActionListener(e -> calculateLoan());
        
        JButton clearButton = new JButton("Clear All");
        UIStyler.styleSecondaryButton(clearButton);
        clearButton.addActionListener(e -> clearFields());
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createLabeledField(JPanel parent, String labelText, String defaultValue) {
        JPanel fieldPanel = new JPanel(new BorderLayout(0, 5));
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        UIStyler.styleLabel(label);
        
        JTextField field = new JTextField(defaultValue);
        UIStyler.styleTextField(field);
        
        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        parent.add(fieldPanel);
        parent.add(Box.createVerticalStrut(10));
        
        return field;
    }
    
    private JLabel createSummaryRow(JPanel parent, String labelText, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(UIStyler.BODY_FONT);
        label.setForeground(UIStyler.TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UIStyler.BODY_FONT);
        valueLabel.setForeground(UIStyler.TEXT_DARK);
        
        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        
        parent.add(row);
        parent.add(Box.createVerticalStrut(8));
        
        return valueLabel;
    }
    
    private void addFieldListeners() {
        java.awt.event.KeyAdapter updateListener = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateSummary();
            }
        };
        
        carPriceField.addKeyListener(updateListener);
        salesTaxField.addKeyListener(updateListener);
        registrationFeeField.addKeyListener(updateListener);
        downPaymentField.addKeyListener(updateListener);
        tradeInValueField.addKeyListener(updateListener);
    }
    
    private void updateSummary() {
        try {
            double carPrice = FormatUtils.parseDouble(carPriceField.getText());
            double taxRate = FormatUtils.parseDouble(salesTaxField.getText());
            double regFee = FormatUtils.parseDouble(registrationFeeField.getText());
            double downPayment = FormatUtils.parseDouble(downPaymentField.getText());
            double tradeIn = FormatUtils.parseDouble(tradeInValueField.getText());
            
            double taxAmount = carPrice * (taxRate / 100.0);
            double totalCost = carPrice + taxAmount + regFee;
            double downPlusTrade = downPayment + tradeIn;
            double amountFinanced = Math.max(0, totalCost - downPlusTrade);
            
            totalCostLabel.setText(FormatUtils.formatCurrency(totalCost));
            taxAmountLabel.setText(FormatUtils.formatCurrency(taxAmount));
            downPlusTradeLabel.setText(FormatUtils.formatCurrency(downPlusTrade));
            amountFinancedLabel.setText(FormatUtils.formatCurrency(amountFinanced));
        } catch (Exception e) {
            // Ignore parsing errors during typing
        }
    }
    
    private void calculateLoan() {
        // Validate inputs
        String errors = ValidationUtils.validateLoanInputs(
            carPriceField.getText(),
            annualInterestRateField.getText(),
            loanTermYearsField.getText(),
            downPaymentField.getText(),
            tradeInValueField.getText()
        );
        
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please correct the following errors:\n\n" + errors,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Create loan calculation
            LoanCalculation loan = LoanController.getInstance().createLoanCalculation(
                FormatUtils.parseDouble(carPriceField.getText()),
                FormatUtils.parseDouble(salesTaxField.getText()),
                FormatUtils.parseDouble(registrationFeeField.getText()),
                FormatUtils.parseDouble(downPaymentField.getText()),
                FormatUtils.parseDouble(tradeInValueField.getText()),
                FormatUtils.parseDouble(annualInterestRateField.getText()),
                FormatUtils.parseInt(loanTermYearsField.getText()),
                (String) compoundingFrequencyCombo.getSelectedItem(),
                FormatUtils.parseDouble(penaltyRateField.getText()),
                FormatUtils.parseInt(missedPaymentsField.getText()),
                FormatUtils.parseDouble(extraPaymentField.getText())
            );
            
            // Show summary dialog
            LoanSummaryDialog dialog = new LoanSummaryDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                loan
            );
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error calculating loan: " + e.getMessage(),
                "Calculation Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        carPriceField.setText("0.00");
        salesTaxField.setText("8.0");
        registrationFeeField.setText("500.00");
        downPaymentField.setText("0.00");
        tradeInValueField.setText("0.00");
        annualInterestRateField.setText("6.5");
        loanTermYearsField.setText("5");
        compoundingFrequencyCombo.setSelectedIndex(0);
        penaltyRateField.setText("2.0");
        missedPaymentsField.setText("0");
        extraPaymentField.setText("0.00");
        
        updateSummary();
    }
    
    public void setSelectedCar(Car car) {
        this.selectedCar = car;
        
        if (car != null) {
            carNameLabel.setText(car.getYear() + " " + car.getFullName());
            carPriceLabel.setText(car.getCategory() + " • " + FormatUtils.formatCurrency(car.getPrice()));
            carPriceField.setText(String.format("%.2f", car.getPrice()));
            updateSummary();
        } else {
            carNameLabel.setText("No car selected");
            carPriceLabel.setText("Please select a car from the Cars tab");
        }
    }
}
