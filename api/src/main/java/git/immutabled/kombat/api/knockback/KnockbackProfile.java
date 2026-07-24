package git.immutabled.kombat.api.knockback;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a knockback configuration profile
 * Defines horizontal and vertical knockback values, friction, and other
 * physics-related parameters for combat.
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
    boolean isAirMovement();
    
    /**
     * Checks if this profile applies extra knockback to sprinting players
     * 
     * @return true if extra sprint knockback is enabled
     */
    boolean isSprintKnockback();
    
    /**
     * Creates a builder for this profile
     * 
     * @return a new builder instance
     */
    static Builder builder() {
        return new DefaultBuilder();
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

    /**
     * Default immutable implementation used by the public builder.
     */
    record Immutable(
            String name,
            double horizontalKnockback,
            double verticalKnockback,
            double friction,
            double sprintMultiplier,
            double maxDistance,
            boolean airMovement,
            boolean sprintKnockback
    ) implements KnockbackProfile {

        public Immutable {
            name = normalizeName(name);
            requireFinite("horizontal", horizontalKnockback);
            requireFinite("vertical", verticalKnockback);
            requireFinite("friction", friction);
            requireFinite("sprint multiplier", sprintMultiplier);
            requireFinite("max distance", maxDistance);
            if (horizontalKnockback < 0 || verticalKnockback < 0 || sprintMultiplier < 0 || maxDistance <= 0) {
                throw new IllegalArgumentException("Knockback values must be positive");
            }
            if (friction < 0 || friction > 1) {
                throw new IllegalArgumentException("Friction must be between 0 and 1");
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public double getHorizontalKnockback() {
            return horizontalKnockback;
        }

        @Override
        public double getVerticalKnockback() {
            return verticalKnockback;
        }

        @Override
        public double getFriction() {
            return friction;
        }

        @Override
        public double getSprintMultiplier() {
            return sprintMultiplier;
        }

        @Override
        public double getMaxDistance() {
            return maxDistance;
        }

        @Override
        public boolean isAirMovement() {
            return airMovement;
        }

        @Override
        public boolean isSprintKnockback() {
            return sprintKnockback;
        }
    }

    final class DefaultBuilder implements Builder {
        private String name;
        private double horizontal = 0.40;
        private double vertical = 0.36;
        private double friction = 0.60;
        private double sprintMultiplier = 1.35;
        private double maxDistance = 1.20;
        private boolean airMovement = true;
        private boolean sprintKnockback = true;

        private DefaultBuilder() {
        }

        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder horizontal(double value) {
            this.horizontal = value;
            return this;
        }

        @Override
        public Builder vertical(double value) {
            this.vertical = value;
            return this;
        }

        @Override
        public Builder friction(double value) {
            this.friction = value;
            return this;
        }

        @Override
        public Builder sprintMultiplier(double value) {
            this.sprintMultiplier = value;
            return this;
        }

        @Override
        public Builder maxDistance(double value) {
            this.maxDistance = value;
            return this;
        }

        @Override
        public Builder allowAirMovement(boolean allow) {
            this.airMovement = allow;
            return this;
        }

        @Override
        public Builder sprintKnockback(boolean enable) {
            this.sprintKnockback = enable;
            return this;
        }

        @Override
        public KnockbackProfile build() {
            return new Immutable(
                    name,
                    horizontal,
                    vertical,
                    friction,
                    sprintMultiplier,
                    maxDistance,
                    airMovement,
                    sprintKnockback
            );
        }
    }

    private static String normalizeName(String name) {
        String normalized = Objects.requireNonNull(name, "name")
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]", "-")
                .replaceAll("-{2,}", "-");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Profile name cannot be blank");
        }
        return normalized;
    }

    private static void requireFinite(String field, double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(field + " must be finite");
        }
    }
}
