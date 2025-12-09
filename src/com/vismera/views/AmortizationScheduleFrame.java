package com.vismera.views;

import com.vismera.models.AmortizationEntry;
import com.vismera.models.LoanCalculation;
import com.vismera.utils.CSVExporter;
import com.vismera.utils.SecureFileExporter;
import com.vismera.utils.FormatUtils;
import com.vismera.utils.UIStyler;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Amortization Schedule Frame - Detailed payment table
 * @author Vismerá Inc.
 */
public class AmortizationScheduleFrame extends JFrame {
    
    private List<AmortizationEntry> schedule;
    private LoanCalculation loan;
    private JTable amortizationTable;
    private DefaultTableModel tableModel;
    private LoanSummaryDialog parentDialog;
    
    public AmortizationScheduleFrame(List<AmortizationEntry> schedule, LoanCalculation loan, LoanSummaryDialog parentDialog) {
        super("Amortization Schedule - Vismerá Inc.");
        this.schedule = schedule;
        this.loan = loan;
        this.parentDialog = parentDialog;
        initComponents();
    }
    
    private void initComponents() {
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        JLabel titleLabel = new JLabel("Amortization Schedule");
        titleLabel.setFont(UIStyler.TITLE_FONT);
        titleLabel.setForeground(UIStyler.TEXT_DARK);
        
        JLabel subtitleLabel = new JLabel(String.format(
            "Loan Amount: %s | Rate: %s | Term: %s",
            FormatUtils.formatCurrency(loan.calculateAmountFinanced()),
            FormatUtils.formatRate(loan.getAnnualInterestRate()),
            FormatUtils.formatYears(loan.getLoanTermYears())
        ));
        subtitleLabel.setFont(UIStyler.BODY_FONT);
        subtitleLabel.setForeground(UIStyler.TEXT_SECONDARY);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create table
        createTable();
        
        JScrollPane scrollPane = new JScrollPane(amortizationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        JButton importButton = new JButton("Import");
        UIStyler.styleSecondaryButton(importButton);
        importButton.addActionListener(e -> importFromTXT());
        
        JButton exportTxtButton = new JButton("Export");
        UIStyler.stylePrimaryButton(exportTxtButton);
        exportTxtButton.addActionListener(e -> exportToSecureTXT());
        
        JButton closeButton = new JButton("Close");
        UIStyler.styleSecondaryButton(closeButton);
        closeButton.addActionListener(e -> closeAndShowParent());
        
        buttonsPanel.add(importButton);
        buttonsPanel.add(exportTxtButton);
        buttonsPanel.add(closeButton);
        
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Also handle window close button (X)
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeAndShowParent();
            }
        });
    }
    
    private void closeAndShowParent() {
        dispose();
        if (parentDialog != null) {
            parentDialog.showAgain();
        }
    }
    
    private void createTable() {
        String[] columns = {
            "Payment #", "Payment", "Principal", "Interest", "Penalty", "Balance", "Total Paid"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Populate table
        for (AmortizationEntry entry : schedule) {
            Object[] row = {
                entry.getPaymentNumber(),
                FormatUtils.formatCurrency(entry.getPayment()),
                FormatUtils.formatCurrency(entry.getPrincipal()),
                FormatUtils.formatCurrency(entry.getInterest()),
                FormatUtils.formatCurrency(entry.getPenalty()),
                FormatUtils.formatCurrency(entry.getBalance()),
                FormatUtils.formatCurrency(entry.getTotalPaid())
            };
            tableModel.addRow(row);
        }
        
        amortizationTable = new JTable(tableModel);
        amortizationTable.setFont(UIStyler.BODY_FONT);
        amortizationTable.setRowHeight(35);
        amortizationTable.setShowGrid(true);
        amortizationTable.setGridColor(UIStyler.BORDER_COLOR);
        amortizationTable.setSelectionBackground(new Color(219, 234, 254)); // Light blue
        amortizationTable.setSelectionForeground(UIStyler.TEXT_DARK);
        
        // Header styling
        JTableHeader header = amortizationTable.getTableHeader();
        header.setFont(UIStyler.SUBHEADER_FONT);
        header.setBackground(UIStyler.PRIMARY_BLUE);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        // Make header text visible with custom renderer
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(229, 231, 235)); // Light gray background
                setForeground(Color.BLACK);
                setFont(UIStyler.SUBHEADER_FONT);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, UIStyler.BORDER_COLOR));
                return c;
            }
        });
        
        // Column alignment and coloring
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Custom renderer for color-coding
        amortizationTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.RIGHT);
                
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                    
                    switch (column) {
                        case 2: // Principal - Blue
                            setForeground(UIStyler.PRIMARY_BLUE);
                            break;
                        case 3: // Interest - Purple
                            setForeground(new Color(139, 92, 246));
                            break;
                        case 4: // Penalty - Red
                            setForeground(UIStyler.ERROR_RED);
                            break;
                        case 6: // Total Paid - Green
                            setForeground(UIStyler.ACCENT_GREEN);
                            break;
                        default:
                            setForeground(UIStyler.TEXT_DARK);
                    }
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
        
        // Set column widths
        TableColumnModel columnModel = amortizationTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Payment #
        columnModel.getColumn(1).setPreferredWidth(120); // Payment
        columnModel.getColumn(2).setPreferredWidth(120); // Principal
        columnModel.getColumn(3).setPreferredWidth(120); // Interest
        columnModel.getColumn(4).setPreferredWidth(100); // Penalty
        columnModel.getColumn(5).setPreferredWidth(130); // Balance
        columnModel.getColumn(6).setPreferredWidth(130); // Total Paid
    }
    
    private void exportToCSV() {
        CSVExporter.exportWithFileChooser(schedule, this);
    }
    
    private void exportToSecureTXT() {
        SecureFileExporter.exportWithFileChooser(schedule, loan, this);
    }
    
    private void importFromTXT() {
        SecureFileExporter.importWithFileChooser(this);
    }
}
