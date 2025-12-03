package git.immutabled.kombat.api.knockback;

/**
 * Represents a knockback configuration profile
 * Defines horizontal and vertical knockback values, friction, and other
 * physics-related parameters for combat.
 *
 * @todo Consider adding more advanced settings
 *
 * @author Immutable
 * @version 2025.0312.01
 */
public interface KnockbackProfile {
    
    /**
     * Gets the profile name
     * 
     * @return the profile name
     */
    String getName();
    
    /**
     * Gets the horizontal knockback multiplier
     * 
     * @return the horizontal knockback value
     */
    double getHorizontalKnockback();
    
    /**
     * Gets the vertical knockback multiplier
     * 
     * @return the vertical knockback value
     */
    double getVerticalKnockback();
    
    /**
     * Gets the knockback friction value
     * 
     * @return the friction value (0.0 - 1.0)
     */
    double getFriction();
    
    /**
     * Gets the sprint knockback multiplier
     * 
     * @return the sprint multiplier
     */
    double getSprintMultiplier();
    
    /**
     * Gets the maximum knockback distance
     * 
     * @return the maximum distance in blocks
     */
    double getMaxDistance();
    
    /**
     * Checks if this profile allows air movement
     * 
     * @return true if air movement is allowed
     */
    boolean allowsAirMovement();
    
    /**
     * Checks if this profile applies extra knockback to sprinting players
     * 
     * @return true if extra sprint knockback is enabled
     */
    boolean hasSprintKnockback();
    
    /**
     * Creates a builder for this profile
     * 
     * @return a new builder instance
     */
    static Builder builder() {
        throw new UnsupportedOperationException("Must be implemented by the core");
    }
    
    /**
     * Builder for creating KnockbackProfile instances
     */
    interface Builder {
        Builder name(String name);
        Builder horizontal(double value);
        Builder vertical(double value);
        Builder friction(double value);
        Builder sprintMultiplier(double value);
        Builder maxDistance(double value);
        Builder allowAirMovement(boolean allow);
        Builder sprintKnockback(boolean enable);
        KnockbackProfile build();
    }
}