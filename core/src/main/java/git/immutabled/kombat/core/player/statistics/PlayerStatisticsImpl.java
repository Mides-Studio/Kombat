package git.immutabled.kombat.core.player.statistics;

import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerStatisticsImpl implements PlayerStatistics {

    private final int kills;
    private final int deaths;
    private final int

    @Override
    public int getKills() {
        return 0;
    }


    @Override
    public int getDeaths() {
        return 0;
    }

    @Override
    public double getKDRatio() {
        return 0;
    }

    @Override
    public double getDamageDealt() {
        return 0;
    }

    @Override
    public double getDamageTaken() {
        return 0;
    }

    @Override
    public int getKillstreak() {
        return 0;
    }

    @Override
    public int getBestKillstreak() {
        return 0;
    }

    @Override
    public int getHitsLanded() {
        return 0;
    }

    @Override
    public int getHitsTaken() {
        return 0;
    }

    @Override
    public double getAccuracy() {
        return 0;
    }

    @Override
    public int getBestCombo() {
        return 0;
    }

    @Override
    public void addKill() {

    }

    @Override
    public void addDeath() {

    }

    @Override
    public void addDamageDealt(double damage) {

    }

    @Override
    public void addDamageTaken(double damage) {

    }

    @Override
    public void addHitLanded() {

    }

    @Override
    public void addHitTaken() {

    }

    @Override
    public void updateComboRecord(int combo) {

    }

    @Override
    public void reset() {

    }
}
