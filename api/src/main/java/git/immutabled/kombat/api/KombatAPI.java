package git.immutabled.kombat.api;

import git.immutabled.kombat.api.configuration.KombatConfig;
import git.immutabled.kombat.api.events.EventBus;
import git.immutabled.kombat.api.player.KombatPlayer;

import java.util.Optional;
import java.util.UUID;

/**
 * Main API interface for Kombat plugin
 * This interface provides access to all core functionality of the Kombat system,
 * including combat management, player data, configuration, and events.
 * 
 * @author Immutable
 * @version 2025.0312.01
 */
public interface KombatAPI {

    /**
     * Gets the event bus for registering and firing events
     * 
     * @return the event bus
     */
    EventBus getEventBus();
    
    /**
     * Gets the global configuration
     * 
     * @return the configuration instance
     */
    KombatConfig getConfig();
    
    /**
     * Gets a Kombat player by UUID
     * 
     * @param uuid the player's UUID
     * @return an optional containing the player if found
     */
    Optional<KombatPlayer> getPlayer(UUID uuid);
    
    /**
     * Gets a Kombat player by name
     * 
     * @param name the player's name
     * @return an optional containing the player if found
     */
    Optional<KombatPlayer> getPlayer(String name);
    
    /**
     * Checks if the Kombat system is enabled
     * 
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();
    
    /**
     * Enables or disables the Kombat system
     * 
     * @param enabled true to enable, false to disable
     */
    void setEnabled(boolean enabled);
    
    /**
     * Gets the API version
     * 
     * @return the API version string
     */
    String getVersion();
    
    /**
     * Reloads the configuration and applies changes
     */
    void reload();
}