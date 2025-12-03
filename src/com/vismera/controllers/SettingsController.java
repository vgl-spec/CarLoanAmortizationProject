package com.vismera.controllers;

import com.vismera.dao.AdminSettingDAO;
import com.vismera.models.AdminSetting;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller for application settings management.
 * 
 * @author Vismerá Inc.
 */
public class SettingsController {
    
    private static final Logger LOGGER = Logger.getLogger(SettingsController.class.getName());
    
    private static SettingsController instance;
    
    private final AdminSettingDAO settingDAO;
    
    private SettingsController() {
        settingDAO = AdminSettingDAO.getInstance();
    }
    
    /**
     * Get singleton instance
     */
    public static SettingsController getInstance() {
        if (instance == null) {
            instance = new SettingsController();
        }
        return instance;
    }
    
    /**
     * Get all settings as a map (key -> value)
     */
    public Map<String, String> getAllSettings() {
        return settingDAO.findAllAsMap();
    }
    
    /**
     * Get all settings as list of AdminSetting objects
     */
    public List<AdminSetting> getAllSettingsList() {
        return settingDAO.findAll();
    }
    
    /**
     * Get all settings as map (alias for getAllSettings)
     */
    public Map<String, String> getAllSettingsMap() {
        return settingDAO.findAllAsMap();
    }
    
    /**
     * Get a setting value with default
     * @param key The setting key
     * @param defaultValue Default value if not found
     * @return The setting value or default
     */
    public String getSetting(String key, String defaultValue) {
        return settingDAO.getValue(key, defaultValue);
    }
    
    /**
     * Get a setting entity by key
     */
    public AdminSetting getSettingEntity(String key) {
        return settingDAO.findByKey(key);
    }
    
    /**
     * Get a string setting value
     */
    public String getString(String key, String defaultValue) {
        return settingDAO.getValue(key, defaultValue);
    }
    
    /**
     * Get a double setting value
     */
    public double getDouble(String key, double defaultValue) {
        return settingDAO.getDoubleValue(key, defaultValue);
    }
    
    /**
     * Get an int setting value
     */
    public int getInt(String key, int defaultValue) {
        return settingDAO.getIntValue(key, defaultValue);
    }
    
    /**
     * Get a boolean setting value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return settingDAO.getBooleanValue(key, defaultValue);
    }
    
    /**
     * Save a setting
     */
    public boolean saveSetting(String key, String value) {
        return settingDAO.setValue(key, value);
    }
    
    /**
     * Save a setting with description
     */
    public boolean saveSetting(String key, String value, String description) {
        AdminSetting setting = new AdminSetting(key, value, description);
        return settingDAO.upsert(setting);
    }
    
    /**
     * Delete a setting
     */
    public boolean deleteSetting(String key) {
        return settingDAO.delete(key);
    }
    
    /**
     * Check if a setting exists
     */
    public boolean exists(String key) {
        return settingDAO.exists(key);
    }
    
    /**
     * Reload settings from database
     */
    public void reloadSettings() {
        settingDAO.reloadCache();
    }
    
    // ==================== CONVENIENCE METHODS FOR COMMON SETTINGS ====================
    
    /**
     * Get company name
     */
    public String getCompanyName() {
        return getString(AdminSetting.KEY_COMPANY_NAME, "Vismerá Inc.");
    }
    
    /**
     * Get default APR
     */
    public double getDefaultApr() {
        return getDouble(AdminSetting.KEY_DEFAULT_APR, 6.5);
    }
    
    /**
     * Get default penalty rate
     */
    public double getDefaultPenaltyRate() {
        return getDouble(AdminSetting.KEY_DEFAULT_PENALTY_RATE, 2.0);
    }
    
    /**
     * Get default grace period (days)
     */
    public int getDefaultGracePeriod() {
        return getInt(AdminSetting.KEY_DEFAULT_GRACE_PERIOD, 5);
    }
    
    /**
     * Get default sales tax rate
     */
    public double getDefaultSalesTaxRate() {
        return getDouble(AdminSetting.KEY_DEFAULT_SALES_TAX_RATE, 8.0);
    }
    
    /**
     * Get default registration fee
     */
    public double getDefaultRegistrationFee() {
        return getDouble(AdminSetting.KEY_DEFAULT_REGISTRATION_FEE, 500.0);
    }
    
