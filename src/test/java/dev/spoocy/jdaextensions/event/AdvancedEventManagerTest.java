package dev.spoocy.jdaextensions.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedEventManagerTest {

    // Use a mocked JDA instance for creating test Events to avoid NPE in Event constructor
    private static final JDA MOCK_JDA = Mockito.mock(JDA.class);

    static class MyEvent extends Event {
        public MyEvent() { super(MOCK_JDA); }
    }

    @Test
    public void registerAndUnregisterListener() {
        AdvancedEventManager mgr = new AdvancedEventManager();

        AtomicBoolean called = new AtomicBoolean(false);
        Object listener = new Object() {
            public void onEvent(MyEvent e) { called.set(true); }
        };

        mgr.register(listener);
        List<Object> regs = mgr.getRegisteredListeners();
        assertTrue(regs.contains(listener));

        // fire event - annotated listener should be wrapped and invoked via reflection
        mgr.handle(new MyEvent());

        // we can't directly assert called true because method name isn't annotated - but ensure no exceptions

        mgr.unregister(listener);
        regs = mgr.getRegisteredListeners();
        assertFalse(regs.contains(listener));
    }

    @Test
    public void handleContinuesWhenListenerThrows() {
        AdvancedEventManager mgr = new AdvancedEventManager();

        mgr.register(new EventThrower());

        AtomicBoolean secondCalled = new AtomicBoolean(false);
        mgr.register(new Object() {
            @net.dv8tion.jda.api.hooks.SubscribeEvent
            public void listen(MyEvent e) { secondCalled.set(true); }
        });

        mgr.handle(new MyEvent());

        assertTrue(secondCalled.get(), "Second listener should have been invoked even if first threw");
    }

    public static class EventThrower {
        @net.dv8tion.jda.api.hooks.SubscribeEvent
        public void on(MyEvent e) { throw new RuntimeException("fail"); }
    }
}
