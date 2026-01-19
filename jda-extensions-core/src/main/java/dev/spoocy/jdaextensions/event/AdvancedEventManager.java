package dev.spoocy.jdaextensions.event;

import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.internal.JDAImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class AdvancedEventManager implements IEventManager {

    private static final ILogger LOGGER = ILogger.forThisClass();

	private final List<EventListener> listeners;
	private final Set<Object> holders;

    public AdvancedEventManager() {
        this.listeners = new CopyOnWriteArrayList<>();
        this.holders = ConcurrentHashMap.newKeySet();
    }

	@Override
	public void register(@NotNull Object listener) {
		this.holders.add(listener);

		if (listener instanceof EventListener) {
			listeners.add((EventListener) listener);
            return;
		}

        listeners.add(new AnnotatedEventListener(listener));
        LOGGER.debug("Registering listener of class: {}", listener.getClass().getSimpleName());
	}

	@Override
	public void unregister(@NotNull Object listener) {
		holders.remove(listener);

		if (listener instanceof EventListener) {
			listeners.remove(listener);
		}

        listeners.remove(new AnnotatedEventListener(listener));
        LOGGER.debug("Unregistering listener of class: {}", listener.getClass().getSimpleName());
	}

    @Override
	public void handle(@NotNull GenericEvent event) {
		for (EventListener listener : listeners) {
			try {
				listener.onEvent(event);
			} catch (Throwable throwable) {
                JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
                if (throwable instanceof Error)
                    throw (Error) throwable;
			}
		}
	}

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return List.copyOf(holders);
    }

}
