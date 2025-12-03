package com.vismera.views.admin;

import com.vismera.controllers.CarController;
import com.vismera.models.Car;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

/**
 * Panel for managing car inventory.
 * 
 * @author Vismerá Inc.
 */
public class CarInventoryPanel extends JPanel {
    
    private final CarController carController;
    private final NumberFormat currencyFormat;
    
    private JTable carTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JLabel recordCountLabel;
    
    public CarInventoryPanel() {
        carController = CarController.getInstance();
        carController.setUseDatabase(true); // Use database mode
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
        
        JLabel titleLabel = new JLabel("Car Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.setOpaque(false);
        
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search cars...");
        searchField.addActionListener(e -> performSearch());
        
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "Sedan", "SUV", "Truck", "Sports", "Luxury", "Compact"});
        categoryFilter.addActionListener(e -> performSearch());
        
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            loadData();
        });
        
        filterPanel.add(new JLabel("Search: "));
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Category: "));
        filterPanel.add(categoryFilter);
        filterPanel.add(searchBtn);
        filterPanel.add(clearBtn);
        
        // Actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionsPanel.setOpaque(false);
        
        JButton addBtn = createButton("Add Car", new Color(46, 204, 113));
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
        
        String[] columns = {"ID", "Make", "Model", "Year", "Price", "Category", "Color", "MPG", "Available", "Created"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 8) return Boolean.class;
                return super.getColumnClass(column);
            }
        };
        
        carTable = new JTable(tableModel);
        carTable.setRowHeight(35);
        carTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        carTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        carTable.getTableHeader().setBackground(new Color(220, 220, 220));
        carTable.getTableHeader().setForeground(Color.BLACK);
        carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carTable.setShowGrid(true);
        carTable.setGridColor(new Color(230, 230, 230));
        
        // Column widths
        carTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        carTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        carTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        carTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        carTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        carTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        carTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        carTable.getColumnModel().getColumn(7).setPreferredWidth(50);
        carTable.getColumnModel().getColumn(8).setPreferredWidth(70);
        carTable.getColumnModel().getColumn(9).setPreferredWidth(100);
        
        sorter = new TableRowSorter<>(tableModel);
        carTable.setRowSorter(sorter);
        
        carTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showEditDialog();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(carTable);
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
        SwingWorker<List<Car>, Void> worker = new SwingWorker<List<Car>, Void>() {
            @Override
            protected List<Car> doInBackground() {
                return carController.getAllCars();
            }
            
            @Override
            protected void done() {
                try {
                    List<Car> cars = get();
                    populateTable(cars);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CarInventoryPanel.this,
                        "Error loading cars: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void populateTable(List<Car> cars) {
        tableModel.setRowCount(0);
        
        for (Car car : cars) {
            BigDecimal price = car.getPriceBigDecimal();
            Object[] row = {
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getYear(),
                price != null ? currencyFormat.format(price) : "$0.00",
                car.getCategory(),
                car.getColor(),
                car.getMpg(),
                car.isAvailable(),
                car.getCreatedAt() != null ? car.getCreatedAt().toLocalDate().toString() : ""
            };
            tableModel.addRow(row);
        }
        
        recordCountLabel.setText(cars.size() + " record" + (cars.size() != 1 ? "s" : ""));
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        String category = (String) categoryFilter.getSelectedItem();
        
        SwingWorker<List<Car>, Void> worker = new SwingWorker<List<Car>, Void>() {
            @Override
            protected List<Car> doInBackground() {
                List<Car> cars = carController.getAllCars();
                
                // Filter by query
                if (!query.isEmpty()) {
                    cars = cars.stream()
                        .filter(c -> c.getMake().toLowerCase().contains(query.toLowerCase()) ||
                                    c.getModel().toLowerCase().contains(query.toLowerCase()) ||
                                    String.valueOf(c.getYear()).contains(query))
                        .toList();
                }
                
                // Filter by category
                if (category != null && !"All Categories".equals(category)) {
                    cars = cars.stream()
                        .filter(c -> category.equalsIgnoreCase(c.getCategory()))
                        .toList();
                }
                
                return cars;
            }
            
            @Override
            protected void done() {
                try {
                    List<Car> cars = get();
                    populateTable(cars);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void showAddDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        CarFormDialog dialog = new CarFormDialog((Frame) window, null);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a car to edit.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = carTable.convertRowIndexToModel(selectedRow);
        int carId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        Car car = carController.getCarById(carId);
        if (car == null) {
            JOptionPane.showMessageDialog(this,
                "Car not found.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Window window = SwingUtilities.getWindowAncestor(this);
        CarFormDialog dialog = new CarFormDialog((Frame) window, car);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadData();
        }
    }
    
    private void deleteSelected() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a car to delete.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int modelRow = carTable.convertRowIndexToModel(selectedRow);
        int carId = (Integer) tableModel.getValueAt(modelRow, 0);
        String carName = tableModel.getValueAt(modelRow, 1) + " " + tableModel.getValueAt(modelRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete:\n" + carName + "?\n\n" +
            "This action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = carController.deleteCar(carId);
            if (deleted) {
                loadData();
                JOptionPane.showMessageDialog(this,
                    "Car deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not delete car.\nCar may be in an active loan.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadData();
    }
}
