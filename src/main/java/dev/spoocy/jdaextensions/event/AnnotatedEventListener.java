package dev.spoocy.jdaextensions.event;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.reflection.ClassWalker;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.ClassAccess;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class AnnotatedEventListener implements EventListener {

    private final ILogger LOGGER = ILogger.forThisClass();
	private final Map<Class<?>, List<MethodAccessor>> listeners = new HashMap<>();
	private final Object holder;

	public AnnotatedEventListener(@NotNull Object holder) {
		this.holder = holder;
		lookUpListeners();
	}

	private void lookUpListeners() {
        ClassAccess access = Reflection.builder()
                        .forClass(holder.getClass())
                        .inheritedMembers()
                        .buildAccess();

		for (MethodAccessor method : access.methodsWithAnnotation(SubscribeEvent.class)) {
            Class<?>[] parameters = method.getMethod().getParameterTypes();

            if (parameters.length != 1) {
                LOGGER.error("Method {} in class {} has @SubscribeEvent annotation but does not have the correct number of parameters.", method.getMethod().getName(), holder.getClass().getName());
                continue;
            }

            if (!GenericEvent.class.isAssignableFrom(parameters[0])) {
                LOGGER.error("Method {} in class {} has @SubscribeEvent annotation but has an incorrect parameter type. ({})", method.getMethod().getName(), holder.getClass().getName(), parameters[0].getName());
                continue;
            }

            List<MethodAccessor> methods = listeners.computeIfAbsent(parameters[0], k -> new ArrayList<>());
            methods.add(method);
        }
	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		for (Class<?> classOfEvent : ClassWalker.walk(event.getClass())) {
			List<MethodAccessor> methods = listeners.get(classOfEvent);
			if (methods == null) continue;

			methods.forEach(method -> {
                try {
                    method.invoke(holder instanceof Class ? null : holder, event);
                } catch (Exception e) {
                    LOGGER.error("Failed to invoke event listener method {} in class {} for event {}.", method.getMethod().getName(), holder.getClass().getName(), event.getClass().getName(), e);
                }
            });
		}
	}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnnotatedEventListener && ((AnnotatedEventListener) obj).holder.equals(holder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(holder);
    }
}
