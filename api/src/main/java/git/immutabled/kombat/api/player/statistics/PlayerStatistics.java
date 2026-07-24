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

    void setKills(int kills);

    void setDeaths(int deaths);

    void setDamageDealt(double damageDealt);

    void setDamageTaken(double damageTaken);

    void setHitsLanded(int hitsLanded);
    void setHitsTaken(int hitsTaken);
    void setBestCombo(int bestCombo);
    void setKillstreak(int killstreak);
    void setBestKillstreak(int bestKillstreak);

    /**
     * Records a successful hit and its final damage.
     *
     * @param damage final damage dealt
     */
    void recordHitLanded(double damage);

    /**
     * Records a received hit and its final damage.
     *
     * @param damage final damage taken
     */
    void recordHitTaken(double damage);

    /**
     * Records a kill and advances the current kill streak.
     */
    void recordKill();

    /**
     * Records a death and resets the current kill streak.
     */
    void recordDeath();

    /**
     * Records a combo if it is a new personal best.
     *
     * @param combo combo length
     */
    void recordCombo(int combo);

    /**
     * Resets all statistics
     */
    void reset();
}
