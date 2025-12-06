package git.immutabled.kombat.core.command.impl;

import git.immutabled.kombat.core.command.AbstractKombatCommand;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class KnockbackCommand<T> extends AbstractKombatCommand<T> {
    public KnockbackCommand(BiConsumer<@NonNull T, Component> consumer) {
        super(consumer);
    }

    // Set-Default

    // Remove
    // Create
    // List
    // Edit with Component
    // Info

    protected void setDefault(@NonNull T sender, String name) {
    }
    protected void edit(@NonNull T sender, @Nullable String name) {
        // Edit knockback profile if name provided edit that one else open editor menu

    }

    protected void remove(@NonNull T sender, String name) {
    }

    protected void create(@NonNull T sender, String name) {
    }
    protected void list(@NonNull T sender) {
    }


}
