package com.vismera.views.admin;

import com.vismera.controllers.SettingsController;
import com.vismera.models.AdminSetting;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Panel for managing system settings.
 * 
 * @author Vismerá Inc.
 */
public class SettingsPanel extends JPanel {
    
    private final SettingsController settingsController;
    
    private JTable settingsTable;
    private DefaultTableModel tableModel;
    
    // Quick access fields
    private JTextField defaultAprField;
    private JTextField defaultTermField;
    private JTextField defaultPenaltyRateField;
    private JTextField defaultGracePeriodField;
    private JComboBox<String> penaltyTypeCombo;
    
    public SettingsPanel() {
        settingsController = SettingsController.getInstance();
        initComponents();
        loadSettings();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content with quick settings and full table
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        
        contentPanel.add(createQuickSettingsPanel());
        contentPanel.add(createAllSettingsPanel());
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer with save button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        
        JButton resetBtn = new JButton("Reset to Defaults");
        resetBtn.addActionListener(e -> resetToDefaults());
        
        JButton saveBtn = new JButton("Save All Settings");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> saveSettings());
        
        footerPanel.add(resetBtn);
        footerPanel.add(saveBtn);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createQuickSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel header = new JLabel("Loan Default Settings");
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(header, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Default APR
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Default APR (%):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        defaultAprField = new JTextField(15);
        formPanel.add(defaultAprField, gbc);
        
        // Default Term
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Default Term (Months):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        defaultTermField = new JTextField(15);
        formPanel.add(defaultTermField, gbc);
        
        // Default Penalty Rate
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Default Penalty Rate (%):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        defaultPenaltyRateField = new JTextField(15);
        formPanel.add(defaultPenaltyRateField, gbc);
        
        // Default Grace Period
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Grace Period (Days):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        defaultGracePeriodField = new JTextField(15);
        formPanel.add(defaultGracePeriodField, gbc);
        
        // Penalty Type
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Penalty Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        penaltyTypeCombo = new JComboBox<>(new String[]{"percentage", "fixed"});
        formPanel.add(penaltyTypeCombo, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAllSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel header = new JLabel("All Settings");
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(header, BorderLayout.NORTH);
        
        String[] columns = {"Setting Key", "Value", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Only value column is editable
            }
        };
        
        settingsTable = new JTable(tableModel);
        settingsTable.setRowHeight(30);
        settingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        settingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(settingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add/Remove buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        
        JButton addBtn = new JButton("Add Setting");
        addBtn.addActionListener(e -> addSetting());
        
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.addActionListener(e -> removeSelectedSetting());
        
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadSettings() {
        // Load into quick access fields
        defaultAprField.setText(settingsController.getSetting("default_apr", "5.99"));
        defaultTermField.setText(settingsController.getSetting("default_term_months", "36"));
        defaultPenaltyRateField.setText(settingsController.getSetting("default_penalty_rate", "5.00"));
        defaultGracePeriodField.setText(settingsController.getSetting("default_grace_period", "5"));
        
        String penaltyType = settingsController.getSetting("default_penalty_type", "percentage");
        penaltyTypeCombo.setSelectedItem(penaltyType);
        
        // Load all settings into table
        tableModel.setRowCount(0);
        Map<String, String> allSettings = settingsController.getAllSettings();
        
        for (Map.Entry<String, String> entry : allSettings.entrySet()) {
            tableModel.addRow(new Object[]{
                entry.getKey(),
                entry.getValue(),
                getSettingDescription(entry.getKey())
            });
        }
    }
    
    private String getSettingDescription(String key) {
        return switch (key) {
            case "default_apr" -> "Default APR for new loans";
            case "default_term_months" -> "Default loan term in months";
            case "default_penalty_rate" -> "Default late payment penalty rate";
            case "default_grace_period" -> "Days before penalty applies";
            case "default_penalty_type" -> "percentage or fixed amount";
            case "company_name" -> "Company display name";
            case "company_address" -> "Company address for documents";
            case "company_phone" -> "Company contact phone";
            case "company_email" -> "Company contact email";
            default -> "";
        };
    }
    
    private void saveSettings() {
        // Save quick access settings
        settingsController.saveSetting("default_apr", defaultAprField.getText().trim());
        settingsController.saveSetting("default_term_months", defaultTermField.getText().trim());
        settingsController.saveSetting("default_penalty_rate", defaultPenaltyRateField.getText().trim());
        settingsController.saveSetting("default_grace_period", defaultGracePeriodField.getText().trim());
        settingsController.saveSetting("default_penalty_type", (String) penaltyTypeCombo.getSelectedItem());
        
        // Save table settings
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String key = (String) tableModel.getValueAt(i, 0);
            String value = (String) tableModel.getValueAt(i, 1);
            if (key != null && !key.trim().isEmpty()) {
                settingsController.saveSetting(key.trim(), value != null ? value.trim() : "");
            }
        }
        
        JOptionPane.showMessageDialog(this,
            "Settings saved successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void resetToDefaults() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all settings to defaults?\n\n" +
            "This will restore the original system settings.",
            "Confirm Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            settingsController.loadDefaultSettings();
            loadSettings();
            
            JOptionPane.showMessageDialog(this,
                "Settings reset to defaults.",
                "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void addSetting() {
        String key = JOptionPane.showInputDialog(this, "Enter setting key:", "Add Setting", JOptionPane.PLAIN_MESSAGE);
        if (key != null && !key.trim().isEmpty()) {
            String value = JOptionPane.showInputDialog(this, "Enter setting value:", "Add Setting", JOptionPane.PLAIN_MESSAGE);
            if (value != null) {
                tableModel.addRow(new Object[]{key.trim(), value.trim(), ""});
            }
        }
    }
    
    private void removeSelectedSetting() {
        int selectedRow = settingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String key = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remove setting: " + key + "?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a setting to remove.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
