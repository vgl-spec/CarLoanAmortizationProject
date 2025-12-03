package com.vismera.dao;

import com.vismera.config.DatabaseConfig;
import com.vismera.models.AdminSetting;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for AdminSetting entity.
 * Handles all database operations for admin_settings table.
 * 
 * @author Vismerá Inc.
 */
public class AdminSettingDAO {
    
    private static final Logger LOGGER = Logger.getLogger(AdminSettingDAO.class.getName());
    
    private static AdminSettingDAO instance;
    
    // Cache for settings to reduce database calls
    private Map<String, AdminSetting> settingsCache;
    private boolean cacheLoaded = false;
    
    private AdminSettingDAO() {
        settingsCache = new HashMap<>();
    }
    
    /**
     * Get singleton instance
     */
    public static AdminSettingDAO getInstance() {
        if (instance == null) {
            instance = new AdminSettingDAO();
        }
        return instance;
    }
    
    /**
     * Insert or update a setting (upsert)
     * @param setting The setting to save
     * @return true if successful
     */
    public boolean upsert(AdminSetting setting) {
        // H2 compatible MERGE statement (upsert)
        String sql = "MERGE INTO admin_settings (key_name, value_text, description, updated_at) " +
                     "KEY (key_name) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, setting.getKeyName());
            stmt.setString(2, setting.getValueText());
            stmt.setString(3, setting.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Update cache
                settingsCache.put(setting.getKeyName(), setting);
                LOGGER.info("Setting saved: " + setting.getKeyName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error upserting setting: " + setting.getKeyName(), e);
        }
        return false;
    }
    
    /**
     * Find setting by key
     * @param keyName The setting key
     * @return The setting, or null if not found
     */
    public AdminSetting findByKey(String keyName) {
        // Check cache first
        if (settingsCache.containsKey(keyName)) {
            return settingsCache.get(keyName);
        }
        
        String sql = "SELECT key_name, value_text, description, updated_at FROM admin_settings WHERE key_name = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, keyName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AdminSetting setting = mapResultSetToSetting(rs);
                    settingsCache.put(keyName, setting);
                    return setting;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding setting by key: " + keyName, e);
        }
        return null;
    }
    
    /**
     * Find all settings
     * @return List of all settings
     */
    public List<AdminSetting> findAll() {
        List<AdminSetting> settings = new ArrayList<>();
        String sql = "SELECT key_name, value_text, description, updated_at FROM admin_settings ORDER BY key_name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                AdminSetting setting = mapResultSetToSetting(rs);
                settings.add(setting);
                settingsCache.put(setting.getKeyName(), setting);
            }
            cacheLoaded = true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all settings", e);
        }
        return settings;
    }
    
    /**
     * Get all settings as a map
     * @return Map of key to value
     */
    public Map<String, String> findAllAsMap() {
        Map<String, String> map = new HashMap<>();
        
        if (cacheLoaded) {
            for (AdminSetting setting : settingsCache.values()) {
                map.put(setting.getKeyName(), setting.getValueText());
            }
            return map;
        }
        
        String sql = "SELECT key_name, value_text FROM admin_settings";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                map.put(rs.getString("key_name"), rs.getString("value_text"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all settings as map", e);
        }
        return map;
    }
    
    /**
     * Delete a setting by key
     * @param keyName The setting key
     * @return true if successful
     */
    public boolean delete(String keyName) {
        String sql = "DELETE FROM admin_settings WHERE key_name = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, keyName);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                settingsCache.remove(keyName);
                LOGGER.info("Setting deleted: " + keyName);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting setting: " + keyName, e);
        }
        return false;
    }
    
    /**
     * Get a setting value with default fallback
     * @param keyName The setting key
     * @param defaultValue Default if not found
     * @return The setting value
     */
    public String getValue(String keyName, String defaultValue) {
        AdminSetting setting = findByKey(keyName);
        return setting != null ? setting.getValueText() : defaultValue;
    }
    
    /**
     * Get a double setting value with default fallback
     * @param keyName The setting key
     * @param defaultValue Default if not found or parse error
     * @return The setting value as double
     */
    public double getDoubleValue(String keyName, double defaultValue) {
        AdminSetting setting = findByKey(keyName);
        if (setting != null) {
            return setting.getValueAsDouble(defaultValue);
        }
        return defaultValue;
    }
    
    /**
     * Get an int setting value with default fallback
     * @param keyName The setting key
     * @param defaultValue Default if not found or parse error
     * @return The setting value as int
     */
    public int getIntValue(String keyName, int defaultValue) {
        AdminSetting setting = findByKey(keyName);
        if (setting != null) {
            return setting.getValueAsInt(defaultValue);
        }
        return defaultValue;
    }
    
    /**
     * Get a boolean setting value with default fallback
     * @param keyName The setting key
     * @param defaultValue Default if not found
     * @return The setting value as boolean
     */
    public boolean getBooleanValue(String keyName, boolean defaultValue) {
        AdminSetting setting = findByKey(keyName);
        if (setting != null) {
            return setting.getValueAsBoolean(defaultValue);
        }
        return defaultValue;
    }
    
    /**
     * Set a value directly
     * @param keyName The setting key
     * @param value The value to set
     * @return true if successful
     */
    public boolean setValue(String keyName, String value) {
        AdminSetting setting = new AdminSetting(keyName, value);
        return upsert(setting);
    }
    
    /**
     * Clear the settings cache
     */
    public void clearCache() {
        settingsCache.clear();
        cacheLoaded = false;
    }
    
    /**
     * Reload settings from database
     */
    public void reloadCache() {
        clearCache();
        findAll();
    }
    
    /**
     * Check if a setting exists
     * @param keyName The setting key
     * @return true if exists
     */
    public boolean exists(String keyName) {
        return findByKey(keyName) != null;
    }
    
    /**
     * Get count of settings
     * @return Setting count
     */
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM admin_settings";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting setting count", e);
        }
        return 0;
    }
    
    /**
     * Map ResultSet row to AdminSetting object
     */
    private AdminSetting mapResultSetToSetting(ResultSet rs) throws SQLException {
        AdminSetting setting = new AdminSetting();
        setting.setKeyName(rs.getString("key_name"));
        setting.setValueText(rs.getString("value_text"));
        setting.setDescription(rs.getString("description"));
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            setting.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return setting;
    }
}
