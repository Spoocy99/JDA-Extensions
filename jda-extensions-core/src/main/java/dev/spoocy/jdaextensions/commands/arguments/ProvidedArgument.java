package dev.spoocy.jdaextensions.commands.arguments;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ProvidedArgument {

    /**
     * @see OptionMapping#getName()
     */
    String getName();

    /**
     * @see OptionMapping#getType()
     */
    OptionType getType();

    /**
     * @see OptionMapping#getMentions()
     */
    Mentions getMentions();

    /**
     * @see OptionMapping#getAsAttachment()
     */
    Message.Attachment getAsAttachment();

    /**
     * @see OptionMapping#getAsString()
     */
    String getAsString();

    /**
     * @see OptionMapping#getAsBoolean()
     */
    boolean getAsBoolean();

    /**
     * @see OptionMapping#getAsInt()
     */
    int getAsInt();

    /**
     * @see OptionMapping#getAsLong()
     */
    long getAsLong();

    /**
     * @see OptionMapping#getAsDouble()
     */
    double getAsDouble();

    /**
     * @see OptionMapping#getAsMentionable()
     */
    IMentionable getAsMentionable();

    /**
     * @see OptionMapping#getAsMember()
     */
    Member getAsMember();

    /**
     * @see OptionMapping#getAsUser()
     */
    User getAsUser();

    /**
     * @see OptionMapping#getAsRole()
     */
    Role getAsRole();

    /**
     * @see OptionMapping#getChannelType()
     */
    ChannelType getAsChannelType();

    /**
     * @see OptionMapping#getAsChannel()
     */
    GuildChannelUnion getAsChannel();
}
