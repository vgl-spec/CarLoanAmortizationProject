package com.vismera.views;

import com.vismera.models.Car;
import com.vismera.controllers.CarController;
import com.vismera.utils.FormatUtils;
import com.vismera.utils.UIStyler;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Cars Panel - Page 1: Car Selection
 * @author Vismerá Inc.
 */
public class CarsPanel extends JPanel {
    
    private JTextField searchField;
    private JPanel carsGridPanel;
    private JPanel selectedCarBanner;
    private JLabel selectedCarLabel;
    private JButton continueButton;
    private Car selectedCar;
    private MainFrame parentFrame;
    
    public CarsPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        initComponents();
        loadCars();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UIStyler.BACKGROUND_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top section with search
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        searchField = new JTextField();
        searchField.setFont(UIStyler.BODY_FONT);
        searchField.setBorder(null);
        searchField.setText("Search by make, model, or type...");
        searchField.setForeground(UIStyler.TEXT_SECONDARY);
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search by make, model, or type...")) {
                    searchField.setText("");
                    searchField.setForeground(UIStyler.TEXT_DARK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search by make, model, or type...");
                    searchField.setForeground(UIStyler.TEXT_SECONDARY);
                }
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterCars();
            }
        });
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setPreferredSize(new Dimension(400, 45));
        
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Selected car banner (hidden initially)
        selectedCarBanner = new JPanel(new BorderLayout(10, 0));
        selectedCarBanner.setBackground(UIStyler.PRIMARY_BLUE);
        selectedCarBanner.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        selectedCarBanner.setVisible(false);
        
        JLabel checkIcon = new JLabel("✓");
        checkIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        checkIcon.setForeground(Color.WHITE);
        
        selectedCarLabel = new JLabel("No car selected");
        selectedCarLabel.setFont(UIStyler.BODY_FONT);
        selectedCarLabel.setForeground(Color.WHITE);
        
        JLabel bannerHint = new JLabel("Click \"Continue to Calculator\" to finance this vehicle");
        bannerHint.setFont(UIStyler.SMALL_FONT);
        bannerHint.setForeground(new Color(255, 255, 255, 200));
        
        JPanel bannerTextPanel = new JPanel();
        bannerTextPanel.setLayout(new BoxLayout(bannerTextPanel, BoxLayout.Y_AXIS));
        bannerTextPanel.setOpaque(false);
        bannerTextPanel.add(selectedCarLabel);
        bannerTextPanel.add(bannerHint);
        
        selectedCarBanner.add(checkIcon, BorderLayout.WEST);
        selectedCarBanner.add(bannerTextPanel, BorderLayout.CENTER);
        
        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
        northPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(selectedCarBanner, BorderLayout.SOUTH);
        
        add(northPanel, BorderLayout.NORTH);
        
        // Cars grid
        carsGridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        carsGridPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        JScrollPane scrollPane = new JScrollPane(carsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UIStyler.BACKGROUND_LIGHT);
        scrollPane.getViewport().setBackground(UIStyler.BACKGROUND_LIGHT);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with continue button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyler.BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel selectedInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedInfoPanel.setOpaque(false);
        
        continueButton = new JButton("Continue to Calculator →");
        UIStyler.stylePrimaryButton(continueButton);
        continueButton.setPreferredSize(new Dimension(220, 45));
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> continueToCalculator());
        
        bottomPanel.add(selectedInfoPanel, BorderLayout.WEST);
        bottomPanel.add(continueButton, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadCars() {
        List<Car> cars = CarController.getInstance().getAllCars();
        displayCars(cars);
    }
    
    private void filterCars() {
        String query = searchField.getText();
        if (query.equals("Search by make, model, or type...")) {
            query = "";
        }
        List<Car> cars = CarController.getInstance().searchCars(query);
        displayCars(cars);
    }
    
    private void displayCars(List<Car> cars) {
        carsGridPanel.removeAll();
        
        for (Car car : cars) {
            JPanel carCard = createCarCard(car);
            carsGridPanel.add(carCard);
        }
        
        carsGridPanel.revalidate();
        carsGridPanel.repaint();
    }
    
    private JPanel createCarCard(Car car) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Image panel with year badge
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        imagePanel.setPreferredSize(new Dimension(300, 180));
        
        // Load car image
        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        imageLabel.setForeground(UIStyler.TEXT_SECONDARY);
        
        // Try to load the actual image
        String imagePath = car.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                // Load from resources folder
                URL imageUrl = getClass().getResource("/resources/images/" + imagePath);
                if (imageUrl != null) {
                    BufferedImage originalImage = ImageIO.read(imageUrl);
                    // Scale image to fit panel
                    Image scaledImage = originalImage.getScaledInstance(290, 170, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText(""); // Clear placeholder text
                } else {
                    // Fallback to initials if image not found
                    imageLabel.setText(car.getMake().charAt(0) + "" + car.getModel().charAt(0));
                }
            } catch (Exception e) {
                // Fallback to initials on error
                imageLabel.setText(car.getMake().charAt(0) + "" + car.getModel().charAt(0));
            }
        } else {
            // No image path, show initials
            imageLabel.setText(car.getMake().charAt(0) + "" + car.getModel().charAt(0));
        }
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Year badge
        JLabel yearBadge = new JLabel(" " + car.getYear() + " ");
        yearBadge.setFont(UIStyler.SMALL_FONT);
        yearBadge.setBackground(Color.WHITE);
        yearBadge.setOpaque(true);
        yearBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        
        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(yearBadge);
        imagePanel.add(badgeWrapper, BorderLayout.SOUTH);
        
        // Selection indicator
        JLabel selectIndicator = new JLabel();
        selectIndicator.setName("selectIndicator");
        if (selectedCar != null && selectedCar.getId() == car.getId()) {
            selectIndicator.setText("✓");
            selectIndicator.setForeground(Color.WHITE);
            selectIndicator.setBackground(UIStyler.PRIMARY_BLUE);
            selectIndicator.setOpaque(true);
            selectIndicator.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            card.setBorder(BorderFactory.createLineBorder(UIStyler.PRIMARY_BLUE, 3));
        }
        
        JPanel indicatorWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        indicatorWrapper.setOpaque(false);
        indicatorWrapper.add(selectIndicator);
        imagePanel.add(indicatorWrapper, BorderLayout.NORTH);
        
        card.add(imagePanel, BorderLayout.NORTH);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel nameLabel = new JLabel(car.getFullName());
        nameLabel.setFont(UIStyler.SUBHEADER_FONT);
        nameLabel.setForeground(UIStyler.TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel categoryLabel = new JLabel(car.getCategory());
        categoryLabel.setFont(UIStyler.SMALL_FONT);
        categoryLabel.setForeground(UIStyler.TEXT_SECONDARY);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Tags panel
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagsPanel.setBackground(Color.WHITE);
        tagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel mpgTag = createTag(car.getMpg() + " MPG");
        JLabel colorTag = createTag(car.getColor());
        
        tagsPanel.add(mpgTag);
        tagsPanel.add(colorTag);
        
        // Price
        JLabel priceLabel = new JLabel(FormatUtils.formatCurrency(car.getPrice()));
        priceLabel.setFont(UIStyler.HEADER_FONT);
        priceLabel.setForeground(UIStyler.PRIMARY_BLUE);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(tagsPanel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(priceLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Click handler
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCar(car);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedCar == null || selectedCar.getId() != car.getId()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIStyler.PRIMARY_BLUE, 2),
                        BorderFactory.createEmptyBorder(0, 0, 14, 0)
                    ));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedCar == null || selectedCar.getId() != car.getId()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(0, 0, 15, 0)
                    ));
                }
            }
        });
        
        return card;
    }
    
    private JLabel createTag(String text) {
        JLabel tag = new JLabel(text);
        tag.setFont(UIStyler.SMALL_FONT);
        tag.setForeground(UIStyler.TEXT_SECONDARY);
        tag.setBackground(UIStyler.BACKGROUND_LIGHT);
        tag.setOpaque(true);
        tag.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return tag;
    }
    
    private void selectCar(Car car) {
        this.selectedCar = car;
        
        // Update banner
        selectedCarLabel.setText(car.getFullName() + " Selected");
        selectedCarBanner.setVisible(true);
        
        // Enable continue button
        continueButton.setEnabled(true);
        
        // Refresh display
        filterCars();
    }
    
    private void continueToCalculator() {
        if (selectedCar != null && parentFrame != null) {
            parentFrame.showCalculatePanel(selectedCar);
        }
    }
    
    public Car getSelectedCar() {
        return selectedCar;
    }
    
    public void clearSelection() {
        selectedCar = null;
        selectedCarBanner.setVisible(false);
        continueButton.setEnabled(false);
        filterCars();
    }
}
