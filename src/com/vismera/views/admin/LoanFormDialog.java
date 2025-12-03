package com.vismera.views.admin;

import com.vismera.controllers.CarController;
import com.vismera.controllers.CustomerController;
import com.vismera.controllers.LoanControllerDB;
import com.vismera.controllers.SettingsController;
import com.vismera.models.Car;
import com.vismera.models.Customer;
import com.vismera.models.Loan;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for creating new loans.
 * 
 * @author Vismerá Inc.
 */
public class LoanFormDialog extends JDialog {
    
    private final Loan existingLoan;
    private boolean saved = false;
    
    private JComboBox<CustomerItem> customerCombo;
    private JComboBox<CarItem> carCombo;
    private JTextField principalField;
    private JTextField aprField;
    private JSpinner termSpinner;
    private JComboBox<String> compoundingCombo;
    private JTextField downPaymentField;
    private JTextField tradeInField;
    private JTextField salesTaxField;
    private JTextField registrationFeeField;
    private JSpinner startDateSpinner;
    
    // Calculated fields (display only)
    private JLabel monthlyPaymentLabel;
    private JLabel totalInterestLabel;
    private JLabel totalAmountLabel;
    
    public LoanFormDialog(Frame owner, Loan loan) {
        super(owner, loan == null ? "Create New Loan" : "Edit Loan", true);
        this.existingLoan = loan;
        initComponents();
        loadDefaults();
        loadCombos();
        setupListeners();
    }
    
