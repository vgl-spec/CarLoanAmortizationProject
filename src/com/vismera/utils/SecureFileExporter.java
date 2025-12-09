package com.vismera.utils;

import com.vismera.models.AmortizationEntry;
import com.vismera.models.LoanCalculation;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for secure export/import of loan data to TXT files.
 * Uses horizontal pipe-delimited format with SHA-256 hash for each record.
 * Format: ID|Name|Details|Amount|Rate|Payment|Interest|Total|Term|DateRange | SHA256_HASH
 * 
 * No filler characters (spaces are not used as padding).
 * 
 * @author Vismerá Inc.
 */
public class SecureFileExporter {

    private static final String RECORD_SEPARATOR = "----------------------------------------";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    /**
     * Generate SHA-256 hash of a string (uppercase hex)
     */
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex.toUpperCase());
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Export loan data with horizontal pipe-delimited format and SHA-256 hash per record.
     * Format: ID|Name|Job|Status|Amount|Rate|Payment|Interest|Total|Term|DateRange | HASH
     * No filler characters - compact format.
     */
    public static boolean exportSecureSchedule(List<AmortizationEntry> entries, 
                                                LoanCalculation loan, 
                                                String filePath) {
        if (entries == null || entries.isEmpty()) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Calculate dates
            LocalDate startDate = LocalDate.now();
            int termMonths = loan.getLoanTermYears() * 12;
            LocalDate endDate = startDate.plusMonths(termMonths);
            String dateRange = startDate.format(DATE_FORMAT) + " - " + endDate.format(DATE_FORMAT);
            
            // Write each amortization entry as a horizontal record with hash
            int recordId = 1;
            for (AmortizationEntry entry : entries) {
                // Build record data (pipe-delimited, no spaces as fillers)
                String recordData = String.format("%d|Loan Payment #%d|Amortization|Active|₱%.2f|%.2f%%|₱%.2f|₱%.2f|₱%.2f|%d|%s",
                    recordId,
                    entry.getPaymentNumber(),
                    loan.calculateAmountFinanced(),
                    loan.getAnnualInterestRate(),
                    entry.getPayment(),
                    entry.getInterest(),
                    entry.getTotalPaid(),
                    loan.getLoanTermYears(),
                    dateRange
                );
                
                // Generate SHA-256 hash for this record
                String hash = hashSHA256(recordData);
                
                // Write: RECORD_DATA | HASH
                writer.println(recordData + " | " + hash);
                writer.println(RECORD_SEPARATOR);
                
                recordId++;
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting secure file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Export with file chooser dialog
     */
    public static void exportWithFileChooser(List<AmortizationEntry> schedule, 
                                              LoanCalculation loan, 
                                              Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Secure TXT File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        
        // Default filename
        String defaultName = "loan_schedule_" + System.currentTimeMillis() + ".txt";
        fileChooser.setSelectedFile(new File(defaultName));
        
        int result = fileChooser.showSaveDialog(parent);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            
            // Ensure .txt extension
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
            }
            
            boolean success = exportSecureSchedule(schedule, loan, filePath);
            
            if (success) {
                JOptionPane.showMessageDialog(parent,
                    "File exported successfully!\n\nLocation: " + filePath + 
                    "\n\nFormat: Horizontal pipe-delimited with SHA-256 hash per record.",
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent,
                    "Failed to export file. Please try again.",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Import and parse secure TXT file, returns list of parsed records
     */
    public static List<ImportedRecord> importSecureFile(String filePath) throws IOException, SecurityException {
        List<ImportedRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip separators and empty lines
                if (line.isEmpty() || line.equals(RECORD_SEPARATOR) || line.startsWith("---")) {
                    continue;
                }
                
                // Parse record: DATA | HASH
                int hashSeparator = line.lastIndexOf(" | ");
                if (hashSeparator == -1) {
                    continue; // Invalid format
                }
                
                String recordData = line.substring(0, hashSeparator).trim();
                String storedHash = line.substring(hashSeparator + 3).trim();
                
                // Verify hash
                String calculatedHash = hashSHA256(recordData);
                boolean hashValid = calculatedHash.equals(storedHash);
                
                // Parse pipe-delimited data
                String[] parts = recordData.split("\\|");
                if (parts.length >= 10) {
                    ImportedRecord record = new ImportedRecord();
                    record.setId(parts[0].trim());
                    record.setName(parts[1].trim());
                    record.setType(parts[2].trim());
                    record.setStatus(parts[3].trim());
                    record.setAmount(parts[4].trim());
                    record.setRate(parts[5].trim());
                    record.setPayment(parts[6].trim());
                    record.setInterest(parts[7].trim());
                    record.setTotal(parts[8].trim());
                    record.setTerm(parts[9].trim());
                    record.setDateRange(parts.length > 10 ? parts[10].trim() : "N/A");
                    record.setStoredHash(storedHash);
                    record.setCalculatedHash(calculatedHash);
                    record.setHashValid(hashValid);
                    record.setRawData(recordData);
                    
                    records.add(record);
                }
            }
        }
        
        return records;
    }

    /**
     * Import with file chooser and display dialog
     */
    public static void importWithFileChooser(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Secure TXT File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        
        int result = fileChooser.showOpenDialog(parent);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try {
                List<ImportedRecord> records = importSecureFile(file.getAbsolutePath());
                
                if (records.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,
                        "No valid records found in the file.",
                        "Import Error",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Check for any tampered records
                long invalidCount = records.stream().filter(r -> !r.isHashValid()).count();
                
                // Show import dialog with data
                showImportedDataDialog(parent, records, file.getName(), invalidCount);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Error reading file: " + e.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(parent,
                    "Security Error: " + e.getMessage(),
                    "Data Integrity Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Show dialog with imported data in tabbed pane
     */
    private static void showImportedDataDialog(Component parent, List<ImportedRecord> records, 
                                                String fileName, long invalidCount) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), 
            "Imported Data - " + fileName, true);
        dialog.setSize(1100, 600);
        dialog.setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header with status
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Imported Loan Data");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        String statusText = String.format("Total Records: %d | Valid: %d | Tampered: %d",
            records.size(), records.size() - invalidCount, invalidCount);
        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(invalidCount > 0 ? new Color(220, 38, 38) : new Color(34, 197, 94));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statusLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Tab 1: Data Table
        JPanel dataPanel = createDataTablePanel(records);
        tabbedPane.addTab("Loan Records", dataPanel);
        
        // Tab 2: Hash Verification
        JPanel hashPanel = createHashVerificationPanel(records);
        tabbedPane.addTab("SHA-256 Hash Verification", hashPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Footer with close button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dialog.dispose());
        
        footerPanel.add(closeButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * Create data table panel
     */
    private static JPanel createDataTablePanel(List<ImportedRecord> records) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Name", "Type", "Status", "Amount", "Rate", "Payment", 
                           "Interest", "Total", "Term", "Date Range", "Hash Valid"};
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (ImportedRecord record : records) {
            Object[] row = {
                record.getId(),
                record.getName(),
                record.getType(),
                record.getStatus(),
                record.getAmount(),
                record.getRate(),
                record.getPayment(),
                record.getInterest(),
                record.getTotal(),
                record.getTerm(),
                record.getDateRange(),
                record.isHashValid() ? "✓ Valid" : "✗ Tampered"
            };
            model.addRow(row);
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(229, 231, 235));
        
        // Custom renderer for hash valid column
        table.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = value != null ? value.toString() : "";
                if (val.contains("Valid")) {
                    setForeground(new Color(34, 197, 94));
                } else {
                    setForeground(new Color(220, 38, 38));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Create hash verification panel
     */
    private static JPanel createHashVerificationPanel(List<ImportedRecord> records) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info label
        JLabel infoLabel = new JLabel(
            "<html><b>SHA-256 Hash Verification</b><br>" +
            "Each record has a SHA-256 hash computed from its data. " +
            "If data is modified, the hash won't match.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        // Hash table
        String[] columns = {"Record ID", "Data Preview", "Stored Hash", "Calculated Hash", "Status"};
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (ImportedRecord record : records) {
            // Truncate data preview
            String preview = record.getRawData();
            if (preview.length() > 50) {
                preview = preview.substring(0, 50) + "...";
            }
            
            Object[] row = {
                record.getId(),
                preview,
                record.getStoredHash(),
                record.getCalculatedHash(),
                record.isHashValid() ? "✓ MATCH" : "✗ MISMATCH"
            };
            model.addRow(row);
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Consolas", Font.PLAIN, 11));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(229, 231, 235));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        // Custom renderer for status column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = value != null ? value.toString() : "";
                if (val.contains("MATCH") && !val.contains("MISMATCH")) {
                    setForeground(new Color(34, 197, 94));
                    setBackground(new Color(220, 252, 231));
                } else {
                    setForeground(new Color(220, 38, 38));
                    setBackground(new Color(254, 226, 226));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Data class for imported records
     */
    public static class ImportedRecord {
        private String id;
        private String name;
        private String type;
        private String status;
        private String amount;
        private String rate;
        private String payment;
        private String interest;
        private String total;
        private String term;
        private String dateRange;
        private String storedHash;
        private String calculatedHash;
        private boolean hashValid;
        private String rawData;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        
        public String getRate() { return rate; }
        public void setRate(String rate) { this.rate = rate; }
        
        public String getPayment() { return payment; }
        public void setPayment(String payment) { this.payment = payment; }
        
        public String getInterest() { return interest; }
        public void setInterest(String interest) { this.interest = interest; }
        
        public String getTotal() { return total; }
        public void setTotal(String total) { this.total = total; }
        
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }
        
        public String getDateRange() { return dateRange; }
        public void setDateRange(String dateRange) { this.dateRange = dateRange; }
        
        public String getStoredHash() { return storedHash; }
        public void setStoredHash(String storedHash) { this.storedHash = storedHash; }
        
        public String getCalculatedHash() { return calculatedHash; }
        public void setCalculatedHash(String calculatedHash) { this.calculatedHash = calculatedHash; }
        
        public boolean isHashValid() { return hashValid; }
        public void setHashValid(boolean hashValid) { this.hashValid = hashValid; }
        
        public String getRawData() { return rawData; }
        public void setRawData(String rawData) { this.rawData = rawData; }
    }
}
