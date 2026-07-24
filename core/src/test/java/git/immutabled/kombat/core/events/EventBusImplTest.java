package git.immutabled.kombat.core.events;

import git.immutabled.kombat.api.events.KombatEvent;
import git.immutabled.kombat.api.events.priority.EventPriority;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EventBusImplTest {

    @Test
    void firesByPriorityAndSupportsOwnerUnregister() {
        EventBusImpl bus = new EventBusImpl();
        Object owner = new Object();
        List<String> calls = new ArrayList<>();

        bus.register(owner, TestEvent.class, EventPriority.HIGH, event -> calls.add("high"));
        bus.register(owner, TestEvent.class, EventPriority.LOW, event -> calls.add("low"));
        bus.fire(new TestEvent());

        assertEquals(List.of("low", "high"), calls);
        assertEquals(2, bus.getListenerCount(TestEvent.class));

        bus.unregisterAll(owner);

        assertEquals(0, bus.getListenerCount(TestEvent.class));
    }

    @Test
    void registrationHandleIsIdempotent() {
        EventBusImpl bus = new EventBusImpl();
        var registration = bus.register(TestEvent.class, ignored -> { });

        registration.unregister();
        registration.unregister();

        assertFalse(registration.isActive());
        assertEquals(0, bus.getListenerCount(TestEvent.class));
    }

    private static final class TestEvent implements KombatEvent {
        @Override
        public long getTimestamp() {
            return 1L;
        }
    }
}
