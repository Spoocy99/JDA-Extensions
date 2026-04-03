package dev.spoocy.jdaextensions.events

import dev.spoocy.jdaextensions.event.EventWaiter
import net.dv8tion.jda.api.events.Event
import java.util.function.Predicate
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

fun <T : Event> EventWaiter.waitFor(
    event: Class<T>,
    condition: (T) -> Boolean = { true },
    action: (T) -> Unit,
    timeout: Duration = Duration.INFINITE,
    timeoutAction: () -> Unit = {}
): EventWaiter.WaitingEvent<T> = this
    .waitFor(event)
    .runIf(condition)
    .run(action)
    .timeoutAfter(timeout.toJavaDuration())
    .runOnTimeout(timeoutAction)
    .build()

var <T : Event> EventWaiter.Builder<T>.condition: Predicate<T>
    get() = throw UnsupportedOperationException("Use runIf instead of cond")
    set(value) {
        this.runIf(value)
    }

var <T : Event> EventWaiter.Builder<T>.action: (T) -> Unit
    get() = throw UnsupportedOperationException("Use run instead of action")
    set(value) {
        this.run(value)
    }

var <T : Event> EventWaiter.Builder<T>.timeout: Duration
    get() = throw UnsupportedOperationException("Use timeoutAfter instead of timeout")
    set(value) {
        this.timeoutAfter(value.toJavaDuration())
    }

var EventWaiter.Builder<*>.timeoutAction: () -> Unit
    get() = throw UnsupportedOperationException("Use runOnTimeout instead of timeoutAction")
    set(value) {
        this.runOnTimeout(value)
    }



