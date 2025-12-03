package com.vismera.views.admin;

import com.vismera.controllers.CarController;
import com.vismera.models.Car;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Dialog for adding/editing car records.
 * 
 * @author Vismerá Inc.
 */
public class CarFormDialog extends JDialog {
    
    private final Car car;
    private boolean saved = false;
    
    private JTextField makeField;
    private JTextField modelField;
    private JSpinner yearSpinner;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;
    private JTextField colorField;
    private JSpinner mpgSpinner;
    private JTextField imagePathField;
    private JTextArea notesArea;
    private JCheckBox availableCheckBox;
    
    public CarFormDialog(Frame owner, Car car) {
        super(owner, car == null ? "Add New Car" : "Edit Car", true);
        this.car = car;
        initComponents();
        populateFields();
    }
    
    private void initComponents() {
        setSize(500, 550);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Make
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Make:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        makeField = new JTextField(25);
        formPanel.add(makeField, gbc);
        
        // Model
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Model:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        modelField = new JTextField(25);
        formPanel.add(modelField, gbc);
        
        // Year
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Year:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        SpinnerNumberModel yearModel = new SpinnerNumberModel(2024, 1990, 2030, 1);
        yearSpinner = new JSpinner(yearModel);
        JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(yearSpinner, "#");
        yearSpinner.setEditor(yearEditor);
        formPanel.add(yearSpinner, gbc);
        
        // Price
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Price:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        priceField = new JTextField(25);
        formPanel.add(priceField, gbc);
        
        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        categoryCombo = new JComboBox<>(new String[]{"Sedan", "SUV", "Truck", "Sports", "Luxury", "Compact", "Hybrid", "Electric"});
        categoryCombo.setEditable(true);
        formPanel.add(categoryCombo, gbc);
        
        // Color
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Color:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        colorField = new JTextField(25);
        formPanel.add(colorField, gbc);
        
        // MPG
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("MPG:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        SpinnerNumberModel mpgModel = new SpinnerNumberModel(25, 0, 100, 1);
        mpgSpinner = new JSpinner(mpgModel);
        formPanel.add(mpgSpinner, gbc);
        
        // Image Path
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Image Path:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imagePanel.setOpaque(false);
        imagePathField = new JTextField(20);
        JButton browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> browseImage());
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseBtn, BorderLayout.EAST);
        formPanel.add(imagePanel, gbc);
        
        // Notes
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1; gbc.weighty = 1;
        notesArea = new JTextArea(3, 25);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        formPanel.add(notesScroll, gbc);
        
        // Available
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createLabel(""), gbc);
        gbc.gridx = 1;
        availableCheckBox = new JCheckBox("Available for loan");
        availableCheckBox.setSelected(true);
        availableCheckBox.setOpaque(false);
        formPanel.add(availableCheckBox, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton saveBtn = new JButton(car == null ? "Add Car" : "Save Changes");
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
        if (car != null) {
            makeField.setText(car.getMake());
            modelField.setText(car.getModel());
            yearSpinner.setValue(car.getYear());
            priceField.setText(car.getPriceBigDecimal() != null ? car.getPriceBigDecimal().toPlainString() : "");
            categoryCombo.setSelectedItem(car.getCategory());
            colorField.setText(car.getColor());
            mpgSpinner.setValue(car.getMpg());
            imagePathField.setText(car.getImagePath());
            notesArea.setText(car.getNotes());
            availableCheckBox.setSelected(car.isAvailable());
        }
    }
    
    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void save() {
        // Validate
        String make = makeField.getText().trim();
        String model = modelField.getText().trim();
        int year = (Integer) yearSpinner.getValue();
        String priceStr = priceField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String color = colorField.getText().trim();
        int mpg = (Integer) mpgSpinner.getValue();
        String imagePath = imagePathField.getText().trim();
        String notes = notesArea.getText().trim();
        boolean available = availableCheckBox.isSelected();
        
        if (make.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Make is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            makeField.requestFocus();
            return;
        }
        
        if (model.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Model is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            modelField.requestFocus();
            return;
        }
        
        BigDecimal price;
        try {
            price = new BigDecimal(priceStr.replace(",", "").replace("$", ""));
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return;
        }
        
        // Save
        CarController controller = CarController.getInstance();
        
        if (car == null) {
            // Add new
            Car newCar = new Car();
            newCar.setMake(make);
            newCar.setModel(model);
            newCar.setYear(year);
            newCar.setPriceBigDecimal(price);
            newCar.setCategory(category != null ? category : "");
            newCar.setColor(color);
            newCar.setMpg(mpg);
            newCar.setImagePath(imagePath);
            newCar.setNotes(notes);
            newCar.setAvailable(available);
            
            int id = controller.addCar(newCar);
            if (id > 0) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Car added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add car.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Update existing
            car.setMake(make);
            car.setModel(model);
            car.setYear(year);
            car.setPriceBigDecimal(price);
            car.setCategory(category != null ? category : "");
            car.setColor(color);
            car.setMpg(mpg);
            car.setImagePath(imagePath);
            car.setNotes(notes);
            car.setAvailable(available);
            
            boolean updated = controller.updateCar(car);
            if (updated) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Car updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update car.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}
