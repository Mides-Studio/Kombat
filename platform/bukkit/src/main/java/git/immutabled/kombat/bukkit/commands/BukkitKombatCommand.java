package git.immutabled.kombat.bukkit.commands;

import git.immutabled.kombat.core.command.impl.KnockbackCommand;
import git.immutabled.kombat.core.command.impl.KombatCommand;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class BukkitKombatCommand extends KombatCommand<CommandSender> implements CommandExecutor {

    public BukkitKombatCommand() {
        super(((commandSender, component) -> {
            commandSender.sendMessage(component.asComponent());
        }));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return false;
    }
}