    private void initComponents() {
        setSize(600, 650);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel(existingLoan == null ? "Create New Loan" : "Edit Loan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Customer
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Customer:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        customerCombo = new JComboBox<>();
        formPanel.add(customerCombo, gbc);
        
        // Car
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Car:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        carCombo = new JComboBox<>();
        formPanel.add(carCombo, gbc);
        
        // Principal (auto-filled from car price)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Loan Principal:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        principalField = new JTextField(20);
        formPanel.add(principalField, gbc);
        
        // APR
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("APR (%):*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        aprField = new JTextField(20);
        formPanel.add(aprField, gbc);
        
        // Term
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Term (Months):*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        termSpinner = new JSpinner(new SpinnerNumberModel(36, 6, 84, 6));
        formPanel.add(termSpinner, gbc);
        
        // Compounding
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Compounding:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        compoundingCombo = new JComboBox<>(new String[]{"monthly", "quarterly", "semi-annually", "annually"});
        formPanel.add(compoundingCombo, gbc);
        
        // Down Payment
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Down Payment:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        downPaymentField = new JTextField("0", 20);
        formPanel.add(downPaymentField, gbc);
        
        // Trade-In Value
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Trade-In Value:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        tradeInField = new JTextField("0", 20);
        formPanel.add(tradeInField, gbc);
        
        // Sales Tax
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Sales Tax (%):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        salesTaxField = new JTextField("0", 20);
        formPanel.add(salesTaxField, gbc);
        
        // Registration Fee
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Registration Fee:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        registrationFeeField = new JTextField("0", 20);
        formPanel.add(registrationFeeField, gbc);
        
        // Start Date
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Start Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(dateEditor);
        formPanel.add(startDateSpinner, gbc);
        
        // Separator
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        
        // Calculated summary
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Monthly Payment:"), gbc);
        gbc.gridx = 1;
        monthlyPaymentLabel = new JLabel("$0.00");
        monthlyPaymentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        monthlyPaymentLabel.setForeground(new Color(46, 204, 113));
        formPanel.add(monthlyPaymentLabel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Total Interest:"), gbc);
        gbc.gridx = 1;
        totalInterestLabel = new JLabel("$0.00");
        totalInterestLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(totalInterestLabel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel("$0.00");
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalAmountLabel.setForeground(new Color(52, 152, 219));
        formPanel.add(totalAmountLabel, gbc);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton calculateBtn = new JButton("Calculate");
        calculateBtn.addActionListener(e -> calculateLoan());
        
        JButton saveBtn = new JButton("Create Loan");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> save());
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(calculateBtn);
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
    
    private void loadDefaults() {
        SettingsController settings = SettingsController.getInstance();
        aprField.setText(settings.getSetting("default_apr", "5.99"));
    }
    
    private void loadCombos() {
        // Load customers
        CustomerController customerController = CustomerController.getInstance();
        List<Customer> customers = customerController.getAllCustomers();
        customerCombo.addItem(new CustomerItem(null, "-- Select Customer --"));
        for (Customer customer : customers) {
            customerCombo.addItem(new CustomerItem(customer, customer.getFullName()));
        }
        
        // Load available cars
        CarController carController = CarController.getInstance();
        carController.setUseDatabase(true);
        List<Car> cars = carController.getAvailableCars();
        carCombo.addItem(new CarItem(null, "-- Select Car --"));
        for (Car car : cars) {
            carCombo.addItem(new CarItem(car, car.getDisplayName() + " - $" + car.getPriceBigDecimal()));
        }
    }
    
    private void setupListeners() {
        // Auto-fill principal when car is selected
        carCombo.addActionListener(e -> {
            CarItem item = (CarItem) carCombo.getSelectedItem();
            if (item != null && item.car != null) {
                principalField.setText(item.car.getPriceBigDecimal().toPlainString());
                calculateLoan();
            }
        });
        
        // Recalculate on field changes
        principalField.addActionListener(e -> calculateLoan());
        aprField.addActionListener(e -> calculateLoan());
        termSpinner.addChangeListener(e -> calculateLoan());
        downPaymentField.addActionListener(e -> calculateLoan());
        tradeInField.addActionListener(e -> calculateLoan());
        salesTaxField.addActionListener(e -> calculateLoan());
    }
    
    private void calculateLoan() {
        try {
            BigDecimal principal = parseDecimal(principalField.getText());
            BigDecimal apr = parseDecimal(aprField.getText());
            int term = (Integer) termSpinner.getValue();
            BigDecimal downPayment = parseDecimal(downPaymentField.getText());
            BigDecimal tradeIn = parseDecimal(tradeInField.getText());
            BigDecimal salesTax = parseDecimal(salesTaxField.getText());
            BigDecimal regFee = parseDecimal(registrationFeeField.getText());
            
            // Calculate net loan amount
            BigDecimal taxAmount = principal.multiply(salesTax.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            BigDecimal netPrincipal = principal.add(taxAmount).add(regFee).subtract(downPayment).subtract(tradeIn);
            
            if (netPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
                monthlyPaymentLabel.setText("Invalid amount");
                return;
            }
            
            // Calculate monthly payment using standard formula
            BigDecimal monthlyRate = apr.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
            
            BigDecimal monthlyPayment;
            if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
                monthlyPayment = netPrincipal.divide(new BigDecimal(term), 2, RoundingMode.HALF_UP);
            } else {
                // M = P * [r(1+r)^n] / [(1+r)^n - 1]
                BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
                BigDecimal onePlusRPowN = onePlusR.pow(term);
                BigDecimal numerator = monthlyRate.multiply(onePlusRPowN);
                BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);
                monthlyPayment = netPrincipal.multiply(numerator.divide(denominator, 10, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            }
            
            BigDecimal totalAmount = monthlyPayment.multiply(new BigDecimal(term));
            BigDecimal totalInterest = totalAmount.subtract(netPrincipal);
            
            monthlyPaymentLabel.setText("$" + monthlyPayment.toPlainString());
            totalInterestLabel.setText("$" + totalInterest.setScale(2, RoundingMode.HALF_UP).toPlainString());
            totalAmountLabel.setText("$" + totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());
            
        } catch (Exception e) {
            monthlyPaymentLabel.setText("Error");
        }
    }
    
    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim().replace(",", "").replace("$", ""));
    }
    
    private void save() {
        // Validate
        CustomerItem customerItem = (CustomerItem) customerCombo.getSelectedItem();
        CarItem carItem = (CarItem) carCombo.getSelectedItem();
        
        if (customerItem == null || customerItem.customer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (carItem == null || carItem.car == null) {
            JOptionPane.showMessageDialog(this, "Please select a car.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            BigDecimal principal = parseDecimal(principalField.getText());
            BigDecimal apr = parseDecimal(aprField.getText());
            int term = (Integer) termSpinner.getValue();
            String compounding = (String) compoundingCombo.getSelectedItem();
            BigDecimal downPayment = parseDecimal(downPaymentField.getText());
            BigDecimal tradeIn = parseDecimal(tradeInField.getText());
            BigDecimal salesTax = parseDecimal(salesTaxField.getText());
            BigDecimal regFee = parseDecimal(registrationFeeField.getText());
            
            if (principal.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Principal must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create loan
            Loan loan = new Loan();
            loan.setCustomerId(customerItem.customer.getId());
            loan.setCarId(carItem.car.getId());
            loan.setPrincipal(principal);
            loan.setApr(apr);
            loan.setTermMonths(term);
            loan.setCompounding(compounding);
            loan.setPaymentFrequency("monthly");
            loan.setDownPayment(downPayment);
            loan.setTradeInValue(tradeIn);
            loan.setSalesTaxRate(salesTax);
            loan.setRegistrationFee(regFee);
            loan.setStartDate(LocalDate.now());
            loan.setStatus(Loan.STATUS_ACTIVE);
            
            // Default penalty settings
            SettingsController settings = SettingsController.getInstance();
            loan.setPenaltyRate(new BigDecimal(settings.getSetting("default_penalty_rate", "5.00")));
            loan.setPenaltyType(settings.getSetting("default_penalty_type", "percentage"));
            loan.setGracePeriodDays(Integer.parseInt(settings.getSetting("default_grace_period", "5")));
            
            // Create loan with schedule
            LoanControllerDB loanController = LoanControllerDB.getInstance();
            int loanId = loanController.createLoan(loan);
            
            if (loanId > 0) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                    "Loan created successfully!\nLoan ID: " + loanId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to create loan.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for all fields.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    // Helper classes for combo box items
    private static class CustomerItem {
        Customer customer;
        String display;
        
        CustomerItem(Customer customer, String display) {
            this.customer = customer;
            this.display = display;
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
    
    private static class CarItem {
        Car car;
        String display;
        
        CarItem(Car car, String display) {
            this.car = car;
            this.display = display;
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
}
