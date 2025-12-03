package com.vismera.views.admin;

import com.vismera.controllers.PaymentController;
import com.vismera.models.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for tracking payments.
 * 
 * @author Vismerá Inc.
 */
public class PaymentTrackingPanel extends JPanel {
    
    private final PaymentController paymentController;
    private final NumberFormat currencyFormat;
    
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JLabel totalLabel;
    private JLabel recordCountLabel;
    
    public PaymentTrackingPanel() {
        paymentController = PaymentController.getInstance();
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
        
        JLabel titleLabel = new JLabel("Payment Tracking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Date filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.setOpaque(false);
        
        // Start date
        SpinnerDateModel startModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startEditor);
        // Default to first of month
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        startDateSpinner.setValue(java.util.Date.from(monthStart.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        
        // End date
        SpinnerDateModel endModel = new SpinnerDateModel();
        endDateSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endEditor);
        
        JButton filterBtn = new JButton("Filter");
        filterBtn.addActionListener(e -> loadData());
        
        JButton todayBtn = new JButton("Today");
        todayBtn.addActionListener(e -> {
            java.util.Date today = java.util.Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            startDateSpinner.setValue(today);
            endDateSpinner.setValue(today);
            loadData();
        });
        
        JButton thisMonthBtn = new JButton("This Month");
        thisMonthBtn.addActionListener(e -> {
            LocalDate now = LocalDate.now();
            LocalDate first = now.withDayOfMonth(1);
            startDateSpinner.setValue(java.util.Date.from(first.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(java.util.Date.from(now.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            loadData();
        });
        
        filterPanel.add(new JLabel("From: "));
        filterPanel.add(startDateSpinner);
        filterPanel.add(new JLabel(" To: "));
        filterPanel.add(endDateSpinner);
        filterPanel.add(filterBtn);
        filterPanel.add(todayBtn);
        filterPanel.add(thisMonthBtn);
        
        // Actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionsPanel.setOpaque(false);
        
        JButton newPaymentBtn = createButton("Record Payment", new Color(46, 204, 113));
        JButton refreshBtn = createButton("Refresh", new Color(149, 165, 166));
        JButton exportBtn = createButton("Export CSV", new Color(52, 152, 219));
        
        newPaymentBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            PaymentRecordDialog dialog = new PaymentRecordDialog((Frame) window);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadData();
            }
        });
        
        refreshBtn.addActionListener(e -> loadData());
        exportBtn.addActionListener(e -> exportToCSV());
        
        actionsPanel.add(newPaymentBtn);
        actionsPanel.add(exportBtn);
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
        
        String[] columns = {"ID", "Loan ID", "Date", "Amount", "Type", "Penalty", "Note", "Recorded By", "Recorded At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(35);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentTable.getTableHeader().setBackground(new Color(220, 220, 220));
        paymentTable.getTableHeader().setForeground(Color.BLACK);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.setShowGrid(true);
        paymentTable.setGridColor(new Color(230, 230, 230));
        
        // Column widths
        paymentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        paymentTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        paymentTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        paymentTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        paymentTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        paymentTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        paymentTable.getColumnModel().getColumn(6).setPreferredWidth(200);
        paymentTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        paymentTable.getColumnModel().getColumn(8).setPreferredWidth(130);
        
        JScrollPane scrollPane = new JScrollPane(paymentTable);
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
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(46, 204, 113));
        
        panel.add(recordCountLabel, BorderLayout.WEST);
        panel.add(totalLabel, BorderLayout.EAST);
        
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
        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();
        
        LocalDate start = startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        
        SwingWorker<List<Payment>, Void> worker = new SwingWorker<List<Payment>, Void>() {
            @Override
            protected List<Payment> doInBackground() {
                return paymentController.getPaymentsInDateRange(start, end);
            }
            
            @Override
            protected void done() {
                try {
                    List<Payment> payments = get();
                    populateTable(payments);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void populateTable(List<Payment> payments) {
        tableModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        
        for (Payment payment : payments) {
            Object[] row = {
                payment.getId(),
                payment.getLoanId(),
                payment.getPaymentDate(),
                currencyFormat.format(payment.getAmount()),
                payment.getTypeDisplayName(),
                payment.getPenaltyApplied() != null ? currencyFormat.format(payment.getPenaltyApplied()) : "$0.00",
                payment.getNote(),
                payment.getRecordedBy(),
                payment.getRecordedAt() != null ? payment.getRecordedAt().toString() : ""
            };
            tableModel.addRow(row);
            total = total.add(payment.getAmount());
        }
        
        recordCountLabel.setText(payments.size() + " record" + (payments.size() != 1 ? "s" : ""));
        totalLabel.setText("Total: " + currencyFormat.format(total));
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Payments to CSV");
        fileChooser.setSelectedFile(new java.io.File("payments_export.csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                // Header
                writer.println("ID,Loan ID,Date,Amount,Type,Penalty,Note,Recorded By,Recorded At");
                
                // Data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        if (j > 0) line.append(",");
                        Object value = tableModel.getValueAt(i, j);
                        String str = value != null ? value.toString() : "";
                        // Escape commas and quotes
                        if (str.contains(",") || str.contains("\"")) {
                            str = "\"" + str.replace("\"", "\"\"") + "\"";
                        }
                        line.append(str);
                    }
                    writer.println(line.toString());
                }
                
                JOptionPane.showMessageDialog(this,
                    "Payments exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (java.io.FileNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting file: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadData();
    }
}
