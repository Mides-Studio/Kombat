package git.immutabled.kombat.core.events;

import git.immutabled.kombat.api.events.EventBus;
import git.immutabled.kombat.api.events.EventRegistration;
import git.immutabled.kombat.api.events.KombatEvent;
import git.immutabled.kombat.api.events.exception.EventException;
import git.immutabled.kombat.api.events.priority.EventPriority;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Thread-safe event bus shared by all platform adapters.
 */
public final class EventBusImpl implements EventBus {

    private static final Comparator<EventRegistrationImpl<?>> PRIORITY_ORDER =
            Comparator.comparingInt(registration -> registration.getPriority().getSlot());

    private final CopyOnWriteArrayList<EventRegistrationImpl<?>> registrations = new CopyOnWriteArrayList<>();

    @Override
    public <T extends KombatEvent> EventRegistration register(Class<T> eventClass, Consumer<T> listener) {
        return register(listener, eventClass, EventPriority.NORMAL, listener);
    }

    @Override
    public <T extends KombatEvent> EventRegistration register(
            Class<T> eventClass,
            EventPriority priority,
            Consumer<T> listener
    ) {
        return register(listener, eventClass, priority, listener);
    }

    @Override
    public <T extends KombatEvent> EventRegistration register(
            Object owner,
            Class<T> eventClass,
            EventPriority priority,
            Consumer<T> listener
    ) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(listener, "listener");

        @SuppressWarnings("unchecked")
        EventRegistrationImpl<T>[] holder = new EventRegistrationImpl[1];
        EventRegistrationImpl<T> registration = new EventRegistrationImpl<>(
                owner,
                eventClass,
                priority,
                listener,
                () -> registrations.remove(holder[0])
        );
        holder[0] = registration;
        registrations.add(registration);
        registrations.sort(PRIORITY_ORDER);
        return registration;
    }

    @Override
    public <T extends KombatEvent> T fire(T event) {
        Objects.requireNonNull(event, "event");
        for (EventRegistrationImpl<?> registration : registrations) {
            if (!registration.accepts(event)) {
                continue;
            }
            try {
                registration.invoke(event);
            } catch (RuntimeException exception) {
                throw new EventException(
                        "Listener failed for " + event.getClass().getSimpleName(),
                        exception
                );
            }
        }
        return event;
    }

    @Override
    public void unregisterAll(Object holder) {
        Objects.requireNonNull(holder, "holder");
        for (EventRegistrationImpl<?> registration : registrations) {
            if (registration.owner() == holder) {
                registration.unregister();
            }
        }
    }

    @Override
    public int getListenerCount(Class<? extends KombatEvent> eventClass) {
        Objects.requireNonNull(eventClass, "eventClass");
        int count = 0;
        for (EventRegistrationImpl<?> registration : registrations) {
            if (registration.isActive() && registration.getEventClass().equals(eventClass)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void clear() {
        for (EventRegistrationImpl<?> registration : registrations) {
            registration.unregister();
        }
    }
}
