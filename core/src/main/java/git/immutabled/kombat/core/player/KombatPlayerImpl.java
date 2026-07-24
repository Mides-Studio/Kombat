package git.immutabled.kombat.core.player;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import git.immutabled.kombat.core.player.statistics.PlayerStatisticsImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class KombatPlayerImpl implements KombatPlayer {

    private final UUID uniqueId;
    private volatile String name;
    private volatile boolean online;
    private final PlayerStatistics statistics;
    private volatile @Nullable KnockbackProfile knockbackProfile;
    private volatile int comboCount;
    private volatile boolean kombatEnabled = true;

    public KombatPlayerImpl(UUID uniqueId, String name) {
        this(uniqueId, name, new PlayerStatisticsImpl());
    }

    public KombatPlayerImpl(UUID uniqueId, String name, PlayerStatistics statistics) {
        this.uniqueId = Objects.requireNonNull(uniqueId, "uniqueId");
        this.name = requireName(name);
        this.statistics = Objects.requireNonNull(statistics, "statistics");
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public PlayerStatistics getStatistics() {
        return statistics;
    }

    @Override
    public @Nullable KnockbackProfile getKnockbackProfile() {
        return knockbackProfile;
    }

    @Override
    public void setKnockbackProfile(@Nullable KnockbackProfile profile) {
        knockbackProfile = profile;
    }

    @Override
    public int getComboCount() {
        return comboCount;
    }

    @Override
    public void setComboCount(int combo) {
        if (combo < 0) {
            throw new IllegalArgumentException("Combo cannot be negative");
        }
        comboCount = combo;
        statistics.recordCombo(combo);
    }

    @Override
    public boolean isKombatEnabled() {
        return kombatEnabled;
    }

    @Override
    public void setKombatEnabled(boolean enabled) {
        kombatEnabled = enabled;
    }

    void updateIdentity(String name, boolean online) {
        this.name = requireName(name);
        this.online = online;
    }

    void setOnline(boolean online) {
        this.online = online;
    }

    private static String requireName(String name) {
        String checked = Objects.requireNonNull(name, "name").trim();
        if (checked.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be blank");
        }
        return checked;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof KombatPlayer player && uniqueId.equals(player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}
