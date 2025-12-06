package git.immutabled.kombat.folia.commands;

import git.immutabled.kombat.core.command.impl.KnockbackCommand;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class FoliaKnockbackCommand extends KnockbackCommand<CommandSender> implements CommandExecutor {

    public FoliaKnockbackCommand() {
        super((Audience::sendMessage));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
