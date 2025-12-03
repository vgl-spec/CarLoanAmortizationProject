package com.vismera.views.admin;

import com.vismera.controllers.CustomerController;
import com.vismera.models.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing customer records.
 * 
 * @author Vismerá Inc.
 */
public class CustomerManagementPanel extends JPanel {
    
    private final CustomerController customerController;
    
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JLabel recordCountLabel;
    
    public CustomerManagementPanel() {
        customerController = CustomerController.getInstance();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Table panel
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Footer panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setOpaque(false);
        
        searchField = new JTextField(25);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, phone, or email...");
        searchField.addActionListener(e -> performSearch());
        
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        
        // Actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionsPanel.setOpaque(false);
        
        JButton addBtn = createButton("Add Customer", new Color(46, 204, 113));
        JButton editBtn = createButton("Edit", new Color(52, 152, 219));
        JButton deleteBtn = createButton("Delete", new Color(231, 76, 60));
        JButton refreshBtn = createButton("Refresh", new Color(149, 165, 166));
        
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadData());
        
        actionsPanel.add(addBtn);
        actionsPanel.add(editBtn);
        actionsPanel.add(deleteBtn);
        actionsPanel.add(refreshBtn);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(actionsPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Table columns
        String[] columns = {"ID", "Full Name", "Contact Number", "Email", "Address", "Created"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(35);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.getTableHeader().setBackground(new Color(220, 220, 220));
        customerTable.getTableHeader().setForeground(Color.BLACK);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setShowGrid(true);
        customerTable.setGridColor(new Color(230, 230, 230));
        
        // Column widths
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        // Row sorter
        sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);
        
        // Double-click to edit
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditDialog();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
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
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
        SwingWorker<List<Customer>, Void> worker = new SwingWorker<List<Customer>, Void>() {
            @Override
            protected List<Customer> doInBackground() {
                return customerController.getAllCustomers();
            }
            
            @Override
            protected void done() {
                try {
                    List<Customer> customers = get();
                    populateTable(customers);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CustomerManagementPanel.this,
                        "Error loading customers: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void populateTable(List<Customer> customers) {
        tableModel.setRowCount(0);
        
        for (Customer customer : customers) {
            Object[] row = {
                customer.getId(),
                customer.getFullName(),
                customer.getContactNumber(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreatedAt() != null ? customer.getCreatedAt().toLocalDate().toString() : ""
            };
            tableModel.addRow(row);
        }
        
        recordCountLabel.setText(customers.size() + " record" + (customers.size() != 1 ? "s" : ""));
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadData();
            return;
        }
        
        SwingWorker<List<Customer>, Void> worker = new SwingWorker<List<Customer>, Void>() {
            @Override
            protected List<Customer> doInBackground() {
                return customerController.searchCustomers(query);
            }
            
            @Override
            protected void done() {
                try {
                    List<Customer> customers = get();
                    populateTable(customers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void showAddDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        CustomerFormDialog dialog = new CustomerFormDialog((Frame) window, null);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to edit.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = customerTable.convertRowIndexToModel(selectedRow);
        int customerId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        Customer customer = customerController.getCustomerById(customerId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this,
                "Customer not found.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Window window = SwingUtilities.getWindowAncestor(this);
        CustomerFormDialog dialog = new CustomerFormDialog((Frame) window, customer);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }
    
    private void deleteSelected() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to delete.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = customerTable.convertRowIndexToModel(selectedRow);
        int customerId = (Integer) tableModel.getValueAt(modelRow, 0);
        String customerName = (String) tableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete customer:\n" + customerName + "?\n\n" +
            "This action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = customerController.deleteCustomer(customerId);
            if (deleted) {
                loadData();
                JOptionPane.showMessageDialog(this,
                    "Customer deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not delete customer.\nCustomer may have active loans.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadData();
    }
}
