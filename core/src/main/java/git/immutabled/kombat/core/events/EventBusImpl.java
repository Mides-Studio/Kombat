package git.immutabled.kombat.core.events;

import git.immutabled.kombat.api.events.EventBus;
import git.immutabled.kombat.api.events.EventRegistration;
import git.immutabled.kombat.api.events.KombatEvent;
import git.immutabled.kombat.api.events.priority.EventPriority;

import java.util.function.Consumer;

public class EventBusImpl implements EventBus {
    @Override
    public <T extends KombatEvent> EventRegistration register(Class<T> eventClass, Consumer<T> listener) {
        return null;
    }

    @Override
    public <T extends KombatEvent> EventRegistration register(Class<T> eventClass, EventPriority priority, Consumer<T> listener) {
        return null;
    }

    @Override
    public <T extends KombatEvent> T fire(T event) {
        return null;
    }

    @Override
    public void unregisterAll(Object holder) {

    }

    @Override
    public int getListenerCount(Class<? extends KombatEvent> eventClass) {
        return 0;
    }

    @Override
    public void clear() {

    }
}
