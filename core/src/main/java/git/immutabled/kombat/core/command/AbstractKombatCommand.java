package git.immutabled.kombat.core.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.function.BiConsumer;

@Setter
@RequiredArgsConstructor
public abstract class AbstractKombatCommand<T>  {

    protected final BiConsumer<@NonNull T, Component> consumer;

    protected String usage;

    protected void sendUsage(String[] args, @NonNull  T sender) {
        this.consumer.accept(sender, Component.text("Usage: ", NamedTextColor.RED).append(Component.text(this.usage, NamedTextColor.RED)));    }

}
