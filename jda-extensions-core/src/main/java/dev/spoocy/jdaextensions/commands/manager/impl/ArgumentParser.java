package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Choice;
import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.arguments.impl.*;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ArgumentParser {

    public static StringArgument parse(
            @NotNull Arguments.Text annotation,
            @Nullable List<Choice.Text> choices
    ) {
        StringArgument argument = new StringArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());

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
            @NotNull Arguments.Integer annotation,
            @Nullable List<Choice.Integer> choices
    ) {
        IntegerArgument arg = new IntegerArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());

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

    public static UserArgument parse(@NotNull Arguments.User annotation) {
        return new UserArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    public static ChannelArgument parse(@NotNull Arguments.Channel annotation) {
        return new ChannelArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete())
                .types(annotation.channelTypes());
    }

    public static RoleArgument parse(@NotNull Arguments.Role annotation) {
        return new RoleArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    public static MentionableArgument parse(@NotNull Arguments.Mentionable annotation) {
        return new MentionableArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    public static NumberArgument parse(@NotNull Arguments.Number annotation, @Nullable List<Choice.Number> choices) {
        NumberArgument arg = new NumberArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());

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

    @NotNull
    public static AbstractArgument parseArgumentFromParam(@NotNull Class<?> param, @NotNull Annotation[] annotations, @NotNull MethodAccessor method) {

        Annotation annotation = Collector.of(annotations)
                .filter(a -> Arguments.ARGUMENT_ANNOTATIONS.contains(a.annotationType()))
                .findFirst().orElseThrow(() -> new IllegalStateException("No argument annotation found on parameter of type " + param.getName()));

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

            return ArgumentParser.parse(textArg, choices);
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

            return ArgumentParser.parse(intArg, choices);
        }

        if (annotation instanceof Arguments.Bool) {
            return ArgumentParser.parse((Arguments.Bool) annotation);
        }

        if (annotation instanceof Arguments.User) {
            Arguments.User userArg = (Arguments.User) annotation;
            return ArgumentParser.parse(userArg);
        }

        if (annotation instanceof Arguments.Channel) {
            Arguments.Channel channelArg = (Arguments.Channel) annotation;
            return ArgumentParser.parse(channelArg);
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

            return ArgumentParser.parse(numberArg, choices);
        }

        if (annotation instanceof Arguments.Attachment) {
            Arguments.Attachment attachmentArg = (Arguments.Attachment) annotation;
            return ArgumentParser.parse(attachmentArg);
        }

        throw new IllegalStateException("Unsupported argument annotation: " + annotation.annotationType().getName());
    }

}
