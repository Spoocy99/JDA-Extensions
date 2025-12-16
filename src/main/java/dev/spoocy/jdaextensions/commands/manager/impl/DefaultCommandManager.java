package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.manager.CommandListener;
import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.structure.CommandNode;
import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.event.CommandPreProcessContext;
import dev.spoocy.jdaextensions.commands.event.SlashCommandContext;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.text.StringUtils;
import dev.spoocy.utils.common.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;
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
    private final boolean useSlashCommands;
    private final String messagePrefix;
    private final CommandListener listener;

    private DefaultCommandManager(
            boolean useSlashCommands,
            @Nullable String messagePrefix,
            @NotNull CommandListener listener) {

        this.useSlashCommands = useSlashCommands;
        this.messagePrefix = messagePrefix;
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
        return Collector.of(commandMap.values()).asList(DiscordCommand.class);
    }

    @Override
    public @Nullable DiscordCommand getCommand(@NotNull String name) {
        return this.commandMap.get(name);
    }

    @Override
    public void register(@NotNull DiscordCommand... command) {
        this.register(Arrays.asList(command));
    }

    @Override
    public void register(@NotNull Collection<DiscordCommand> command) {
        for (DiscordCommand c : command) {
            CommandData data = (CommandData) c;
            this.commandMap.put(data.name(), data);
            LOGGER.debug("Registered command '{}' ({} Commands)", data.name(), this.commandMap.size());
        }
    }

    @Override
    public void registerClasses(@NotNull Class<?>... annotatedClass) {
        this.registerClasses(Arrays.asList(annotatedClass));
    }

    @Override
    public void registerClasses(@NotNull Collection<Class<?>> annotatedClasses) {
        this.register(
                Collector.of(annotatedClasses)
                        .map(CommandAnnotationProcessor::parseCommand)
                        .asList(DiscordCommand.class)
        );
    }

    @Override
    public @NotNull CommandManager removeCommand(@NotNull String name) {
        this.commandMap.remove(name);
        LOGGER.debug("Unregistered command '{}' ({} Commands)", name, this.commandMap.size());
        return this;
    }

    @Override
    public void updateCommands(@NotNull JDA jda) {
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

    private void handleUnknownCommand(@NotNull MessageReceivedEvent event) {
        Scheduler.runAsync(() -> this.listener.onUnknownCommand(event))
                .onException(e -> LOGGER.error("Exception in unknown command handler.", e));
    }

    private void handleNoPermissions(@NotNull CommandContext context) {
        Scheduler.runAsync(() -> this.listener.onNoPermissions(context))
                .onException(e -> this.listener.onException(context, e));
    }

    private void handleCooldown(@NotNull CommandContext context) {
        Scheduler.runAsync(() -> this.listener.onCooldown(context))
                .onException(e -> this.listener.onException(context, e));
    }

    @Override
    public void handleCommand(@NotNull SlashCommandInteractionEvent event) {
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
            return;
        }

        if (data.acknowledge()) {
            event.deferReply(data.ephemeral()).queue();
        }

        CommandContext context = new SlashCommandContext(this, event);
        executeContext(CommandData.extract(data), data, context);
    }

    @Override
    public void handlePrefixCommand(@NotNull MessageReceivedEvent event) { }

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
        } catch (Exception e) {
            LOGGER.error("Failed to find command node for interaction event", e);
            return null;
        }
    }

    private void executeContext(@NotNull CommandData data, @NotNull CommandNodeData subCommand, @NotNull CommandContext context) {

        CommandPreProcessContext preProcessEvent = new CommandPreProcessContext(data, subCommand, context);
        this.listener.onPreProcess(preProcessEvent);
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

        if (subCommand.hasCooldown() && !subCommand.cooldown().shouldExecute(context)) {
            this.handleCooldown(context);
            return;
        }

        if (subCommand.sendTyping()) {
            context.getChannel().sendTyping().queue();
        }

        // Execute Command async
        if (subCommand.async()) {
            subCommand.executeAsync(context)
                    .onException(e -> this.listener.onException(context, e));
            return;
        }

        // Execute Command sync
        subCommand.execute(context)
                .onException(e -> this.listener.onException(context, e));

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private CommandListener listener = new CommandListener() {
        };
        private boolean useSlashCommands = true;
        private String messagePrefix = null;
        private final List<DiscordCommand> commands = new ArrayList<>();
        private final List<Class<?>> commandAnnotationClasses = new ArrayList<>();

        public Builder() {

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

        public Builder register(@NotNull Class<?>... annotatedClass) {
            this.commandAnnotationClasses.addAll(Arrays.asList(annotatedClass));
            return this;
        }

        public DefaultCommandManager build() {
            DefaultCommandManager manager = new DefaultCommandManager(
                    this.useSlashCommands,
                    this.messagePrefix,
                    this.listener
            );

            manager.register(this.commands);
            manager.registerClasses(this.commandAnnotationClasses);
            return manager;
        }

    }

}
