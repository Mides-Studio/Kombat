package git.immutabled.kombat.core.configuration;

import git.immutabled.kombat.api.configuration.KombatConfig;
import git.immutabled.kombat.api.knockback.KnockbackProfile;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class KombatConfigImpl implements KombatConfig {
    @Override
    public double getAttackSpeed() {
        return 0;
    }

    @Override
    public boolean isSweepEnabled() {
        return false;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public KnockbackProfile getDefaultKnockbackProfile() {
        return null;
    }

    @Override
    public Optional<KnockbackProfile> getKnockbackProfile(String name) {
        return Optional.empty();
    }

    @Override
    public List<KnockbackProfile> getKnockbackProfiles() {
        return List.of();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return false;
    }

    @Override
    public boolean isComboSystemEnabled() {
        return false;
    }

    @Override
    public long getComboTimeout() {
        return 0;
    }

    @Override
    public boolean isDebugMode() {
        return false;
    }

    @Override
    public Optional<Object> get(String path) {
        return Optional.empty();
    }

    @Override
    public <T> T get(String path, T defaultValue) {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
