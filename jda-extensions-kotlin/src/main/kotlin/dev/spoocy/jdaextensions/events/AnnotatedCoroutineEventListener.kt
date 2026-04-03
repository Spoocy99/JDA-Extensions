package dev.spoocy.jdaextensions.events

import dev.spoocy.utils.common.log.ILogger
import dev.spoocy.utils.reflection.ClassWalker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.time.Duration

/**
 * An event listener that scans for methods annotated with [SubscribeCoroutineEvent]
 * and invokes them as suspend functions within a coroutine scope.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
class AnnotatedCoroutineEventListener(
    private val scope: CoroutineScope,
    private val holder: Any,
    private val defaultTimeout: Duration
) : EventListener {

    companion object {
        private val log = ILogger.forThisClass()
    }

    private val listeners: MutableMap<Class<*>, MutableList<SuspendMethodInfo>> = mutableMapOf()

    init {
        lookUpListeners()
    }

    private fun lookUpListeners() {
        val kClass: KClass<*> = holder::class

        for (function in kClass.memberFunctions) {
            val annotation: SubscribeCoroutineEvent = function.findAnnotation() ?: continue

            if (!function.isSuspend) {
                log.error(
                    "Method {} in class {} has @SubscribeCoroutineEvent annotation but is not a suspend function.",
                    function.name,
                    kClass.simpleName
                )
                continue
            }

            val parameters: List<KParameter> = function.parameters
            // parameters[0] is the receiver (this), parameters[1] should be the event
            if (parameters.size != 2) {
                log.error(
                    "Method {} in class {} has @SubscribeCoroutineEvent annotation but does not have the correct number of parameters.",
                    function.name,
                    kClass.simpleName
                )
                continue
            }

            val eventParam: KParameter = parameters[1]
            val eventType: KClass<*>? = eventParam.type.classifier as? KClass<*>

            if (eventType == null || !GenericEvent::class.java.isAssignableFrom(eventType.java)) {
                log.error(
                    "Method {} in class {} has @SubscribeCoroutineEvent annotation but has an incorrect parameter type. ({})",
                    function.name,
                    kClass.simpleName,
                    eventParam.type
                )
                continue
            }

            val timeout: Duration = when (annotation.timeout) {
                NO_TIMEOUT -> Duration.INFINITE
                INHERITED_TIMEOUT -> defaultTimeout
                else -> annotation.timeoutDuration
            }

            val methodInfo = SuspendMethodInfo(function, timeout)
            listeners.getOrPut(eventType.java) { mutableListOf() }.add(methodInfo)

            log.debug(
                "Registered coroutine event listener: {}.{} for event {}",
                kClass.simpleName,
                function.name,
                eventType.simpleName
            )
        }
    }

    override fun onEvent(event: GenericEvent) {
        for (classOfEvent in ClassWalker.walk(event.javaClass)) {
            val methods: MutableList<SuspendMethodInfo> = listeners[classOfEvent] ?: continue

            for (methodInfo in methods) {
                scope.launch {
                    try {

                        if (methodInfo.timeout.isInfinite()) {
                            methodInfo.function.callSuspend(holder, event)
                        } else {
                            withTimeout(methodInfo.timeout) {
                                methodInfo.function.callSuspend(holder, event)
                            }
                        }

                    } catch (e: Exception) {
                        log.error(
                            "Failed to invoke coroutine event listener method {} in class {} for event {}.",
                            methodInfo.function.name,
                            holder::class.simpleName,
                            event::class.simpleName,
                            e
                        )
                    }
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotatedCoroutineEventListener) return false
        return holder == other.holder
    }

    override fun hashCode(): Int {
        return holder.hashCode()
    }

    private data class SuspendMethodInfo(
        val function: KFunction<*>,
        val timeout: Duration
    )
}

