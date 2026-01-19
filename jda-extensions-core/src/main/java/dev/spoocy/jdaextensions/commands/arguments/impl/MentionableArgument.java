package dev.spoocy.jdaextensions.commands.arguments.impl;

import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MentionableArgument extends AbstractArgument {

    public MentionableArgument(@NotNull String name,
                               @NotNull String description,
                               boolean required,
                               boolean autoComplete
    ) {
        super(IMentionable.class, name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.MENTIONABLE;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
    }

    @Override
    protected @NotNull Object parseValue(@NotNull Class<?> expected, @NotNull ProvidedArgument arg) {
        return arg.getAsMentionable();
    }
}
