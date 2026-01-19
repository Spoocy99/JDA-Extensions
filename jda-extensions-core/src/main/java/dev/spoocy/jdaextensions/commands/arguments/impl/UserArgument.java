package dev.spoocy.jdaextensions.commands.arguments.impl;

import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class UserArgument extends AbstractArgument {

    public UserArgument(
            @NotNull String name,
            @NotNull String description,
            boolean required,
            boolean autoComplete
    ) {
        this(User.class, name, description, required, autoComplete);
    }

    public UserArgument(
            @NotNull Class<?> type,
            @NotNull String name,
            @NotNull String description,
            boolean required,
            boolean autoComplete
    ) {
        super(type, name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.USER;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
    }

    @Override
    protected @NotNull Object parseValue(@NotNull Class<?> expected, @NotNull ProvidedArgument arg) {
        if(expected == Member.class) {
            return arg.getAsMember();
        }
        return arg.getAsUser();
    }
}
