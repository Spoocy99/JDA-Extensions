package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandInvoker implements Consumer<CommandContext> {

    public static CommandInvoker fromInstance(
            @NotNull AbstractArgument[] arguments,
            @NotNull MethodAccessor method,
            @NotNull Object instance
    ) {
        return new CommandInvoker(arguments, method, instance);
    }

    public static CommandInvoker fromStatic(
            @NotNull AbstractArgument[] arguments,
            @NotNull MethodAccessor method
    ) {
        return new CommandInvoker(arguments, method, null);
    }

    private final AbstractArgument[] arguments;
    private final MethodAccessor method;
    private final Class<?>[] parameterTypes;

    @Nullable
    private final Object instance;

    protected CommandInvoker(
            @NotNull AbstractArgument[] arguments,
            @NotNull MethodAccessor method,
            @Nullable Object instance
    ) {
        this.arguments = arguments;
        this.method = method;
        this.instance = instance;
        this.parameterTypes = Arrays.copyOfRange(method.getMethod()
                .getParameterTypes(), 1, method.getMethod()
                .getParameterCount());
    }

    @NotNull
    public AbstractArgument[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }

    @NotNull
    public MethodAccessor getMethod() {
        return this.method;
    }

    @Nullable
    public Object getInstance() {
        return this.instance;
    }

    @Override
    public void accept(CommandContext commandContext) {
        Object[] args = prepareArguments(commandContext);

        try {
            method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke command method " + method.getMethod().getName() + " with arguments " + Arrays.toString(args), e);
        }
    }

    protected Object[] prepareArguments(CommandContext commandContext) {
        Object[] args = new Object[arguments.length + 1];
        args[0] = commandContext;

        for (int i = 0; i < arguments.length; i++) {
            AbstractArgument argument = arguments[i];
            Class<?> type = parameterTypes[i];

            Object value = argument.extractValue(commandContext, type);

            if (argument.required() && value == null) {
                // This should never happen as discord ensures required arguments are provided
                throw new IllegalStateException("Required argument '" + argument.name() + "' is missing in the command context!");
            }

            args[i + 1] = value;
        }

        return args;
    }


}
