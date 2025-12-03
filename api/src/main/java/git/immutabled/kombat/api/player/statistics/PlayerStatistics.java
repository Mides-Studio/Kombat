package git.immutabled.kombat.api.player.statistics;

/**
 * Tracks player combat statistics
 * Stores various combat-related metrics including kills, deaths,
 * damage dealt/taken, and other performance indicators.
 * 
 * @author Immutable
 * @version 2025.0312.01
 * @since 1.0.0
 */
public interface PlayerStatistics {
    
    /**
     * Gets the total kills
     * 
     * @return the kill count
     */
    int getKills();
    
    /**
     * Gets the total deaths
     * 
     * @return the death count
     */
    int getDeaths();
    
    /**
     * Gets the kill/death ratio
     * 
     * @return the K/D ratio
     */
    double getKDRatio();
    
    /**
     * Gets the total damage dealt
     * 
     * @return the damage dealt
     */
    double getDamageDealt();
    
    /**
     * Gets the total damage taken
     * 
     * @return the damage taken
     */
    double getDamageTaken();
    
    /**
     * Gets the current killstreak
     * 
     * @return the killstreak count
     */
    int getKillstreak();
    
    /**
     * Gets the highest killstreak achieved
     * 
     * @return the best killstreak
     */
    int getBestKillstreak();
    
    /**
     * Gets the total hits landed
     * 
     * @return the hit count
     */
    int getHitsLanded();
    
    /**
     * Gets the total hits taken
     * 
     * @return the hits taken count
     */
    int getHitsTaken();
    
    /**
     * Gets the hit accuracy percentage
     * 
     * @return the accuracy (0-100)
     */
    double getAccuracy();
    
    /**
     * Gets the longest combo achieved
     * 
     * @return the best combo count
     */
    int getBestCombo();
    
    /**
     * Increments the kill counter
     */
    void addKill();
    
    /**
     * Increments the death counter
     */
    void addDeath();
    
    /**
     * Adds damage dealt
     * 
     * @param damage the damage amount
     */
    void addDamageDealt(double damage);
    
    /**
     * Adds damage taken
     * 
     * @param damage the damage amount
     */
    void addDamageTaken(double damage);
    
    /**
     * Increments hits landed
     */
    void addHitLanded();
    
    /**
     * Increments hits taken
     */
    void addHitTaken();
    
    /**
     * Updates the combo record if current combo is higher
     * 
     * @param combo the current combo count
     */
    void updateComboRecord(int combo);
    
    /**
     * Resets all statistics
     */
    void reset();
}