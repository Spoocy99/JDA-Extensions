package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.jdaextensions.commands.annotations.Permissions;
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.cooldown.Cooldown;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.manager.CommandAnnotationProcessor;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DefaultCommandAnnotationProcessor implements CommandAnnotationProcessor {

    public static final DefaultCommandAnnotationProcessor DEFAULT = new DefaultCommandAnnotationProcessor.Builder().build();

    public static Builder builder() {
        return new Builder();
    }

    private final boolean allowStaticContext;
    private final boolean allowInstanceContext;
    private final boolean checkForKotlinSingletons;

    public DefaultCommandAnnotationProcessor(@NotNull Builder builder) {
        this(builder.allowStaticContext, builder.allowInstanceContext, builder.kotlinSupport);
    }

    protected DefaultCommandAnnotationProcessor(boolean allowStaticContext, boolean allowInstanceContext, boolean checkForKotlinSingletons) {
        this.allowStaticContext = allowStaticContext;
        this.allowInstanceContext = allowInstanceContext;
        this.checkForKotlinSingletons = checkForKotlinSingletons;
    }

    @Override
    public CommandData parseCommand(@NotNull Class<?> clazz) {
        if (!this.allowStaticContext) {
            throw new IllegalStateException("Static context is not allowed for this CommandAnnotationProcessor.");
        }

        if (this.checkForKotlinSingletons) {
            try {
                // Check for Kotlin singleton object
                Object instance = clazz.getField("INSTANCE")
                        .get(null);
                if (instance != null) {
                    return parseCommand(instance);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        CommandData data = CommandResolver.createData(clazz);
        applyRootCommandNode(data, clazz, null);
        applySubCommandNodes(data, clazz, null);
        return data;
    }

    @Override
    public CommandData parseCommand(@NotNull Object instance) {
        if (!this.allowInstanceContext) {
            throw new IllegalStateException("Instance context is not allowed for this CommandAnnotationProcessor.");
        }

        CommandData base = CommandResolver.createData(instance.getClass());
        applyRootCommandNode(base, instance.getClass(), instance);
        applySubCommandNodes(base, instance.getClass(), instance);
        return base;
    }

    private void applyRootCommandNode(@NotNull CommandData data, @NotNull Class<?> clazz, @Nullable Object executingInstance) {
        MethodAccessor method = CommandResolver.getRootCommandMethod(clazz, executingInstance == null);

        if (method != null) {
            Command.Default annotation = method.getAnnotation(Command.Default.class);

            CommandNodeData node = parseCommandNode(
                    executingInstance,
                    method,
                    data,
                    data.name(),
                    data.description(),
                    annotation.async(),
                    annotation.sendTyping(),
                    !method.hasAnnotation(Command.DisableAcknowledge.class),
                    annotation.ephemeral()
            );
            data.setRootCommand(node);
        }
    }

    private void applySubCommandNodes(@NotNull CommandData data, @NotNull Class<?> clazz, @Nullable Object executingInstance) {
        Set<MethodAccessor> commandMethods = CommandResolver.getSubCommandMethods(clazz, executingInstance == null);

        for (MethodAccessor method : commandMethods) {
            Command.Sub annotation = method.getAnnotation(Command.Sub.class);

            CommandNodeData node = parseCommandNode(
                    executingInstance,
                    method,
                    data,
                    annotation.name(),
                    annotation.description(),
                    annotation.async(),
                    annotation.sendTyping(),
                    !method.hasAnnotation(Command.DisableAcknowledge.class),
                    annotation.ephemeral()
            );

            Command.Group group = method.getAnnotation(Command.Group.class);
            if (group != null) {
                data.getOrCreateSubCommandGroup(group.name(), group.description())
                        .addSubCommandData(node);
                continue;
            }

            data.addSubCommandData(node);
        }

    }

    protected <T> CommandNodeData parseCommandNode(
            @Nullable T executingInstance,
            @NotNull MethodAccessor method,
            @NotNull CommandData parent,
            @NotNull String name,
            @NotNull String description,
            boolean async,
            boolean sendTyping,
            boolean acknowledge,
            boolean ephemeral
    ) {

        AbstractArgument[] arguments = parseArguments(method);
        Consumer<CommandContext> executor = createExecutor(executingInstance, method, arguments);

        return new CommandNodeData(
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
        );

    }

    protected Consumer<CommandContext> createExecutor(
            @Nullable Object executingInstance,
            @NotNull MethodAccessor method,
            @NotNull AbstractArgument[] arguments
    ) {
        return executingInstance == null
                ? CommandInvoker.fromStatic(arguments, method)
                : CommandInvoker.fromInstance(arguments, method, executingInstance);
    }

    @NotNull
    protected Cooldown parseCooldown(@NotNull MethodAccessor method) {
        dev.spoocy.jdaextensions.commands.annotations.Cooldown annotation
                = method.getAnnotation(dev.spoocy.jdaextensions.commands.annotations.Cooldown.class);

        return annotation == null
                ? Cooldown.NONE
                : annotation.scope()
                .cooldown(Duration.of(annotation.value(), annotation.unit()
                        .toChronoUnit()));
    }

    @NotNull
    protected CommandPermission[] parsePermissions(@NotNull MethodAccessor method) {
        List<CommandPermission> perms = new ArrayList<>();

        Permissions permissionsAnnotation = method.getAnnotation(Permissions.class);
        if (permissionsAnnotation != null) {
            if (permissionsAnnotation.scope() == Permissions.Scope.GUILD) {
                for (Permission perm : permissionsAnnotation.value()) {
                    perms.add(CommandPermission.guild(perm));
                }
            } else {
                for (Permission perm : permissionsAnnotation.value()) {
                    perms.add(CommandPermission.channel(perm));
                }
            }
        }

        Permissions.Owner ownerPermissionsAnnotation = method.getAnnotation(Permissions.Owner.class);
        if (ownerPermissionsAnnotation != null) {
            perms.add(CommandPermission.OWNER);
        }

        return perms.toArray(CommandPermission[]::new);
    }

    @NotNull
    protected AbstractArgument[] parseArguments(@NotNull MethodAccessor method) {
        List<AbstractArgument> arguments = new ArrayList<>();

        if (method.getMethod().getParameterCount() == 0) {
            throw new IllegalStateException("Command method must have at least one parameter of type CommandContext!");
        }

        if (!CommandContext.class.isAssignableFrom(method.getMethod().getParameterTypes()[0])) {
            throw new IllegalStateException("First parameter of command method must be of type CommandContext!");
        }

        int parameterCount = getParameterCount(method);

        // first parameter is always CommandContext, so we can skip it
        for (int i = 1; i < parameterCount; i++) {
            Parameter param = method.getMethod().getParameters()[i];

            AbstractArgument argument = ArgumentParser.parseArgumentFromParam(
                    param.getType(),
                    param.getAnnotations(),
                    method
            );

            arguments.add(argument);
        }

        return arguments.toArray(AbstractArgument[]::new);
    }

    protected int getParameterCount(@NotNull MethodAccessor method) {
        return method.getMethod().getParameterCount();
    }


    public static class Builder {
        private boolean allowStaticContext = true;
        private boolean allowInstanceContext = true;
        private boolean kotlinSupport = false;

        public Builder allowStaticContext(boolean allowStaticContext) {
            this.allowStaticContext = allowStaticContext;
            return this;
        }

        public Builder allowInstanceContext(boolean allowInstanceContext) {
            this.allowInstanceContext = allowInstanceContext;
            return this;
        }

        public Builder setKotlin(boolean kotlin) {
            this.kotlinSupport = kotlin;
            return this;
        }

        public DefaultCommandAnnotationProcessor build() {
            return new DefaultCommandAnnotationProcessor(this);
        }

    }

}
