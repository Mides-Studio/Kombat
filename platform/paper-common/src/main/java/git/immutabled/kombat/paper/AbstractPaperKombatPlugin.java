package git.immutabled.kombat.paper;

import git.immutabled.kombat.api.KombatProvider;
import git.immutabled.kombat.api.platform.Platform;
import git.immutabled.kombat.api.platform.PlatformLoaded;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.core.configuration.KombatConfigImpl;
import git.immutabled.kombat.paper.command.PaperKnockbackCommand;
import git.immutabled.kombat.paper.command.PaperKombatCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Shared lifecycle used by the Bukkit/Paper and Folia distributions.
 */
public abstract class AbstractPaperKombatPlugin extends JavaPlugin {

    private KombatConfigImpl kombatConfig;
    private KombatAPIImpl kombat;
    private PaperCombatListener combatListener;
    private PlayerDataStore playerDataStore;

    @Override
    public final void onEnable() {
        saveDefaultConfig();
        kombatConfig = new KombatConfigImpl(
                PaperConfigLoader.load(getConfig(), getLogger()),
                this::reloadConfigurationSnapshot,
                this::saveConfig
        );
        kombat = new KombatAPIImpl(
                new Platform(getServer().getMinecraftVersion(), platformType()),
                kombatConfig
        );
        kombat.publish();

        playerDataStore = new PlayerDataStore(getDataFolder().toPath(), getLogger());
        combatListener = new PaperCombatListener(this, kombat, playerDataStore);
        getServer().getPluginManager().registerEvents(combatListener, this);

        PaperKombatCommand kombatCommand = new PaperKombatCommand(this, kombat);
        registerCommand("kombat", kombatCommand, kombatCommand);
        PaperKnockbackCommand knockbackCommand = new PaperKnockbackCommand(this, kombat);
        registerCommand("knockback", knockbackCommand, knockbackCommand);

        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            combatListener.connect(onlinePlayer);
        }
        getLogger().info("Kombat " + getPluginMeta().getVersion() + " enabled for " + platformType());
    }

    @Override
    public final void onDisable() {
        if (combatListener != null) {
            combatListener.restoreAllAttackSpeeds();
        }
        if (playerDataStore != null && kombat != null) {
            playerDataStore.close(kombat.getPlayers());
        }
        if (kombat != null) {
            kombat.close();
        } else if (KombatProvider.isLoaded()) {
            KombatProvider.unset();
        }
    }

    public final void reloadKombat() {
        kombat.reload();
        combatListener.refreshOnlinePlayers();
    }

    public final KombatAPIImpl kombat() {
        return Objects.requireNonNull(kombat, "Kombat has not been enabled");
    }

    public final void savePlayer(KombatPlayer player) {
        playerDataStore.saveAsync(player);
    }

    public final void setDefaultProfile(String profileName) {
        getConfig().set("knockback.default-profile", profileName);
        saveConfig();
        reloadKombat();
    }

    protected abstract PlatformLoaded platformType();

    private void reloadConfigurationSnapshot() {
        reloadConfig();
        kombatConfig.replace(PaperConfigLoader.load(getConfig(), getLogger()));
    }

    private void registerCommand(
            String name,
            org.bukkit.command.CommandExecutor executor,
            org.bukkit.command.TabCompleter tabCompleter
    ) {
        PluginCommand command = Objects.requireNonNull(
                getCommand(name),
                "Command missing from plugin.yml: " + name
        );
        command.setExecutor(executor);
        command.setTabCompleter(tabCompleter);
    }
}
