package git.immutabled.kombat.folia.commands;

import git.immutabled.kombat.core.command.impl.KombatCommand;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class FoliaKombatCommand extends KombatCommand<CommandSender> implements CommandExecutor {

    public FoliaKombatCommand(BiConsumer<@NonNull CommandSender, Component> consumer) {
        super((consumer));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
