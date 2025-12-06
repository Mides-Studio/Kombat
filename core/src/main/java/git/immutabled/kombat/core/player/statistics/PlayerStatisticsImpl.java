package git.immutabled.kombat.core.player.statistics;

import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerStatisticsImpl implements PlayerStatistics {

    private int kills;
    private int deaths;
    private int killstreak;
    private int bestKillstreak;
    private double damageDealt;
    private double damageTaken;
    private int hitsLanded;
    private int hitsTaken;
    private int bestCombo;
    private double accuracy;
    private double KDRatio;

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
