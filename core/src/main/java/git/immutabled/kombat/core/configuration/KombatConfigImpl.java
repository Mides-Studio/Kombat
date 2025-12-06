package git.immutabled.kombat.core.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import git.immutabled.kombat.api.configuration.KombatConfig;
import git.immutabled.kombat.api.knockback.KnockbackProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementation of the KombatConfig interface using OkaeriConfig
 * Provides configuration settings for the Kombat plugin.
 * @todo Implement the comments and javadocs for each configuration field.
 *
 *
 * @author Immutable
 * @version 2025.0312.01
 */
@Header(" Kombat Configuration \n Configure the Kombat plugin settings below ")
@Setter @Getter
public class KombatConfigImpl extends OkaeriConfig implements KombatConfig {


    private double attackSpeed = 1.0;
    private boolean sweepEnabled = true;
    private Locale locale = Locale.ENGLISH;
    private KnockbackProfile defaultKnockbackProfile;
    private List<KnockbackProfile> knockbackProfiles;
    private HashMap<String, KnockbackProfile> knockbackProfileByName;
    private boolean statisticsEnabled = false;
    private boolean comboSystemEnabled = false;
    private long comboTimeout = 3000;
    private boolean debugMode = false;


    @Override
    public Optional<KnockbackProfile> getKnockbackProfile(String name) {
        return Optional.empty();
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

    protected void serialize() {
        // Custom serialization logic if needed
    }

    public static void deserialize() {
        // Custom deserialization logic if needed
    }
}
