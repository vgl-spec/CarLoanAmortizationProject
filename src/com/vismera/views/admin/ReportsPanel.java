package com.vismera.views.admin;

import com.vismera.controllers.ReportController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Panel for generating and viewing reports.
 * 
 * @author Vismerá Inc.
 */
public class ReportsPanel extends JPanel {
    
    private final ReportController reportController;
    private final NumberFormat currencyFormat;
    
    private JComboBox<String> reportTypeCombo;
    private JPanel reportContentPanel;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    
    public ReportsPanel() {
        reportController = ReportController.getInstance();
        currencyFormat = NumberFormat.getCurrencyInstance();
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Report selection
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectPanel.setOpaque(false);
        
        reportTypeCombo = new JComboBox<>(new String[]{
            "Select Report...",
            "All Loans Report",
            "Active Loans Report",
            "Overdue Loans Report",
            "Monthly Summary",
            "Payments Report"
        });
        reportTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JButton generateBtn = createButton("Generate Report", new Color(52, 152, 219));
        generateBtn.addActionListener(e -> generateReport());
        
        JButton exportBtn = createButton("Export to CSV", new Color(46, 204, 113));
        exportBtn.addActionListener(e -> exportReport());
        
        selectPanel.add(new JLabel("Report Type: "));
        selectPanel.add(reportTypeCombo);
        selectPanel.add(generateBtn);
        selectPanel.add(exportBtn);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(selectPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        reportContentPanel = new JPanel(new BorderLayout());
        reportContentPanel.setBackground(Color.WHITE);
        reportContentPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Default placeholder
        JLabel placeholder = new JLabel("Select a report type and click 'Generate Report'");
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        placeholder.setForeground(new Color(149, 165, 166));
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        reportContentPanel.add(placeholder, BorderLayout.CENTER);
        
        return reportContentPanel;
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
    
    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        if (reportType == null || reportType.startsWith("Select")) {
            JOptionPane.showMessageDialog(this,
                "Please select a report type.",
                "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        reportContentPanel.removeAll();
        
        switch (reportType) {
            case "All Loans Report":
                generateLoansReport(null);
                break;
            case "Active Loans Report":
                generateLoansReport("active");
                break;
            case "Overdue Loans Report":
                generateOverdueReport();
                break;
            case "Monthly Summary":
                generateMonthlySummary();
                break;
            case "Payments Report":
                generatePaymentsReport();
                break;
        }
        
        reportContentPanel.revalidate();
        reportContentPanel.repaint();
    }
    
    private void generateLoansReport(String status) {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return reportController.getLoansReport(status);
            }
            
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> report = get();
                    displayLoansReport(report, status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void displayLoansReport(List<Map<String, Object>> report, String status) {
        String[] columns = {"ID", "Customer", "Car", "Principal", "APR%", "Term", "Monthly", "Start Date", "Status", "Paid", "Remaining", "Progress"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Map<String, Object> row : report) {
            Object[] data = {
                row.get("id"),
                row.get("customer"),
                row.get("car"),
                currencyFormat.format(row.get("principal")),
                row.get("apr") + "%",
                row.get("termMonths") + " mo",
                currencyFormat.format(row.get("monthlyPayment")),
                row.get("startDate"),
                row.get("status"),
                currencyFormat.format(row.get("totalPaid")),
                currencyFormat.format(row.get("remainingBalance")),
                String.format("%.1f%%", row.get("progressPercent"))
            };
            tableModel.addRow(data);
        }
        
        reportTable = new JTable(tableModel);
        setupTable(reportTable);
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(null);
        
        String title = status == null ? "All Loans Report" : status.substring(0,1).toUpperCase() + status.substring(1) + " Loans Report";
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel header = new JLabel(title + " (" + report.size() + " records)");
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        reportContentPanel.add(panel, BorderLayout.CENTER);
    }
    
    private void generateOverdueReport() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return reportController.getOverdueLoansReport();
            }
            
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> report = get();
                    displayOverdueReport(report);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void displayOverdueReport(List<Map<String, Object>> report) {
        String[] columns = {"Loan ID", "Customer", "Phone", "Car", "Overdue Periods", "Overdue Amount", "Oldest Due Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Map<String, Object> row : report) {
            Object[] data = {
                row.get("loanId"),
                row.get("customer"),
                row.get("customerPhone"),
                row.get("car"),
                row.get("overdueCount"),
                currencyFormat.format(row.get("overdueAmount")),
                row.get("oldestDueDate")
            };
            tableModel.addRow(data);
        }
        
        reportTable = new JTable(tableModel);
        setupTable(reportTable);
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel header = new JLabel("Overdue Loans Report (" + report.size() + " loans with overdue payments)");
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(new Color(231, 76, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        reportContentPanel.add(panel, BorderLayout.CENTER);
    }
    
    private void generateMonthlySummary() {
        YearMonth currentMonth = YearMonth.now();
        
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() {
                return reportController.getMonthlySummary(currentMonth.getYear(), currentMonth.getMonthValue());
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> summary = get();
                    displayMonthlySummary(summary);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void displayMonthlySummary(Map<String, Object> summary) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Monthly Summary: " + summary.get("monthName"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, gbc);
        gbc.gridwidth = 1;
        
        // Stats
        int row = 1;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createStatCard("Total Payments Received", 
            currencyFormat.format(summary.get("totalPayments")), 
            new Color(46, 204, 113)), gbc);
        
        gbc.gridx = 1;
        panel.add(createStatCard("Number of Payments", 
            String.valueOf(summary.get("paymentCount")), 
            new Color(52, 152, 219)), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createStatCard("Total Penalties Collected", 
            currencyFormat.format(summary.get("totalPenalties")), 
            new Color(231, 76, 60)), gbc);
        
        reportContentPanel.add(panel, BorderLayout.CENTER);
    }
    
    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(new Color(127, 140, 141));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(nameLabel);
        
        return card;
    }
    
    private void generatePaymentsReport() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);
        
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return reportController.getPaymentsReport(start, end);
            }
            
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> report = get();
                    displayPaymentsReport(report);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void displayPaymentsReport(List<Map<String, Object>> report) {
        String[] columns = {"ID", "Loan", "Customer", "Car", "Date", "Amount", "Type", "Penalty"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> row : report) {
            Object[] data = {
                row.get("id"),
                row.get("loanId"),
                row.get("customerName"),
                row.get("carName"),
                row.get("paymentDate"),
                currencyFormat.format(row.get("amount")),
                row.get("type"),
                currencyFormat.format(row.get("penaltyApplied"))
            };
            tableModel.addRow(data);
            total = total.add((BigDecimal) row.get("amount"));
        }
        
        reportTable = new JTable(tableModel);
        setupTable(reportTable);
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel header = new JLabel("Payments Report (Last 30 Days) - Total: " + currencyFormat.format(total));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        reportContentPanel.add(panel, BorderLayout.CENTER);
    }
    
    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
    }
    
    private void exportReport() {
        if (tableModel == null || tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No report data to export. Please generate a report first.",
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report to CSV");
        fileChooser.setSelectedFile(new java.io.File("report_export.csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                // Header
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (i > 0) header.append(",");
                    header.append(tableModel.getColumnName(i));
                }
                writer.println(header.toString());
                
                // Data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        if (j > 0) line.append(",");
                        Object value = tableModel.getValueAt(i, j);
                        String str = value != null ? value.toString() : "";
                        if (str.contains(",") || str.contains("\"")) {
                            str = "\"" + str.replace("\"", "\"\"") + "\"";
                        }
                        line.append(str);
                    }
                    writer.println(line.toString());
                }
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + file.getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (java.io.FileNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting file: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
