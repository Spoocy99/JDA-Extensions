package dev.spoocy.jdaextensions.commands.arguments;

import dev.spoocy.utils.common.collections.Collector;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class WrappedOption implements ProvidedArgument {

    public static List<WrappedOption> wrap(@NotNull List<OptionMapping> options) {
        return Collector.of(options)
                .map(WrappedOption::new)
                .asList();
    }

    private final OptionMapping option;

    public WrappedOption(@NotNull OptionMapping option) {
        this.option = option;
    }

    @Override
    public String getName() {
        return this.option.getName();
    }

    @Override
    public OptionType getType() {
        return this.option.getType();
    }

    @Override
    public Mentions getMentions() {
        return this.option.getMentions();
    }

    @Override
    public Message.Attachment getAsAttachment() {
        return this.option.getAsAttachment();
    }

    @Override
    public String getAsString() {
        return this.option.getAsString();
    }

    @Override
    public boolean getAsBoolean() {
        return this.option.getAsBoolean();
    }

    @Override
    public int getAsInt() {
        return this.option.getAsInt();
    }

    @Override
    public long getAsLong() {
        return this.option.getAsLong();
    }

    @Override
    public double getAsDouble() {
        return this.option.getAsDouble();
    }

    @Override
    public IMentionable getAsMentionable() {
        return this.option.getAsMentionable();
    }

    @Override
    public Member getAsMember() {
        return this.option.getAsMember();
    }

    @Override
    public User getAsUser() {
        return this.option.getAsUser();
    }

    @Override
    public Role getAsRole() {
        return this.option.getAsRole();
    }

    @Override
    public ChannelType getAsChannelType() {
        return this.option.getChannelType();
    }

    @Override
    public GuildChannelUnion getAsChannel() {
        return this.option.getAsChannel();
    }
}
