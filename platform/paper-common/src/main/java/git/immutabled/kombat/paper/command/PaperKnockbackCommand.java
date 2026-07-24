package git.immutabled.kombat.paper.command;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.KombatPlayer;
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

public final class PaperKnockbackCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = ChatColor.DARK_RED + "[Kombat] " + ChatColor.RESET;

    private final AbstractPaperKombatPlugin plugin;
    private final KombatAPIImpl api;

    public PaperKnockbackCommand(AbstractPaperKombatPlugin plugin, KombatAPIImpl api) {
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
            case "list" -> list(sender);
            case "info" -> info(sender, args);
            case "set" -> assign(sender, args, false);
            case "reset" -> assign(sender, args, true);
            case "default" -> setDefault(sender, args);
            default -> {
                sender.sendMessage(PREFIX + ChatColor.RED + "Unknown subcommand. Use /knockback help.");
                yield true;
            }
        };
    }

    private boolean help(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Knockback profiles");
        sender.sendMessage(ChatColor.RED + "/knockback list" + ChatColor.GRAY + " - available profiles");
        sender.sendMessage(ChatColor.RED + "/knockback info <profile>" + ChatColor.GRAY + " - profile values");
        if (sender.hasPermission("kombat.knockback.assign")) {
            sender.sendMessage(ChatColor.RED + "/knockback set <profile> [player]" + ChatColor.GRAY + " - assign profile");
            sender.sendMessage(ChatColor.RED + "/knockback reset [player]" + ChatColor.GRAY + " - use the default");
            sender.sendMessage(ChatColor.RED + "/knockback default <profile>" + ChatColor.GRAY + " - change global default");
        }
        return true;
    }

    private boolean list(CommandSender sender) {
        String names = String.join(
                ChatColor.GRAY + ", " + ChatColor.RED,
                api.getKnockbackRegistry().profiles().stream().map(KnockbackProfile::getName).toList()
        );
        sender.sendMessage(PREFIX + ChatColor.GRAY + "Profiles: " + ChatColor.RED + names
                + ChatColor.GRAY + " (default: " + ChatColor.WHITE
                + api.getKnockbackRegistry().defaultProfile().getName() + ChatColor.GRAY + ")");
        return true;
    }

    private boolean info(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /knockback info <profile>");
            return true;
        }
        KnockbackProfile profile = api.getKnockbackRegistry().find(args[1]).orElse(null);
        if (profile == null) {
            return unknownProfile(sender, args[1]);
        }
        sender.sendMessage(ChatColor.DARK_RED + "Profile " + profile.getName());
        sender.sendMessage(ChatColor.GRAY + "Horizontal/vertical: " + ChatColor.WHITE
                + profile.getHorizontalKnockback() + ChatColor.GRAY + "/" + ChatColor.WHITE
                + profile.getVerticalKnockback());
        sender.sendMessage(ChatColor.GRAY + "Friction/sprint/max: " + ChatColor.WHITE
                + profile.getFriction() + ChatColor.GRAY + "/" + ChatColor.WHITE
                + profile.getSprintMultiplier() + ChatColor.GRAY + "/" + ChatColor.WHITE
                + profile.getMaxDistance());
        return true;
    }

    private boolean assign(CommandSender sender, String[] args, boolean reset) {
        if (!sender.hasPermission("kombat.knockback.assign")) {
            return denied(sender);
        }
        int playerIndex = reset ? 1 : 2;
        Player target;
        if (args.length > playerIndex) {
            target = plugin.getServer().getPlayerExact(args[playerIndex]);
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage(PREFIX + ChatColor.RED + "Specify an online player.");
            return true;
        }
        if (target == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "That player is not online.");
            return true;
        }

        KnockbackProfile profile = null;
        if (!reset) {
            if (args.length < 2) {
                sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /knockback set <profile> [player]");
                return true;
            }
            profile = api.getKnockbackRegistry().find(args[1]).orElse(null);
            if (profile == null) {
                return unknownProfile(sender, args[1]);
            }
        }

        KombatPlayer kombatPlayer = api.getPlayer(target.getUniqueId())
                .orElseGet(() -> api.registerPlayer(target.getUniqueId(), target.getName()));
        kombatPlayer.setKnockbackProfile(profile);
        plugin.savePlayer(kombatPlayer);
        sender.sendMessage(PREFIX + ChatColor.GREEN + target.getName() + " now uses "
                + (profile == null ? "the default profile." : profile.getName() + "."));
        return true;
    }

    private boolean setDefault(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kombat.admin")) {
            return denied(sender);
        }
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Usage: /knockback default <profile>");
            return true;
        }
        if (api.getKnockbackRegistry().find(args[1]).isEmpty()) {
            return unknownProfile(sender, args[1]);
        }
        plugin.setDefaultProfile(args[1]);
        sender.sendMessage(PREFIX + ChatColor.GREEN + "Default profile changed to " + args[1] + ".");
        return true;
    }

    private boolean unknownProfile(CommandSender sender, String name) {
        sender.sendMessage(PREFIX + ChatColor.RED + "Unknown profile: " + name);
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
            List<String> commands = sender.hasPermission("kombat.knockback.assign")
                    ? List.of("help", "list", "info", "set", "reset", "default")
                    : List.of("help", "list", "info");
            return filter(commands, args[0]);
        }
        if (args.length == 2 && List.of("info", "set", "default").contains(args[0].toLowerCase(Locale.ROOT))) {
            return filter(
                    api.getKnockbackRegistry().profiles().stream().map(KnockbackProfile::getName).toList(),
                    args[1]
            );
        }
        if ((args.length == 2 && args[0].equalsIgnoreCase("reset"))
                || (args.length == 3 && args[0].equalsIgnoreCase("set"))) {
            String input = args[args.length - 1];
            return filter(plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList(), input);
        }
        return List.of();
    }

    private static List<String> filter(List<String> values, String input) {
        String prefix = input.toLowerCase(Locale.ROOT);
        return values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
    }
}
