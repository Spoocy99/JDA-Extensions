package dev.spoocy.jdaextensions.event;

import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.reflection.ClassWalker;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class EventWaiter implements EventListener {

    private static final ILogger LOGGER = ILogger.forThisClass();
    private final HashMap<Class<?>, Set<WaitingEvent<? extends GenericEvent>>> waitingEvents;
    private final ScheduledExecutorService timeoutExecutor;
    private final boolean shutdownAutomatically;

    public EventWaiter(@NotNull ScheduledExecutorService threadpool, boolean shutdownAutomatically) {
        if (threadpool.isShutdown())
            throw new IllegalArgumentException("Threadpool is already shutdown!");

        this.waitingEvents = new HashMap<>();
        this.timeoutExecutor = threadpool;
        this.shutdownAutomatically = shutdownAutomatically;
    }

    public boolean isShutdown() {
        return timeoutExecutor.isShutdown();
    }

    public void shutdown() {
        if (shutdownAutomatically)
            throw new UnsupportedOperationException("Shutting down EventWaiters that are set to automatically close is unsupported!");
        timeoutExecutor.shutdown();
    }

    public <T extends Event> Builder<T> waitFor(@NotNull Class<T> eventType) {
        return new Builder<>(eventType);
    }

    @Override
    public final void onEvent(@NotNull GenericEvent event) {
        Class<? extends GenericEvent> c = event.getClass();

        ClassWalker.walk(c).forEach(clazz -> {
            if (this.waitingEvents.containsKey(clazz)) {
                Set<WaitingEvent<? extends GenericEvent>> set = waitingEvents.get(clazz);
                WaitingEvent[] toRemove = set.toArray(WaitingEvent[]::new);

                Collector.of(toRemove).filter(we -> {

                    try {
                        return we.attempt(event);
                    } catch (ClassCastException e) {
                        return false;
                    }

                }).asList().forEach(set::remove);
            }
        });

        if (event instanceof ShutdownEvent && shutdownAutomatically) {
            timeoutExecutor.shutdown();
        }
    }

    public class Builder<T extends Event> {

        private final Class<T> eventType;
        private Predicate<T> condition = (e) -> true;
        private Consumer<T> action = (e) -> { };
        private Runnable timeoutAction = () -> { };
        private TimeUnit unit = TimeUnit.SECONDS;
        private long timeout = -1;

        private Builder(@NotNull Class<T> eventType) {
            this.eventType = eventType;
        }

        public Builder<T> runIf(@NotNull Predicate<T> condition) {
            this.condition = condition;
            return this;
        }

        public Builder<T> run(@NotNull Consumer<T> action) {
            this.action = action;
            return this;
        }

        public Builder<T> timeoutAfter(long time, @NotNull TimeUnit unit) {
            this.unit = unit;
            this.timeout = time;
            return this;
        }

        public Builder<T> runOnTimeout(@NotNull Runnable action) {
            this.timeoutAction = action;
            return this;
        }

        public WaitingEvent<T> build() {
            WaitingEvent<T> event = new WaitingEvent<>(condition, action);

            // Register the event in the waiting events map
            Set<WaitingEvent<?>> set = waitingEvents.computeIfAbsent(eventType, c -> new HashSet<>());
            set.add(event);

            if (timeout > 0 && unit != null) {
                timeoutExecutor.schedule(() -> {

                    if (event.wasExecuted()) {
                        return;
                    }

                    try {

                        if (set.remove(event)) {
                            timeoutAction.run();
                        }

                    } catch (Throwable ex) {
                        LOGGER.error("Failed to run timeout Action.", ex);
                    }

                }, timeout, unit);
            }

            return event;
        }

    }

    public static final class WaitingEvent<T extends GenericEvent> {
        final Predicate<T> condition;
        final Consumer<T> action;
        private boolean executed = false;

        private WaitingEvent(@NotNull Predicate<T> condition, @NotNull Consumer<T> action) {
            this.condition = condition;
            this.action = action;
        }

        public boolean attempt(@NotNull T event) {
            if (executed) return false;

            if (condition.test(event)) {
                this.executed = true;
                action.accept(event);
                return true;
            }
            return false;
        }

        public boolean wasExecuted() {
            return executed;
        }
    }
}
