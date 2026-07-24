package git.immutabled.kombat.api.events;

import git.immutabled.kombat.api.events.priority.EventPriority;

import java.util.function.Consumer;

/**
 * Event bus for registering and firing events
 * Provides a type-safe event system for Kombat plugin events.
 * 
 * @author Immutable
 * @version 2025.0312.01
 * @since 1.0.0
 */
public interface EventBus {
    
    /**
     * Registers an event listener
     * 
     * @param <T> the event type
     * @param eventClass the event class
     * @param listener the listener callback
     * @return a registration handle that can be used to unregister
     */
    <T extends KombatEvent> EventRegistration register(Class<T> eventClass, Consumer<T> listener);
    
    /**
     * Registers an event listener with a priority
     * 
     * @param <T> the event type
     * @param eventClass the event class
     * @param priority the event priority
     * @param listener the listener callback
     * @return a registration handle
     */
    <T extends KombatEvent> EventRegistration register(Class<T> eventClass, EventPriority priority, Consumer<T> listener);

    /**
     * Registers a listener associated with an owner. All registrations for the
     * owner can later be removed with {@link #unregisterAll(Object)}.
     *
     * @param owner registration owner
     * @param eventClass event class
     * @param priority listener priority
     * @param listener listener callback
     * @param <T> event type
     * @return a registration handle
     */
    <T extends KombatEvent> EventRegistration register(
            Object owner,
            Class<T> eventClass,
            EventPriority priority,
            Consumer<T> listener
    );
    
    /**
     * Fires an event to all registered listeners
     * 
     * @param <T> the event type
     * @param event the event to fire
     * @return the event after processing
     */
    <T extends KombatEvent> T fire(T event);
    
    /**
     * Unregisters all listeners from an object
     * 
     * @param holder the object whose listeners should be unregistered
     */
    void unregisterAll(Object holder);
    
    /**
     * Gets the number of registered listeners for an event type
     * 
     * @param eventClass the event class
     * @return the listener count
     */
    int getListenerCount(Class<? extends KombatEvent> eventClass);
    
    /**
     * Clears all registered listeners
     */
    void clear();
}
