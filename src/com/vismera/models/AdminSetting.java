package com.vismera.models;

import java.time.LocalDateTime;

/**
 * Entity class representing a system configuration setting.
 * Maps to the 'admin_settings' table in the database.
 * 
 * @author Vismerá Inc.
 */
public class AdminSetting {
    
    private String keyName;
    private String valueText;
    private String description;
    private LocalDateTime updatedAt;
    
    // Common setting key constants
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_DEFAULT_APR = "default_apr";
    public static final String KEY_DEFAULT_TERM_MONTHS = "default_term_months";
    public static final String KEY_DEFAULT_PENALTY_RATE = "default_penalty_rate";
    public static final String KEY_DEFAULT_PENALTY_TYPE = "default_penalty_type";
    public static final String KEY_DEFAULT_GRACE_PERIOD = "default_grace_period";
    public static final String KEY_DEFAULT_SALES_TAX_RATE = "default_sales_tax_rate";
    public static final String KEY_DEFAULT_REGISTRATION_FEE = "default_registration_fee";
    public static final String KEY_DEFAULT_COMPOUNDING = "default_compounding";
    public static final String KEY_CURRENCY_SYMBOL = "currency_symbol";
    public static final String KEY_DATE_FORMAT = "date_format";
    public static final String KEY_APP_VERSION = "app_version";
    
    /**
     * Default constructor
     */
    public AdminSetting() {
    }
    
    /**
     * Constructor for creating a new setting
     */
    public AdminSetting(String keyName, String valueText) {
        this.keyName = keyName;
        this.valueText = valueText;
    }
    
    /**
     * Constructor with description
     */
    public AdminSetting(String keyName, String valueText, String description) {
        this.keyName = keyName;
        this.valueText = valueText;
        this.description = description;
    }
    
    /**
     * Full constructor with all fields
     */
    public AdminSetting(String keyName, String valueText, String description, LocalDateTime updatedAt) {
        this.keyName = keyName;
        this.valueText = valueText;
        this.description = description;
        this.updatedAt = updatedAt;
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    public String getKeyName() {
        return keyName;
    }
    
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
    
    public String getValueText() {
        return valueText;
    }
    
    public void setValueText(String valueText) {
        this.valueText = valueText;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ==================== VALUE CONVERSION METHODS ====================
    
    /**
     * Get value as double
     * @return The value as double, or 0.0 if conversion fails
     */
    public double getValueAsDouble() {
        return getValueAsDouble(0.0);
    }
    
    /**
     * Get value as double with default
     * @param defaultValue Default value if conversion fails
     * @return The value as double
     */
    public double getValueAsDouble(double defaultValue) {
        if (valueText == null || valueText.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(valueText.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get value as int
     * @return The value as int, or 0 if conversion fails
     */
    public int getValueAsInt() {
        return getValueAsInt(0);
    }
    
    /**
     * Get value as int with default
     * @param defaultValue Default value if conversion fails
     * @return The value as int
     */
    public int getValueAsInt(int defaultValue) {
        if (valueText == null || valueText.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(valueText.trim());
        } catch (NumberFormatException e) {
            // Try parsing as double first then convert
            try {
                return (int) Double.parseDouble(valueText.trim());
            } catch (NumberFormatException e2) {
                return defaultValue;
            }
        }
    }
    
    /**
     * Get value as boolean
     * @return The value as boolean (true if "true", "1", "yes", "on")
     */
    public boolean getValueAsBoolean() {
        return getValueAsBoolean(false);
    }
    
    /**
     * Get value as boolean with default
     * @param defaultValue Default value if conversion fails
     * @return The value as boolean
     */
    public boolean getValueAsBoolean(boolean defaultValue) {
        if (valueText == null || valueText.trim().isEmpty()) {
            return defaultValue;
        }
        String val = valueText.trim().toLowerCase();
        return "true".equals(val) || "1".equals(val) || "yes".equals(val) || "on".equals(val);
    }
    
    /**
     * Set value from double
     */
    public void setValueFromDouble(double value) {
        this.valueText = String.valueOf(value);
    }
    
    /**
     * Set value from int
     */
    public void setValueFromInt(int value) {
        this.valueText = String.valueOf(value);
    }
    
    /**
     * Set value from boolean
     */
    public void setValueFromBoolean(boolean value) {
        this.valueText = String.valueOf(value);
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Check if setting has required fields
     */
    public boolean isValid() {
        return keyName != null && !keyName.trim().isEmpty();
    }
    
    /**
     * Get display name (converts key_name to Key Name)
     */
    public String getDisplayName() {
        if (keyName == null) return "";
        String[] parts = keyName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1).toLowerCase());
                }
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
    
    @Override
    public String toString() {
        return keyName + "=" + valueText;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AdminSetting setting = (AdminSetting) obj;
        return keyName != null && keyName.equals(setting.keyName);
    }
    
    @Override
    public int hashCode() {
        return keyName != null ? keyName.hashCode() : 0;
    }
}
