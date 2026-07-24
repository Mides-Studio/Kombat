package git.immutabled.kombat.minestorm;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.core.configuration.KombatConfigImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

final class MinestomConfigLoader {

    private static final String FILE_NAME = "kombat.properties";

    private MinestomConfigLoader() {
    }

    static KombatConfigImpl.Snapshot load(Path dataDirectory) {
        Properties properties = readOrCreate(dataDirectory);
        List<KnockbackProfile> profiles = new ArrayList<>();
        for (String rawName : properties.getProperty("knockback.profiles", "classic,boxing").split(",")) {
            String name = rawName.trim();
            if (!name.isEmpty()) {
                profiles.add(readProfile(properties, name));
            }
        }
        if (profiles.isEmpty()) {
            profiles.add(readProfile(defaults(), "classic"));
        }

        return KombatConfigImpl.Snapshot.builder()
                .attackSpeed(number(properties, "combat.attack-speed", 24.0))
                .sweepEnabled(bool(properties, "combat.enable-sweep", false))
                .locale(Locale.forLanguageTag(properties.getProperty("language", "en")))
                .profiles(profiles)
                .defaultProfile(properties.getProperty("knockback.default-profile", "classic"))
                .statisticsEnabled(bool(properties, "features.statistics", true))
                .comboSystemEnabled(bool(properties, "features.combos", true))
                .comboTimeout(longNumber(properties, "combo.timeout-ms", 3_000))
                .debugMode(bool(properties, "debug", false))
                .value("combat.hit-delay-ms", longNumber(properties, "combat.hit-delay-ms", 450))
                .value(
                        "combat.base-damage-multiplier",
                        number(properties, "combat.base-damage-multiplier", 1.0)
                )
                .value(
                        "combat.critical-damage-multiplier",
                        number(properties, "combat.critical-damage-multiplier", 1.5)
                )
                .value(
                        "combat.first-hit-damage-boost",
                        number(properties, "combat.first-hit-damage-boost", 1.0)
                )
                .value("combat.critical-enabled", bool(properties, "combat.critical-enabled", true))
                .value("features.knockback", bool(properties, "features.knockback", true))
                .build();
    }

    static void setDefaultProfile(Path dataDirectory, String profileName) {
        Properties properties = readOrCreate(dataDirectory);
        properties.setProperty("knockback.default-profile", profileName);
        write(dataDirectory, properties);
    }

    private static KnockbackProfile readProfile(Properties properties, String name) {
        String prefix = "knockback." + name + ".";
        return KnockbackProfile.builder()
                .name(name)
                .horizontal(number(properties, prefix + "horizontal", 0.40))
                .vertical(number(properties, prefix + "vertical", 0.36))
                .friction(number(properties, prefix + "friction", 0.60))
                .sprintMultiplier(number(properties, prefix + "sprint-multiplier", 1.35))
                .maxDistance(number(properties, prefix + "max-distance", 1.20))
                .allowAirMovement(bool(properties, prefix + "air-movement", true))
                .sprintKnockback(bool(properties, prefix + "sprint-knockback", true))
                .build();
    }

    private static Properties readOrCreate(Path dataDirectory) {
        Path file = dataDirectory.resolve(FILE_NAME);
        try {
            Files.createDirectories(dataDirectory);
            if (Files.notExists(file)) {
                Properties properties = defaults();
                write(dataDirectory, properties);
                return properties;
            }
            Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(file)) {
                properties.load(input);
            }
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load " + file, exception);
        }
    }

    private static void write(Path dataDirectory, Properties properties) {
        Path file = dataDirectory.resolve(FILE_NAME);
        try {
            Files.createDirectories(dataDirectory);
            try (OutputStream output = Files.newOutputStream(file)) {
                properties.store(output, "Kombat Minestom configuration");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save " + file, exception);
        }
    }

    private static Properties defaults() {
        Properties properties = new Properties();
        properties.setProperty("combat.attack-speed", "24.0");
        properties.setProperty("combat.enable-sweep", "false");
        properties.setProperty("combat.hit-delay-ms", "450");
        properties.setProperty("combat.base-damage-multiplier", "1.0");
        properties.setProperty("combat.critical-damage-multiplier", "1.5");
        properties.setProperty("combat.first-hit-damage-boost", "1.0");
        properties.setProperty("combat.critical-enabled", "true");
        properties.setProperty("knockback.default-profile", "classic");
        properties.setProperty("knockback.profiles", "classic,boxing");
        putProfile(properties, "classic", 0.40, 0.36, 0.60, 1.35, 1.20);
        putProfile(properties, "boxing", 0.36, 0.34, 0.55, 1.25, 1.05);
        properties.setProperty("features.knockback", "true");
        properties.setProperty("features.statistics", "true");
        properties.setProperty("features.combos", "true");
        properties.setProperty("combo.timeout-ms", "3000");
        properties.setProperty("language", "en");
        properties.setProperty("debug", "false");
        return properties;
    }

    private static void putProfile(
            Properties properties,
            String name,
            double horizontal,
            double vertical,
            double friction,
            double sprintMultiplier,
            double maxDistance
    ) {
        String prefix = "knockback." + name + ".";
        properties.setProperty(prefix + "horizontal", Double.toString(horizontal));
        properties.setProperty(prefix + "vertical", Double.toString(vertical));
        properties.setProperty(prefix + "friction", Double.toString(friction));
        properties.setProperty(prefix + "sprint-multiplier", Double.toString(sprintMultiplier));
        properties.setProperty(prefix + "max-distance", Double.toString(maxDistance));
        properties.setProperty(prefix + "air-movement", "true");
        properties.setProperty(prefix + "sprint-knockback", "true");
    }

    private static double number(Properties properties, String key, double fallback) {
        try {
            return Double.parseDouble(properties.getProperty(key, Double.toString(fallback)));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static long longNumber(Properties properties, String key, long fallback) {
        try {
            return Long.parseLong(properties.getProperty(key, Long.toString(fallback)));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static boolean bool(Properties properties, String key, boolean fallback) {
        return Boolean.parseBoolean(properties.getProperty(key, Boolean.toString(fallback)));
    }
}
