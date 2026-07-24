package git.immutabled.kombat.paper;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.core.configuration.KombatConfigImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

final class PaperConfigLoader {

    private PaperConfigLoader() {
    }

    static KombatConfigImpl.Snapshot load(FileConfiguration source, Logger logger) {
        List<KnockbackProfile> profiles = loadProfiles(source, logger);
        String defaultProfile = source.getString("knockback.default-profile", "classic");

        try {
            return KombatConfigImpl.Snapshot.builder()
                    .attackSpeed(source.getDouble("combat.attack-speed", 24.0))
                    .sweepEnabled(source.getBoolean("combat.enable-sweep", false))
                    .locale(Locale.forLanguageTag(source.getString("language", "en")))
                    .profiles(profiles)
                    .defaultProfile(defaultProfile)
                    .statisticsEnabled(source.getBoolean("features.statistics", true))
                    .comboSystemEnabled(source.getBoolean("features.combos", true))
                    .comboTimeout(source.getLong("combo.timeout-ms", 3_000))
                    .debugMode(source.getBoolean("debug", false))
                    .value("combat.hit-delay-ms", source.getLong("combat.hit-delay-ms", 450))
                    .value(
                            "combat.base-damage-multiplier",
                            source.getDouble("combat.base-damage-multiplier", 1.0)
                    )
                    .value(
                            "combat.critical-damage-multiplier",
                            source.getDouble("combat.critical-damage-multiplier", 1.5)
                    )
                    .value(
                            "combat.first-hit-damage-boost",
                            source.getDouble("combat.first-hit-damage-boost", 1.0)
                    )
                    .value("combat.critical-enabled", source.getBoolean("combat.critical-enabled", true))
                    .value("combat.critical-particles", source.getBoolean("combat.critical-particles", true))
                    .value("combat.hit-sound", source.getBoolean("combat.hit-sound", true))
                    .value("features.knockback", source.getBoolean("features.knockback", true))
                    .build();
        } catch (IllegalArgumentException exception) {
            logger.warning("Invalid configuration; using safe defaults: " + exception.getMessage());
            return KombatConfigImpl.Snapshot.defaults();
        }
    }

    private static List<KnockbackProfile> loadProfiles(FileConfiguration source, Logger logger) {
        ConfigurationSection section = source.getConfigurationSection("knockback.profiles");
        if (section == null) {
            return List.of(KombatConfigImpl.Snapshot.defaults().profiles().get("classic"));
        }

        List<KnockbackProfile> profiles = new ArrayList<>();
        for (String name : section.getKeys(false)) {
            String path = "knockback.profiles." + name + ".";
            try {
                profiles.add(KnockbackProfile.builder()
                        .name(name)
                        .horizontal(source.getDouble(path + "horizontal", 0.40))
                        .vertical(source.getDouble(path + "vertical", 0.36))
                        .friction(source.getDouble(path + "friction", 0.60))
                        .sprintMultiplier(source.getDouble(path + "sprint-multiplier", 1.35))
                        .maxDistance(source.getDouble(path + "max-distance", 1.20))
                        .allowAirMovement(source.getBoolean(path + "air-movement", true))
                        .sprintKnockback(source.getBoolean(path + "sprint-knockback", true))
                        .build());
            } catch (IllegalArgumentException exception) {
                logger.warning("Ignoring invalid knockback profile '" + name + "': " + exception.getMessage());
            }
        }
        if (profiles.isEmpty()) {
            profiles.add(KombatConfigImpl.Snapshot.defaults().profiles().get("classic"));
        }
        return profiles;
    }
}
