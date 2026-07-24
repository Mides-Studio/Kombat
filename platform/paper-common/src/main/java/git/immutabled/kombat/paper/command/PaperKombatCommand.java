package git.immutabled.kombat.paper.command;

import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.api.player.statistics.PlayerStatistics;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.paper.AbstractPaperKombatPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public final class PaperKombatCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = ChatColor.DARK_RED + "[Kombat] " + ChatColor.RESET;

    private final AbstractPaperKombatPlugin plugin;
    private final KombatAPIImpl api;

    public PaperKombatCommand(AbstractPaperKombatPlugin plugin, KombatAPIImpl api) {
        this.plugin = plugin;
        this.api = api;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        String subcommand = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        return switch (subcommand) {
            case "help" -> help(sender);
            case "version" -> version(sender);
            case "status" -> status(sender);
            case "reload" -> reload(sender);
            case "toggle" -> toggle(sender);
            case "stats" -> stats(sender, args);
            case "enable" -> setGlobalState(sender, true);
            case "disable" -> setGlobalState(sender, false);
            default -> {
                sender.sendMessage(PREFIX + ChatColor.RED + "Unknown subcommand. Use /kombat help.");
                yield true;
            }
        };
    }

    private boolean help(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Kombat" + ChatColor.GRAY + " — classic combat engine");
        sender.sendMessage(ChatColor.RED + "/kombat status" + ChatColor.GRAY + " - runtime status");
        sender.sendMessage(ChatColor.RED + "/kombat toggle" + ChatColor.GRAY + " - toggle your mechanics");
        sender.sendMessage(ChatColor.RED + "/kombat stats [player]" + ChatColor.GRAY + " - combat statistics");
        if (sender.hasPermission("kombat.admin")) {
            sender.sendMessage(ChatColor.RED + "/kombat reload|enable|disable" + ChatColor.GRAY + " - administration");
        }
        return true;
    }

    private boolean version(CommandSender sender) {
        sender.sendMessage(PREFIX + ChatColor.GRAY + "Version " + ChatColor.WHITE
                + plugin.getDescription().getVersion() + ChatColor.GRAY + " on "
                + ChatColor.WHITE + api.getPlatform().platform());
        return true;
    }

    private boolean status(CommandSender sender) {
        sender.sendMessage(PREFIX + (api.isEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")
                + ChatColor.GRAY + ", " + api.getPlayers().size() + " known players, "
                + api.getKnockbackRegistry().profiles().size() + " knockback profiles.");
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!sender.hasPermission("kombat.admin")) {
            return denied(sender);
        }
        plugin.reloadKombat();
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Configuration reloaded.");
        return true;
    }

    private boolean toggle(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Only players can toggle their mechanics.");
            return true;
        }
        if (!sender.hasPermission("kombat.toggle")) {
            return denied(sender);
        }
        KombatPlayer kombatPlayer = api.getPlayer(player.getUniqueId())
                .orElseGet(() -> api.registerPlayer(player.getUniqueId(), player.getName()));
        kombatPlayer.setKombatEnabled(!kombatPlayer.isKombatEnabled());
        plugin.savePlayer(kombatPlayer);
        sender.sendMessage(PREFIX + (kombatPlayer.isKombatEnabled()
                ? ChatColor.GREEN + "Your classic combat mechanics are enabled."
                : ChatColor.YELLOW + "Your classic combat mechanics are disabled."));
        return true;
    }

    private boolean stats(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kombat.stats")) {
            return denied(sender);
        }
        KombatPlayer target;
        if (args.length >= 2) {
            if (!sender.hasPermission("kombat.stats.others")) {
                return denied(sender);
            }
            target = api.getPlayer(args[1]).orElse(null);
        } else if (sender instanceof Player player) {
            target = api.getPlayer(player.getUniqueId()).orElse(null);
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "Specify a player.");
            return true;
        }
        if (target == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "No statistics are loaded for that player.");
            return true;
        }

        PlayerStatistics stats = target.getStatistics();
        sender.sendMessage(ChatColor.DARK_RED + target.getName() + "'s combat statistics");
        sender.sendMessage(ChatColor.GRAY + "Kills/deaths: " + ChatColor.WHITE + stats.getKills()
                + ChatColor.GRAY + "/" + ChatColor.WHITE + stats.getDeaths()
                + ChatColor.GRAY + " (K/D " + ChatColor.WHITE + String.format(Locale.ROOT, "%.2f", stats.getKDRatio())
                + ChatColor.GRAY + ")");
        sender.sendMessage(ChatColor.GRAY + "Hits: " + ChatColor.WHITE + stats.getHitsLanded()
                + ChatColor.GRAY + " | Damage: " + ChatColor.WHITE
                + String.format(Locale.ROOT, "%.1f", stats.getDamageDealt()));
        sender.sendMessage(ChatColor.GRAY + "Best combo: " + ChatColor.WHITE + stats.getBestCombo()
                + ChatColor.GRAY + " | Best streak: " + ChatColor.WHITE + stats.getBestKillstreak());
        return true;
    }

    private boolean setGlobalState(CommandSender sender, boolean enabled) {
        if (!sender.hasPermission("kombat.admin")) {
            return denied(sender);
        }
        api.setEnabled(enabled);
        sender.sendMessage(PREFIX + (enabled ? ChatColor.GREEN + "Enabled globally." : ChatColor.YELLOW + "Disabled globally."));
        return true;
    }

    private boolean denied(CommandSender sender) {
        sender.sendMessage(PREFIX + ChatColor.RED + "You do not have permission.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            List<String> commands = sender.hasPermission("kombat.admin")
                    ? List.of("help", "version", "status", "toggle", "stats", "reload", "enable", "disable")
                    : List.of("help", "version", "status", "toggle", "stats");
            return filter(commands, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("stats")
                && sender.hasPermission("kombat.stats.others")) {
            return filter(api.getPlayers().stream().map(KombatPlayer::getName).toList(), args[1]);
        }
        return List.of();
    }

    private static List<String> filter(List<String> values, String input) {
        String prefix = input.toLowerCase(Locale.ROOT);
        return values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
    }
}
