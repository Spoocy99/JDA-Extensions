package dev.spoocy.jdaextensions.event;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.JDA;
import org.mockito.Mockito;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class EventWaiterTest {

    private ScheduledExecutorService executor;
    private static final JDA MOCK_JDA = Mockito.mock(JDA.class);

    @AfterEach
    public void tearDown() {
        if (executor != null && !executor.isShutdown()) executor.shutdownNow();
    }

    static class TestEvent extends Event {
        public TestEvent() {
            super(MOCK_JDA);
        }
    }

    @Test
    public void waitingEventExecutesWhenConditionMatches() throws Exception {
        executor = Executors.newSingleThreadScheduledExecutor();
        EventWaiter waiter = new EventWaiter(executor, false);

        AtomicBoolean ran = new AtomicBoolean(false);

        EventWaiter.WaitingEvent<TestEvent> ev = waiter.waitFor(TestEvent.class)
                .run(e -> ran.set(true))
                .build();

        // send event
        waiter.onEvent(new TestEvent());

        // small sleep to allow immediate action
        TimeUnit.MILLISECONDS.sleep(50);

        assertTrue(ev.wasExecuted() || ran.get(), "WaitingEvent should have executed when event was fired");
    }

    @Test
    public void timeoutRunsWhenNoEvent() throws Exception {
        executor = Executors.newSingleThreadScheduledExecutor();
        EventWaiter waiter = new EventWaiter(executor, false);

        AtomicBoolean timedOut = new AtomicBoolean(false);

        waiter.waitFor(TestEvent.class)
                .timeoutAfter(50, TimeUnit.MILLISECONDS)
                .runOnTimeout(() -> timedOut.set(true))
                .build();

        // wait longer than timeout
        TimeUnit.MILLISECONDS.sleep(200);

        assertTrue(timedOut.get(), "Timeout action should have been executed");
    }

    @Test
    public void constructorRejectsShutdownExecutor() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.shutdown();
        assertThrows(IllegalArgumentException.class, () -> new EventWaiter(executor, false));
    }
}
