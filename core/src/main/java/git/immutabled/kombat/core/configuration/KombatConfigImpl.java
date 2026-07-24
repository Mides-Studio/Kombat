package git.immutabled.kombat.core.configuration;

import git.immutabled.kombat.api.configuration.KombatConfig;
import git.immutabled.kombat.api.knockback.KnockbackProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Platform-neutral configuration snapshot.
 *
 * <p>Platform adapters can replace the snapshot atomically on reload while API
 * consumers keep the same configuration instance.</p>
 */
public final class KombatConfigImpl implements KombatConfig {

    private volatile Snapshot snapshot;
    private final Runnable reloadAction;
    private final Runnable saveAction;

    public KombatConfigImpl() {
        this(Snapshot.defaults(), () -> { }, () -> { });
    }

    public KombatConfigImpl(Snapshot snapshot, Runnable reloadAction, Runnable saveAction) {
        this.snapshot = Objects.requireNonNull(snapshot, "snapshot");
        this.reloadAction = Objects.requireNonNull(reloadAction, "reloadAction");
        this.saveAction = Objects.requireNonNull(saveAction, "saveAction");
    }

    public void replace(Snapshot snapshot) {
        this.snapshot = Objects.requireNonNull(snapshot, "snapshot");
    }

    @Override
    public double getAttackSpeed() {
        return snapshot.attackSpeed();
    }

    @Override
    public boolean isSweepEnabled() {
        return snapshot.sweepEnabled();
    }

    @Override
    public Locale getLocale() {
        return snapshot.locale();
    }

    @Override
    public KnockbackProfile getDefaultKnockbackProfile() {
        Snapshot current = snapshot;
        return current.profiles().get(current.defaultProfile());
    }

