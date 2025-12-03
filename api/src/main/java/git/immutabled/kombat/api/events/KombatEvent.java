package git.immutabled.kombat.api.events;

/**
 * Base interface for all Kombat events
 * 
 * @author Immutable
 * @version 2025.0312.01
 * @since 1.0.0
 */
public interface KombatEvent {
    
    /**
     * Gets the timestamp when this event was created
     * 
     * @return the timestamp in milliseconds
     */
    long getTimestamp();
    
    /**
     * Checks if this event has been cancelled
     * 
     * @return true if cancelled
     */
    default boolean isCancelled() {
        return false;
    }
    
    /**
     * Sets the cancellation state of this event
     * 
     * @param cancelled true to cancel
     */
    default void setCancelled(boolean cancelled) {
        throw new UnsupportedOperationException("This event cannot be cancelled");
    }
}