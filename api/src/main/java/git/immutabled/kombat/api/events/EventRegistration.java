package git.immutabled.kombat.api.events;

import git.immutabled.kombat.api.events.priority.EventPriority;

/**
 * Represents a registered event listener
 *
 * Can be used to unregister the listener.
 *
 * @author Immutable
 * @version 2025.0312.01
 * @since 1.0.0
 */
public interface EventRegistration {

    /**
     * Gets the event class this registration is for
     *
     * @return the event class
     */
    Class<? extends KombatEvent> getEventClass();

    /**
     * Gets the priority of this listener
     *
     * @return the priority
     */
    EventPriority getPriority();

    /**
     * Checks if this registration is active
     *
     * @return true if active
     */
    boolean isActive();

    /**
     * Unregisters this listener
     */
    void unregister();
}