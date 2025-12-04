package com.vismera.views.admin;

import com.vismera.storage.TextFileDatabase;
import com.vismera.utils.UIStyler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main administrative interface frame.
 * Contains tabbed pane for different management modules.
 * 
 * @author Vismerá Inc.
 */
public class AdminMainFrame extends JFrame {
    
    private JTabbedPane adminTabs;
    private JLabel statusLabel;
    private JLabel connectionStatusLabel;
    
    // Management panels
    private CustomerManagementPanel customerPanel;
    private CarInventoryPanel carPanel;
    private LoanManagementPanel loanPanel;
    private PaymentTrackingPanel paymentPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    
    public AdminMainFrame() {
        initComponents();
        setupLayout();
        checkDatabaseConnection();
    }
    
    private void initComponents() {
        setTitle("Vismerá Inc. - Car Loan Administration System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        
        // Set application icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/images/app_icon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
        
        // Create tabbed pane
        adminTabs = new JTabbedPane(JTabbedPane.LEFT);
        adminTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Create management panels
        customerPanel = new CustomerManagementPanel();
        carPanel = new CarInventoryPanel();
        loanPanel = new LoanManagementPanel();
        paymentPanel = new PaymentTrackingPanel();
        reportsPanel = new ReportsPanel();
        settingsPanel = new SettingsPanel();
        
        // Add tabs with icons
        adminTabs.addTab("  Dashboard  ", createIcon("dashboard"), new DashboardPanel(), "Overview and statistics");
        adminTabs.addTab("  Customers  ", createIcon("customers"), customerPanel, "Manage customer records");
        adminTabs.addTab("  Cars  ", createIcon("cars"), carPanel, "Manage car inventory");
        adminTabs.addTab("  Loans  ", createIcon("loans"), loanPanel, "Manage loan applications");
        adminTabs.addTab("  Payments  ", createIcon("payments"), paymentPanel, "Track payment records");
        adminTabs.addTab("  Reports  ", createIcon("reports"), reportsPanel, "Generate reports and analytics");
        adminTabs.addTab("  Settings  ", createIcon("settings"), settingsPanel, "System configuration");
        
        // Status bar
        statusLabel = new JLabel("Ready");
        connectionStatusLabel = new JLabel("Checking database...");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content with tabs
        add(adminTabs, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Car Loan Administration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Vismerá Inc. - Loan Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        
        JButton newLoanBtn = createHeaderButton("New Loan", "loan_add");
        JButton newCustomerBtn = createHeaderButton("New Customer", "customer_add");
        JButton recordPaymentBtn = createHeaderButton("Record Payment", "payment_add");
        
        newLoanBtn.addActionListener(e -> showNewLoanDialog());
        newCustomerBtn.addActionListener(e -> showNewCustomerDialog());
        recordPaymentBtn.addActionListener(e -> showRecordPaymentDialog());
        
        actionsPanel.add(newCustomerBtn);
        actionsPanel.add(newLoanBtn);
        actionsPanel.add(recordPaymentBtn);
        
        panel.add(actionsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createHeaderButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        
        return button;
    }
    
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(127, 140, 141));
        
        connectionStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        connectionStatusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(connectionStatusLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private Icon createIcon(String name) {
        // Try to load icon from resources, return null if not found
        try {
            return new ImageIcon(getClass().getResource("/resources/images/" + name + ".png"));
        } catch (Exception e) {
            return null;
        }
    }
    
    private void checkDatabaseConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                // Text file database is always "connected"
                TextFileDatabase.getInstance();
                return true;
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        connectionStatusLabel.setText("✓ Data storage ready");
                        connectionStatusLabel.setForeground(new Color(39, 174, 96));
                    } else {
                        connectionStatusLabel.setText("✗ Storage error");
                        connectionStatusLabel.setForeground(new Color(231, 76, 60));
                        showDatabaseError();
                    }
                } catch (Exception e) {
                    connectionStatusLabel.setText("✗ Connection error");
                    connectionStatusLabel.setForeground(new Color(231, 76, 60));
                }
            }
        };
        worker.execute();
    }
    
    private void showDatabaseError() {
        JOptionPane.showMessageDialog(
            this,
            "Unable to connect to the database.\n" +
            "Please check your database configuration and ensure MySQL is running.\n\n" +
            "Database: car_loan_amortization_db\n" +
            "Host: localhost:3306",
            "Database Connection Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void showNewCustomerDialog() {
        CustomerFormDialog dialog = new CustomerFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            customerPanel.refreshData();
            setStatus("New customer added successfully");
        }
    }
    
    private void showNewLoanDialog() {
        LoanFormDialog dialog = new LoanFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loanPanel.refreshData();
            setStatus("New loan created successfully");
        }
    }
    
    private void showRecordPaymentDialog() {
        PaymentRecordDialog dialog = new PaymentRecordDialog(this);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            paymentPanel.refreshData();
            loanPanel.refreshData();
            setStatus("Payment recorded successfully");
        }
    }
    
    public void setStatus(String message) {
        statusLabel.setText(message);
        // Clear status after 5 seconds
        Timer timer = new Timer(5000, e -> statusLabel.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }
    
    public void navigateToTab(int index) {
        if (index >= 0 && index < adminTabs.getTabCount()) {
            adminTabs.setSelectedIndex(index);
        }
    }
    
    private void onClose() {
        // Cleanup if needed
    }
    
    /**
     * Launch the admin frame
     */
    public static void showAdminFrame() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            AdminMainFrame frame = new AdminMainFrame();
            frame.setVisible(true);
        });
    }
    
    public static void main(String[] args) {
        showAdminFrame();
    }
}
