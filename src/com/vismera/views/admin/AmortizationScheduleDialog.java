package com.vismera.views.admin;

import com.vismera.dao.AmortizationRowDAO;
import com.vismera.dao.LoanDAO;
import com.vismera.models.AmortizationRow;
import com.vismera.models.Loan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for displaying amortization schedule.
 * 
 * @author Vismerá Inc.
 */
public class AmortizationScheduleDialog extends JDialog {
    
    private final int loanId;
    private final NumberFormat currencyFormat;
    
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;
    
    public AmortizationScheduleDialog(Frame owner, int loanId) {
        super(owner, "Amortization Schedule - Loan #" + loanId, true);
        this.loanId = loanId;
        this.currencyFormat = NumberFormat.getCurrencyInstance();
        initComponents();
        loadSchedule();
    }
    
    private void initComponents() {
        setSize(900, 600);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Amortization Schedule - Loan #" + loanId);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        summaryLabel = new JLabel(" ");
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryLabel.setForeground(new Color(127, 140, 141));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"#", "Due Date", "Opening Balance", "Payment", "Principal", "Interest", "Penalty", "Closing Balance", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        scheduleTable = new JTable(tableModel);
        scheduleTable.setRowHeight(30);
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        scheduleTable.getTableHeader().setBackground(new Color(220, 220, 220));
        scheduleTable.getTableHeader().setForeground(Color.BLACK);
        scheduleTable.setShowGrid(true);
        scheduleTable.setGridColor(new Color(230, 230, 230));
        
        // Column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        
        // Custom renderer for status column
        scheduleTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if ("Paid".equals(status)) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else if ("Overdue".equals(status)) {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                } else {
                    setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer with summary and buttons
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        footerPanel.add(summaryLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.addActionListener(e -> exportToCSV());
        
        JButton printBtn = new JButton("Print");
        printBtn.addActionListener(e -> printSchedule());
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(exportBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void loadSchedule() {
        AmortizationRowDAO rowDAO = AmortizationRowDAO.getInstance();
        List<AmortizationRow> rows = rowDAO.findByLoanId(loanId);
        
        LocalDate today = LocalDate.now();
        int paidCount = 0;
        int overdueCount = 0;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalRemaining = BigDecimal.ZERO;
        
        tableModel.setRowCount(0);
        
        for (AmortizationRow row : rows) {
            String status;
            if (row.isPaid()) {
                status = "Paid";
                paidCount++;
                totalPaid = totalPaid.add(row.getScheduledPayment());
            } else if (row.getDueDate().isBefore(today)) {
                status = "Overdue";
                overdueCount++;
                totalRemaining = totalRemaining.add(row.getScheduledPayment());
            } else {
                status = "Pending";
                totalRemaining = totalRemaining.add(row.getScheduledPayment());
            }
            
            Object[] tableRow = {
                row.getPeriodIndex(),
                row.getDueDate(),
                currencyFormat.format(row.getOpeningBalance()),
                currencyFormat.format(row.getScheduledPayment()),
                currencyFormat.format(row.getPrincipalPaid()),
                currencyFormat.format(row.getInterestPaid()),
                row.getPenaltyAmount() != null ? currencyFormat.format(row.getPenaltyAmount()) : "$0.00",
                currencyFormat.format(row.getClosingBalance()),
                status
            };
            tableModel.addRow(tableRow);
        }
        
        summaryLabel.setText(String.format("Total Periods: %d | Paid: %d | Overdue: %d | Pending: %d | Total Paid: %s | Remaining: %s",
            rows.size(), paidCount, overdueCount, rows.size() - paidCount,
            currencyFormat.format(totalPaid), currencyFormat.format(totalRemaining)));
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Schedule to CSV");
        fileChooser.setSelectedFile(new java.io.File("loan_" + loanId + "_schedule.csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                // Header
                writer.println("Period,Due Date,Opening Balance,Payment,Principal,Interest,Penalty,Closing Balance,Status");
                
                // Data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        if (j > 0) line.append(",");
                        Object value = tableModel.getValueAt(i, j);
                        String str = value != null ? value.toString() : "";
                        if (str.contains(",")) {
                            str = "\"" + str + "\"";
                        }
                        line.append(str);
                    }
                    writer.println(line.toString());
                }
                
                JOptionPane.showMessageDialog(this,
                    "Schedule exported successfully!",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (java.io.FileNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting file: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void printSchedule() {
        try {
            boolean printed = scheduleTable.print(JTable.PrintMode.FIT_WIDTH,
                new java.text.MessageFormat("Amortization Schedule - Loan #" + loanId),
                new java.text.MessageFormat("Page {0}"));
            
            if (printed) {
                JOptionPane.showMessageDialog(this,
                    "Schedule printed successfully!",
                    "Print Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(this,
                "Error printing: " + e.getMessage(),
                "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
