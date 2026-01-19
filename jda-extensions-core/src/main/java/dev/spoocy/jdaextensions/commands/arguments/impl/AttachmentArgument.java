package dev.spoocy.jdaextensions.commands.arguments.impl;

import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class AttachmentArgument extends AbstractArgument {

    public AttachmentArgument(
            @NotNull String name,
            @NotNull String description,
            boolean required,
            boolean autoComplete
    ) {
        super(Message.Attachment.class, name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.ATTACHMENT;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
    }

    @Override
    protected @NotNull Object parseValue(@NotNull Class<?> expected, @NotNull ProvidedArgument arg) {
        return arg.getAsAttachment();
    }
}
