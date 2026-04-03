package dev.spoocy.jdaextensions.commands

import dev.spoocy.jdaextensions.commands.annotations.Command
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument
import dev.spoocy.jdaextensions.commands.cooldown.Cooldown
import dev.spoocy.jdaextensions.commands.event.CommandContext
import dev.spoocy.jdaextensions.commands.manager.CommandAnnotationProcessor
import dev.spoocy.jdaextensions.commands.manager.impl.ArgumentParser
import dev.spoocy.jdaextensions.commands.manager.impl.CommandInvoker
import dev.spoocy.jdaextensions.commands.manager.impl.CommandResolver
import dev.spoocy.jdaextensions.commands.permission.CommandPermission
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData
import dev.spoocy.jdaextensions.commands.annotations.Cooldown as CooldownAnnotation
import dev.spoocy.jdaextensions.commands.annotations.Permissions
import dev.spoocy.utils.reflection.accessor.MethodAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.time.Duration
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
    private val allowStaticContext: Boolean,
    private val allowInstanceContext: Boolean
) : CommandAnnotationProcessor {

    companion object {
        /**
         * Creates a new builder for KotlinCommandAnnotationProcessor.
         */
        @JvmStatic
        fun builder(): Builder = Builder()
    }

    override fun parseCommand(clazz: Class<*>): CommandData {
        if (!allowStaticContext) {
            throw IllegalStateException("Static context is not allowed for this CommandAnnotationProcessor.")
        }

        // Check for Kotlin singleton object
        try {
            val instance = clazz.getField("INSTANCE").get(null)
            if (instance != null) {
                return parseCommand(instance)
            }
        } catch (_: NoSuchFieldException) {
        } catch (_: IllegalAccessException) {
        }

        val data = CommandResolver.createData(clazz)
        applyRootCommandNode(data, clazz, null)
        applySubCommandNodes(data, clazz, null)
        return data
    }

    override fun parseCommand(instance: Any): CommandData {
        if (!allowInstanceContext) {
            throw IllegalStateException("Instance context is not allowed for this CommandAnnotationProcessor.")
        }

        val data = CommandResolver.createData(instance.javaClass)
        applyRootCommandNode(data, instance.javaClass, instance)
        applySubCommandNodes(data, instance.javaClass, instance)
        return data
    }

    private fun applyRootCommandNode(data: CommandData, clazz: Class<*>, executingInstance: Any?) {
        val method = CommandResolver.getRootCommandMethod(clazz, executingInstance == null) ?: return
        val annotation = method.getAnnotation(Command.Default::class.java)

        val node = parseCommandNode(
            executingInstance,
            method,
            data,
            data.name(),
            data.description(),
            annotation.async,
            annotation.sendTyping,
            !method.hasAnnotation(Command.DisableAcknowledge::class.java),
            annotation.ephemeral
        )
        data.setRootCommand(node)
    }

    private fun applySubCommandNodes(data: CommandData, clazz: Class<*>, executingInstance: Any?) {
        val commandMethods = CommandResolver.getSubCommandMethods(clazz, executingInstance == null)

        for (method in commandMethods) {
            val annotation = method.getAnnotation(Command.Sub::class.java)

            val node = parseCommandNode(
                executingInstance,
                method,
                data,
                annotation.name,
                annotation.description,
                annotation.async,
                annotation.sendTyping,
                !method.hasAnnotation(Command.DisableAcknowledge::class.java),
                annotation.ephemeral
            )

            val group = method.getAnnotation(Command.Group::class.java)
            if (group != null) {
                data.getOrCreateSubCommandGroup(group.name, group.description)
                    .addSubCommandData(node)
                continue
            }

            data.addSubCommandData(node)
        }
    }

    private fun parseCommandNode(
        executingInstance: Any?,
        method: MethodAccessor,
        parent: CommandData,
        name: String,
        description: String,
        async: Boolean,
        sendTyping: Boolean,
        acknowledge: Boolean,
        ephemeral: Boolean
    ): CommandNodeData {
        val arguments = parseArguments(method)

        // Try to create a suspend invoker first, fall back to regular invoker
        val executor: Consumer<CommandContext> = if (executingInstance == null) {
            SuspendCommandInvoker.fromStatic(arguments, method, scope)
                ?: CommandInvoker.fromStatic(arguments, method)
        } else {
            SuspendCommandInvoker.fromInstance(arguments, method, executingInstance, scope)
                ?: CommandInvoker.fromInstance(arguments, method, executingInstance)
        }

        return CommandNodeData(
            parent,
            name,
            description,
            parsePermissions(method),
            async,
            sendTyping,
            acknowledge,
            ephemeral,
            arguments,
            parseCooldown(method),
            executor
        )
    }

    private fun parseCooldown(method: MethodAccessor): Cooldown {
        val annotation = method.getAnnotation(CooldownAnnotation::class.java) ?: return Cooldown.NONE
        return annotation.scope.cooldown(Duration.of(annotation.value, annotation.unit.toChronoUnit()))
    }

    private fun parsePermissions(method: MethodAccessor): Array<CommandPermission> {
        val perms = mutableListOf<CommandPermission>()

        val permissionsAnnotation = method.getAnnotation(Permissions::class.java)
        if (permissionsAnnotation != null) {
            if (permissionsAnnotation.scope == Permissions.Scope.GUILD) {
                for (perm in permissionsAnnotation.value) {
                    perms.add(CommandPermission.guild(perm))
                }
            } else {
                for (perm in permissionsAnnotation.value) {
                    perms.add(CommandPermission.channel(perm))
                }
            }
        }

        val ownerPermissionsAnnotation = method.getAnnotation(Permissions.Owner::class.java)
        if (ownerPermissionsAnnotation != null) {
            perms.add(CommandPermission.OWNER)
        }

        return perms.toTypedArray()
    }

    private fun parseArguments(method: MethodAccessor): Array<AbstractArgument> {
        val arguments = mutableListOf<AbstractArgument>()
        val javaMethod = method.method

        if (javaMethod.parameterCount == 0) {
            throw IllegalStateException("Command method must have at least one parameter of type CommandContext!")
        }

        if (!CommandContext::class.java.isAssignableFrom(javaMethod.parameterTypes[0])) {
            throw IllegalStateException("First parameter of command method must be of type CommandContext!")
        }

        // First parameter is always CommandContext, so we can skip it
        // For suspend functions, Kotlin adds a Continuation parameter at the end, which we need to ignore
        val isSuspend = SuspendCommandInvoker.isSuspendFunction(method)
        val paramCount = if (isSuspend) javaMethod.parameterCount - 1 else javaMethod.parameterCount

        for (i in 1 until paramCount) {
            val param = javaMethod.parameters[i]
            val argument = ArgumentParser.parseArgumentFromParam(
                param.type,
                param.annotations,
                method
            )
            arguments.add(argument)
        }

        return arguments.toTypedArray()
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

