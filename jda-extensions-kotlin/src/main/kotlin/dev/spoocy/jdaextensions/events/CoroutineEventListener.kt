package dev.spoocy.jdaextensions.events

import net.dv8tion.jda.api.events.GenericEvent
import java.lang.annotation.Inherited
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toDuration
import kotlin.time.toDurationUnit

/**
 * A functional interface for handling JDA events in a coroutine context.
 * This allows writing event listeners using suspend functions.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

@FunctionalInterface
interface CoroutineEventListener {

    val timeout: Duration? get() = null

    /**
     * Handles any [GenericEvent][GenericEvent]
     *
     * @param  event
     *         The Event to handle.
     */
    suspend fun onEvent(event: GenericEvent)

}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Inherited
annotation class SubscribeCoroutineEvent(

    /**
     * The time to wait for the event before timing out.
     *
     * The following values are reserved:
     * - [NO_TIMEOUT]: Wait indefinitely for the event.
     * - [INHERITED_TIMEOUT]: Inherit the timeout from the parent event manager
     *
     */
    val timeout: Long = INHERITED_TIMEOUT,

    /**
     * The time unit for the timeout. Defaults to [TimeUnit.MILLISECONDS]
     */
    val unit: TimeUnit = TimeUnit.MILLISECONDS

)

const val NO_TIMEOUT: Long = 0
const val INHERITED_TIMEOUT: Long = -1

val SubscribeCoroutineEvent.timeoutDuration: Duration
    get() = if (timeout < 0) Duration.INFINITE else timeout.toDuration(unit.toDurationUnit())