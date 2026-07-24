package git.immutabled.kombat.core.knockback;

import git.immutabled.kombat.api.knockback.KnockbackProfile;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Runtime registry for knockback profiles.
 */
public final class KnockbackRegistry {

    private final ConcurrentMap<String, KnockbackProfile> profiles = new ConcurrentHashMap<>();
    private volatile String defaultProfile;

    public KnockbackRegistry(Collection<? extends KnockbackProfile> profiles, String defaultProfile) {
        replaceAll(profiles, defaultProfile);
    }

    public synchronized void replaceAll(
            Collection<? extends KnockbackProfile> replacements,
            String newDefaultProfile
    ) {
        Objects.requireNonNull(replacements, "replacements");
        String normalizedDefault = normalize(newDefaultProfile);
        ConcurrentMap<String, KnockbackProfile> checked = new ConcurrentHashMap<>();
        for (KnockbackProfile profile : replacements) {
            KnockbackProfile present = checked.putIfAbsent(normalize(profile.getName()), profile);
            if (present != null) {
                throw new IllegalArgumentException("Duplicate knockback profile: " + profile.getName());
            }
        }
        if (!checked.containsKey(normalizedDefault)) {
            throw new IllegalArgumentException("Default knockback profile does not exist: " + normalizedDefault);
        }
        profiles.clear();
        profiles.putAll(checked);
        defaultProfile = normalizedDefault;
    }

    public KnockbackProfile register(KnockbackProfile profile) {
        Objects.requireNonNull(profile, "profile");
        profiles.put(normalize(profile.getName()), profile);
        return profile;
    }

    public Optional<KnockbackProfile> find(String name) {
        return name == null ? Optional.empty() : Optional.ofNullable(profiles.get(normalize(name)));
    }

    public List<KnockbackProfile> profiles() {
        return profiles.values().stream()
                .sorted(java.util.Comparator.comparing(KnockbackProfile::getName))
                .toList();
    }

    public KnockbackProfile defaultProfile() {
        return profiles.get(defaultProfile);
    }

    public void setDefault(String name) {
        String normalized = normalize(name);
        if (!profiles.containsKey(normalized)) {
            throw new IllegalArgumentException("Unknown knockback profile: " + name);
        }
        defaultProfile = normalized;
    }

    public boolean remove(String name) {
        String normalized = normalize(name);
        if (normalized.equals(defaultProfile)) {
            throw new IllegalStateException("The default knockback profile cannot be removed");
        }
        return profiles.remove(normalized) != null;
    }

    private static String normalize(String value) {
        return Objects.requireNonNull(value, "name").trim().toLowerCase(Locale.ROOT);
    }
}
