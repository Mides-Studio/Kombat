package git.immutabled.kombat.api;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provider for accessing the Kombat API instance
 * This class provides a static accessor for the API implementation.
 * The implementation is set by the core plugin on startup.
 * 
 * @author Immutable
 * @version 2025.0312.01
 */
public final class KombatProvider {
    
    private static final AtomicReference<KombatAPI> INSTANCE = new AtomicReference<>();
    
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
        KombatAPI instance = INSTANCE.get();
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
        Objects.requireNonNull(api, "api");
        if (!INSTANCE.compareAndSet(null, api)) {
            throw new IllegalStateException("API instance already set");
        }
    }
    
    /**
     * Unsets the API instance
     * This should only be called by the core implementation on shutdown.
     */
    public static void unset() {
        INSTANCE.set(null);
    }
    
    /**
     * Checks if the API is loaded
     * 
     * @return true if loaded
     */
    public static boolean isLoaded() {
        return INSTANCE.get() != null;
    }
}
