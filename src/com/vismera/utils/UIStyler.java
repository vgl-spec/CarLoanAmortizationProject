package com.vismera.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Utility class for consistent UI styling across the application.
 * Based on the Vismerá Inc. design system.
 * @author Vismerá Inc.
 */
public class UIStyler {

    // Color Scheme
    public static final Color PRIMARY_BLUE = new Color(37, 99, 235);       // #2563EB
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);      // Darker blue on hover
    public static final Color SECONDARY_WHITE = Color.WHITE;                // #FFFFFF
    public static final Color ACCENT_GREEN = new Color(16, 185, 129);      // #10B981
    public static final Color TEXT_DARK = new Color(31, 41, 55);           // #1F2937
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);   // #6B7280
    public static final Color BACKGROUND_LIGHT = new Color(243, 244, 246); // #F3F4F6
    public static final Color BORDER_COLOR = new Color(229, 231, 235);     // #E5E7EB
    public static final Color ERROR_RED = new Color(239, 68, 68);          // #EF4444
    public static final Color SUCCESS_GREEN = ACCENT_GREEN;
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Style a primary button (blue background, white text)
     */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_BLUE);
            }
        });
    }

    /**
     * Style a secondary button (white background, blue text, bordered)
     */
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_BLUE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BACKGROUND_LIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
    }

    /**
     * Style a success button (green background)
     */
    public static void styleSuccessButton(JButton button) {
        button.setBackground(ACCENT_GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Style a text field with modern appearance
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(BODY_FONT);
        textField.setForeground(TEXT_DARK);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setPreferredSize(new Dimension(200, 40));
    }

    /**
     * Style a label
     */
    public static void styleLabel(JLabel label) {
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_DARK);
    }

    /**
     * Style a header label
     */
    public static void styleHeaderLabel(JLabel label) {
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_DARK);
    }

    /**
     * Style a title label
     */
    public static void styleTitleLabel(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_DARK);
    }

    /**
     * Create a card panel with white background and subtle border
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    /**
     * Create a section header panel
     */
    public static JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel label = new JLabel(title);
        label.setFont(SUBHEADER_FONT);
        label.setForeground(TEXT_DARK);
        panel.add(label);
        
        return panel;
    }

    /**
     * Get a rounded border
     */
    public static Border createRoundedBorder(int radius) {
        return new RoundedBorder(radius, BORDER_COLOR);
    }

    /**
     * Custom rounded border class
     */
    static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public java.awt.Insets getBorderInsets(Component c) {
            return new java.awt.Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * Create a summary card for displaying metrics
     */
    public static JPanel createSummaryCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new java.awt.BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(180, 100));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SMALL_FONT);
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(HEADER_FONT);
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, java.awt.BorderLayout.NORTH);
        card.add(valueLabel, java.awt.BorderLayout.CENTER);

        return card;
    }
}
