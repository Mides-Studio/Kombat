package git.immutabled.kombat.api.player;


import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.statistics.PlayerStatistics;

import java.util.UUID;

/**
 * Represents a player in the Kombat system
 * Wraps a platform-specific player object and provides access to
 * Kombat-specific data and statistics.
 * 
 * @author Immutable
 * @version 2025.0312.01
 */
public interface KombatPlayer {
    
    /**
     * Gets the player's UUID
     * 
     * @return the UUID
     */
    UUID getUniqueId();
    
    /**
     * Gets the player's name
     * 
     * @return the player name
     */
    String getName();
    
    /**
     * Checks if the player is online
     * 
     * @return true if online
     */
    boolean isOnline();
    
    /**
     * Gets the platform-specific player object
     * 
     * @param <T> the platform player type
     * @return the platform player
     */
    <T> T getPlatformPlayer();
    
    /**
     * Gets the player's combat statistics
     * 
     * @return the player statistics
     */
    PlayerStatistics getStatistics();
    
    /**
     * Gets the player's custom knockback profile
     * 
     * @return the knockback profile, or null if using defaults
     */
    KnockbackProfile getKnockbackProfile();
    
    /**
     * Sets a custom knockback profile for this player
     * 
     * @param profile the profile to use
     */
    void setKnockbackProfile(KnockbackProfile profile);
    
    /**
     * Resets the player to defaults knockback profile
     */
    void resetKnockbackProfile();
    
    /**
     * Gets the player's current combo count
     * 
     * @return the combo count
     */
    int getComboCount();
    
    /**
     * Increments the player's combo counter
     */
    void incrementCombo();
    
    /**
     * Resets the player's combo counter
     */
    void resetCombo();
    
    /**
     * Checks if Kombat mechanics are enabled for this player
     * 
     * @return true if enabled
     */
    boolean isKombatEnabled();
    
    /**
     * Enables or disables Kombat mechanics for this player
     * 
     * @param enabled true to enable
     */
    void setKombatEnabled(boolean enabled);
}