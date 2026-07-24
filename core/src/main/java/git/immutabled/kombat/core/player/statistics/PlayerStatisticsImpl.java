package git.immutabled.kombat.core.player.statistics;

import git.immutabled.kombat.api.player.statistics.PlayerStatistics;

/**
 * Mutable statistics owned by a single player.
 *
 * <p>Mutating operations are synchronized because Folia and Minestom may
 * update different players on different threads.</p>
 */
public final class PlayerStatisticsImpl implements PlayerStatistics {

    private int kills;
    private int deaths;
    private int killstreak;
    private int bestKillstreak;
    private double damageDealt;
    private double damageTaken;
    private int hitsLanded;
    private int hitsTaken;
    private int bestCombo;

    @Override
    public synchronized int getKills() {
        return kills;
    }

    @Override
    public synchronized int getDeaths() {
        return deaths;
    }

    @Override
    public synchronized double getKDRatio() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    @Override
    public synchronized double getDamageDealt() {
        return damageDealt;
    }

    @Override
    public synchronized double getDamageTaken() {
        return damageTaken;
    }

    @Override
    public synchronized int getKillstreak() {
        return killstreak;
    }

    @Override
    public synchronized int getBestKillstreak() {
        return bestKillstreak;
    }

    @Override
    public synchronized int getHitsLanded() {
        return hitsLanded;
    }

    @Override
    public synchronized int getHitsTaken() {
        return hitsTaken;
    }

    @Override
    public synchronized double getAccuracy() {
        int attempts = hitsLanded + hitsTaken;
        return attempts == 0 ? 0 : (hitsLanded * 100.0) / attempts;
    }

    @Override
    public synchronized int getBestCombo() {
        return bestCombo;
    }

    @Override
    public synchronized void setKills(int kills) {
        this.kills = nonNegative(kills);
    }

    @Override
    public synchronized void setDeaths(int deaths) {
        this.deaths = nonNegative(deaths);
    }

    @Override
    public synchronized void setDamageDealt(double damageDealt) {
        this.damageDealt = nonNegative(damageDealt);
    }

    @Override
    public synchronized void setDamageTaken(double damageTaken) {
        this.damageTaken = nonNegative(damageTaken);
    }

    @Override
    public synchronized void setHitsLanded(int hitsLanded) {
        this.hitsLanded = nonNegative(hitsLanded);
    }

    @Override
    public synchronized void setHitsTaken(int hitsTaken) {
        this.hitsTaken = nonNegative(hitsTaken);
    }

    @Override
    public synchronized void setBestCombo(int bestCombo) {
        this.bestCombo = nonNegative(bestCombo);
    }

    @Override
    public synchronized void setKillstreak(int killstreak) {
        this.killstreak = nonNegative(killstreak);
    }

    @Override
    public synchronized void setBestKillstreak(int bestKillstreak) {
        this.bestKillstreak = nonNegative(bestKillstreak);
    }

    @Override
    public synchronized void recordHitLanded(double damage) {
        hitsLanded++;
        damageDealt += nonNegative(damage);
    }

    @Override
    public synchronized void recordHitTaken(double damage) {
        hitsTaken++;
        damageTaken += nonNegative(damage);
    }

    @Override
    public synchronized void recordKill() {
        kills++;
        killstreak++;
        bestKillstreak = Math.max(bestKillstreak, killstreak);
    }

    @Override
    public synchronized void recordDeath() {
        deaths++;
        killstreak = 0;
    }

    @Override
    public synchronized void recordCombo(int combo) {
        bestCombo = Math.max(bestCombo, nonNegative(combo));
    }

    @Override
    public synchronized void reset() {
        kills = 0;
        deaths = 0;
        killstreak = 0;
        bestKillstreak = 0;
        damageDealt = 0;
        damageTaken = 0;
        hitsLanded = 0;
        hitsTaken = 0;
        bestCombo = 0;
    }

    private static int nonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Statistics cannot be negative");
        }
        return value;
    }

    private static double nonNegative(double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Statistics must be positive and finite");
        }
        return value;
    }
}
