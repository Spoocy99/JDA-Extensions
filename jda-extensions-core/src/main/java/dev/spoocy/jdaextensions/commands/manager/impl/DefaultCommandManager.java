package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.manager.CommandAnnotationProcessor;
import dev.spoocy.jdaextensions.commands.manager.CommandListener;
import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.event.CommandPreProcessContext;
import dev.spoocy.jdaextensions.commands.event.SlashCommandContext;
import dev.spoocy.jdaextensions.core.DiscordBot;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.text.StringUtils;
import dev.spoocy.utils.common.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

    private DefaultCommandManager(
            boolean useSlashCommands,
            @Nullable String messagePrefix,
            @NotNull CommandAnnotationProcessor annotationProcessor,
            @NotNull CommandListener listener
    ) {

        this.useSlashCommands = useSlashCommands;
        this.messagePrefix = messagePrefix;
        this.annotationProcessor = annotationProcessor;
        this.listener = listener;
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
                    LOGGER.warn("Guild with ID {} not found on shard {}. Skipping command commit for this guild.", guildId, jda.getShardInfo().getShardId());
                    continue;
                }

                CommandListUpdateAction commands = guild.updateCommands();

                int count = 0;
                for (CommandData data : this.commandMap.values()) {
                    commands = commands.addCommands(data.buildJDA());
                    count++;
                }

                commands.queue();
                LOGGER.debug("Commited {} commands to guild ID {} on shard {}", count, guildId, jda.getShardInfo()
                        .getShardId());
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

    private void handleCommandPreProcess(@NotNull CommandPreProcessContext context) {
        Scheduler.runSync(() -> this.listener.onPreProcess(context))
                .onException(e -> this.listener.onException(context.getContext(), e));
    }

    private void handleUnknownCommand(@NotNull SlashCommandInteractionEvent event) {
        Scheduler.runAsync(() -> this.listener.onUnknownCommand(event))
                .onException(e -> LOGGER.error("Exception in unknown command handler.", e));
    }

    private void handleUnknownCommand(@NotNull MessageReceivedEvent event) {
        Scheduler.runAsync(() -> this.listener.onUnknownCommand(event))
                .onException(e -> LOGGER.error("Exception in unknown command handler.", e));
    }

    private void handleNoPermissions(@NotNull CommandContext context) {
        ensureAcknowledged(context);
        Scheduler.runAsync(() -> this.listener.onNoPermissions(context))
                .onException(e -> LOGGER.error("Exception in no permissions handler.", e));
    }

    private void handleCooldown(@NotNull CommandContext context) {
        ensureAcknowledged(context);
        Scheduler.runAsync(() -> this.listener.onCooldown(context))
                .onException(e -> LOGGER.error("Exception in cooldown handler.", e));
    }

    private void handleException(@NotNull CommandContext context, @NotNull Throwable throwable) {
        ensureAcknowledged(context);
        Scheduler.runAsync(() -> this.listener.onException(context, throwable))
                .onException(e -> LOGGER.error("Exception in exception handler.", e));
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

        if(data.acknowledge()) {
            LOGGER.debug("Acknowledging interaction for command '{}'.", data.name());
            context.acknowledge(data.ephemeral());
        }

        executeContext(CommandData.extract(data), data, context);
    }

    @Override
    public void handlePrefixCommand(@NotNull MessageReceivedEvent event, @NotNull DiscordBot bot) {
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
        this.handleCommandPreProcess(preProcessEvent);
        if (preProcessEvent.isCancelled()) {
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

        if (subCommand.hasCooldown() && !subCommand.cooldown()
                .shouldExecute(context)) {

            if (!subCommand.acknowledge()) {
                LOGGER.warn("Non-acknowledged command '{}' has cooldown scope specified!");
            }

            LOGGER.debug("Command '{}' is on cooldown for user {}.", subCommand.name(), context.getUser()
                    .getAsTag());
            this.handleCooldown(context);
            return;
        }

        if (subCommand.sendTyping()) {
            LOGGER.debug("Sending typing for command '{}'", subCommand.name());
            context.getChannel()
                    .sendTyping()
                    .queue();
        }

        // Execute Command async
        if (subCommand.async()) {
            LOGGER.debug("Executing command '{}' asynchronously", subCommand.name());
            subCommand.executeAsync(context)
                    .onException(e -> this.handleException(context, e));
            return;
        }

        LOGGER.debug("Executing command '{}' synchronously", subCommand.name());
        // Execute Command sync
        subCommand.execute(context)
                .onException(e -> this.handleException(context, e));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private CommandListener listener = new CommandListener() {
        };
        private CommandAnnotationProcessor annotationProcessor = DefaultCommandAnnotationProcessor.DEFAULT;
        private boolean useSlashCommands = true;
        private String messagePrefix = null;
        private final List<DiscordCommand> commands = new ArrayList<>();
        private final List<Object> annotatedInstances = new ArrayList<>();
        private final List<Class<?>> commandAnnotationClasses = new ArrayList<>();

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
                    this.listener
            );

            manager.register(this.commands);
            this.annotatedInstances.forEach(manager::addCommand);
            this.commandAnnotationClasses.forEach(manager::addCommand);
            return manager;
        }

    }

}
