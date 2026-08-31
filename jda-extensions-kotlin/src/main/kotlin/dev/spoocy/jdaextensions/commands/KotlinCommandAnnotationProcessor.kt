package dev.spoocy.jdaextensions.commands

import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument
import dev.spoocy.jdaextensions.commands.event.CommandContext
import dev.spoocy.jdaextensions.commands.manager.impl.DefaultCommandAnnotationProcessor
import dev.spoocy.utils.reflection.accessor.MethodAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.function.Consumer

/**
 * A Kotlin-aware command annotation processor that supports suspend functions.
 *
 * When a command method is a Kotlin suspend function, it will be invoked
 * within the configured coroutine scope.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
class KotlinCommandAnnotationProcessor private constructor(
    private val scope: CoroutineScope,
    allowStaticContext: Boolean,
    allowInstanceContext: Boolean
) : DefaultCommandAnnotationProcessor(allowStaticContext, allowInstanceContext, true) {

    companion object {
        /**
         * Creates a new builder for KotlinCommandAnnotationProcessor.
         */
        @JvmStatic
        fun builder(): Builder = Builder()
    }

    override fun createExecutor(
        executingInstance: Any?,
        method: MethodAccessor,
        arguments: Array<AbstractArgument>
    ): Consumer<CommandContext> {
        return if (executingInstance == null) {
            SuspendCommandInvoker.fromStatic(arguments, method, scope)
                ?: super.createExecutor(executingInstance, method, arguments)
        } else {
            SuspendCommandInvoker.fromInstance(arguments, method, executingInstance, scope)
                ?: super.createExecutor(executingInstance, method, arguments)
        }
    }

    override fun getParameterCount(method: MethodAccessor): Int {
        val isSuspend = SuspendCommandInvoker.isSuspendFunction(method)
        val count = method.method.parameterCount
        return if (isSuspend) count - 1 else count
    }

    class Builder {
        private var scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        private var allowStaticContext: Boolean = true
        private var allowInstanceContext: Boolean = true

        /**
         * Sets the coroutine scope to use for invoking suspend functions.
         *
         * @param scope the coroutine scope
         * @return this builder
         */
        fun withScope(scope: CoroutineScope): Builder {
            this.scope = scope
            return this
        }

        /**
         * Sets whether static context (class-based) command registration is allowed.
         *
         * @param allowed true to allow static context
         * @return this builder
         */
        fun allowStaticContext(allowed: Boolean): Builder {
            this.allowStaticContext = allowed
            return this
        }

        /**
         * Sets whether instance context (object-based) command registration is allowed.
         *
         * @param allowed true to allow instance context
         * @return this builder
         */
        fun allowInstanceContext(allowed: Boolean): Builder {
            this.allowInstanceContext = allowed
            return this
        }

        /**
         * Builds the KotlinCommandAnnotationProcessor.
         *
         * @return the configured processor
         */
        fun build(): KotlinCommandAnnotationProcessor {
            return KotlinCommandAnnotationProcessor(scope, allowStaticContext, allowInstanceContext)
        }
    }
}
