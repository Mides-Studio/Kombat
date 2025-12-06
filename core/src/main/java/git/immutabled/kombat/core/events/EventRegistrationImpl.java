package git.immutabled.kombat.core.events;

import git.immutabled.kombat.api.events.EventRegistration;
import git.immutabled.kombat.api.events.KombatEvent;
import git.immutabled.kombat.api.events.priority.EventPriority;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventRegistrationImpl implements EventRegistration {

    private final Class<? extends KombatEvent> eventClass;
    private final EventPriority priority;


    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void unregister() {

    }
}
