/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package carloanamortizationproject;

/**
 *
 * @author verge
 */
public class MainForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainForm.class.getName());

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        initCustomComponents();
    }
    
    private void initCustomComponents() {
        setTitle("Vismerá Inc. - Car Loan Amortization System");
        setLayout(new java.awt.BorderLayout(10, 10));
        
        // Title Panel
        javax.swing.JPanel titlePanel = new javax.swing.JPanel();
        javax.swing.JLabel titleLabel = new javax.swing.JLabel("Vismerá Inc. Car Loan System");
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, java.awt.BorderLayout.NORTH);
        
        // Main Content Panel
        javax.swing.JPanel contentPanel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input Panel
        javax.swing.JPanel inputPanel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Loan Details"));
        
        inputPanel.add(new javax.swing.JLabel("Loan Amount:"));
        txtLoanAmount = new javax.swing.JTextField();
        inputPanel.add(txtLoanAmount);
        
        inputPanel.add(new javax.swing.JLabel("Annual Interest Rate (%):"));
        txtInterestRate = new javax.swing.JTextField();
        inputPanel.add(txtInterestRate);
        
        inputPanel.add(new javax.swing.JLabel("Loan Term (Years):"));
        txtLoanTerm = new javax.swing.JTextField();
        inputPanel.add(txtLoanTerm);
        
        inputPanel.add(new javax.swing.JLabel("Penalty Rate (%):"));
        txtPenaltyRate = new javax.swing.JTextField();
        inputPanel.add(txtPenaltyRate);
        
        // Buttons
        btnCalculate = new javax.swing.JButton("Calculate");
        btnClear = new javax.swing.JButton("Clear");
        
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        buttonPanel.add(btnCalculate);
        buttonPanel.add(btnClear);
        
        javax.swing.JPanel topContainer = new javax.swing.JPanel(new java.awt.BorderLayout());
        topContainer.add(inputPanel, java.awt.BorderLayout.CENTER);
        topContainer.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
        contentPanel.add(topContainer, java.awt.BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Month", "Beginning Balance", "Payment", "Principal", "Interest", "Penalty", "Ending Balance"};
        tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0);
        resultTable = new javax.swing.JTable(tableModel);
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(resultTable);
        contentPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
        
        add(contentPanel, java.awt.BorderLayout.CENTER);
        
        // Event Listeners
        btnCalculate.addActionListener(evt -> calculateLoan());
        btnClear.addActionListener(evt -> clearFields());
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private javax.swing.JTextField txtLoanAmount;
    private javax.swing.JTextField txtInterestRate;
    private javax.swing.JTextField txtLoanTerm;
    private javax.swing.JTextField txtPenaltyRate;
    private javax.swing.JButton btnCalculate;
    private javax.swing.JButton btnClear;
    private javax.swing.JTable resultTable;
    private javax.swing.table.DefaultTableModel tableModel;

    private void calculateLoan() {
        try {
            // 1. Get and Validate Input
            double principal;
            double annualRate;
            int years;
            double penaltyRate;

            try {
                principal = Double.parseDouble(txtLoanAmount.getText());
                annualRate = Double.parseDouble(txtInterestRate.getText());
                years = Integer.parseInt(txtLoanTerm.getText());
                String penaltyText = txtPenaltyRate.getText();
                penaltyRate = penaltyText.isEmpty() ? 0.0 : Double.parseDouble(penaltyText);
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Input Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (principal <= 0 || annualRate < 0 || years <= 0 || penaltyRate < 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Values must be positive.", "Input Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Clear existing table
            tableModel.setRowCount(0);

            // 3. Calculation Logic
            double monthlyRate = annualRate / 100.0 / 12.0;
            int totalMonths = years * 12;
            
            // Standard Amortization Formula
            double monthlyPayment;
            if (monthlyRate > 0) {
                monthlyPayment = (principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -totalMonths));
            } else {
                monthlyPayment = principal / totalMonths;
            }

            double balance = principal;
            java.text.NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance();

            for (int month = 1; month <= totalMonths; month++) {
                double interest = balance * monthlyRate;
                double principalPart = monthlyPayment - interest;
                
                // Handle last month rounding issues
                if (month == totalMonths) {
                    if (Math.abs(balance - principalPart) > 0.01) {
                        principalPart = balance;
                        monthlyPayment = principalPart + interest;
                    }
                }

                balance -= principalPart;
                if (balance < 0) balance = 0; // Floating point correction

                // Add row to table
                // "Month", "Beginning Balance", "Payment", "Principal", "Interest", "Penalty", "Ending Balance"
                // Note: Beginning Balance for the row is (balance + principalPart)
                
                Object[] row = {
                    month,
                    currencyFormat.format(balance + principalPart),
                    currencyFormat.format(monthlyPayment),
                    currencyFormat.format(principalPart),
                    currencyFormat.format(interest),
                    currencyFormat.format(0.0), // Penalty is 0 for standard schedule
                    currencyFormat.format(balance)
                };
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error calculating loan", e);
            javax.swing.JOptionPane.showMessageDialog(this, "An error occurred during calculation: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtLoanAmount.setText("");
        txtInterestRate.setText("");
        txtLoanTerm.setText("");
        txtPenaltyRate.setText("");
        tableModel.setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 680, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 443, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
