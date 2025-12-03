package git.immutabled.kombat.api.events.defaults;


import git.immutabled.kombat.api.events.KombatEvent;
import git.immutabled.kombat.api.player.KombatPlayer;

/**
 * Called when a player damages another player
 * This event is cancellable.
 * 
 * @author Immutable
 * @version 2025.0312.01
 */
public interface PlayerDamageEvent extends KombatEvent {
    
    /**
     * Gets the attacker
     * 
     * @return the attacking player
     */
    KombatPlayer getAttacker();
    
    /**
     * Gets the victim
     * 
     * @return the damaged player
     */
    KombatPlayer getVictim();
    
    /**
     * Gets the damage amount
     * 
     * @return the damage
     */
    double getDamage();
    
    /**
     * Sets the damage amount
     * 
     * @param damage the new damage value
     */
    void setDamage(double damage);
    
    /**
     * Gets the damage cause
     * 
     * @return the cause
     */
    DamageCause getCause();
    
    /**
     * Checks if this was a critical hit
     * 
     * @return true if critical
     */
    boolean isCritical();
    
    /**
     * Sets whether this is a critical hit
     * 
     * @param critical true for critical
     */
    void setCritical(boolean critical);
    
    /**
     * Checks if knockback should be applied
     * 
     * @return true if knockback enabled
     */
    boolean hasKnockback();
    
    /**
     * Sets whether knockback should be applied
     * 
     * @param knockback true to enable knockback
     */
    void setKnockback(boolean knockback);

    enum DamageCause {

        /**
         * Damage from a melee attack
         */
        MELEE,

        /**
         * Damage from a projectile (arrow, snowball, etc)
         */
        PROJECTILE,

        /**
         * Damage from a sweep attack
         */
        SWEEP,

        /**
         * Damage from environmental hazards
         */
        ENVIRONMENTAL,

        /**
         * Damage from an unknown source
         */
        UNKNOWN
    }
}

