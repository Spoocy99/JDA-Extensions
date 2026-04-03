package dev.spoocy.jdaextensions.commands.event;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.message.action.WrappedMessageReplyAction;
import dev.spoocy.jdaextensions.commands.message.action.WrappedWebhookReplyAction;
import dev.spoocy.jdaextensions.commands.message.ReplyAction;
import dev.spoocy.jdaextensions.core.DiscordBot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractCommandContext implements CommandContext, GenericEvent {

    protected final DiscordBot bot;
    protected final CommandManager manager;
    protected final MessageChannel channel;

    protected AbstractCommandContext(
            @NotNull DiscordBot bot,
            @NotNull CommandManager manager,
            @NotNull MessageChannel channel
    ) {
        this.bot = bot;
        this.manager = manager;
        this.channel = channel;
    }

    protected void throwIfNotInGuild() {
        if (!isGuild()) throw new IllegalStateException("Command is not called in a guild!");
    }

    protected void throwIfNotInPrivate() {
        if (!isPrivate()) throw new IllegalStateException("Command is not called in a private channel!");
    }

    protected ReplyAction wrap(@NotNull MessageCreateAction action) {
        return WrappedMessageReplyAction.wrap(action);
    }

    protected ReplyAction wrap(@NotNull WebhookMessageCreateAction<Message> action) {
        return WrappedWebhookReplyAction.wrap(action);
    }

    @Override
    public @NotNull DiscordBot getProvidingBot() {
        return this.bot;
    }

    @Override
    public @NotNull CommandManager getCommandManager() {
        return this.manager;
    }

    @Override
    @NotNull
    public MessageChannel getChannel() {
        return channel;
    }
}
