package dev.spoocy.jdaextensions.commands.arguments;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ProvidedArgument {

    String getName();

    OptionType getType();

    Mentions getMentions();

    Message.Attachment getAsAttachment();

    String getAsString();

    boolean getAsBoolean();

    int getAsInt();

    long getAsLong();

    double getAsDouble();

    IMentionable getAsMentionable();

    Member getAsMember();

    User getAsUser();

    Role getAsRole();

    ChannelType getAsChannelType();

    GuildChannelUnion getAsChannel();
}
