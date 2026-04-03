package dev.spoocy.jdaextensions.commands

import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument
import dev.spoocy.jdaextensions.commands.event.CommandContext
import dev.spoocy.jdaextensions.commands.manager.impl.CommandInvoker
import dev.spoocy.utils.reflection.accessor.MethodAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.function.Consumer
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

/**
 * A command invoker that supports Kotlin suspend functions.
 * When a command method is a suspend function, it will be invoked
 * within the provided coroutine scope.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
class SuspendCommandInvoker private constructor(
    private val arguments: Array<AbstractArgument>,
    private val method: MethodAccessor,
    private val instance: Any?,
    private val scope: CoroutineScope,
    private val kFunction: KFunction<*>
) : Consumer<CommandContext> {

    companion object {
        /**
         * Creates a SuspendCommandInvoker for an instance method.
         *
         * @param arguments the command arguments
         * @param method the method accessor
         * @param instance the instance to invoke the method on
         * @param scope the coroutine scope to use for suspend function invocation
         * @return the SuspendCommandInvoker, or null if the method is not a suspend function
         */
        @JvmStatic
        fun fromInstance(
            arguments: Array<AbstractArgument>,
            method: MethodAccessor,
            instance: Any,
            scope: CoroutineScope
        ): SuspendCommandInvoker? {
            val kFunction = method.method.kotlinFunction ?: return null
            if (!kFunction.isSuspend) return null
            return SuspendCommandInvoker(arguments, method, instance, scope, kFunction)
        }

        /**
         * Creates a SuspendCommandInvoker for a static method.
         *
         * @param arguments the command arguments
         * @param method the method accessor
         * @param scope the coroutine scope to use for suspend function invocation
         * @return the SuspendCommandInvoker, or null if the method is not a suspend function
         */
        @JvmStatic
        fun fromStatic(
            arguments: Array<AbstractArgument>,
            method: MethodAccessor,
            scope: CoroutineScope
        ): SuspendCommandInvoker? {
            val kFunction = method.method.kotlinFunction ?: return null
            if (!kFunction.isSuspend) return null
            return SuspendCommandInvoker(arguments, method, null, scope, kFunction)
        }

        /**
         * Checks if the given method is a Kotlin suspend function.
         *
         * @param method the method accessor to check
         * @return true if the method is a suspend function, false otherwise
         */
        @JvmStatic
        fun isSuspendFunction(method: MethodAccessor): Boolean {
            return method.method.kotlinFunction?.isSuspend == true
        }
    }

    override fun accept(commandContext: CommandContext) {
        val args = arrayOfNulls<Any>(arguments.size + 1)
        args[0] = commandContext

        // Calculate parameter types, skipping the first (CommandContext) and last (Continuation) for suspend functions
        val javaMethod = method.method
        val paramCount = javaMethod.parameterCount - 1 // -1 for Continuation
        val parameterTypes = javaMethod.parameterTypes.copyOfRange(1, paramCount)

        for (i in arguments.indices) {
            val argument = arguments[i]
            val type = parameterTypes[i]
            val value = argument.extractValue(commandContext, type)

            if (argument.required() && value == null) {
                throw IllegalStateException("Required argument '${argument.name()}' is missing in the command context!")
            }

            args[i + 1] = value
        }

        execute(args)
    }

    private fun execute(args: Array<Any?>) {
        scope.launch {
            try {
                kFunction.callSuspend(instance, *args)
            } catch (e: Exception) {
                throw RuntimeException("Failed to invoke suspend command method: ${method.method.name}", e)
            }
        }
    }
}

