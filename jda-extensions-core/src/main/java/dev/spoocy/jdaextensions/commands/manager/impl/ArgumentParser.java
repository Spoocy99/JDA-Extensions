package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Choice;
import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.arguments.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ArgumentParser {

    public static StringArgument parse(
            @NotNull Class<?> type,
            @NotNull Arguments.Text annotation,
            @Nullable List<Choice.Text> choices
    ) {
        StringArgument argument = new StringArgument(type, annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());

        if (annotation.minLength() >= 0) argument.minLength(annotation.minLength());
        if (annotation.maxLength() >= 0) argument.maxLength(annotation.maxLength());

        if (choices != null && !choices.isEmpty()) {
            for (Choice.Text choice : choices) {
                argument.choice(choice.name(), choice.value());
            }
        }

        return argument;
    }

    public static IntegerArgument parse(
            @NotNull Class<?> type,
            @NotNull Arguments.Integer annotation,
            @Nullable List<Choice.Integer> choices
    ) {
        IntegerArgument arg = new IntegerArgument(type, annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());

        if (annotation.minValue() != Long.MIN_VALUE) arg.minValue(annotation.minValue());
        if (annotation.maxValue() != Long.MAX_VALUE) arg.maxValue(annotation.maxValue());

        if (choices != null && !choices.isEmpty()) {
            for (Choice.Integer choice : choices) {
                arg.choice(choice.name(), choice.value());
            }
        }

        return arg;
    }

    public static BooleanArgument parse(@NotNull Arguments.Bool annotation) {
        return new BooleanArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    public static UserArgument parse(@NotNull Class<?> type, @NotNull Arguments.User annotation) {
        return new UserArgument(type, annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    public static ChannelArgument parse(@NotNull Class<?> type, @NotNull Arguments.Channel annotation) {
        return new ChannelArgument(type, annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete())
                .type(annotation.channelTypes());
    }

    public static RoleArgument parse(@NotNull Arguments.Role annotation) {
        return new RoleArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    public static MentionableArgument parse(@NotNull Arguments.Mentionable annotation) {
        return new MentionableArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    public static NumberArgument parse(@NotNull Class<?> type, @NotNull Arguments.Number annotation, @Nullable List<Choice.Number> choices) {
        NumberArgument arg = new NumberArgument(type, annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());

        if(annotation.minValue() != Double.MIN_VALUE) arg.minValue(annotation.minValue());
        if(annotation.maxValue() != Double.MAX_VALUE) arg.maxValue(annotation.maxValue());

        if (choices != null && !choices.isEmpty()) {
            for (Choice.Number choice : choices) {
                arg.choice(choice.name(), choice.value());
            }
        }

        return arg;
    }

    public static AttachmentArgument parse(@NotNull Arguments.Attachment annotation) {
        return new AttachmentArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }


}
