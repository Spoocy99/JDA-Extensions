package dev.spoocy.jdaextensions.commands.message.action;

import dev.spoocy.jdaextensions.commands.message.AbstractReplyAction;
import dev.spoocy.jdaextensions.commands.message.ReplyAction;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class WrappedMessageReplyAction extends AbstractReplyAction<MessageCreateAction, Message> {

    public WrappedMessageReplyAction(@NotNull MessageCreateAction action) {
        super(action);
    }

    @NotNull
    @Override
    public Function<Message, Message> getMapper() {
        return message -> message;
    }

    @Override
    public @NotNull ReplyAction addContent(@NotNull String content) {
        this.action.addContent(content);
        return this;
    }

    @Override
    public @NotNull ReplyAction addEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        this.action.addEmbeds(embeds);
        return this;
    }

    @Override
    public @NotNull ReplyAction addComponents(@NotNull Collection<? extends MessageTopLevelComponent> components) {
        this.action.addComponents(components);
        return this;
    }

    @Override
    public @NotNull ReplyAction addFiles(@NotNull Collection<? extends FileUpload> files) {
        this.action.addFiles(files);
        return this;
    }

    @Override
    public @NotNull List<FileUpload> getAttachments() {
        return this.action.getAttachments();
    }

    @Override
    public @NotNull ReplyAction setPoll(@Nullable MessagePollData poll) {
        this.action.setPoll(poll);
        return this;
    }

    @Override
    public @NotNull ReplyAction setTTS(boolean tts) {
        this.action.setTTS(tts);
        return this;
    }

    @Override
    public @NotNull ReplyAction setSuppressedNotifications(boolean suppressed) {
        this.action.setSuppressedNotifications(suppressed);
        return this;
    }

    @Override
    public @NotNull ReplyAction setVoiceMessage(boolean voiceMessage) {
        this.action.setVoiceMessage(voiceMessage);
        return this;
    }
}
