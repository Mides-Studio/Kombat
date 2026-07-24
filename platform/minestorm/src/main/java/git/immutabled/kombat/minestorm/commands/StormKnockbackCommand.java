package git.immutabled.kombat.minestorm.commands;

import git.immutabled.kombat.api.knockback.KnockbackProfile;
import git.immutabled.kombat.api.player.KombatPlayer;
import git.immutabled.kombat.core.KombatAPIImpl;
import git.immutabled.kombat.minestorm.StormKombatPlugin;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class StormKnockbackCommand extends Command {

    private final StormKombatPlugin plugin;
    private final KombatAPIImpl api;
    private final Predicate<CommandSender> administratorCheck;

    public StormKnockbackCommand(
            StormKombatPlugin plugin,
            KombatAPIImpl api,
            Predicate<CommandSender> administratorCheck
    ) {
        super("knockback", "kb");
        this.plugin = plugin;
        this.api = api;
        this.administratorCheck = administratorCheck;

        setDefaultExecutor((sender, context) -> help(sender));

        var help = ArgumentType.Literal("help");
        var list = ArgumentType.Literal("list");
        var info = ArgumentType.Literal("info");
        var set = ArgumentType.Literal("set");
        var reset = ArgumentType.Literal("reset");
        var defaultProfile = ArgumentType.Literal("default");
        var profileName = ArgumentType.Word("profile");
        var playerName = ArgumentType.Word("player");

        addSyntax((sender, context) -> help(sender), help);
        addSyntax((sender, context) -> list(sender), list);
        addSyntax((sender, context) -> info(sender, context.get(profileName)), info, profileName);
        addSyntax((sender, context) -> assignSelf(sender, context.get(profileName)), set, profileName);
        addSyntax((sender, context) -> resetSelf(sender), reset);
        addSyntax(
                (sender, context) -> assignOther(
                        sender,
                        context.get(profileName),
                        context.get(playerName)
                ),
                set,
                profileName,
                playerName
        );
        addSyntax(
                (sender, context) -> setDefault(sender, context.get(profileName)),
                defaultProfile,
                profileName
        );
    }

    private void help(CommandSender sender) {
        sender.sendMessage("[Kombat] /knockback list | info <profile> | set <profile> | reset");
        if (administratorCheck.test(sender)) {
            sender.sendMessage("[Kombat] Admin: /knockback set <profile> <player> | default <profile>");
        }
    }

    private void list(CommandSender sender) {
        String profiles = api.getKnockbackRegistry().profiles().stream()
                .map(KnockbackProfile::getName)
                .collect(Collectors.joining(", "));
        sender.sendMessage("[Kombat] Profiles: " + profiles + " (default: "
                + api.getKnockbackRegistry().defaultProfile().getName() + ").");
    }

    private void info(CommandSender sender, String profileName) {
        api.getKnockbackRegistry().find(profileName).ifPresentOrElse(
                profile -> sender.sendMessage("[Kombat] " + profile.getName()
                        + " — horizontal " + profile.getHorizontalKnockback()
                        + ", vertical " + profile.getVerticalKnockback()
                        + ", friction " + profile.getFriction()
                        + ", sprint " + profile.getSprintMultiplier()
                        + ", max " + profile.getMaxDistance() + "."),
                () -> sender.sendMessage("[Kombat] Unknown profile: " + profileName)
        );
    }

    private void assignSelf(CommandSender sender, String profileName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[Kombat] Specify a player name.");
            return;
        }
        assign(sender, profileName, player.getUsername());
    }

    private void resetSelf(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[Kombat] Only players can reset their profile.");
            return;
        }
        KombatPlayer target = api.getPlayer(player.getUuid())
                .orElseGet(() -> api.registerPlayer(player.getUuid(), player.getUsername()));
        target.setKnockbackProfile(null);
        sender.sendMessage("[Kombat] You now use the default knockback profile.");
    }

    private void assignOther(CommandSender sender, String profileName, String playerName) {
        if (!administratorCheck.test(sender)) {
            sender.sendMessage("[Kombat] You do not have permission.");
            return;
        }
        assign(sender, profileName, playerName);
    }

    private void assign(CommandSender sender, String profileName, String playerName) {
        KnockbackProfile profile = api.getKnockbackRegistry().find(profileName).orElse(null);
        if (profile == null) {
            sender.sendMessage("[Kombat] Unknown profile: " + profileName);
            return;
        }
        KombatPlayer target = api.getPlayer(playerName).orElse(null);
        if (target == null) {
            sender.sendMessage("[Kombat] No loaded player named " + playerName + ".");
            return;
        }
        target.setKnockbackProfile(profile);
        sender.sendMessage("[Kombat] " + target.getName() + " now uses " + profile.getName() + ".");
    }

    private void setDefault(CommandSender sender, String profileName) {
        if (!administratorCheck.test(sender)) {
            sender.sendMessage("[Kombat] You do not have permission.");
            return;
        }
        if (api.getKnockbackRegistry().find(profileName).isEmpty()) {
            sender.sendMessage("[Kombat] Unknown profile: " + profileName);
            return;
        }
        plugin.setDefaultProfile(profileName);
        sender.sendMessage("[Kombat] Default profile changed to " + profileName + ".");
    }
}
