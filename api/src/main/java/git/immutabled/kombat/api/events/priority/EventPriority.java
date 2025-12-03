package git.immutabled.kombat.api.events.priority;

/**
 * Event listener priority levels
 * Determines the order in which event listeners are called.
 * 
 * @author Immutable
 * @version 2025.0312.01
 * @since 1.0.0
 */
public enum EventPriority {
    
    /**
     * Called first, use for early modifications
     */
    LOWEST(0),
    
    /**
     * Called after LOWEST
     */
    LOW(1),
    
    /**
     * Normal priority, defaults for most listeners
     */
    NORMAL(2),
    
    /**
     * Called after NORMAL
     */
    HIGH(3),
    
    /**
     * Called last, use for final checks or monitoring
     */
    HIGHEST(4),
    
    /**
     * Called after all other priorities, cannot modify event
     */
    MONITOR(5);
    
    private final int slot;
    
    EventPriority(int slot) {
        this.slot = slot;
    }
    
    /**
     * Gets the priority slot number
     * 
     * @return the slot
     */
    public int getSlot() {
        return slot;
    }
}