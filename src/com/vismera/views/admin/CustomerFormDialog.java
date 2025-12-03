package com.vismera.views.admin;

import com.vismera.controllers.CustomerController;
import com.vismera.models.Customer;
import com.vismera.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding/editing customer records.
 * 
 * @author Vismerá Inc.
 */
public class CustomerFormDialog extends JDialog {
    
    private final Customer customer;
    private boolean saved = false;
    
    private JTextField fullNameField;
    private JTextField contactNumberField;
    private JTextField emailField;
    private JTextArea addressArea;
    
    public CustomerFormDialog(Frame owner, Customer customer) {
        super(owner, customer == null ? "Add New Customer" : "Edit Customer", true);
        this.customer = customer;
        initComponents();
        populateFields();
    }
    
    private void initComponents() {
        setSize(450, 400);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Full Name:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        fullNameField = new JTextField(25);
        formPanel.add(fullNameField, gbc);
        
        // Contact Number
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Contact Number:*"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        contactNumberField = new JTextField(25);
        formPanel.add(contactNumberField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        emailField = new JTextField(25);
        formPanel.add(emailField, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Address:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1; gbc.weighty = 1;
        addressArea = new JTextArea(4, 25);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        formPanel.add(addressScroll, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton saveBtn = new JButton(customer == null ? "Add Customer" : "Save Changes");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> save());
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Required fields note
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        noteLabel.setForeground(new Color(127, 140, 141));
        mainPanel.add(noteLabel, BorderLayout.NORTH);
        
        setContentPane(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }
    
    private void populateFields() {
        if (customer != null) {
            fullNameField.setText(customer.getFullName());
            contactNumberField.setText(customer.getContactNumber());
            emailField.setText(customer.getEmail());
            addressArea.setText(customer.getAddress());
        }
    }
    
    private void save() {
        // Validate
        String fullName = fullNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();
        
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Full name is required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            fullNameField.requestFocus();
            return;
        }
        
        if (contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Contact number is required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            contactNumberField.requestFocus();
            return;
        }
        
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return;
        }
        
        // Save
        CustomerController controller = CustomerController.getInstance();
        
        if (customer == null) {
            // Add new
            Customer newCustomer = new Customer();
            newCustomer.setFullName(fullName);
            newCustomer.setContactNumber(contactNumber);
            newCustomer.setEmail(email);
            newCustomer.setAddress(address);
            
            int id = controller.addCustomer(newCustomer);
            if (id > 0) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                    "Customer added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to add customer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Update existing
            customer.setFullName(fullName);
            customer.setContactNumber(contactNumber);
            customer.setEmail(email);
            customer.setAddress(address);
            
            boolean updated = controller.updateCustomer(customer);
            if (updated) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                    "Customer updated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update customer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
