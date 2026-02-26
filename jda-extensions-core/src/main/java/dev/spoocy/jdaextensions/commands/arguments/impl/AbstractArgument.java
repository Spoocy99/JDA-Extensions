package dev.spoocy.jdaextensions.commands.arguments.impl;

import dev.spoocy.jdaextensions.commands.arguments.Argument;
import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractArgument implements Argument {

    private static final ILogger LOGGER = ILogger.forThisClass();

    protected final String name;
    protected final String description;
    protected final boolean required;
    protected final boolean autoComplete;

    public AbstractArgument(
            @NotNull String name,
            @NotNull String description,
            boolean required,
            boolean autoComplete
    ) {
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

    /**
     * Extracts the argument value from the provided command context and casts it to the expected type.
     *
     * @param <T> the expected type of the argument value
     * @param context the command context containing the provided arguments
     * @param expected the expected type of the argument value
     *
     * @return the extracted argument value cast to the expected type
     *
     * @throws IllegalStateException if the argument is not present in the context
     * @throws IllegalStateException if the argument type is incompatible with the expected type
     */
    @Nullable
    public <T> T extractValue(@NotNull CommandContext context, @NotNull Class<T> expected) {
        ProvidedArgument argument = context.getArgument(this.name);
        if (argument == null) {
            return null;
        }

        try {

            if (isOfType(expected, ProvidedArgument.class)) {
                return (T) argument;
            }

            if(isOfType(expected, Mentions.class)) {
                return (T) argument.getMentions();
            }

            if (isOfType(expected, Message.Attachment.class)) {
                return (T) argument.getAsAttachment();
            }

            if (isOfType(expected, String.class)) {
                return (T) argument.getAsString();
            }

            if (isOfType(expected, boolean.class, Boolean.class)) {
                return (T) Boolean.valueOf(argument.getAsBoolean());
            }

            if (isOfType(expected, long.class, Long.class)) {
                return (T) Long.valueOf(argument.getAsLong());
            }

            if (isOfType(expected, int.class, Integer.class)) {
                return (T) Integer.valueOf(argument.getAsInt());
            }

            if (isOfType(expected, double.class, Double.class)) {
                return (T) Double.valueOf(argument.getAsDouble());
            }

            if (isOfType(expected, IMentionable.class)) {
                return (T) argument.getAsMentionable();
            }

            if (isOfType(expected, Member.class)) {
                return (T) argument.getAsMember();
            }

            if (isOfType(expected, User.class)) {
                return (T) argument.getAsUser();
            }

            if (isOfType(expected, Role.class)) {
                return (T) argument.getAsRole();
            }

            if (isOfType(expected, ChannelType.class)) {
                return (T) argument.getAsChannelType();
            }

            if (isOfType(expected, GuildChannelUnion.class)) {
                return (T) argument.getAsChannel();
            }

        } catch (IllegalStateException ignored) { }

        throw new IllegalStateException("Incompatible argument type: " + expected.getName() + " --> " + this.type().name());
    }

    private boolean isOfType(@NotNull Class<?> expected, @NotNull Class<?>... maybe) {
        for (Class<?> type : maybe) {
            if (expected.equals(type)) {
                return true;
            }
        }
        return false;
    }

    protected abstract void apply(@NotNull OptionData optionData);

}
