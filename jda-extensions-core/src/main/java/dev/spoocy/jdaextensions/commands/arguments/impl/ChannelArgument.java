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

    public ChannelArgument(
            @NotNull String name,
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

    public ChannelArgument types(@NotNull EnumSet<ChannelType> channelTypes) {
        this.channelTypes.addAll(channelTypes);
        return this;
    }

    public ChannelArgument types(@NotNull ChannelType channelType, @NotNull ChannelType... others) {
        this.channelTypes.add(channelType);
        this.channelTypes.addAll(Arrays.asList(others));
        return this;
    }

    public ChannelArgument types(@NotNull ChannelType[] channelTypes) {
        this.channelTypes.addAll(Arrays.asList(channelTypes));
        return this;
    }

    @Override
    protected void apply(@NotNull OptionData optionData) {
        if (!this.channelTypes.isEmpty()) {
            optionData.setChannelTypes(this.channelTypes);
        }
    }
}
