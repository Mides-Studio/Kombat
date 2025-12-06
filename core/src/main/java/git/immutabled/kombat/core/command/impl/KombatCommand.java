package git.immutabled.kombat.core.command.impl;

import git.immutabled.kombat.api.KombatProvider;
import git.immutabled.kombat.core.command.AbstractKombatCommand;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.function.BiConsumer;

public class KombatCommand<T> extends AbstractKombatCommand<T> {


    public KombatCommand(BiConsumer<@NonNull T, Component> consumer) {
        super(consumer);
    }

    protected void getCurrentVersion(@NonNull T sender) {
        this.consumer.accept(sender, Component.text("Kombat is running version ", NamedTextColor.GREEN)
                .append(Component.text(KombatProvider.get().getPlatform().version(), NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        );
    }

    protected void toggle(@NonNull T sender) {
        //TODO> Implement toggling Kombat features per player
        this.consumer.accept(
                sender, Component.text("Toggled Kombat features for you.", NamedTextColor.GREEN)
        );

    }

    protected void reload(@NonNull T sender) {
        KombatProvider.get().reload();
        this.consumer.accept(
                sender, Component.text("Kombat configuration reloaded.", NamedTextColor.GREEN)
        );
    }

    protected void help(@NonNull T sender) {

    }




}
