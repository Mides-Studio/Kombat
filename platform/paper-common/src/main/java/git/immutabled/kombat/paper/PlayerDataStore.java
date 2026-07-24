package git.immutabled.kombat.paper;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import git.immutabled.kombat.core.KombatAPIImpl;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lightweight YAML persistence for player preferences and statistics.
 */
final class PlayerDataStore {

    private final File file;
    private final Logger logger;
    private final YamlConfiguration data;
    private final ExecutorService writer;
    private final Object fileLock = new Object();

    PlayerDataStore(Path dataFolder, Logger logger) {
        this.logger = logger;
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create plugin data directory", exception);
        }
        file = dataFolder.resolve("players.yml").toFile();
        data = YamlConfiguration.loadConfiguration(file);
        writer = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "kombat-player-data");
            thread.setDaemon(true);
            return thread;
        });
    }

    void load(KombatPlayer player, KombatAPIImpl api) {
        String root = root(player);
        synchronized (fileLock) {
            player.setKombatEnabled(data.getBoolean(root + ".kombat-enabled", true));
            String profileName = data.getString(root + ".knockback-profile");
            if (profileName != null) {
                api.getKnockbackRegistry().find(profileName).ifPresent(player::setKnockbackProfile);
            }
            PlayerStatistics statistics = player.getStatistics();
            statistics.setKills(data.getInt(root + ".statistics.kills"));
            statistics.setDeaths(data.getInt(root + ".statistics.deaths"));
            statistics.setKillstreak(data.getInt(root + ".statistics.killstreak"));
            statistics.setBestKillstreak(data.getInt(root + ".statistics.best-killstreak"));
            statistics.setDamageDealt(data.getDouble(root + ".statistics.damage-dealt"));
            statistics.setDamageTaken(data.getDouble(root + ".statistics.damage-taken"));
            statistics.setHitsLanded(data.getInt(root + ".statistics.hits-landed"));
            statistics.setHitsTaken(data.getInt(root + ".statistics.hits-taken"));
            statistics.setBestCombo(data.getInt(root + ".statistics.best-combo"));
        }
    }

    void saveAsync(KombatPlayer player) {
        PlayerData snapshot = PlayerData.from(player);
        writer.execute(() -> write(List.of(snapshot)));
    }

    void close(Collection<KombatPlayer> players) {
        List<PlayerData> snapshots = players.stream().map(PlayerData::from).toList();
        writer.execute(() -> write(snapshots));
        writer.shutdown();
        try {
            if (!writer.awaitTermination(Duration.ofSeconds(8).toMillis(), TimeUnit.MILLISECONDS)) {
                logger.warning("Timed out while saving player data");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.warning("Interrupted while saving player data");
        }
    }

    private void write(List<PlayerData> snapshots) {
        synchronized (fileLock) {
            for (PlayerData snapshot : snapshots) {
                String root = "players." + snapshot.uniqueId();
                data.set(root + ".name", snapshot.name());
                data.set(root + ".kombat-enabled", snapshot.kombatEnabled());
                data.set(root + ".knockback-profile", snapshot.profileName().orElse(null));
                data.set(root + ".statistics.kills", snapshot.kills());
                data.set(root + ".statistics.deaths", snapshot.deaths());
                data.set(root + ".statistics.killstreak", snapshot.killstreak());
                data.set(root + ".statistics.best-killstreak", snapshot.bestKillstreak());
                data.set(root + ".statistics.damage-dealt", snapshot.damageDealt());
                data.set(root + ".statistics.damage-taken", snapshot.damageTaken());
                data.set(root + ".statistics.hits-landed", snapshot.hitsLanded());
                data.set(root + ".statistics.hits-taken", snapshot.hitsTaken());
                data.set(root + ".statistics.best-combo", snapshot.bestCombo());
            }
            try {
                data.save(file);
            } catch (IOException exception) {
                logger.log(Level.SEVERE, "Could not save players.yml", exception);
            }
        }
    }

    private static String root(KombatPlayer player) {
        return "players." + player.getUniqueId();
    }

    private record PlayerData(
            String uniqueId,
            String name,
            boolean kombatEnabled,
            Optional<String> profileName,
            int kills,
            int deaths,
            int killstreak,
            int bestKillstreak,
            double damageDealt,
            double damageTaken,
            int hitsLanded,
            int hitsTaken,
            int bestCombo
    ) {
        private static PlayerData from(KombatPlayer player) {
            PlayerStatistics statistics = player.getStatistics();
            return new PlayerData(
                    player.getUniqueId().toString(),
                    player.getName(),
                    player.isKombatEnabled(),
                    Optional.ofNullable(player.getKnockbackProfile()).map(KnockbackProfile::getName),
                    statistics.getKills(),
                    statistics.getDeaths(),
                    statistics.getKillstreak(),
                    statistics.getBestKillstreak(),
                    statistics.getDamageDealt(),
                    statistics.getDamageTaken(),
                    statistics.getHitsLanded(),
                    statistics.getHitsTaken(),
                    statistics.getBestCombo()
            );
        }
    }
}
