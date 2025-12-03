package com.vismera.utils;

import java.util.Map;

/**
 * Utility class for validating user inputs.
 * @author Vismerá Inc.
 */
public class ValidationUtils {

    /**
     * Validate that a string represents a positive number
     * @return true if valid positive number, false otherwise
     */
    public static boolean validatePositiveNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            String cleaned = input.replaceAll("[\\$,\\s]", "").trim();
            double value = Double.parseDouble(cleaned);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate that a string represents a non-negative number (zero allowed)
     * @return true if valid non-negative number, false otherwise
     */
    public static boolean validateNonNegativeNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true; // Empty is treated as 0, which is valid
        }
        try {
            String cleaned = input.replaceAll("[\\$,\\s]", "").trim();
            double value = Double.parseDouble(cleaned);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate that a string represents a valid percentage (0-100)
     * @return true if valid percentage, false otherwise
     */
    public static boolean validatePercentage(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            String cleaned = input.replaceAll("[%\\s]", "").trim();
            double value = Double.parseDouble(cleaned);
            return value >= 0 && value <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate that a string represents a valid interest rate (0-50%)
     * @return true if valid rate, false otherwise
     */
    public static boolean validateInterestRate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            String cleaned = input.replaceAll("[%\\s]", "").trim();
            double value = Double.parseDouble(cleaned);
            return value >= 0 && value <= 50;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate that a string represents a positive integer
     * @return true if valid positive integer, false otherwise
     */
    public static boolean validatePositiveInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            String cleaned = input.replaceAll("[,\\s]", "").trim();
            int value = Integer.parseInt(cleaned);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate that a string represents a non-negative integer
     * @return true if valid non-negative integer, false otherwise
     */
    public static boolean validateNonNegativeInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true; // Empty treated as 0
        }
        try {
            String cleaned = input.replaceAll("[,\\s]", "").trim();
            int value = Integer.parseInt(cleaned);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate multiple required fields
     * @param fields Map of field names to field values
     * @return Empty string if all valid, otherwise error message
     */
    public static String validateRequiredFields(Map<String, String> fields) {
        StringBuilder errors = new StringBuilder();
        
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                if (errors.length() > 0) {
                    errors.append("\n");
                }
                errors.append("• ").append(entry.getKey()).append(" is required");
            }
        }
        
        return errors.toString();
    }

    /**
     * Check if a string is empty or null
     */
    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    /**
     * Get a validation error message for loan calculation fields
     */
    public static String validateLoanInputs(String carPrice, String interestRate, 
                                           String loanTerm, String downPayment, 
                                           String tradeIn) {
        StringBuilder errors = new StringBuilder();

        if (!validatePositiveNumber(carPrice)) {
            errors.append("• Car Price must be a positive number\n");
        }
        if (!validateInterestRate(interestRate)) {
            errors.append("• Interest Rate must be between 0% and 50%\n");
        }
        if (!validatePositiveInteger(loanTerm)) {
            errors.append("• Loan Term must be a positive whole number\n");
        }
        if (!isEmpty(downPayment) && !validateNonNegativeNumber(downPayment)) {
            errors.append("• Down Payment must be a non-negative number\n");
        }
        if (!isEmpty(tradeIn) && !validateNonNegativeNumber(tradeIn)) {
            errors.append("• Trade-In Value must be a non-negative number\n");
        }

        return errors.toString();
    }
}
