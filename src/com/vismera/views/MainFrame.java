package com.vismera.views;

import com.vismera.models.Car;
import com.vismera.utils.UIStyler;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main Application Frame with navigation
 * @author Vismerá Inc.
 */
public class MainFrame extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Panels
    private CarsPanel carsPanel;
    private CalculatePanel calculatePanel;
    
    // Navigation buttons
    private JButton calculateNavBtn;
    private JButton carsNavBtn;
    
    private String currentPanel = "cars";
    
    public MainFrame() {
        super("Vismerá Inc. - Auto Loan Calculator Pro");
        initComponents();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Main layout
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyler.BACKGROUND_LIGHT);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        // Create panels
        carsPanel = new CarsPanel(this);
        calculatePanel = new CalculatePanel(this);
                
        contentPanel.add(carsPanel, "cars");
        contentPanel.add(calculatePanel, "calculate");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show cars panel by default
        showPanel("cars");
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyler.BORDER_COLOR));
        
        // Top section with logo and title
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));
        
        // Logo and title
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        logoPanel.setOpaque(false);
        
        JLabel logoIcon = new JLabel("🔗");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        logoIcon.setForeground(UIStyler.PRIMARY_BLUE);
        
        JLabel titleLabel = new JLabel("Auto Loan Calculator Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(UIStyler.TEXT_DARK);
        
        logoPanel.add(logoIcon);
        logoPanel.add(titleLabel);
        
        JLabel taglineLabel = new JLabel("Smart financing for your dream car with advanced amortization analysis");
        taglineLabel.setFont(UIStyler.BODY_FONT);
        taglineLabel.setForeground(UIStyler.TEXT_SECONDARY);
        taglineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topSection.add(logoPanel, BorderLayout.NORTH);
        topSection.add(taglineLabel, BorderLayout.SOUTH);
        
        // Navigation section
        JPanel navSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        navSection.setBackground(Color.WHITE);
        navSection.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        // Navigation buttons container with rounded border
        JPanel navContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        navContainer.setBackground(UIStyler.BACKGROUND_LIGHT);
        navContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        
        calculateNavBtn = createNavButton("📊 Calculate", "calculate");
        carsNavBtn = createNavButton("🚗 Cars", "cars");
        
        navContainer.add(calculateNavBtn);
        navContainer.add(carsNavBtn);
        
        navSection.add(navContainer);
        
        header.add(topSection, BorderLayout.NORTH);
        header.add(navSection, BorderLayout.SOUTH);
        
        return header;
    }
    
    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFont(UIStyler.BUTTON_FONT);
        button.setPreferredSize(new Dimension(130, 40));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Default style (inactive)
        button.setBackground(UIStyler.BACKGROUND_LIGHT);
        button.setForeground(UIStyler.TEXT_SECONDARY);
        
        button.addActionListener(e -> showPanel(panelName));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!currentPanel.equals(panelName)) {
                    button.setBackground(Color.WHITE);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!currentPanel.equals(panelName)) {
                    button.setBackground(UIStyler.BACKGROUND_LIGHT);
                }
            }
        });
        
        return button;
    }
    
    private void updateNavButtons() {
        // Reset all buttons
        calculateNavBtn.setBackground(UIStyler.BACKGROUND_LIGHT);
        calculateNavBtn.setForeground(UIStyler.TEXT_SECONDARY);
        
        carsNavBtn.setBackground(UIStyler.BACKGROUND_LIGHT);
        carsNavBtn.setForeground(UIStyler.TEXT_SECONDARY);
        
        // Highlight active button
        JButton activeBtn = null;
        switch (currentPanel) {
            case "calculate":
                activeBtn = calculateNavBtn;
                break;
            case "cars":
                activeBtn = carsNavBtn;
                break;
        }
        
        if (activeBtn != null) {
            activeBtn.setBackground(UIStyler.PRIMARY_BLUE);
            activeBtn.setForeground(Color.WHITE);
        }
    }
    
    public void showPanel(String panelName) {
        currentPanel = panelName;
        cardLayout.show(contentPanel, panelName);
        updateNavButtons();
    }
    
    public void showCalculatePanel(Car selectedCar) {
        calculatePanel.setSelectedCar(selectedCar);
        showPanel("calculate");
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
        }
        
        // Customize Nimbus colors
        UIManager.put("control", UIStyler.BACKGROUND_LIGHT);
        UIManager.put("nimbusBase", UIStyler.PRIMARY_BLUE);
        UIManager.put("nimbusBlueGrey", UIStyler.BACKGROUND_LIGHT);
        
        // Launch application
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
