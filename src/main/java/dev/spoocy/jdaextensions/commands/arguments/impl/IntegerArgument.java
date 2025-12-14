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

public class IntegerArgument extends AbstractArgument {

    private final List<Command.Choice> choices = new ArrayList<>();
    private Integer minValue;
    private Integer maxValue;

    public IntegerArgument(@NotNull String name,
                           @NotNull String description,
                           boolean required,
                           boolean autoComplete
    ) {
        super(name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.INTEGER;
    }

    @Override
    public boolean autoComplete() {
        return super.autoComplete && this.choices.isEmpty();
    }

    public IntegerArgument minValue(@Nullable Integer minValue) {
        this.minValue = minValue;
        return this;
    }

    public IntegerArgument maxValue(@Nullable Integer maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public IntegerArgument choice(@NotNull String name, int value) {
        this.choices.add(new Command.Choice(name, value));
        return this;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
        if (this.minValue != null) {
            optionData.setMinValue(this.minValue);
        }
        if (this.maxValue != null) {
            optionData.setMaxValue(this.maxValue);
        }
        if (!this.choices.isEmpty()) {
            optionData.addChoices(this.choices);
        }
    }
}
