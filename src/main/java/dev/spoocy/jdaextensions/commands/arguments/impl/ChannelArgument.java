package dev.spoocy.jdaextensions.commands.arguments.impl;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ChannelArgument extends AbstractArgument {

    private final EnumSet<ChannelType> channelTypes = EnumSet.noneOf(ChannelType.class);

    public ChannelArgument(@NotNull String name,
                           @NotNull String description,
                           boolean required,
                           boolean autoComplete
    ) {
        super(name, description, required, autoComplete);
    }

    @Override
    public @NotNull OptionType type() {
        return OptionType.CHANNEL;
    }

    public ChannelArgument type(@NotNull ChannelType channelType) {
        this.channelTypes.add(channelType);
        return this;
    }

    public ChannelArgument type(@NotNull ChannelType... channelType) {
        if (channelType.length == 0) return this;

        this.channelTypes.addAll(Arrays.asList(channelType));
        return this;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
        if (!this.channelTypes.isEmpty()) {
            optionData.setChannelTypes(this.channelTypes);
        }
    }
}
