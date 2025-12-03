package com.vismera.views;

import com.vismera.controllers.ComparisonController;
import com.vismera.models.LoanScenario;
import com.vismera.utils.FormatUtils;
import com.vismera.utils.UIStyler;
import com.vismera.utils.ValidationUtils;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Compare Panel - Page 3: Scenario Comparison
 * @author Vismerá Inc.
 */
public class ComparePanel extends JPanel {
    
    private MainFrame parentFrame;
    private ComparisonController controller;
    
    // Input fields
    private JTextField scenarioNameField;
    private JTextField loanAmountField;
    private JTextField interestRateField;
    private JTextField termYearsField;
    
    // Table
    private JTable comparisonTable;
    private DefaultTableModel tableModel;
    
    // Best deal label
    private JLabel bestDealLabel;
    
    public ComparePanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.controller = ComparisonController.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyler.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top - Input form
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);
        
        // Center - Comparison table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Bottom - Best deal indicator
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = UIStyler.createCardPanel();
        panel.setLayout(new BorderLayout(15, 15));
        
        JLabel header = new JLabel("Add Loan Scenario");
        header.setFont(UIStyler.HEADER_FONT);
        header.setForeground(UIStyler.TEXT_DARK);
        
        JPanel fieldsPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        fieldsPanel.setOpaque(false);
        
        // Scenario Name
        JPanel namePanel = createFieldPanel("Scenario Name");
        scenarioNameField = new JTextField("Scenario " + (controller.getScenarioCount() + 1));
        UIStyler.styleTextField(scenarioNameField);
        namePanel.add(scenarioNameField, BorderLayout.CENTER);
        fieldsPanel.add(namePanel);
        
        // Loan Amount
        JPanel amountPanel = createFieldPanel("Loan Amount ($)");
        loanAmountField = new JTextField("50000");
        UIStyler.styleTextField(loanAmountField);
        amountPanel.add(loanAmountField, BorderLayout.CENTER);
        fieldsPanel.add(amountPanel);
        
        // Interest Rate
        JPanel ratePanel = createFieldPanel("Interest Rate (%)");
        interestRateField = new JTextField("6.5");
        UIStyler.styleTextField(interestRateField);
        ratePanel.add(interestRateField, BorderLayout.CENTER);
        fieldsPanel.add(ratePanel);
        
        // Term Years
        JPanel termPanel = createFieldPanel("Term (Years)");
        termYearsField = new JTextField("5");
        UIStyler.styleTextField(termYearsField);
        termPanel.add(termYearsField, BorderLayout.CENTER);
        fieldsPanel.add(termPanel);
        
        // Add button
        JPanel buttonPanel = createFieldPanel("");
        JButton addButton = new JButton("+ Add Scenario");
        UIStyler.stylePrimaryButton(addButton);
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.addActionListener(e -> addScenario());
        buttonPanel.add(addButton, BorderLayout.CENTER);
        fieldsPanel.add(buttonPanel);
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFieldPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(UIStyler.SMALL_FONT);
        label.setForeground(UIStyler.TEXT_SECONDARY);
        
        panel.add(label, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        JLabel header = new JLabel("Comparison Results");
        header.setFont(UIStyler.HEADER_FONT);
        header.setForeground(UIStyler.TEXT_DARK);
        
        // Create table
        String[] columns = {
            "Scenario", "Loan Amount", "Rate", "Term", "Monthly Payment", "Total Interest", "Total Cost", "Action"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only action column is "editable"
            }
        };
        
        comparisonTable = new JTable(tableModel);
        comparisonTable.setFont(UIStyler.BODY_FONT);
        comparisonTable.setRowHeight(45);
        comparisonTable.setShowGrid(true);
        comparisonTable.setGridColor(UIStyler.BORDER_COLOR);
        comparisonTable.setSelectionBackground(new Color(219, 234, 254));
        
        // Header styling
        JTableHeader tableHeader = comparisonTable.getTableHeader();
        tableHeader.setFont(UIStyler.SUBHEADER_FONT);
        tableHeader.setBackground(UIStyler.PRIMARY_BLUE);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 45));
        
        // Custom renderer
        comparisonTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
                
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                    
                    // Check if this row is the best deal
                    List<LoanScenario> scenarios = controller.getAllScenarios();
                    if (row < scenarios.size() && scenarios.get(row).isBestDeal()) {
                        setBackground(new Color(220, 252, 231)); // Light green
                        if (column == 0) {
                            setText(value + " ⭐");
                        }
                    }
                    
                    if (column == 4) { // Monthly Payment
                        setForeground(UIStyler.PRIMARY_BLUE);
                        setFont(UIStyler.SUBHEADER_FONT);
                    } else if (column == 6) { // Total Cost
                        setForeground(UIStyler.ACCENT_GREEN);
                        setFont(UIStyler.SUBHEADER_FONT);
                    } else {
                        setForeground(UIStyler.TEXT_DARK);
                        setFont(UIStyler.BODY_FONT);
                    }
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
        
        // Action column - Delete button
        TableColumn actionColumn = comparisonTable.getColumnModel().getColumn(7);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        actionColumn.setPreferredWidth(80);
        
        // Column widths
        TableColumnModel columnModel = comparisonTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120); // Scenario
        columnModel.getColumn(1).setPreferredWidth(120); // Loan Amount
        columnModel.getColumn(2).setPreferredWidth(80);  // Rate
        columnModel.getColumn(3).setPreferredWidth(80);  // Term
        columnModel.getColumn(4).setPreferredWidth(130); // Monthly Payment
        columnModel.getColumn(5).setPreferredWidth(120); // Total Interest
        columnModel.getColumn(6).setPreferredWidth(120); // Total Cost
        
        JScrollPane scrollPane = new JScrollPane(comparisonTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        bestDealLabel = new JLabel("");
        bestDealLabel.setFont(UIStyler.HEADER_FONT);
        bestDealLabel.setForeground(UIStyler.ACCENT_GREEN);
        
        JButton clearAllButton = new JButton("Clear All Scenarios");
        UIStyler.styleSecondaryButton(clearAllButton);
        clearAllButton.addActionListener(e -> clearAllScenarios());
        
        panel.add(bestDealLabel, BorderLayout.WEST);
        panel.add(clearAllButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void addScenario() {
        // Validate
        if (ValidationUtils.isEmpty(scenarioNameField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a scenario name.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.validatePositiveNumber(loanAmountField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid loan amount.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.validateInterestRate(interestRateField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid interest rate (0-50%).", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.validatePositiveInteger(termYearsField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid term in years.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create and add scenario
        String name = scenarioNameField.getText();
        double amount = FormatUtils.parseDouble(loanAmountField.getText());
        double rate = FormatUtils.parseDouble(interestRateField.getText());
        int term = FormatUtils.parseInt(termYearsField.getText());
        
        controller.createScenario(name, amount, rate, term);
        
        // Refresh table
        refreshTable();
        
        // Reset fields for next entry
        scenarioNameField.setText("Scenario " + (controller.getScenarioCount() + 1));
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        List<LoanScenario> scenarios = controller.getAllScenarios();
        LoanScenario bestDeal = null;
        
        for (LoanScenario scenario : scenarios) {
            Object[] row = {
                scenario.getScenarioName(),
                FormatUtils.formatCurrency(scenario.getLoanAmount()),
                FormatUtils.formatRate(scenario.getInterestRate()),
                scenario.getTermYears() + " yrs",
                FormatUtils.formatCurrency(scenario.getMonthlyPayment()),
                FormatUtils.formatCurrency(scenario.getTotalInterest()),
                FormatUtils.formatCurrency(scenario.getTotalCost()),
                "Delete"
            };
            tableModel.addRow(row);
            
            if (scenario.isBestDeal()) {
                bestDeal = scenario;
            }
        }
        
        // Update best deal label
        if (bestDeal != null && scenarios.size() > 1) {
            double savings = controller.getSavingsFromBestDeal();
            bestDealLabel.setText("⭐ Best Deal: " + bestDeal.getScenarioName() + 
                " (Save " + FormatUtils.formatCurrency(savings) + ")");
        } else {
            bestDealLabel.setText("");
        }
    }
    
    private void deleteScenario(int row) {
        if (row >= 0 && row < controller.getScenarioCount()) {
            controller.removeScenario(row);
            refreshTable();
        }
    }
    
    private void clearAllScenarios() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all scenarios?",
            "Confirm Clear", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.clearScenarios();
            refreshTable();
            scenarioNameField.setText("Scenario 1");
        }
    }
    
    // Button renderer for table
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(UIStyler.SMALL_FONT);
            setForeground(UIStyler.ERROR_RED);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(UIStyler.ERROR_RED, 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("🗑 Delete");
            return this;
        }
    }
    
    // Button editor for table
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(UIStyler.SMALL_FONT);
            button.setForeground(UIStyler.ERROR_RED);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(UIStyler.ERROR_RED, 1));
            button.addActionListener(e -> {
                fireEditingStopped();
                deleteScenario(row);
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            button.setText("🗑 Delete");
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }
    }
}
