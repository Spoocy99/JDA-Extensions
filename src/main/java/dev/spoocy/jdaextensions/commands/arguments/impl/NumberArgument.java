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

public class NumberArgument extends AbstractArgument {

    private final List<Command.Choice> choices = new ArrayList<>();
    private Double minValue;
    private Double maxValue;

    public NumberArgument(@NotNull String name,
                          @NotNull String description,
                          boolean required,
                          boolean autoComplete
    ) {
        super(name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.NUMBER;
    }

    @Override
    public boolean autoComplete() {
        return super.autoComplete && this.choices.isEmpty();
    }

    public NumberArgument minValue(@Nullable Double minValue) {
        this.minValue = minValue;
        return this;
    }

    public NumberArgument maxValue(@Nullable Double maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public NumberArgument choice(@NotNull String name, long value) {
        this.choices.add(new Command.Choice(name, value));
        return this;
    }

    public NumberArgument choice(@NotNull String name, double value) {
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
