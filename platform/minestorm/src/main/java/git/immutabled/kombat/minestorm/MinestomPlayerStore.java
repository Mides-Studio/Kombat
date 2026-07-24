package git.immutabled.kombat.minestorm;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import git.immutabled.kombat.core.KombatAPIImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Properties-backed player data storage for embedded Minestom servers.
 */
final class MinestomPlayerStore {

    private static final System.Logger LOGGER = System.getLogger(MinestomPlayerStore.class.getName());

    private final Path file;
    private final Properties data = new Properties();
    private final Object fileLock = new Object();
    private final ExecutorService writer;

    MinestomPlayerStore(Path dataDirectory) {
        file = dataDirectory.resolve("players.properties");
        try {
            Files.createDirectories(dataDirectory);
            if (Files.exists(file)) {
                try (InputStream input = Files.newInputStream(file)) {
                    data.load(input);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load " + file, exception);
        }
        writer = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "kombat-minestom-player-data");
            thread.setDaemon(true);
            return thread;
        });
    }

    void load(KombatPlayer player, KombatAPIImpl api) {
        String root = root(player);
        synchronized (fileLock) {
            player.setKombatEnabled(bool(root + "kombat-enabled", true));
            Optional.ofNullable(data.getProperty(root + "knockback-profile"))
                    .flatMap(api.getKnockbackRegistry()::find)
                    .ifPresent(player::setKnockbackProfile);

            PlayerStatistics statistics = player.getStatistics();
            statistics.setKills(integer(root + "statistics.kills"));
            statistics.setDeaths(integer(root + "statistics.deaths"));
            statistics.setKillstreak(integer(root + "statistics.killstreak"));
            statistics.setBestKillstreak(integer(root + "statistics.best-killstreak"));
            statistics.setDamageDealt(decimal(root + "statistics.damage-dealt"));
            statistics.setDamageTaken(decimal(root + "statistics.damage-taken"));
            statistics.setHitsLanded(integer(root + "statistics.hits-landed"));
            statistics.setHitsTaken(integer(root + "statistics.hits-taken"));
            statistics.setBestCombo(integer(root + "statistics.best-combo"));
        }
    }

    void saveAsync(KombatPlayer player) {
        PlayerData snapshot = PlayerData.from(player);
        try {
            writer.execute(() -> write(List.of(snapshot)));
        } catch (RejectedExecutionException ignored) {
            LOGGER.log(System.Logger.Level.WARNING, "Ignored player save after Kombat shutdown");
        }
    }

    void close(Collection<KombatPlayer> players) {
        List<PlayerData> snapshots = players.stream().map(PlayerData::from).toList();
        writer.execute(() -> write(snapshots));
        writer.shutdown();
        try {
            if (!writer.awaitTermination(Duration.ofSeconds(8).toMillis(), TimeUnit.MILLISECONDS)) {
                LOGGER.log(System.Logger.Level.WARNING, "Timed out while saving Minestom player data");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            LOGGER.log(System.Logger.Level.WARNING, "Interrupted while saving Minestom player data");
        }
    }

    private void write(List<PlayerData> snapshots) {
        synchronized (fileLock) {
            for (PlayerData snapshot : snapshots) {
                String root = "players." + snapshot.uniqueId() + ".";
                data.setProperty(root + "name", snapshot.name());
                data.setProperty(root + "kombat-enabled", Boolean.toString(snapshot.kombatEnabled()));
                if (snapshot.profileName().isPresent()) {
                    data.setProperty(root + "knockback-profile", snapshot.profileName().orElseThrow());
                } else {
                    data.remove(root + "knockback-profile");
                }
                data.setProperty(root + "statistics.kills", Integer.toString(snapshot.kills()));
                data.setProperty(root + "statistics.deaths", Integer.toString(snapshot.deaths()));
                data.setProperty(root + "statistics.killstreak", Integer.toString(snapshot.killstreak()));
                data.setProperty(root + "statistics.best-killstreak", Integer.toString(snapshot.bestKillstreak()));
                data.setProperty(root + "statistics.damage-dealt", Double.toString(snapshot.damageDealt()));
                data.setProperty(root + "statistics.damage-taken", Double.toString(snapshot.damageTaken()));
                data.setProperty(root + "statistics.hits-landed", Integer.toString(snapshot.hitsLanded()));
                data.setProperty(root + "statistics.hits-taken", Integer.toString(snapshot.hitsTaken()));
                data.setProperty(root + "statistics.best-combo", Integer.toString(snapshot.bestCombo()));
            }
            saveAtomically();
        }
    }

    private void saveAtomically() {
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try (OutputStream output = Files.newOutputStream(temporary)) {
            data.store(output, "Kombat Minestom player data");
        } catch (IOException exception) {
            LOGGER.log(System.Logger.Level.ERROR, "Could not write " + temporary, exception);
            return;
        }

        try {
            try {
                Files.move(
                        temporary,
                        file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.log(System.Logger.Level.ERROR, "Could not replace " + file, exception);
        }
    }

    private int integer(String key) {
        try {
            return Math.max(0, Integer.parseInt(data.getProperty(key, "0")));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private double decimal(String key) {
        try {
            double value = Double.parseDouble(data.getProperty(key, "0"));
            return Double.isFinite(value) && value >= 0 ? value : 0;
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private boolean bool(String key, boolean fallback) {
        return Boolean.parseBoolean(data.getProperty(key, Boolean.toString(fallback)));
    }

    private static String root(KombatPlayer player) {
        return "players." + player.getUniqueId() + ".";
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
