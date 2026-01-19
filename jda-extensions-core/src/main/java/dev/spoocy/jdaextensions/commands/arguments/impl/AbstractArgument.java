package dev.spoocy.jdaextensions.commands.arguments.impl;

import dev.spoocy.jdaextensions.commands.arguments.Argument;
import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractArgument implements Argument {

    private static final ILogger LOGGER = ILogger.forThisClass();

    protected final Class<?> expectedValueClass;
    protected final String name;
    protected final String description;
    protected final boolean required;
    protected final boolean autoComplete;

    public AbstractArgument(
            @NotNull Class<?> expectedValueClass,
            @NotNull String name,

            @NotNull String description,
            boolean required,
            boolean autoComplete
    ) {
        this.expectedValueClass = expectedValueClass;
        this.name = name;
        this.description = description;
        this.required = required;
        this.autoComplete = autoComplete;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @NotNull String description() {
        return this.description;
    }

    @Override
    public boolean required() {
        return this.required;
    }

    @Override
    public boolean autoComplete() {
        return this.autoComplete;
    }

    public OptionData buildJDA() {
        OptionData optionData = new OptionData(this.type(), this.name, this.description, this.required);
        optionData.setAutoComplete(this.autoComplete());
        apply(optionData);
        return optionData;
    }

    public Class<?> getValueClass() {
        return this.expectedValueClass;
    }

    @Nullable
    public Object extractValue(@NotNull CommandContext context) {
        ProvidedArgument argument = context.getArgument(this.name);
        if (argument == null) {
            return null;
        }

        Class<?> expected = wrapPrimitive(this.expectedValueClass);

        if (expected == ProvidedArgument.class) {
            return argument;
        }

        Object parsedValue = parseValue(expected, argument);

        if (!expected.isInstance(parsedValue)) {
            throw new IllegalStateException(
                    "Parsed argument value is not of expected type! Expected: "
                            + expected.getName()
                            + ", but got: "
                            + parsedValue.getClass().getName()
            );
        }

        return parsedValue;
    }

    private static Class<?> wrapPrimitive(Class<?> cls) {
        if (cls == null || !cls.isPrimitive()) {
            return cls;
        }
        if (cls == boolean.class) return Boolean.class;
        if (cls == byte.class) return Byte.class;
        if (cls == char.class) return Character.class;
        if (cls == short.class) return Short.class;
        if (cls == int.class) return Integer.class;
        if (cls == long.class) return Long.class;
        if (cls == float.class) return Float.class;
        if (cls == double.class) return Double.class;
        if (cls == void.class) return Void.class;
        return cls;
    }

    protected abstract void apply(@NotNull OptionData optionData);

    @NotNull
    protected abstract Object parseValue(@NotNull Class<?> expected, @NotNull ProvidedArgument arg);

}
