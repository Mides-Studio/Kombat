package git.immutabled.kombat.folia.commands;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class FoliaKnockbackCommand extends FoliaKombatCommand implements CommandExecutor {

    public FoliaKnockbackCommand(BiConsumer<@NonNull CommandSender, Component> consumer) {
        super(((sender, component) ->
                consumer.accept(sender,component)));    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
