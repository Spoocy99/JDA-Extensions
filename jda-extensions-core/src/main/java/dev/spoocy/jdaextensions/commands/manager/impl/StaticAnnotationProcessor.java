package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Choice;
import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.jdaextensions.commands.annotations.Permissions;
import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.cooldown.*;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.manager.CommandAnnotationProcessor;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import kotlin.Pair;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class StaticAnnotationProcessor implements CommandAnnotationProcessor {

    protected abstract @Nullable Object getExecutorInstance(@NotNull Class<?> clazz);

    protected abstract @Nullable MethodAccessor getDefaultMethod(@NotNull Class<?> clazz);

    protected abstract @NotNull Set<MethodAccessor> getSubCommandMethods(@NotNull Class<?> clazz);

    @Override
    public CommandData parseCommand(@NotNull Class<?> clazz) {
        CommandData data = createData(clazz);

        MethodAccessor defaultMethod = getDefaultMethod(clazz);
        if (defaultMethod != null) {
            Command.Default defaultAnnotation = defaultMethod.getAnnotation(Command.Default.class);
            boolean acknowledge = !defaultMethod.hasAnnotation(Command.DisableAcknowledge.class);

            CommandNodeData defaultCommandNode = parseCommandNode(
                    clazz,
                    data,
                    defaultMethod,
                    data.name(),
                    data.description(),
                    new CommandPermission[0],
                    defaultAnnotation.async(),
                    defaultAnnotation.sendTyping(),
                    acknowledge,
                    defaultAnnotation.ephemeral()
            );
            data.setRootCommand(defaultCommandNode);
        }

        for (MethodAccessor method : getSubCommandMethods(clazz)) {
            Command.Sub subAnnotation = method.getAnnotation(Command.Sub.class);
            Command.Group groupAnnotation = method.getAnnotation(Command.Group.class);
            boolean acknowledge = !method.hasAnnotation(Command.DisableAcknowledge.class);

            CommandNodeData subCommandNode = parseCommandNode(
                    clazz,
                    data,
                    method,
                    subAnnotation.name(),
                    subAnnotation.description(),
                    new CommandPermission[0],
                    subAnnotation.async(),
                    subAnnotation.sendTyping(),
                    acknowledge,
                    subAnnotation.ephemeral()
            );

            if (groupAnnotation != null) {
                data.getOrCreateSubCommandGroup(groupAnnotation.name(), groupAnnotation.description())
                        .addSubCommandData(subCommandNode);
            } else {
                data.addSubCommandData(subCommandNode);
            }


        }

        return data;
    }

    private CommandNodeData parseCommandNode(
            @NotNull Class<?> clazz,
            @NotNull CommandData parent,
            @NotNull MethodAccessor method,
            @NotNull String name,
            @NotNull String description,
            @NotNull CommandPermission[] permissions,
            boolean async,
            boolean sendTyping,
            boolean acknowledge,
            boolean ephemeral
    ) {

        Pair<Annotation[], Class<?>[]> types = getArgumentAnnotations(method);
        Annotation[] argumentAnnotations = types.getFirst();
        Class<?>[] expectedArgumentType = types.getSecond();

        List<AbstractArgument> arguments = createArgumentList(argumentAnnotations,  expectedArgumentType, method);
        Consumer<CommandContext> executor = createExecutor(getExecutorInstance(clazz), method, arguments);

        Cooldown cooldown;
        dev.spoocy.jdaextensions.commands.annotations.Cooldown cooldownAnnotation = method.getAnnotation(dev.spoocy.jdaextensions.commands.annotations.Cooldown.class);
        if (cooldownAnnotation != null) {
            long dur = cooldownAnnotation.value();
            TimeUnit unit = cooldownAnnotation.unit();
            Duration cooldownDuration = Duration.ofMillis(unit.toMillis(dur));
            cooldown = createCooldown(cooldownAnnotation.scope(), cooldownDuration);
        } else {
            cooldown = Cooldown.NONE;
        }

        List<CommandPermission> perms = new ArrayList<>(Arrays.asList(permissions));

        Permissions permissionsAnnotation = method.getAnnotation(Permissions.class);
        if (permissionsAnnotation != null) {
            if (permissionsAnnotation.scope() == Permissions.Scope.GUILD) {
                for (net.dv8tion.jda.api.Permission perm : permissionsAnnotation.value()) {
                    perms.add(CommandPermission.guild(perm));
                }
            } else {
                for (net.dv8tion.jda.api.Permission perm : permissionsAnnotation.value()) {
                    perms.add(CommandPermission.channel(perm));
                }
            }
        }

        Permissions.Owner ownerPermissionsAnnotation = method.getAnnotation(Permissions.Owner.class);
        if (ownerPermissionsAnnotation != null) {
            perms.add(CommandPermission.OWNER);
        }

        return new CommandNodeData(
                parent,
                name,
                description,
                perms.toArray(CommandPermission[]::new),
                async,
                sendTyping,
                acknowledge,
                ephemeral,
                arguments,
                cooldown,
                executor
        );

    }


    private CommandData createData(@NotNull Class<?> clazz) {
        Command parentAnnotation = clazz.getAnnotation(Command.class);
        if (parentAnnotation == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @Command!");
        }

        DefaultMemberPermissions perm = DefaultMemberPermissions.ENABLED;
        Permissions.Default permissionsAnnotation = clazz.getAnnotation(Permissions.Default.class);
        if (permissionsAnnotation != null) {

            if (permissionsAnnotation.disable()) {
                perm = DefaultMemberPermissions.DISABLED;
            } else {
                List<Permission> permissionList = Arrays.asList(permissionsAnnotation.value());
                perm = DefaultMemberPermissions.enabledFor(permissionList);
            }

        }

        return new CommandData(
                parentAnnotation.name(),
                parentAnnotation.description(),
                parentAnnotation.nsfw(),
                perm,
                parentAnnotation.context()
        );
    }

    private Pair<Annotation[], Class<?>[]> getArgumentAnnotations(@NotNull MethodAccessor method) {
        Parameter[] parameters = method.getMethod()
                .getParameters();
        Annotation[] argumentAnnotations = new Annotation[parameters.length - 1];
        Class<?>[] expectedArgumentType = new Class<?>[parameters.length - 1];

        for (int i = 1; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Annotation[] annotations = parameter.getAnnotations();

            if (annotations.length == 0) {
                throw new IllegalArgumentException("Parameter '" + parameter.getName() + "' is not annotated with any argument annotation!");
            }

            Annotation argumentAnnotation = null;
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> type = annotation.annotationType();

                if (type == Arguments.Text.class ||
                        type == Arguments.Integer.class ||
                        type == Arguments.Bool.class ||
                        type == Arguments.User.class ||
                        type == Arguments.Channel.class ||
                        type == Arguments.Role.class ||
                        type == Arguments.Mentionable.class ||
                        type == Arguments.Number.class ||
                        type == Arguments.Attachment.class
                ) {
                    argumentAnnotation = annotation;
                    break;
                }
            }

            if (argumentAnnotation == null) {
                throw new IllegalArgumentException("Parameter '" + parameter.getName() + "' is not annotated with any argument annotation!");
            }

            argumentAnnotations[i - 1] = argumentAnnotation;
            expectedArgumentType[i - 1] = parameter.getType();
        }

        return new Pair<>(argumentAnnotations, expectedArgumentType);
    }

    private List<AbstractArgument> createArgumentList(
            @NotNull Annotation[] argumentAnnotations,
            @NotNull Class<?>[] expectedArgumentType,
            @NotNull MethodAccessor method
    ) {

        List<AbstractArgument> arguments = new ArrayList<>();
        for (int i = 0; i < argumentAnnotations.length; i++) {
            Annotation annotation = argumentAnnotations[i];
            Class<?> type = expectedArgumentType[i];

            AbstractArgument argument = parseArgument(annotation, type, method);

            if (argument == null) {
                throw new IllegalArgumentException("Unsupported argument annotation: " + annotation.annotationType().getName());
            }

            arguments.add(argument);
        }
        return arguments;
    }

    private Consumer<CommandContext> createExecutor(
            @Nullable Object instance,
            @NotNull MethodAccessor method,
            @NotNull List<AbstractArgument> arguments
    ) {

        return context -> {
            Object[] args = new Object[arguments.size() + 1];
            args[0] = context;

            for (int i = 0; i < arguments.size(); i++) {
                AbstractArgument argument = arguments.get(i);
                Object value = argument.extractValue(context);

                if(argument.required() && value == null) {
                    // This should never happen as JDA ensures required arguments are provided
                    throw new IllegalStateException("Required argument '" + argument.name() + "' is missing in the command context!");
                }

                args[i + 1] = value;
            }

            method.invoke(instance, args);

        };
    }

    private Cooldown createCooldown(@NotNull CooldownScope scope, @NotNull Duration duration) {
        switch (scope) {
            case USER:
                return new UserCooldown(duration);
            case GUILD:
                return new GuildCooldown(duration);
            case GLOBAL:
                return new GlobalCooldown(duration);
            default:
                return Cooldown.NONE;
        }
    }

    private AbstractArgument parseArgument(@NotNull Annotation annotation, Class<?> type, @NotNull MethodAccessor method) {
        if (annotation instanceof Arguments.Text) {
            Arguments.Text textArg = (Arguments.Text) annotation;
            List<Choice.Text> choices = null;

            Choice.Text choiceAnnotation = method.getAnnotation(Choice.Text.class);
            if (choiceAnnotation != null && choiceAnnotation.argument()
                    .equals(textArg.name())) {
                choices = Collections.singletonList(choiceAnnotation);
            }

            Choice.Texts choiceAnnotations = method.getAnnotation(Choice.Texts.class);
            if (choiceAnnotations != null) {
                choices = Collector.of(choiceAnnotations.value())
                        .filter(c -> c.argument()
                                .equals(textArg.name()))
                        .asList();
            }

            return ArgumentParser.parse(type, textArg, choices);
        }

        if (annotation instanceof Arguments.Integer) {
            Arguments.Integer intArg = (Arguments.Integer) annotation;
            List<Choice.Integer> choices = null;

            Choice.Integer choiceAnnotation = method.getAnnotation(Choice.Integer.class);
            if (choiceAnnotation != null && choiceAnnotation.argument()
                    .equals(intArg.name())) {
                choices = Collections.singletonList(choiceAnnotation);
            }

            Choice.Integers choiceAnnotations = method.getAnnotation(Choice.Integers.class);
            if (choiceAnnotations != null) {
                choices = Collector.of(choiceAnnotations.value())
                        .filter(c -> c.argument()
                                .equals(intArg.name()))
                        .asList();
            }

            return ArgumentParser.parse(type, intArg, choices);
        }

        if (annotation instanceof Arguments.Bool) {
            Arguments.Bool boolArg = (Arguments.Bool) annotation;
            return ArgumentParser.parse(boolArg);
        }

        if (annotation instanceof Arguments.User) {
            Arguments.User userArg = (Arguments.User) annotation;
            return ArgumentParser.parse(type, userArg);
        }

        if (annotation instanceof Arguments.Channel) {
            Arguments.Channel channelArg = (Arguments.Channel) annotation;
            return ArgumentParser.parse(type, channelArg);
        }

        if (annotation instanceof Arguments.Role) {
            Arguments.Role roleArg = (Arguments.Role) annotation;
            return ArgumentParser.parse(roleArg);
        }

        if (annotation instanceof Arguments.Mentionable) {
            Arguments.Mentionable mentionableArg = (Arguments.Mentionable) annotation;
            return ArgumentParser.parse(mentionableArg);
        }

        if (annotation instanceof Arguments.Number) {
            Arguments.Number numberArg = (Arguments.Number) annotation;
            List<Choice.Number> choices = null;

            Choice.Number choiceAnnotation = method.getAnnotation(Choice.Number.class);
            if (choiceAnnotation != null && choiceAnnotation.argument()
                    .equals(numberArg.name())) {
                choices = Collections.singletonList(choiceAnnotation);
            }

            Choice.Numbers choiceAnnotations = method.getAnnotation(Choice.Numbers.class);
            if (choiceAnnotations != null) {
                choices = Collector.of(choiceAnnotations.value())
                        .filter(c -> c.argument()
                                .equals(numberArg.name()))
                        .asList();
            }

            return ArgumentParser.parse(type, numberArg, choices);
        }

        if (annotation instanceof Arguments.Attachment) {
            Arguments.Attachment attachmentArg = (Arguments.Attachment) annotation;
            return ArgumentParser.parse(attachmentArg);
        }

        return null;
    }

    @Nullable
    private Object getArgumentValue(@NotNull Annotation annotation, @NotNull CommandContext context, @NotNull Class<?> expectedType) {
        if (annotation instanceof Arguments.Text) {
            Arguments.Text textArg = (Arguments.Text) annotation;
            ProvidedArgument arg = context.getArgument(textArg.name());
            return arg != null ? arg.getAsString() : null;
        }

        if (annotation instanceof Arguments.Integer) {
            Arguments.Integer intArg = (Arguments.Integer) annotation;
            ProvidedArgument arg = context.getArgument(intArg.name());
            return arg != null ? arg.getAsInt() : null;
        }

        if (annotation instanceof Arguments.Bool) {
            Arguments.Bool boolArg = (Arguments.Bool) annotation;
            ProvidedArgument arg = context.getArgument(boolArg.name());
            return arg != null ? arg.getAsBoolean() : null;
        }

        if (annotation instanceof Arguments.User) {
            Arguments.User userArg = (Arguments.User) annotation;
            ProvidedArgument arg = context.getArgument(userArg.name());
            return arg != null ? (expectedType == User.class ? arg.getAsUser() : arg.getAsMember()) : null;
        }

        if (annotation instanceof Arguments.Channel) {
            Arguments.Channel channelArg = (Arguments.Channel) annotation;
            ProvidedArgument arg = context.getArgument(channelArg.name());
            return arg != null ? arg.getAsChannel() : null;
        }

        if (annotation instanceof Arguments.Role) {
            Arguments.Role roleArg = (Arguments.Role) annotation;
            ProvidedArgument arg = context.getArgument(roleArg.name());
            return arg != null ? arg.getAsRole() : null;
        }

        if (annotation instanceof Arguments.Mentionable) {
            Arguments.Mentionable mentionableArg = (Arguments.Mentionable) annotation;
            ProvidedArgument arg = context.getArgument(mentionableArg.name());
            return arg != null ? arg.getAsMentionable() : null;
        }

        if (annotation instanceof Arguments.Number) {
            Arguments.Number numberArg = (Arguments.Number) annotation;
            ProvidedArgument arg = context.getArgument(numberArg.name());
            return arg != null ? ((expectedType == long.class || expectedType == Long.class) ? arg.getAsLong() : arg.getAsDouble()) : null;
        }

        if (annotation instanceof Arguments.Attachment) {
            Arguments.Attachment attachmentArg = (Arguments.Attachment) annotation;
            ProvidedArgument arg = context.getArgument(attachmentArg.name());
            return arg != null ? arg.getAsAttachment() : null;
        }

        return null;
    }


}
