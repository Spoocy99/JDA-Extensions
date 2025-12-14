package dev.spoocy.jdaextensions.commands.arguments.impl;


import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class StringArgument extends AbstractArgument {

    private final List<Command.Choice> choices = new ArrayList<>();
    private int minLength = -1;
    private int maxLength = -1;

    public StringArgument(@NotNull String name,
                          @NotNull String description,
                          boolean required,
                          boolean autoComplete
    ) {
        super(name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.STRING;
    }

    @Override
    public boolean autoComplete() {
        return super.autoComplete && this.choices.isEmpty();
    }

    public StringArgument maxLength(@Nullable Integer maxLength) {
        this.maxLength = maxLength == null ? -1 : maxLength;
        return this;
    }

    public StringArgument minLength(@Nullable Integer minLength) {
        this.minLength = minLength == null ? -1 : minLength;
        return this;
    }

    public StringArgument choice(@NotNull String name, @NotNull String value) {
        this.choices.add(new Command.Choice(name, value));
        return this;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
        if (this.minLength > 0) {
            optionData.setMinLength(this.minLength);
        }
        if (this.maxLength > 0) {
            optionData.setMaxLength(this.maxLength);
        }
        if (!this.choices.isEmpty()) {
            optionData.addChoices(this.choices);
        }
    }
}
