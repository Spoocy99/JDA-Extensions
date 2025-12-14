package dev.spoocy.jdaextensions.commands.message;

import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ReplyAction extends RestAction<Message> {

    @NotNull
    @Override
    ReplyAction setCheck(@Nullable BooleanSupplier checks);

    @NotNull
    @Override
    ReplyAction deadline(long timestamp);

    @NotNull
    @Override
    default ReplyAction timeout(long timeout, @NotNull TimeUnit unit) {
		return (ReplyAction) RestAction.super.timeout(timeout, unit);
	}

    @NotNull
    ReplyAction addContent(@NotNull String content);

    @NotNull
    ReplyAction addEmbeds(@NotNull Collection<? extends MessageEmbed> embeds);

    @NotNull
    default ReplyAction addEmbeds(final @NotNull MessageEmbed... embeds) {
        return addEmbeds(Arrays.asList(embeds));
    }

    @NotNull
    ReplyAction addComponents(@NotNull Collection<? extends MessageTopLevelComponent> components);

    @NotNull
    default ReplyAction addComponents(@NotNull MessageTopLevelComponent... components) {
        return addComponents(Arrays.asList(components));
    }

    @NotNull
    default ReplyAction addComponents(@NotNull ComponentTree<? extends MessageTopLevelComponent> tree) {
        return addComponents(tree.getComponents());
    }

    @NotNull
    ReplyAction addFiles(@NotNull Collection<? extends FileUpload> files);

    @NotNull
    List<FileUpload> getAttachments();

    @NotNull
    ReplyAction setPoll(@Nullable MessagePollData poll);

    @NotNull
    ReplyAction setTTS(boolean tts);

    @NotNull
    ReplyAction setSuppressedNotifications(boolean suppressed);

    @NotNull
    ReplyAction setVoiceMessage(boolean voiceMessage);

    @NotNull
    @Deprecated
    default ReplyAction setActionRow(@NotNull ActionRow... components) {
        return this.addComponents(components);
    }

}
