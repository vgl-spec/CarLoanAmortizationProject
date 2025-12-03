package com.vismera.views;

import com.vismera.models.AmortizationEntry;
import com.vismera.models.LoanCalculation;
import com.vismera.utils.FormatUtils;
import com.vismera.utils.UIStyler;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Loan Summary Dialog - Popup after calculation
 * @author Vismerá Inc.
 */
public class LoanSummaryDialog extends JDialog {
    
    private LoanCalculation loan;
    
    public LoanSummaryDialog(Frame parent, LoanCalculation loan) {
        super(parent, "Loan Summary - Vismerá Inc.", true);
        this.loan = loan;
        initComponents();
    }
    
    private void initComponents() {
        setSize(700, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Header
        JLabel titleLabel = new JLabel("Loan Calculation Results");
        titleLabel.setFont(UIStyler.TITLE_FONT);
        titleLabel.setForeground(UIStyler.TEXT_DARK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Summary cards grid
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        cardsPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        // Calculate values
        double monthlyPayment = loan.getMonthlyPayment();
        double totalPaid = loan.getTotalAmountPaid();
        double totalInterest = loan.getTotalInterest();
        double totalPenalties = loan.getTotalPenalties();
        double interestRate = loan.getAnnualInterestRate();
        int termMonths = loan.getLoanTermYears() * 12;
        
        // Create summary cards
        cardsPanel.add(createSummaryCard("📅 Monthly Payment", 
            FormatUtils.formatCurrency(monthlyPayment), UIStyler.PRIMARY_BLUE));
        
        cardsPanel.add(createSummaryCard("💰 Total Amount Paid", 
            FormatUtils.formatCurrency(totalPaid), UIStyler.TEXT_DARK));
        
        cardsPanel.add(createSummaryCard("📈 Total Interest", 
            FormatUtils.formatCurrency(totalInterest), new Color(139, 92, 246))); // Purple
        
        cardsPanel.add(createSummaryCard("⚠️ Total Penalties", 
            FormatUtils.formatCurrency(totalPenalties), UIStyler.ERROR_RED));
        
        cardsPanel.add(createSummaryCard("📊 Interest Rate", 
            FormatUtils.formatRate(interestRate), UIStyler.ACCENT_GREEN));
        
        cardsPanel.add(createSummaryCard("⏱️ Loan Duration", 
            FormatUtils.formatMonths(termMonths), UIStyler.TEXT_SECONDARY));
        
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setBackground(UIStyler.BACKGROUND_LIGHT);
        
        JButton viewAmortizationButton = new JButton("View Amortization Schedule");
        UIStyler.stylePrimaryButton(viewAmortizationButton);
        viewAmortizationButton.setPreferredSize(new Dimension(250, 45));
        viewAmortizationButton.addActionListener(e -> viewAmortizationSchedule());
        
        JButton closeButton = new JButton("Close");
        UIStyler.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(120, 45));
        closeButton.addActionListener(e -> dispose());
        
        buttonsPanel.add(viewAmortizationButton);
        buttonsPanel.add(closeButton);
        
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createSummaryCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyler.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyler.BODY_FONT);
        titleLabel.setForeground(UIStyler.TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(valueColor);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void viewAmortizationSchedule() {
        // Hide this dialog
        setVisible(false);
        
        List<AmortizationEntry> schedule = loan.getAmortizationSchedule();
        AmortizationScheduleFrame frame = new AmortizationScheduleFrame(schedule, loan, this);
        frame.setVisible(true);
    }
    
    /**
     * Called when amortization schedule is closed to show this dialog again
     */
    public void showAgain() {
        setVisible(true);
    }
}
