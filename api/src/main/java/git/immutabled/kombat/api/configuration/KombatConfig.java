package git.immutabled.kombat.api.configuration;

import git.immutabled.kombat.api.knockback.KnockbackProfile;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Configuration interface for Kombat plugin
 * Provides access to all configurable settings.
 * 
 * @author Immutable
 * @version 2025.0312.01
 */
public interface KombatConfig {
    
    /**
     * Gets the attack speed multiplier
     * 
     * @return the attack speed
     */
    double getAttackSpeed();
    
    /**
     * Checks if sweep attacks are enabled
     * 
     * @return true if enabled
     */
    boolean isSweepEnabled();
    
    /**
     * Gets the configured locale
     * 
     * @return the locale
     */
    Locale getLocale();
    
    /**
     * Gets the defaults knockback profile
     * 
     * @return the defaults profile
     */
    KnockbackProfile getDefaultKnockbackProfile();
    
    /**
     * Gets a custom knockback profile by name
     * 
     * @param name the profile name
     * @return optional containing the profile if found
     */
    Optional<KnockbackProfile> getKnockbackProfile(String name);
    
    /**
     * Gets all available knockback profiles
     * 
     * @return list of profiles
     */
    List<KnockbackProfile> getKnockbackProfiles();
    
    /**
     * Checks if statistics tracking is enabled
     * 
     * @return true if enabled
     */
    boolean isStatisticsEnabled();
    
    /**
     * Checks if combo system is enabled
     * 
     * @return true if enabled
     */
    boolean isComboSystemEnabled();
    
    /**
     * Gets the combo timeout in milliseconds
     * 
     * @return the timeout
     */
    long getComboTimeout();
    
    /**
     * Checks if debug mode is enabled
     * 
     * @return true if enabled
     */
    boolean isDebugMode();
    
    /**
     * Gets a configuration value
     * 
     * @param path the configuration path
     * @return optional containing the value
     */
    Optional<Object> get(String path);
    
    /**
     * Gets a configuration value with a defaults
     * 
     * @param <T> the value type
     * @param path the configuration path
     * @param defaultValue the defaults value
     * @return the value or defaults
     */
    <T> T get(String path, T defaultValue);
    
    /**
     * Reloads the configuration from file
     */
    void reload();
    
    /**
     * Saves the current configuration to file
     */
    void save();
}