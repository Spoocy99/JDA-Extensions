package dev.spoocy.jdaextensions.commands.event;

import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.arguments.WrappedOption;
import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.message.ReplyAction;
import dev.spoocy.utils.common.collections.Collector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MessageCommandContext extends AbstractCommandContext {

    private final String fullName;
    private final String commandName;
    private final String subcommandName;

    private final MessageReceivedEvent event;
    private final List<WrappedOption> arguments;

    public MessageCommandContext(@NotNull String fullName,
                                 @NotNull String commandName,
                                 @Nullable String subcommandName,
                                 @NotNull List<WrappedOption> arguments,
                                 @NotNull CommandManager manager,
                                 @NotNull MessageReceivedEvent event
    ) {
        super(manager, event.getChannel());
        this.fullName = fullName;
        this.commandName = commandName;
        this.subcommandName = subcommandName;
        this.arguments = arguments;
        this.event = event;
    }

    @Override
    public @NotNull JDA getJDA() {
        return this.event.getJDA();
    }

    @Override
    public long getResponseNumber() {
        return this.event.getResponseNumber();
    }

    @Override
    public @Nullable DataObject getRawData() {
        return this.event.getRawData();
    }

    @NotNull
    @Override
    public String getFullCommand() {
        return this.fullName;
    }

    @NotNull
    @Override
    public String getCommand() {
        return this.commandName;
    }

    @Nullable
    @Override
    public String getSubCommand() {
        return this.subcommandName;
    }

    @NotNull
    @Override
    public List<? extends ProvidedArgument> getArguments() {
        return this.arguments;
    }

    @Nullable
    @Override
    public ProvidedArgument getArgument(@NotNull String name) {
        return Collector.of(this.arguments).first(a -> a.getName().equals(name)).orElse(null);
    }

    @Override
    public @NotNull User getUser() {
        return this.event.getAuthor();
    }

    @Override
    public boolean isGuild() {
        return this.event.isFromGuild();
    }

    @Override
    public boolean isPrivate() {
        return this.event.getChannel().getType().equals(ChannelType.PRIVATE);
    }

    @NotNull
    @Override
    public Member getMember() {
        this.throwIfNotInGuild();
        return this.event.getMember();
    }

    @NotNull
    @Override
    public Guild getGuild() {
        this.throwIfNotInGuild();
        return event.getGuild();
    }

    @NotNull
    @Override
    public TextChannel getTextChannel() {
        return this.event.getChannel().asTextChannel();
    }

    @Override
    public @NotNull PrivateChannel getPrivateChannel() {
        return getUser().openPrivateChannel().complete();
    }

    @Override
    public boolean isInteraction() {
        return false;
    }

    @Override
    public @NotNull Interaction getInteraction() {
        throw new IllegalStateException("Command is not called as an interaction!");
    }

    @Override
    public @Nullable Event getUnderlyingEvent() {
        return this.event;
    }

    @Override
    public ReplyAction reply(@NotNull String content) {
        return wrap(this.event.getMessage().reply(content));
    }

    @Override
    public ReplyAction reply(@NotNull Message message) {
        return wrap(this.event.getMessage().reply(new MessageCreateBuilder().applyMessage(message).build()));
    }

    @Override
    public ReplyAction reply(@NotNull MessageCreateData message) {
        return wrap(this.event.getMessage().reply(message));
    }

    @Override
    public ReplyAction reply(@NotNull MessageEmbed embed, MessageEmbed... other) {
        MessageCreateBuilder builder = new MessageCreateBuilder().addEmbeds(embed);
        for (MessageEmbed e : other) {
            builder.addEmbeds(e);
        }
        return wrap(this.event.getMessage().reply(builder.build()));
    }

    @Override
    public ReplyAction reply(@NotNull Collection<MessageEmbed> embed) {
        return wrap(this.event.getMessage().reply(new MessageCreateBuilder().addEmbeds(embed).build()));
    }

    @Override
    public ModalCallbackAction replyModal(@NotNull Modal modal) {
        throw new UnsupportedOperationException("Cannot reply with a modal to a message command!");
    }

    @Override
    public ReplyAction replyComponents(@NotNull Collection<? extends MessageTopLevelComponent> components) {
        return wrap(this.event.getMessage().replyComponents(components));
    }

    @Override
    public ReplyAction replyComponents(@NotNull MessageTopLevelComponent component, @NotNull MessageTopLevelComponent... other) {
        return wrap(this.event.getMessage().replyComponents(component, other));
    }

    @Override
    public ReplyAction replyComponents(@NotNull ComponentTree<? extends MessageTopLevelComponent> tree) {
        return wrap(this.event.getMessage().replyComponents(tree));
    }

    @Override
    public ReplyAction replyFiles(@NotNull FileUpload... files) {
        return wrap(this.event.getMessage().replyFiles(files));
    }

    @Override
    public ReplyAction replyFiles(@NotNull Collection<? extends FileUpload> files) {
        return wrap(this.event.getMessage().replyFiles(files));
    }

    @Override
    public ReplyAction replyPoll(@NotNull MessagePollData poll) {
        return wrap(this.event.getMessage().replyPoll(poll));
    }

    @Override
    public ReplyAction send(@NotNull String message) {
        return wrap(this.event.getMessage().reply(message));
    }

    @Override
    public ReplyAction send(@NotNull Message message) {
        return wrap(this.event.getMessage().reply(new MessageCreateBuilder().applyMessage(message).build()));
    }

    @Override
    public ReplyAction send(@NotNull MessageEmbed embed) {
        return wrap(this.event.getMessage().reply(new MessageCreateBuilder().addEmbeds(embed).build()));
    }

    @Override
    public ReplyAction sendPrivate(@NotNull String message) {
        return wrap(getPrivateChannel().sendMessage(message));
    }

    @Override
    public ReplyAction sendPrivate(@NotNull Message message) {
        return wrap(getPrivateChannel().sendMessage(new MessageCreateBuilder().applyMessage(message).build()));
    }

    @Override
    public ReplyAction sendPrivate(@NotNull MessageEmbed embed) {
        return wrap(getPrivateChannel().sendMessage(new MessageCreateBuilder().addEmbeds(embed).build()));
    }
}