    /**
     * Get default compounding frequency
     */
    public String getDefaultCompounding() {
        return getString(AdminSetting.KEY_DEFAULT_COMPOUNDING, "monthly");
    }
    
    /**
     * Get currency symbol
     */
    public String getCurrencySymbol() {
        return getString(AdminSetting.KEY_CURRENCY_SYMBOL, "$");
    }
    
    /**
     * Get date format pattern
     */
    public String getDateFormat() {
        return getString(AdminSetting.KEY_DATE_FORMAT, "MM/dd/yyyy");
    }
    
    /**
     * Get application version
     */
    public String getAppVersion() {
        return getString(AdminSetting.KEY_APP_VERSION, "1.0.0");
    }
    
    // ==================== SETTER CONVENIENCE METHODS ====================
    
    /**
     * Set company name
     */
    public boolean setCompanyName(String value) {
        return saveSetting(AdminSetting.KEY_COMPANY_NAME, value, "Company name displayed in reports");
    }
    
    /**
     * Set default APR
     */
    public boolean setDefaultApr(double value) {
        return saveSetting(AdminSetting.KEY_DEFAULT_APR, String.valueOf(value), "Default annual percentage rate");
    }
    
    /**
     * Set default penalty rate
     */
    public boolean setDefaultPenaltyRate(double value) {
        return saveSetting(AdminSetting.KEY_DEFAULT_PENALTY_RATE, String.valueOf(value), "Default penalty rate for late payments");
    }
    
    /**
     * Set default grace period
     */
    public boolean setDefaultGracePeriod(int value) {
        return saveSetting(AdminSetting.KEY_DEFAULT_GRACE_PERIOD, String.valueOf(value), "Default grace period in days");
    }
    
    /**
     * Set default sales tax rate
     */
    public boolean setDefaultSalesTaxRate(double value) {
        return saveSetting(AdminSetting.KEY_DEFAULT_SALES_TAX_RATE, String.valueOf(value), "Default sales tax rate");
    }
    
    /**
     * Set default registration fee
     */
    public boolean setDefaultRegistrationFee(double value) {
        return saveSetting(AdminSetting.KEY_DEFAULT_REGISTRATION_FEE, String.valueOf(value), "Default vehicle registration fee");
    }
    
    /**
     * Set default compounding
     */
    public boolean setDefaultCompounding(String value) {
        return saveSetting(AdminSetting.KEY_DEFAULT_COMPOUNDING, value, "Default interest compounding frequency");
    }
    
    /**
     * Load/reset all settings to their default values
     */
    public void loadDefaultSettings() {
        LOGGER.info("Resetting all settings to defaults");
        
        // Company settings
        saveSetting(AdminSetting.KEY_COMPANY_NAME, "Vismerá Inc.", "Company name displayed in reports");
        
        // Loan defaults
        saveSetting(AdminSetting.KEY_DEFAULT_APR, "5.99", "Default annual percentage rate");
        saveSetting(AdminSetting.KEY_DEFAULT_TERM_MONTHS, "36", "Default loan term in months");
        saveSetting(AdminSetting.KEY_DEFAULT_PENALTY_RATE, "5.00", "Default penalty rate for late payments");
        saveSetting(AdminSetting.KEY_DEFAULT_PENALTY_TYPE, "percentage", "Default penalty type (percentage or fixed)");
        saveSetting(AdminSetting.KEY_DEFAULT_GRACE_PERIOD, "5", "Default grace period in days");
        saveSetting(AdminSetting.KEY_DEFAULT_SALES_TAX_RATE, "8.00", "Default sales tax rate");
        saveSetting(AdminSetting.KEY_DEFAULT_REGISTRATION_FEE, "500.00", "Default vehicle registration fee");
        saveSetting(AdminSetting.KEY_DEFAULT_COMPOUNDING, "monthly", "Default interest compounding frequency");
        
        // System settings
        saveSetting(AdminSetting.KEY_DATE_FORMAT, "MM/dd/yyyy", "Date format for display");
        saveSetting(AdminSetting.KEY_APP_VERSION, "2.0.0", "Application version");
        
        LOGGER.info("Default settings loaded successfully");
    }
}
