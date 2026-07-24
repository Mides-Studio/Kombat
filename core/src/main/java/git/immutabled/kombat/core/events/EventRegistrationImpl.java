package git.immutabled.kombat.core.events;

import git.immutabled.kombat.api.events.EventRegistration;
import git.immutabled.kombat.api.events.KombatEvent;
import git.immutabled.kombat.api.events.priority.EventPriority;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

final class EventRegistrationImpl<T extends KombatEvent> implements EventRegistration {

    private final Object owner;
    private final Class<T> eventClass;
    private final EventPriority priority;
    private final Consumer<T> listener;
    private final Runnable unregisterAction;
    private final AtomicBoolean active = new AtomicBoolean(true);

    EventRegistrationImpl(
            Object owner,
            Class<T> eventClass,
            EventPriority priority,
            Consumer<T> listener,
            Runnable unregisterAction
    ) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.eventClass = Objects.requireNonNull(eventClass, "eventClass");
        this.priority = Objects.requireNonNull(priority, "priority");
        this.listener = Objects.requireNonNull(listener, "listener");
        this.unregisterAction = Objects.requireNonNull(unregisterAction, "unregisterAction");
    }

    @Override
    public Class<? extends KombatEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public EventPriority getPriority() {
        return priority;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void unregister() {
        if (active.compareAndSet(true, false)) {
            unregisterAction.run();
        }
    }

    Object owner() {
        return owner;
    }

    boolean accepts(KombatEvent event) {
        return eventClass.isInstance(event);
    }

    void invoke(KombatEvent event) {
        if (active.get()) {
            listener.accept(eventClass.cast(event));
        }
    }
}
