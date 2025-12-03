package git.immutabled.kombat.api;

/**
 * Provider for accessing the Kombat API instance
 * This class provides a static accessor for the API implementation.
 * The implementation is set by the core plugin on startup.
 * 
 * @author Immutable
 * @version 2025.0312.01
 */
public final class KombatProvider {
    
    private static KombatAPI instance;
    
    private KombatProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
    
    /**
     * Gets the Kombat API instance
     * 
     * @return the API instance
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static KombatAPI get() {
        if (instance == null) {
            throw new IllegalStateException("Kombat API is not loaded yet");
        }
        return instance;
    }
    
    /**
     * Sets the API instance
     * This should only be called by the core implementation.
     * 
     * @param api the API instance
     * @throws IllegalStateException if the API is already set
     */
    public static void set(KombatAPI api) {
        if (instance != null) {
            throw new IllegalStateException("API instance already set");
        }
        instance = api;
    }
    
    /**
     * Unsets the API instance
     * This should only be called by the core implementation on shutdown.
     */
    public static void unset() {
        instance = null;
    }
    
    /**
     * Checks if the API is loaded
     * 
     * @return true if loaded
     */
    public static boolean isLoaded() {
        return instance != null;
    }
}