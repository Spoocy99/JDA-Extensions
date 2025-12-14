package dev.spoocy.jdaextensions.commands.arguments;

import dev.spoocy.jdaextensions.commands.annotations.Choice;
import dev.spoocy.jdaextensions.commands.arguments.impl.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public enum Arguments {
    /**
     * Argument type for {@link String} values.
     */
    STRING(OptionType.STRING, StringArgument.class),
    /**
     * Argument type for {@link Integer} values.
     */
    INTEGER(OptionType.INTEGER, IntegerArgument.class),
    /**
     * Argument type for {@link Boolean} values.
     */
    BOOLEAN(OptionType.BOOLEAN, BooleanArgument.class),
    /**
     * Argument type for {@link net.dv8tion.jda.api.entities.User} or {@link net.dv8tion.jda.api.entities.Member} values.
     */
    USER(OptionType.USER, UserArgument.class),
    /**
     * Argument type for {@link net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion} values.
     */
    CHANNEL(OptionType.CHANNEL, ChannelArgument.class),
    /**
     * Argument type for {@link net.dv8tion.jda.api.entities.Role} values.
     */
    ROLE(OptionType.ROLE, RoleArgument.class),
    /**
     * Argument type for {@link net.dv8tion.jda.api.entities.IMentionable} values.
     */
    MENTIONABLE(OptionType.MENTIONABLE, MentionableArgument.class),
    /**
     * Argument type for {@link Double} values.
     */
    NUMBER(OptionType.NUMBER, NumberArgument.class),
    /**
     * Argument type for {@link net.dv8tion.jda.api.entities.Message.Attachment} values.
     */
    ATTACHMENT(OptionType.ATTACHMENT, AttachmentArgument.class),
    ;

    private final OptionType optionType;
    private final Class<? extends Argument> argumentClass;

    Arguments(@NotNull OptionType optionType,
              @NotNull Class<? extends Argument> argumentClass) {
        this.optionType = optionType;
        this.argumentClass = argumentClass;
    }

    @NotNull
    public OptionType getOptionType() {
        return this.optionType;
    }

    @NotNull
    public Class<? extends Argument> getArgumentClass() {
        return this.argumentClass;
    }

    /**
     * Creates a new {@link StringArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent.
     *         This does not work if choices are added to the argument.
     *
     * @return the created StringArgument instance
     *
     * @see StringArgument#choice(String, String)
     * @see ProvidedArgument#getAsString()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static StringArgument string(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new StringArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link IntegerArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent.
     *         This does not work if choices are added to the argument.
     *
     * @return the created IntegerArgument instance
     *
     * @see IntegerArgument#choice(String, int)
     * @see ProvidedArgument#getAsInt()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static IntegerArgument integer(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new IntegerArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link BooleanArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent
     *
     * @return the created BooleanArgument instance
     *
     * @see ProvidedArgument#getAsBoolean()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static BooleanArgument bool(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new BooleanArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link UserArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent
     *
     * @return the created UserArgument instance
     *
     * @see ProvidedArgument#getAsUser()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static UserArgument user(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new UserArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link ChannelArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent
     *
     * @return the created ChannelArgument instance
     *
     * @see ChannelArgument#type(ChannelType...)
     * @see ProvidedArgument#getAsChannel()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static ChannelArgument channel(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new ChannelArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link RoleArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent
     *
     * @return the created RoleArgument instance
     *
     * @see ProvidedArgument#getAsRole()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static RoleArgument role(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new RoleArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link MentionableArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent
     *
     * @return the created MentionableArgument instance
     *
     * @see ProvidedArgument#getAsMentionable()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static MentionableArgument mentionable(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new MentionableArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link NumberArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent.
     *         This does not work if choices are added to the argument.
     *
     * @return the created NumberArgument instance
     *
     * @see NumberArgument#choice(String, long)
     * @see NumberArgument#choice(String, double)
     * @see ProvidedArgument#getAsDouble()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static NumberArgument number(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new NumberArgument(name, description, required, autoComplete);
    }

    /**
     * Creates a new {@link AttachmentArgument}.
     * <br> Useful for creating arguments in the {@link dev.spoocy.jdaextensions.commands.tree.CommandTree}.
     *
     * @param name
     *          the name of the argument
     * @param description
     *         the description of the argument
     * @param required
     *         whether the argument is required
     * @param autoComplete
     *         whether the argument supports the CommandAutoCompleteInteractionEvent
     *
     * @return the created AttachmentArgument instance
     *
     * @see ProvidedArgument#getAsAttachment()
     *
     * @see net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
     * @see dev.spoocy.jdaextensions.commands.tree.AbstractCommandTree#arg(AbstractArgument)
     */
    public static AttachmentArgument attachment(@NotNull String name, @NotNull String description, boolean required, boolean autoComplete) {
        return new AttachmentArgument(name, description, required, autoComplete);
    }

    /*
     * Annotations for when implementing command arguments via annotations.
     */

    /**
     * Annotations for {@link Arguments#STRING}.
     * <p>
     * Compatible Type: {@link String}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Text(
                        name = "some_text",
                        description = "Some example text argument",
                        minLength = 1,
                        maxLength = 100,
                        required = true
                    ) String text

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Text {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;

        int minLength();

        int maxLength();
    }

    public static StringArgument parse(@NotNull Text annotation, @Nullable List<Choice.Text> choices) {
        StringArgument argument = new StringArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete())
                .minLength(annotation.minLength())
                .maxLength(annotation.maxLength());

        if (choices != null && !choices.isEmpty()) {
            for (Choice.Text choice : choices) {
                argument.choice(choice.name(), choice.value());
            }
        }

        return argument;
    }

    /**
     * Annotations for {@link Arguments#INTEGER}.
     * <p>
     * Compatible Type: {@link Integer}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Integer(
                        name = "some_integer",
                        description = "Some example integer argument",
                        minValue = 0,
                        maxValue = 100,
                        required = true
                    ) int number

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Integer {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;

        int minValue();

        int maxValue();
    }

    public static IntegerArgument parse(@NotNull Integer annotation, @Nullable List<Choice.Integer> choices) {
        IntegerArgument arg = new IntegerArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete())
                .minValue(annotation.minValue())
                .maxValue(annotation.maxValue());

        if (choices != null && !choices.isEmpty()) {
            for (Choice.Integer choice : choices) {
                arg.choice(choice.name(), choice.value());
            }
        }

        return arg;
    }

    /**
     * Annotations for {@link Arguments#BOOLEAN}.
     * <p>
     * Compatible Type: {@link Boolean}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Bool(
                        name = "some_bool",
                        description = "Some example boolean argument",
                        required = true
                    ) boolean flag

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Bool {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;
    }

    public static BooleanArgument parse(@NotNull Bool annotation) {
        return new BooleanArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    /**
     * Annotations for {@link Arguments#USER}.
     * <p>
     * Compatible Type: {@link net.dv8tion.jda.api.entities.User} or {@link net.dv8tion.jda.api.entities.Member}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.User(
                        name = "some_user",
                        description = "Some example user argument",
                        required = true
                    ) User user

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface User {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;
    }

    public static UserArgument parse(@NotNull User annotation) {
        return new UserArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    /**
     * Annotations for {@link Arguments#CHANNEL}.
     * <p>
     * Compatible Type: {@link net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Channel(
                        name = "some_channel",
                        description = "Some example channel argument",
                        channelTypes = { ChannelType.TEXT, ChannelType.VOICE },
                        required = true
                    ) GuildChannelUnion channel

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Channel {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;

        ChannelType[] channelTypes() default {};
    }

    public static ChannelArgument parse(@NotNull Channel annotation) {
        return new ChannelArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete())
                .type(annotation.channelTypes());
    }

    /**
     * Annotations for {@link Arguments#ROLE}.
     * <p>
     * Compatible Type: {@link net.dv8tion.jda.api.entities.Role}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Role(
                        name = "some_role",
                        description = "Some example role argument",
                        required = true
                    ) Role role

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Role {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;
    }

    public static RoleArgument parse(@NotNull Role annotation) {
        return new RoleArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    /**
     * Annotations for {@link Arguments#MENTIONABLE}.
     * <p>
     * Compatible Type: {@link net.dv8tion.jda.api.entities.IMentionable}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Mentionable(
                        name = "some_mentionable",
                        description = "Some example mentionable argument",
                        required = true
                    ) IMentionable mentionable

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Mentionable {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;
    }

    public static MentionableArgument parse(@NotNull Mentionable annotation) {
        return new MentionableArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }

    /**
     * Annotations for {@link Arguments#NUMBER}.
     * <p>
     * Compatible Type: {@link Double}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Number(
                        name = "some_number",
                        description = "Some example number argument",
                        minValue = 0.0,
                        maxValue = 100.0,
                        required = true
                    ) double number

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Number {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;

        double minValue();

        double maxValue();
    }

    public static NumberArgument parse(@NotNull Number annotation, @Nullable List<Choice.Number> choices) {
        NumberArgument arg = new NumberArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete())
                .minValue(annotation.minValue())
                .maxValue(annotation.maxValue());

        if (choices != null && !choices.isEmpty()) {
            for (Choice.Number choice : choices) {
                arg.choice(choice.name(), choice.value());
            }
        }

        return arg;
    }

    /**
     * Annotations for {@link Arguments#ATTACHMENT}.
     * <p>
     * Compatible Type: {@link net.dv8tion.jda.api.entities.Message.Attachment}
     * <p>
     * Example Usage:
     * <pre>
     * {@code
        public static void execute(@NotNull CommandContext context,

                    @Arguments.Attachment(
                        name = "some_attachment",
                        description = "Some example attachment argument",
                        required = true
                    ) Message.Attachment attachment

                ) {
     * }
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Attachment {
        String name();

        String description();

        boolean required() default true;

        boolean autoComplete() default false;
    }

    public static AttachmentArgument parse(@NotNull Attachment annotation) {
        return new AttachmentArgument(annotation.name(), annotation.description(), annotation.required(), annotation.autoComplete());
    }


}
