package com.vismera.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting currency, percentages, and parsing numbers.
 * @author Vismerá Inc.
 */
public class FormatUtils {
    
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("0.00%");
    private static final DecimalFormat RATE_FORMAT = new DecimalFormat("0.00");

    /**
     * Format a double value as currency ($XX,XXX.XX)
     */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    /**
     * Format a double value as a percentage (X.XX%)
     * @param rate The rate as a decimal (e.g., 0.05 for 5%)
     */
    public static String formatPercentage(double rate) {
        return PERCENTAGE_FORMAT.format(rate);
    }

    /**
     * Format a rate value (e.g., 5.25 becomes "5.25%")
     * @param rate The rate as a percentage value (e.g., 5.25 for 5.25%)
     */
    public static String formatRate(double rate) {
        return RATE_FORMAT.format(rate) + "%";
    }

    /**
     * Format years as a duration string
     */
    public static String formatYears(int years) {
        return years + (years == 1 ? " Year" : " Years");
    }

    /**
     * Format months as a duration string
     */
    public static String formatMonths(int months) {
        int years = months / 12;
        int remainingMonths = months % 12;
        
        if (years == 0) {
            return remainingMonths + (remainingMonths == 1 ? " Month" : " Months");
        } else if (remainingMonths == 0) {
            return formatYears(years);
        } else {
            return formatYears(years) + ", " + remainingMonths + " Mo";
        }
    }

    /**
     * Safely parse a string to double, returning 0 on failure
     */
    public static double parseDouble(String text) {
        return parseDouble(text, 0.0);
    }

    /**
     * Safely parse a string to double, returning default value on failure
     */
    public static double parseDouble(String text, double defaultValue) {
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            // Remove currency symbols and commas
            String cleaned = text.replaceAll("[\\$,\\s]", "").trim();
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parse a string to integer, returning 0 on failure
     */
    public static int parseInt(String text) {
        return parseInt(text, 0);
    }

    /**
     * Safely parse a string to integer, returning default value on failure
     */
    public static int parseInt(String text, int defaultValue) {
        if (text == null || text.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            String cleaned = text.replaceAll("[,\\s]", "").trim();
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Format a number with thousand separators
     */
    public static String formatNumber(double number) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(number);
    }

    /**
     * Format an integer with thousand separators
     */
    public static String formatNumber(int number) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(number);
    }
}
