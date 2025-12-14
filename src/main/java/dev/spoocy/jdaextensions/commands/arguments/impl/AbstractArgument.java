package dev.spoocy.jdaextensions.commands.arguments.impl;

import dev.spoocy.jdaextensions.commands.arguments.Argument;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractArgument implements Argument {

    protected final String name;
    protected final String description;
    protected final boolean required;
    protected final boolean autoComplete;

    public AbstractArgument(@NotNull String name,
                               @NotNull String description,
                               boolean required,
                                 boolean autoComplete
    ) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.autoComplete = autoComplete;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @NotNull String description() {
        return this.description;
    }

    @Override
    public boolean required() {
        return this.required;
    }

    @Override
    public boolean autoComplete() {
        return this.autoComplete;
    }

    public OptionData buildJDA() {
        OptionData optionData = new OptionData(this.type(), this.name, this.description, this.required);
        optionData.setAutoComplete(this.autoComplete());
        apply(optionData);
        return optionData;
    }

    protected abstract void apply(@NotNull OptionData optionData);

}
