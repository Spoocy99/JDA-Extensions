package dev.spoocy.jdaextensions.commands.arguments.impl;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BooleanArgument extends AbstractArgument {

    public BooleanArgument(@NotNull String name,
                           @NotNull String description,
                           boolean required,
                           boolean autoComplete
    ) {
        super(name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.BOOLEAN;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
    }
}
