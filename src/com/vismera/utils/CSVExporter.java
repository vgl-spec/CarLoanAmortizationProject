package com.vismera.utils;

import com.vismera.models.AmortizationEntry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Utility class for exporting data to CSV files.
 * @author Vismerá Inc.
 */
public class CSVExporter {

    /**
     * Export amortization schedule to a CSV file
     * @param entries The list of amortization entries
     * @param filePath The path to save the CSV file
     * @return true if export successful, false otherwise
     */
    public static boolean exportAmortizationSchedule(List<AmortizationEntry> entries, String filePath) {
        if (entries == null || entries.isEmpty()) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Payment #,Payment,Principal,Interest,Penalty,Balance,Total Paid");

            // Write data rows
            for (AmortizationEntry entry : entries) {
                writer.printf("%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                    entry.getPaymentNumber(),
                    entry.getPayment(),
                    entry.getPrincipal(),
                    entry.getInterest(),
                    entry.getPenalty(),
                    entry.getBalance(),
                    entry.getTotalPaid()
                );
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Open a file chooser dialog and export amortization schedule
     * @param entries The list of amortization entries
     * @param parent The parent component for the dialog
     * @return true if export successful, false if cancelled or failed
     */
    public static boolean exportWithFileChooser(List<AmortizationEntry> entries, java.awt.Component parent) {
        if (entries == null || entries.isEmpty()) {
            JOptionPane.showMessageDialog(parent, 
                "No data to export!", 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Amortization Schedule");
        fileChooser.setSelectedFile(new java.io.File("amortization_schedule.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        int result = fileChooser.showSaveDialog(parent);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            
            // Ensure .csv extension
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            // Check if file exists
            java.io.File file = new java.io.File(filePath);
            if (file.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(parent,
                    "File already exists. Do you want to overwrite it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (overwrite != JOptionPane.YES_OPTION) {
                    return false;
                }
            }

            boolean success = exportAmortizationSchedule(entries, filePath);
            
            if (success) {
                JOptionPane.showMessageDialog(parent,
                    "Amortization schedule exported successfully!\n\nFile: " + filePath,
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent,
                    "Failed to export file. Please try again.",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
            return success;
        }
        
        return false;
    }

    /**
     * Export a comparison table to CSV
     */
    public static boolean exportComparisonTable(List<Object[]> data, String[] headers, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println(String.join(",", headers));

            // Write data rows
            for (Object[] row : data) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) sb.append(",");
                    String value = row[i] != null ? row[i].toString() : "";
                    // Escape commas and quotes
                    if (value.contains(",") || value.contains("\"")) {
                        value = "\"" + value.replace("\"", "\"\"") + "\"";
                    }
                    sb.append(value);
                }
                writer.println(sb.toString());
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }
}
