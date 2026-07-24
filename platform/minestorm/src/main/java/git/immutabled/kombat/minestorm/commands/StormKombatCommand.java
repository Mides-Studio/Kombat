package git.immutabled.kombat.minestorm.commands;

import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.minestorm.StormKombatPlugin;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.Locale;
import java.util.function.Predicate;

public final class StormKombatCommand extends Command {

    private final StormKombatPlugin plugin;
    private final KombatAPIImpl api;
    private final Predicate<CommandSender> administratorCheck;

    public StormKombatCommand(
            StormKombatPlugin plugin,
            KombatAPIImpl api,
            Predicate<CommandSender> administratorCheck
    ) {
        super("kombat");
        this.plugin = plugin;
        this.api = api;
        this.administratorCheck = administratorCheck;

        setDefaultExecutor((sender, context) -> help(sender));

        var help = ArgumentType.Literal("help");
        var version = ArgumentType.Literal("version");
        var status = ArgumentType.Literal("status");
        var toggle = ArgumentType.Literal("toggle");
        var stats = ArgumentType.Literal("stats");
        var playerName = ArgumentType.Word("player");
        var reload = ArgumentType.Literal("reload");
        var enable = ArgumentType.Literal("enable");
        var disable = ArgumentType.Literal("disable");

        addSyntax((sender, context) -> help(sender), help);
        addSyntax((sender, context) -> sender.sendMessage(
                "[Kombat] Running on " + api.getPlatform().platform() + " " + api.getPlatform().version()
        ), version);
        addSyntax((sender, context) -> status(sender), status);
        addSyntax((sender, context) -> toggle(sender), toggle);
        addSyntax((sender, context) -> ownStats(sender), stats);
        addSyntax((sender, context) -> otherStats(sender, context.get(playerName)), stats, playerName);
        addSyntax((sender, context) -> reload(sender), reload);
        addSyntax((sender, context) -> setEnabled(sender, true), enable);
        addSyntax((sender, context) -> setEnabled(sender, false), disable);
    }

    private void help(CommandSender sender) {
        sender.sendMessage("[Kombat] /kombat status | toggle | stats [player] | version");
        if (administratorCheck.test(sender)) {
            sender.sendMessage("[Kombat] Admin: /kombat reload | enable | disable");
        }
    }

    private void status(CommandSender sender) {
        sender.sendMessage("[Kombat] " + (api.isEnabled() ? "enabled" : "disabled")
                + ", " + api.getPlayers().size() + " known players, "
                + api.getKnockbackRegistry().profiles().size() + " profiles.");
    }

    private void toggle(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[Kombat] Only players can toggle their mechanics.");
            return;
        }
        KombatPlayer kombatPlayer = api.getPlayer(player.getUuid())
                .orElseGet(() -> api.registerPlayer(player.getUuid(), player.getUsername()));
        kombatPlayer.setKombatEnabled(!kombatPlayer.isKombatEnabled());
        sender.sendMessage("[Kombat] Classic mechanics "
                + (kombatPlayer.isKombatEnabled() ? "enabled." : "disabled."));
    }

    private void ownStats(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[Kombat] Specify a player name.");
            return;
        }
        api.getPlayer(player.getUuid()).ifPresentOrElse(
                target -> showStats(sender, target),
                () -> sender.sendMessage("[Kombat] No statistics are loaded.")
        );
    }

    private void otherStats(CommandSender sender, String playerName) {
        if (!administratorCheck.test(sender)) {
            sender.sendMessage("[Kombat] You do not have permission.");
            return;
        }
        api.getPlayer(playerName).ifPresentOrElse(
                target -> showStats(sender, target),
                () -> sender.sendMessage("[Kombat] No statistics are loaded for " + playerName + ".")
        );
    }

    private void showStats(CommandSender sender, KombatPlayer target) {
        PlayerStatistics stats = target.getStatistics();
        sender.sendMessage("[Kombat] " + target.getName() + ": "
                + stats.getKills() + " kills, " + stats.getDeaths() + " deaths, K/D "
                + String.format(Locale.ROOT, "%.2f", stats.getKDRatio()) + ", best combo "
                + stats.getBestCombo() + ".");
    }

    private void reload(CommandSender sender) {
        if (!administratorCheck.test(sender)) {
            sender.sendMessage("[Kombat] You do not have permission.");
            return;
        }
        plugin.reload();
        sender.sendMessage("[Kombat] Configuration reloaded.");
    }

    private void setEnabled(CommandSender sender, boolean enabled) {
        if (!administratorCheck.test(sender)) {
            sender.sendMessage("[Kombat] You do not have permission.");
            return;
        }
        api.setEnabled(enabled);
        sender.sendMessage("[Kombat] " + (enabled ? "Enabled." : "Disabled."));
    }
}