    @Override
    public Optional<KnockbackProfile> getKnockbackProfile(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshot.profiles().get(normalize(name)));
    }

    @Override
    public List<KnockbackProfile> getKnockbackProfiles() {
        return List.copyOf(snapshot.profiles().values());
    }

    @Override
    public boolean isStatisticsEnabled() {
        return snapshot.statisticsEnabled();
    }

    @Override
    public boolean isComboSystemEnabled() {
        return snapshot.comboSystemEnabled();
    }

    @Override
    public long getComboTimeout() {
        return snapshot.comboTimeout();
    }

    @Override
    public boolean isDebugMode() {
        return snapshot.debugMode();
    }

    @Override
    public Optional<Object> get(String path) {
        return Optional.ofNullable(snapshot.values().get(path));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String path, T defaultValue) {
        Object value = snapshot.values().get(path);
        if (value == null) {
            return defaultValue;
        }
        try {
            if (defaultValue instanceof Double && value instanceof Number number) {
                return (T) Double.valueOf(number.doubleValue());
            }
            if (defaultValue instanceof Integer && value instanceof Number number) {
                return (T) Integer.valueOf(number.intValue());
            }
            if (defaultValue instanceof Long && value instanceof Number number) {
                return (T) Long.valueOf(number.longValue());
            }
            return (T) value;
        } catch (ClassCastException ignored) {
            return defaultValue;
        }
    }

    @Override
    public void reload() {
        reloadAction.run();
    }

    @Override
    public void save() {
        saveAction.run();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Immutable configuration data used for safe, atomic reloads.
     */
    public record Snapshot(
            double attackSpeed,
            boolean sweepEnabled,
            Locale locale,
            String defaultProfile,
            Map<String, KnockbackProfile> profiles,
            boolean statisticsEnabled,
            boolean comboSystemEnabled,
            long comboTimeout,
            boolean debugMode,
            Map<String, Object> values
    ) {
        public Snapshot {
            if (!Double.isFinite(attackSpeed) || attackSpeed <= 0) {
                throw new IllegalArgumentException("Attack speed must be positive and finite");
            }
            locale = Objects.requireNonNull(locale, "locale");
            defaultProfile = normalize(Objects.requireNonNull(defaultProfile, "defaultProfile"));
            profiles = normalizeProfiles(profiles);
            if (!profiles.containsKey(defaultProfile)) {
                throw new IllegalArgumentException("Default knockback profile does not exist: " + defaultProfile);
            }
            if (comboTimeout < 0) {
                throw new IllegalArgumentException("Combo timeout cannot be negative");
            }
            values = Map.copyOf(Objects.requireNonNull(values, "values"));
        }

        public static Snapshot defaults() {
            KnockbackProfile classic = KnockbackProfile.builder()
                    .name("classic")
                    .horizontal(0.40)
                    .vertical(0.36)
                    .friction(0.60)
                    .sprintMultiplier(1.35)
                    .maxDistance(1.20)
                    .allowAirMovement(true)
                    .sprintKnockback(true)
                    .build();
            Map<String, Object> values = Map.of(
                    "combat.hit-delay-ms", 450L,
                    "combat.base-damage-multiplier", 1.0D,
                    "combat.critical-damage-multiplier", 1.5D,
                    "combat.first-hit-damage-boost", 1.0D,
                    "combat.critical-enabled", true,
                    "features.knockback", true
            );
            return new Snapshot(
                    24.0,
                    false,
                    Locale.ENGLISH,
                    classic.getName(),
                    Map.of(classic.getName(), classic),
                    true,
                    true,
                    3_000,
                    false,
                    values
            );
        }

        public static Builder builder() {
            return new Builder();
        }

        private static Map<String, KnockbackProfile> normalizeProfiles(
                Collection<? extends Map.Entry<String, KnockbackProfile>> entries
        ) {
            Map<String, KnockbackProfile> normalized = new LinkedHashMap<>();
            for (Map.Entry<String, KnockbackProfile> entry : entries) {
                KnockbackProfile profile = Objects.requireNonNull(entry.getValue(), "profile");
                normalized.put(normalize(profile.getName()), profile);
            }
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("At least one knockback profile is required");
            }
            return Map.copyOf(normalized);
        }

        private static Map<String, KnockbackProfile> normalizeProfiles(Map<String, KnockbackProfile> profiles) {
            return normalizeProfiles(Objects.requireNonNull(profiles, "profiles").entrySet());
        }
    }

    public static final class Builder {
        private double attackSpeed = 24.0;
        private boolean sweepEnabled;
        private Locale locale = Locale.ENGLISH;
        private String defaultProfile = "classic";
        private final Map<String, KnockbackProfile> profiles = new LinkedHashMap<>();
        private boolean statisticsEnabled = true;
        private boolean comboSystemEnabled = true;
        private long comboTimeout = 3_000;
        private boolean debugMode;
        private final Map<String, Object> values = new LinkedHashMap<>();

        public Builder() {
            Snapshot defaults = Snapshot.defaults();
            profiles.putAll(defaults.profiles());
            values.putAll(defaults.values());
        }

        public Builder attackSpeed(double value) {
            attackSpeed = value;
            return this;
        }

        public Builder sweepEnabled(boolean value) {
            sweepEnabled = value;
            return this;
        }

        public Builder locale(Locale value) {
            locale = value;
            return this;
        }

        public Builder defaultProfile(String value) {
            defaultProfile = value;
            return this;
        }

        public Builder profiles(Collection<? extends KnockbackProfile> values) {
            profiles.clear();
            for (KnockbackProfile profile : new ArrayList<>(values)) {
                profiles.put(normalize(profile.getName()), profile);
            }
            return this;
        }

        public Builder statisticsEnabled(boolean value) {
            statisticsEnabled = value;
            return this;
        }

        public Builder comboSystemEnabled(boolean value) {
            comboSystemEnabled = value;
            return this;
        }

        public Builder comboTimeout(long value) {
            comboTimeout = value;
            return this;
        }

        public Builder debugMode(boolean value) {
            debugMode = value;
            return this;
        }

        public Builder value(String path, Object value) {
            values.put(path, value);
            return this;
        }

        public Snapshot build() {
            return new Snapshot(
                    attackSpeed,
                    sweepEnabled,
                    locale,
                    defaultProfile,
                    profiles,
                    statisticsEnabled,
                    comboSystemEnabled,
                    comboTimeout,
                    debugMode,
                    values
            );
        }
    }
}
