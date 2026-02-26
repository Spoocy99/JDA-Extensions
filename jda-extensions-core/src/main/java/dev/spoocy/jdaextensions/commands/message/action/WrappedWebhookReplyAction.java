package dev.spoocy.jdaextensions.commands.message.action;

import dev.spoocy.jdaextensions.commands.message.AbstractReplyAction;
import dev.spoocy.jdaextensions.commands.message.ReplyAction;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class WrappedWebhookReplyAction extends AbstractReplyAction<WebhookMessageCreateAction<Message>, Message> {

    public static ReplyAction wrap(@NotNull WebhookMessageCreateAction<Message> action) {
        return new WrappedWebhookReplyAction(action);
    }

    private WrappedWebhookReplyAction(@NotNull WebhookMessageCreateAction<Message> action) {
        super(action);
    }

    @NotNull
    @Override
    public Function<Message, Message> getMapper() {
        return message -> message;
    }

    @Override
    public @NotNull ReplyAction addContent(@NotNull String content) {
        this.action.setContent(content);
        return this;
    }

    @Override
    public @NotNull ReplyAction addEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        this.action.setEmbeds(embeds);
        return this;
    }

    @Override
    public @NotNull ReplyAction addComponents(@NotNull Collection<? extends MessageTopLevelComponent> components) {
        this.action.setComponents(components);
        return this;
    }

    @Override
    public @NotNull ReplyAction addFiles(@NotNull Collection<? extends FileUpload> files) {
        this.action.addFiles(files);
        return this;
    }

    @Override
    public @NotNull String getContent() {
        return this.action.getContent();
    }

    @Override
    public @NotNull List<MessageEmbed> getEmbeds() {
        return this.action.getEmbeds();
    }

    @Override
    public @NotNull List<MessageTopLevelComponentUnion> getComponents() {
        return this.action.getComponents();
    }

    @Override
    public boolean isUsingComponentsV2() {
        return this.action.isUsingComponentsV2();
    }

    @Override
    public @NotNull List<FileUpload> getAttachments() {
        return this.action.getAttachments();
    }

    @Override
    public boolean isSuppressEmbeds() {
        return this.action.isSuppressEmbeds();
    }

    @Override
    public @NotNull Set<String> getMentionedUsers() {
        return this.action.getMentionedUsers();
    }

    @Override
    public @NotNull Set<String> getMentionedRoles() {
        return this.action.getMentionedRoles();
    }

    @Override
    public @NotNull EnumSet<Message.MentionType> getAllowedMentions() {
        return this.action.getAllowedMentions();
    }

    @Override
    public boolean isMentionRepliedUser() {
        return this.action.isMentionRepliedUser();
    }

    @Override
    public @Nullable MessagePollData getPoll() {
        return this.action.getPoll();
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
        this.action.setSuppressEmbeds(suppressed);
        return this;
    }

    @Override
    public @NotNull ReplyAction setVoiceMessage(boolean voiceMessage) {
        this.action.setVoiceMessage(voiceMessage);
        return this;
    }

    @Override
    public @NotNull ReplyAction setContent(@Nullable String s) {
        this.action.setContent(s);
        return this;
    }

    @Override
    public @NotNull ReplyAction setEmbeds(@NotNull Collection<? extends MessageEmbed> collection) {
        this.action.setEmbeds(collection);
        return this;
    }

    @Override
    public @NotNull ReplyAction setComponents(@NotNull Collection<? extends MessageTopLevelComponent> collection) {
        this.action.setComponents(collection);
        return this;
    }

    @Override
    public @NotNull ReplyAction useComponentsV2(boolean b) {
        this.action.useComponentsV2(b);
        return this;
    }

    @Override
    public @NotNull ReplyAction setSuppressEmbeds(boolean b) {
        this.action.setSuppressEmbeds(b);
        return this;
    }

    @Override
    public @NotNull ReplyAction setFiles(@Nullable Collection<? extends FileUpload> collection) {
        this.action.setFiles(collection);
        return this;
    }

    @Override
    public @NotNull ReplyAction mentionRepliedUser(boolean b) {
        return wrap(this.action.mentionRepliedUser(b));
    }

    @Override
    public @NotNull ReplyAction setAllowedMentions(@Nullable Collection<Message.MentionType> collection) {
        return wrap(this.action.setAllowedMentions(collection));
    }

    @Override
    public @NotNull ReplyAction mention(@NotNull Collection<? extends IMentionable> collection) {
        return wrap(this.action.mention(collection));
    }

    @Override
    public @NotNull ReplyAction mentionUsers(@NotNull Collection<String> collection) {
        return wrap(this.action.mentionUsers(collection));
    }

    @Override
    public @NotNull ReplyAction mentionRoles(@NotNull Collection<String> collection) {
        return wrap(this.action.mentionRoles(collection));
    }
}
