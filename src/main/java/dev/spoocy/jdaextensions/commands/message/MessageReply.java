package dev.spoocy.jdaextensions.commands.message;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface MessageReply {

    /**
     * @see IReplyCallback#reply(String)
     */
    ReplyAction reply(@NotNull String content);

    /**
     * Reply to this interaction and acknowledge it.
     * <br>This will send a reply message for this interaction.
     *
     * @see IReplyCallback#reply(String)
     */
	ReplyAction reply(@NotNull Message message);

    /**
     * @see IReplyCallback#reply(MessageCreateData)
     */
    ReplyAction reply(@NotNull MessageCreateData message);

    /**
     * @see IReplyCallback#replyEmbeds(MessageEmbed, MessageEmbed...)
     */
    ReplyAction reply(@NotNull MessageEmbed embed, MessageEmbed... other);

    /**
     * @see IReplyCallback#replyEmbeds(Collection)
     */
    ReplyAction reply(@NotNull Collection<MessageEmbed> embed);

    /**
     * Reply to this interaction and acknowledge it.
     * <br>This will send a reply message for this interaction.
     * <p>
     * Automatically builds the embed from the provided builder.
     *
     * @see IReplyCallback#replyEmbeds(MessageEmbed, MessageEmbed...)
     */
    default ReplyAction reply(@NotNull EmbedBuilder builder) {
        return reply(builder.build());
    }

    /**
     * @see net.dv8tion.jda.api.interactions.callbacks.IModalCallback#replyModal(Modal)
     */
    ModalCallbackAction replyModal(@Nonnull Modal modal);

    /**
     * @see IReplyCallback#replyComponents(Collection)
     */
    ReplyAction replyComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components);

    /**
     * @see IReplyCallback#replyComponents(MessageTopLevelComponent, MessageTopLevelComponent...)
     */
    ReplyAction replyComponents(@Nonnull MessageTopLevelComponent component, @Nonnull MessageTopLevelComponent... other);

    /**
     * @see IReplyCallback#replyComponents(ComponentTree)
     */
    ReplyAction replyComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree);

    /**
     * @see IReplyCallback#replyFiles(FileUpload...)
     */
    ReplyAction replyFiles(@Nonnull FileUpload... files);

    /**
     * @see IReplyCallback#replyFiles(Collection)
     */
    ReplyAction replyFiles(@Nonnull Collection<? extends FileUpload> files);

    /**
     * @see IReplyCallback#replyPoll(MessagePollData)
     */
    ReplyAction replyPoll(@Nonnull MessagePollData poll);

    /**
     * Sends a reply message in the same channel as the original message.
     *
     * @param message
     *          the message to send
     *
     * @return the reply action
     */
    ReplyAction send(@NotNull String message);

    /**
     * Sends a reply message in the same channel as the original message.
     *
     * @param message
     *          the message to send
     *
     * @return the reply action
     */
    ReplyAction send(@NotNull Message message);

    /**
     * Sends a reply message in the same channel as the original message.
     *
     * @param embed
     *          the embed to send
     *
     * @return the reply action
     */
    ReplyAction send(@NotNull MessageEmbed embed);

    /**
     * Sends a reply message in the same channel as the original message.
     * <p>
     * Automatically builds the embed from the provided builder.
     *
     * @param builder
     *          the embed builder to build and send
     *
     * @return the reply action
     */
    default ReplyAction send(@NotNull EmbedBuilder builder) {
        return send(builder.build());
    }

    /**
     * Sends a private reply message to the user who triggered the original message.
     *
     * @param message
     *          the message to send
     *
     * @return the reply action
     */
    ReplyAction sendPrivate(@NotNull String message);

    /**
     * Sends a private reply message to the user who triggered the original message.
     *
     * @param message
     *          the message to send
     *
     * @return the reply action
     */
    ReplyAction sendPrivate(@NotNull Message message);

    /**
     * Sends a private reply message to the user who triggered the original message.
     *
     * @param embed
     *          the embed to send
     *
     * @return the reply action
     */
    ReplyAction sendPrivate(@NotNull MessageEmbed embed);

    /**
     * Sends a private reply message to the user who triggered the original message.
     * <p>
     * Automatically builds the embed from the provided builder.
     *
     * @param builder
     *          the embed builder to build and send
     *
     * @return the reply action
     */
    default ReplyAction sendPrivate(@NotNull EmbedBuilder builder) {
        return sendPrivate(builder.build());
    }
}
