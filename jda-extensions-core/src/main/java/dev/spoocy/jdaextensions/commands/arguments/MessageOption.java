package dev.spoocy.jdaextensions.commands.arguments;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class MessageOption implements ProvidedArgument {

    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("^<@!?(\\d+)>$");
    private static final Pattern ROLE_MENTION_PATTERN = Pattern.compile("^<@&(\\d+)>$");
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("^<#(\\d+)>$");

    private final String name;
    private final OptionType type;
    private final String rawValue;
    private final MessageReceivedEvent event;
    private final Message.Attachment attachment;

    public MessageOption(
            @NotNull String name,
            @NotNull OptionType type,
            @Nullable String rawValue,
            @NotNull MessageReceivedEvent event,
            @Nullable Message.Attachment attachment
    ) {
        this.name = name;
        this.type = type;
        this.rawValue = rawValue;
        this.event = event;
        this.attachment = attachment;
    }

    public MessageOption(@NotNull String name, @NotNull OptionType type, @NotNull String rawValue, @NotNull MessageReceivedEvent event) {
        this(name, type, rawValue, event, null);
    }

    public MessageOption(@NotNull String name, @NotNull Message.Attachment attachment, @NotNull MessageReceivedEvent event) {
        this(name, OptionType.ATTACHMENT, null, event, attachment);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public OptionType getType() {
        return this.type;
    }

    @Override
    public Mentions getMentions() {
        return this.event.getMessage().getMentions();
    }

    @Override
    public Message.Attachment getAsAttachment() {
        if (this.attachment != null) {
            return this.attachment;
        }
        if (!this.event.getMessage().getAttachments().isEmpty()) {
            return this.event.getMessage().getAttachments().get(0);
        }
        throw new IllegalStateException("No attachment provided for argument '" + name + "'");
    }

    @Override
    public String getAsString() {
        return this.rawValue != null ? this.rawValue : "";
    }

    @Override
    public boolean getAsBoolean() {
        if (this.rawValue == null) return false;
        String val = this.rawValue.toLowerCase();
        return val.equals("true") || val.equals("1") || val.equals("yes") || val.equals("y");
    }

    @Override
    public int getAsInt() {
        return (int) getAsLong();
    }

    @Override
    public long getAsLong() {
        if (this.rawValue == null) return 0L;
        try {
            return Long.parseLong(this.rawValue);
        } catch (NumberFormatException e) {
            Matcher userMatcher = USER_MENTION_PATTERN.matcher(this.rawValue);
            if (userMatcher.matches()) {
                return Long.parseLong(userMatcher.group(1));
            }
            Matcher roleMatcher = ROLE_MENTION_PATTERN.matcher(this.rawValue);
            if (roleMatcher.matches()) {
                return Long.parseLong(roleMatcher.group(1));
            }
            Matcher channelMatcher = CHANNEL_MENTION_PATTERN.matcher(this.rawValue);
            if (channelMatcher.matches()) {
                return Long.parseLong(channelMatcher.group(1));
            }
            throw new IllegalArgumentException("Cannot parse '" + this.rawValue + "' as long for argument '" + name + "'", e);
        }
    }

    @Override
    public double getAsDouble() {
        if (this.rawValue == null) return 0.0;
        return Double.parseDouble(this.rawValue);
    }

    @Override
    public IMentionable getAsMentionable() {
        try {
            User user = getAsUser();
            if (user != null) return user;
        } catch (Exception ignored) {}
        try {
            Role role = getAsRole();
            if (role != null) return role;
        } catch (Exception ignored) {}
        throw new IllegalStateException("No mentionable entity found for argument '" + name + "'");
    }

    @Override
    public Member getAsMember() {
        User user = getAsUser();
        if (user != null && event.isFromGuild()) {
            Member member = event.getGuild().getMember(user);
            if (member != null) return member;
            return event.getGuild().retrieveMember(user).complete();
        }
        throw new IllegalStateException("Cannot resolve member for argument '" + name + "'");
    }

    @Override
    public User getAsUser() {
        if (!event.getMessage().getMentions().getUsers().isEmpty()) {
            return event.getMessage().getMentions().getUsers().get(0);
        }
        if (rawValue != null) {
            Matcher matcher = USER_MENTION_PATTERN.matcher(rawValue);
            long id = matcher.matches() ? Long.parseLong(matcher.group(1)) : getAsLong();
            User user = event.getJDA().getUserById(id);
            if (user != null) return user;
            return event.getJDA().retrieveUserById(id).complete();
        }
        throw new IllegalStateException("Cannot resolve user for argument '" + name + "'");
    }

    @Override
    public Role getAsRole() {
        if (!event.getMessage().getMentions().getRoles().isEmpty()) {
            return event.getMessage().getMentions().getRoles().get(0);
        }
        if (rawValue != null && event.isFromGuild()) {
            Matcher matcher = ROLE_MENTION_PATTERN.matcher(rawValue);
            long id = matcher.matches() ? Long.parseLong(matcher.group(1)) : getAsLong();
            Role role = event.getGuild().getRoleById(id);
            if (role != null) return role;
        }
        throw new IllegalStateException("Cannot resolve role for argument '" + name + "'");
    }

    @Override
    public ChannelType getAsChannelType() {
        GuildChannelUnion channel = getAsChannel();
        return channel != null ? channel.getType() : ChannelType.UNKNOWN;
    }

    @Override
    public GuildChannelUnion getAsChannel() {
        if (!event.getMessage().getMentions().getChannels().isEmpty()) {
            GuildChannel channel = event.getMessage().getMentions().getChannels().get(0);
            if (channel instanceof GuildChannelUnion) {
                return (GuildChannelUnion) channel;
            }
        }
        if (rawValue != null && event.isFromGuild()) {
            Matcher matcher = CHANNEL_MENTION_PATTERN.matcher(rawValue);
            long id = matcher.matches() ? Long.parseLong(matcher.group(1)) : getAsLong();
            GuildChannel channel = event.getGuild().getGuildChannelById(id);
            if (channel instanceof GuildChannelUnion) {
                return (GuildChannelUnion) channel;
            }
        }
        throw new IllegalStateException("Cannot resolve channel for argument '" + name + "'");
    }
}
