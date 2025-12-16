package dev.spoocy.jdaextensions.commands.event;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.message.MessageReply;
import dev.spoocy.jdaextensions.commands.message.ReplyAction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandContext extends MessageReply {

    /**
     * Gets the full command string as entered by the user.
     *
     * @return the full command string
     */
    @NotNull
    String getFullCommand();

    /**
     * Gets the main command name.
     *
     * @return the main command name
     */
    @NotNull
    String getCommand();

    /**
     * Gets the sub-command if present.
     *
     * @return the sub-command, or {@code null} if not present
     */
    @Nullable
    String getSubCommand();

    /**
     * Gets the list of provided arguments in this CommandContext.
     *
     * @return the list of provided arguments
     */
    @NotNull
    List<? extends ProvidedArgument> getArguments();

    /**
     * Gets the provided argument by its name.
     *
     * @param name
     *         the name of the argument
     *
     * @return the provided argument, or {@code null} if not found
     */
    @Nullable
    ProvidedArgument getArgument(@NotNull String name);

    /**
     * Gets the {@link CommandManager} that manages this CommandContext.
     *
     * @return the command manager
     */
    @NotNull
    CommandManager getCommandManager();

    /**
     * Gets the JDA instance.
     *
     * @return the JDA instance
     */
    @NotNull
    JDA getJDA();

    /**
     * Gets the {@link ShardManager} if the JDA instance is sharded.
     *
     * @return the shard manager, or {@code null} if not sharded
     */
    @Nullable
	default ShardManager getShardManager() {
		return getJDA().getShardManager();
	}

    /**
     * Gets the {@link SelfUser} representation of the bot itself.
     *
     * @return the self user
     */
	@NotNull
	default SelfUser getSelfUser() {
		return getJDA().getSelfUser();
	}

    /**
     * Gets the {@link User} who triggered the CommandContext.
     *
     * @return the user
     */
    @NotNull
    User getUser();

    /**
     * Checks if the CommandContext was triggered in a guild.
     *
     * @return {@code true} if triggered in a guild, {@code false} otherwise
     */
    boolean isGuild();

    /**
     * Checks if the CommandContext was triggered in a private channel (DM).
     *
     * @return {@code true} if triggered in a private channel, {@code false} otherwise
     */
    boolean isPrivate();

    /**
     * Gets the {@link Member} who triggered the CommandContext.
     *
     * @return the member
     */
    @NotNull
    Member getMember();

    /**
     * Gets the {@link Guild} where the CommandContext was triggered.
     *
     * @return the guild
     *
     * @throws IllegalStateException
     *          if the CommandContext was not triggered in a guild
     */
    @NotNull
    Guild getGuild();

    /**
     * Gets the {@link Member} representation of the bot itself in the guild where the CommandContext was triggered.
     *
     * @return the self member
     */
	@NotNull
	default Member getSelfMember() {
		return getGuild().getSelfMember();
	}

    /**
     * Checks if the member has the specified permissions.
     *
     * @param permission
     *         the permissions to check
     *
     * @return {@code true} if the member has all specified permissions, {@code false} otherwise
     */
    default boolean hasPermission(@NotNull Permission... permission) {
		return getMember().hasPermission(permission);
	}

    /**
     * Checks if the member has the specified permissions in the channel where the CommandContext was triggered.
     *
     * @param permissions
     *         the permissions to check
     *
     * @return {@code true} if the member has all specified permissions in the channel, {@code false} otherwise
     */
	default boolean hasChannelPermission(@NotNull Permission... permissions) {
		return getMember().hasPermission((GuildChannel) getChannel(), permissions);
	}

    /**
     * Gets the {@link MessageChannel} where the CommandContext was triggered.
     *
     * @return the message channel
     */
    @NotNull
    MessageChannel getChannel();

    /**
     * Gets the {@link ChannelType} of the channel where the CommandContext was triggered.
     *
     * @return the channel type
     */
    @NotNull
    default ChannelType getChannelType() {
		return getChannel().getType();
	}

    /**
     * Gets the {@link TextChannel} if the CommandContext was triggered in a guild text channel.
     *
     * @return the text channel
     *
     * @throws IllegalStateException
     *          if the CommandContext was not triggered in a guild text channel
     */
    @NotNull
    TextChannel getTextChannel();

    /**
     * Gets the {@link PrivateChannel} if the CommandContext was triggered in a private channel.
     *
     * @return the private channel
     *
     * @throws IllegalStateException
     *          if the CommandContext was not triggered in a private channel
     */
    @NotNull
    PrivateChannel getPrivateChannel();

    /**
     * Checks if this CommandContext was triggered by a JDA Interaction.
     *
     * @return {@code true} if triggered by an Interaction, {@code false} otherwise
     */
    boolean isInteraction();

    /**
     * Gets the underlying JDA {@link Interaction} that triggered this CommandContext.
     *
     * @return the underlying Interaction
     *
     * @throws IllegalStateException
     *          if the CommandContext was not triggered by an Interaction
     */
    @NotNull
    Interaction getInteraction();

    /**
     * Gets the underlying JDA {@link net.dv8tion.jda.api.events.GenericEvent} that triggered this CommandContext.
     *
     * @return the underlying Event, or {@code null} if not applicable
     */
    @Nullable
    Event getUnderlyingEvent();

    /**
     * Acknowledgement of this interaction with a {@link Modal Modal}.
     *
     * <p>This will open a popup on the target user's Discord client.
     *
     * <p>Interactions can only be acknowledged once.
     *
     * <p><b>This will only work when this {@link dev.spoocy.jdaextensions.commands.event.CommandContext} was triggered by an interaction.
     * @param  modal
     *         The Modal to send
     *
     * @throws IllegalArgumentException
     *         If the provided modal is null
     *
     * @throws UnsupportedOperationException
     *        If this context was not triggered by an interaction
     *
     * @return ModalCallbackAction
     *
     * @see CommandContext#isInteraction()
     */
    @NotNull
    ModalCallbackAction replyModal(@NotNull Modal modal);
}
