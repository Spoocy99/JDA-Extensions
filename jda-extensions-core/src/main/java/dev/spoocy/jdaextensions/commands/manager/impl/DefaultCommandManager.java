package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.arguments.MessageOption;
import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.event.CommandPreProcessContext;
import dev.spoocy.jdaextensions.commands.event.MessageCommandContext;
import dev.spoocy.jdaextensions.commands.event.SlashCommandContext;
import dev.spoocy.jdaextensions.commands.manager.CommandAnnotationProcessor;
import dev.spoocy.jdaextensions.commands.manager.CommandListener;
import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandGroupData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import dev.spoocy.jdaextensions.core.DiscordBot;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.scheduler.JavaScheduler;
import dev.spoocy.utils.common.scheduler.Scheduler;
import dev.spoocy.utils.common.text.StringUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DefaultCommandManager implements CommandManager {

    private final ILogger LOGGER = ILogger.forThisClass();

    private final Map<String, CommandData> commandMap = new ConcurrentHashMap<>();
    private final CommandAnnotationProcessor annotationProcessor;
    private final boolean useSlashCommands;
    private final String messagePrefix;
    private final CommandListener listener;
    private final Scheduler commandScheduler;

    private DefaultCommandManager(
            boolean useSlashCommands,
            @Nullable String messagePrefix,
            @NotNull CommandAnnotationProcessor annotationProcessor,
            @NotNull CommandListener listener,
            int commandSchedulerCorePoolSize
    ) {

        this.useSlashCommands = useSlashCommands;
        this.messagePrefix = messagePrefix;
        this.annotationProcessor = annotationProcessor;
        this.listener = listener;
        this.commandScheduler = new JavaScheduler(commandSchedulerCorePoolSize);
    }

    @Override
    public boolean useSlashCommands() {
        return this.useSlashCommands;
    }

    @Override
    public boolean usePrefixCommands() {
        return !StringUtils.isNullOrEmpty(this.messagePrefix);
    }

    @Override
    public @Nullable String getPrefix() {
        return this.messagePrefix;
    }

    @Override
    public @NotNull CommandListener getListener() {
        return this.listener;
    }

    @Override
    public @NotNull Collection<DiscordCommand> getCommands() {
        return Collector.of(commandMap.values())
                .asList(DiscordCommand.class);
    }

    @Override
    public @Nullable DiscordCommand getCommand(@NotNull String name) {
        return this.commandMap.get(name);
    }

    @Override
    public void register(@NotNull DiscordCommand command) {
        CommandData data = (CommandData) command;
        this.commandMap.put(data.name(), data);
        LOGGER.debug("Registered command '{}' ({} Commands)", data.name(), this.commandMap.size());
    }

    @Override
    public void register(@NotNull Collection<DiscordCommand> command) {
        for (DiscordCommand cmd : command) {
            this.register(cmd);
        }
    }

    @Override
    public void addCommand(@NotNull Object annotatedEntity) {
        this.register(this.annotationProcessor.parseCommand(annotatedEntity));

    }

    @Override
    public void addCommand(@NotNull Class<?> annotatedClasses) {
        this.register(this.annotationProcessor.parseCommand(annotatedClasses));
    }

    @Override
    public @NotNull CommandManager removeCommand(@NotNull String name) {
        this.commandMap.remove(name);
        LOGGER.debug("Unregistered command '{}' ({} Commands)", name, this.commandMap.size());
        return this;
    }

    @Override
    public void commitCommands(@NotNull JDA jda, @Nullable Set<Long> guildIds) {

        if (guildIds != null) {

            for (Long guildId : guildIds) {
                Guild guild = jda.getGuildById(guildId);

                if (guild == null) {
                    LOGGER.warn(
                            "Guild with ID {} not found on shard {}. Skipping command commit for this guild.",
                            guildId,
                            jda.getShardInfo().getShardId()
                    );
                    continue;
                }

                CommandListUpdateAction commands = guild.updateCommands();

                int count = 0;
                for (CommandData data : this.commandMap.values()) {
                    commands = commands.addCommands(data.buildJDA());
                    count++;
                }

                commands.queue();
                LOGGER.debug(
                        "Commited {} commands to guild ID {} on shard {}", count, guildId, jda.getShardInfo()
                                .getShardId()
                );
            }

            LOGGER.info("Commited {} guild commands on shard {}", guildIds.size(), jda.getShardInfo().getShardId());
            return;
        }

        CommandListUpdateAction commands = jda.updateCommands();

        int count = 0;
        for (CommandData data : this.commandMap.values()) {
            commands = commands.addCommands(data.buildJDA());
            count++;
        }

        commands.queue();
        LOGGER.info("Commited {} commands on shard {}", count, jda.getShardInfo().getShardId());
    }

    @Override
    public void shutdown() {
        if (this.commandScheduler instanceof AutoCloseable) {
            try {
                ((AutoCloseable) this.commandScheduler).close();
            } catch (Exception e) {
                LOGGER.error("Failed to shutdown command scheduler.", e);
            }
        }
    }

    private boolean handleCommandPreProcess(@NotNull CommandPreProcessContext preProcessContext) {
        try {
            this.listener.onPreProcess(preProcessContext);
        } catch (Throwable e) {
            handleException(preProcessContext.getContext(), e);
            return true;
        }
        return preProcessContext.isCancelled();
    }

    private void handleUnknownCommand(@NotNull SlashCommandInteractionEvent event) {
        try {
            this.listener.onUnknownCommand(event);
        } catch (Throwable e) {
            LOGGER.error("Exception in unknown command handler.", e);
        }
    }

    private void handleUnknownCommand(@NotNull MessageReceivedEvent event) {
        try {
            this.listener.onUnknownCommand(event);
        } catch (Throwable e) {
            LOGGER.error("Exception in unknown command handler.", e);
        }
    }

    private void handleNoPermissions(@NotNull CommandContext context) {
        ensureAcknowledged(context);
        try {
            this.listener.onNoPermissions(context);
        } catch (Throwable e) {
            LOGGER.error("Exception in no permissions handler.", e);
        }
    }

    private void handleCooldown(@NotNull CommandContext context) {
        ensureAcknowledged(context);
        try {
            this.listener.onCooldown(context);
        } catch (Throwable e) {
            LOGGER.error("Exception in cooldown handler.", e);
        }
    }

    private void handleException(@NotNull CommandContext context, @NotNull Throwable throwable) {
        ensureAcknowledged(context);
        try {
            this.listener.onException(context, throwable);
        } catch (Throwable e) {
            LOGGER.error("Exception in exception handler for context {}.", context, e);
        }
    }

    private void ensureAcknowledged(@NotNull CommandContext context) {
        if (context.isInteraction() && !context.isAcknowledged()) {
            LOGGER.debug("Acknowledging interaction for context {} to send response messages.", context);
            context.acknowledge(false);
        }
    }

    @Override
    public void handleCommand(@NotNull SlashCommandInteractionEvent event, @NotNull DiscordBot bot) {
        if (!this.useSlashCommands()) {
            return;
        }

        CommandNodeData data = findNode(
                event.getName(),
                event.getSubcommandGroup(),
                event.getSubcommandName()
        );

        if (data == null) {
            LOGGER.warn("Received slash command interaction for unregistered command '{}'", event.getName());
            this.handleUnknownCommand(event);
            return;
        }

        CommandContext context = new SlashCommandContext(bot, this, event);

        if (data.acknowledge()) {
            LOGGER.debug("Acknowledging interaction for command '{}'.", data.name());
            context.acknowledge(data.ephemeral());
        }

        executeContext(CommandData.extract(data), data, context);
    }

    @Override
    public void handlePrefixCommand(@NotNull MessageReceivedEvent event, @NotNull DiscordBot bot) {
        if (!this.usePrefixCommands()) {
            return;
        }

        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        String rawContent = event.getMessage().getContentRaw();
        if (this.messagePrefix == null || !rawContent.startsWith(this.messagePrefix)) {
            return;
        }

        String stripped = rawContent.substring(this.messagePrefix.length()).trim();
        if (stripped.isEmpty()) {
            return;
        }

        List<String> tokens = parseTokens(stripped);
        if (tokens.isEmpty()) {
            return;
        }

        String commandName = tokens.get(0);
        CommandData data = (CommandData) getCommand(commandName);

        if (data == null) {
            this.handleUnknownCommand(event);
            return;
        }

        CommandNodeData targetNode = null;
        String fullCommandName = commandName;
        String subcommandName = null;
        int argStartIndex = 1;

        if (tokens.size() > 2 && data.hasGroup(tokens.get(1))) {
            CommandGroupData group = data.getSubCommandGroupData(tokens.get(1));
            if (group.hasNode(tokens.get(2))) {
                targetNode = (CommandNodeData) group.getNode(tokens.get(2));
                fullCommandName = commandName + " " + tokens.get(1) + " " + tokens.get(2);
                subcommandName = tokens.get(1) + "/" + tokens.get(2);
                argStartIndex = 3;
            }
        }

        if (targetNode == null && tokens.size() > 1 && data.hasNode(tokens.get(1))) {
            targetNode = (CommandNodeData) data.getNode(tokens.get(1));
            fullCommandName = commandName + " " + tokens.get(1);
            subcommandName = tokens.get(1);
            argStartIndex = 2;
        }

        if (targetNode == null) {
            try {
                targetNode = data.rootNode();
                fullCommandName = commandName;
                subcommandName = null;
                argStartIndex = 1;
            } catch (IllegalStateException ignored) {
                // Command has no root node and no matching subcommands
            }
        }

        if (targetNode == null) {
            this.handleUnknownCommand(event);
            return;
        }

        List<String> argTokens = tokens.subList(argStartIndex, tokens.size());
        List<AbstractArgument> argumentData = targetNode.getArgumentData();
        List<ProvidedArgument> providedArguments = new ArrayList<>();

        int tokenIndex = 0;
        int attachmentIndex = 0;
        List<Message.Attachment> attachments = event.getMessage().getAttachments();

        for (int i = 0; i < argumentData.size(); i++) {
            AbstractArgument arg = argumentData.get(i);
            if (arg.type() == OptionType.ATTACHMENT) {
                if (attachmentIndex < attachments.size()) {
                    providedArguments.add(new MessageOption(arg.name(), attachments.get(attachmentIndex++), event));
                }
            } else if (tokenIndex < argTokens.size()) {
                String rawValue;
                if (i == argumentData.size() - 1 && arg.type() == OptionType.STRING) {
                    rawValue = String.join(" ", argTokens.subList(tokenIndex, argTokens.size()));
                    tokenIndex = argTokens.size();
                } else {
                    rawValue = argTokens.get(tokenIndex++);
                }
                providedArguments.add(new MessageOption(arg.name(), arg.type(), rawValue, event));
            }
        }

        CommandContext context = new MessageCommandContext(
                fullCommandName,
                commandName,
                subcommandName,
                providedArguments,
                bot,
                this,
                event
        );

        executeContext(data, targetNode, context);
    }

    private static List<String> parseTokens(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuotes = true;
                    quoteChar = c;
                } else if (Character.isWhitespace(c)) {
                    if (current.length() > 0) {
                        tokens.add(current.toString());
                        current.setLength(0);
                    }
                } else {
                    current.append(c);
                }
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    @Nullable
    private CommandNodeData findNode(
            @NotNull String commandName,
            @Nullable String subCommandGroup,
            @Nullable String subCommandName
    ) {
        try {
            CommandData data = (CommandData) getCommand(commandName);
            if (data == null) {
                return null;
            }

            CommandNodeHolder holder = data;

            if (StringUtils.isNullOrEmpty(subCommandName)) {
                return data.rootNode();
            }

            if (!StringUtils.isNullOrEmpty(subCommandGroup)) {
                holder = data.getSubCommandGroupData(subCommandGroup);
            }

            return (CommandNodeData) holder.getNode(subCommandName);

        } catch (IllegalStateException e) {
            // This occurs when the command has no root but root was called
            return null;

        } catch (Exception e) {
            LOGGER.error("Failed to find command node for interaction event", e);
            return null;
        }
    }

    private void executeContext(
            @NotNull CommandData data,
            @NotNull CommandNodeData subCommand,
            @NotNull CommandContext context
    ) {

        CommandPreProcessContext preProcessEvent = new CommandPreProcessContext(data, subCommand, context);
        if (this.handleCommandPreProcess(preProcessEvent)) {
            return;
        }

        CommandPermission[] permission = subCommand.permissions();

        Member member = context.isGuild() ? context.getMember() : null;
        if (member != null && permission != null) {

            for (CommandPermission p : permission) {
                if (!p.isCovered(context)) {
                    this.handleNoPermissions(context);
                    return;
                }
            }

        }

        if (subCommand.hasCooldown() && !subCommand.cooldown().shouldExecute(context)) {

            if (!subCommand.acknowledge()) {
                LOGGER.warn("Non-acknowledged command '{}' has cooldown scope specified!", subCommand.name());
            }

            LOGGER.debug(
                    "Command '{}' is on cooldown for user {}.",
                    subCommand.name(),
                    context.getUser().getAsTag()
            );
            this.handleCooldown(context);
            return;
        }

        if (subCommand.sendTyping()) {
            LOGGER.debug("Sending typing for command '{}'", subCommand.name());
            context.getChannel()
                    .sendTyping()
                    .queue();
        }

        boolean isAsync = subCommand.async();

        if (isAsync && context.isInteraction() && !context.isAcknowledged()) {
            LOGGER.debug("Auto-acknowledging interaction for async command '{}'.", subCommand.name());
            context.acknowledge(subCommand.ephemeral());
        }

        Runnable task = () -> {
            try {
                subCommand.execute(context);
            } catch (Throwable e) {
                this.handleException(context, e);
            }
        };

        if (isAsync) {
            LOGGER.debug("Executing command '{}' asynchronously", subCommand.name());
            commandScheduler.runAsync(task);
        } else {
            LOGGER.debug("Executing command '{}' synchronously", subCommand.name());
            task.run();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<DiscordCommand> commands = new ArrayList<>();
        private final List<Object> annotatedInstances = new ArrayList<>();
        private final List<Class<?>> commandAnnotationClasses = new ArrayList<>();

        private CommandListener listener = new CommandListener() {
        };
        private CommandAnnotationProcessor annotationProcessor = DefaultCommandAnnotationProcessor.DEFAULT;
        private boolean useSlashCommands = true;
        private String messagePrefix = null;
        private int commandSchedulerCorePoolSize = 2;

        public Builder() {

        }

        public Builder annotationProcessor(@NotNull CommandAnnotationProcessor processor) {
            this.annotationProcessor = processor;
            return this;
        }

        public Builder listener(@NotNull CommandListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder useSlashCommands(boolean useSlashCommands) {
            this.useSlashCommands = useSlashCommands;
            return this;
        }

        public Builder messagePrefix(@Nullable String prefix) {
            this.messagePrefix = prefix;
            return this;
        }

        public Builder commandSchedulerCorePoolSize(int corePoolSize) {
            this.commandSchedulerCorePoolSize = corePoolSize;
            return this;
        }

        public Builder register(@NotNull DiscordCommand... command) {
            this.commands.addAll(Arrays.asList(command));
            return this;
        }

        public Builder registerCommand(@NotNull Object... instance) {
            this.annotatedInstances.addAll(Arrays.asList(instance));
            return this;
        }

        public Builder registerCommand(@NotNull Class<?>... annotatedClass) {
            this.commandAnnotationClasses.addAll(Arrays.asList(annotatedClass));
            return this;
        }

        public DefaultCommandManager build() {
            DefaultCommandManager manager = new DefaultCommandManager(
                    this.useSlashCommands,
                    this.messagePrefix,
                    this.annotationProcessor,
                    this.listener,
                    this.commandSchedulerCorePoolSize
            );

            manager.register(this.commands);
            this.annotatedInstances.forEach(manager::addCommand);
            this.commandAnnotationClasses.forEach(manager::addCommand);
            return manager;
        }

    }

}
