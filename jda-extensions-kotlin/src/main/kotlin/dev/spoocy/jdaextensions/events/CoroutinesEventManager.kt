package dev.spoocy.jdaextensions.events

import dev.spoocy.jdaextensions.event.AdvancedEventManager
import dev.spoocy.utils.common.log.ILogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import kotlin.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

fun createCoroutineScope(

    dispatcher: CoroutineDispatcher = Dispatchers.Default,

    job: Job = SupervisorJob(),

    errorHandler: CoroutineExceptionHandler? = null,

    context: CoroutineContext = EmptyCoroutineContext

): CoroutineScope {

    val handler = errorHandler ?: CoroutineExceptionHandler { _, throwable ->
        CoroutinesEventManager.log.error("Uncaught exception from coroutine", throwable)

        if (throwable is Error) {
            job.cancel()
            throw throwable
        }

    }

    return CoroutineScope(dispatcher + job + handler + context)
}

class CoroutinesEventManager(
    val scope: CoroutineScope = createCoroutineScope(),
    val timeout: Duration = Duration.INFINITE
) : AdvancedEventManager(), CoroutineScope by scope {

    companion object {
        internal val log = ILogger.forThisClass()
    }

    override fun register(listener: Any) {
        if (listener is CoroutineEventListener) {
            super.register(WrappedCoroutineEventListener(scope, listener, listener.timeout ?: this.timeout))
            return
        }

        if (listener is EventListener) {
            super.register(listener)
            return
        }

        // Register both regular AnnotatedEventListener and AnnotatedCoroutineEventListener
        // to support both @SubscribeEvent and @SubscribeCoroutineEvent annotations
        super.register(listener)
        listeners.add(AnnotatedCoroutineEventListener(scope, listener, timeout))
    }

    override fun unregister(listener: Any) {
        super.unregister(listener)

        if (listener !is EventListener && listener !is CoroutineEventListener) {
            listeners.remove(AnnotatedCoroutineEventListener(scope, listener, timeout))
        }
    }
}

class WrappedCoroutineEventListener(
    val scope: CoroutineScope,
    val listener: CoroutineEventListener,
    val timeout: Duration
) : EventListener {

    override fun onEvent(event: GenericEvent) {

        scope.launch {
            try {

                if (timeout.isInfinite()) {
                    listener.onEvent(event)
                } else {
                    withTimeout(timeout) {
                        listener.onEvent(event)
                    }
                }

            } catch (e: Exception) {
                CoroutinesEventManager.log.error(
                    "Failed to invoke coroutine event listener {} for event {}.",
                    listener::class.simpleName,
                    event::class.simpleName,
                    e
                )
            }
        }

    }

}