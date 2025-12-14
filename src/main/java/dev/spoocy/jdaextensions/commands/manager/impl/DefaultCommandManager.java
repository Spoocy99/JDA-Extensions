package dev.spoocy.jdaextensions.commands.manager.impl;

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
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.text.StringUtils;
import dev.spoocy.utils.common.scheduler.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
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
    private CommandListener listener = new CommandListener() {
    };

    public DefaultCommandManager() {

    }

    @Override
    public @NotNull CommandListener getListener() {
        return this.listener;
    }

    @Override
    public CommandManager setListener(@NotNull CommandListener listener) {
        this.listener = listener;
        return this;
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

    private void handleEvent(@NotNull CommandData data, @NotNull CommandNodeData subCommand, @NotNull CommandContext context) {

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
                    Scheduler.runAsync(() -> this.listener.onNoPermissions(context))
                            .onException(e -> this.listener.onException(context, e));
                    return;
                }
            }

        }

        if (subCommand.hasCooldown() && !subCommand.cooldown().shouldExecute(context)) {
            Scheduler.runAsync(() -> this.listener.onCooldown(context))
                    .onException(e -> this.listener.onException(context, e));
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

    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String name = event.getName();
        CommandData data = (CommandData) getCommand(name);
        if (data == null) {
            LOGGER.warn("Received slash command interaction for unregistered command '{}'", name);
            return;
        }
        CommandNodeData subCommand = findNode(data, event);

        if (subCommand == null) {
            LOGGER.warn("Received slash command interaction for unregistered sub-command of '{}'", name);
            return;
        }

        if(subCommand.acknowledge()) {
            event.deferReply(subCommand.ephemeral()).queue();
        }

        CommandContext context = new SlashCommandContext(this, event);
        handleEvent(data, subCommand, context);
    }

    private CommandNodeData findNode(@NotNull CommandData data, @NotNull SlashCommandInteractionEvent event) {
        try {
            CommandNodeHolder holder = data;

            String subCommandName = event.getSubcommandName();
            if (StringUtils.isNullOrEmpty(subCommandName)) {
                return data.rootNode();
            }

            String group = event.getSubcommandGroup();
            if (!StringUtils.isNullOrEmpty(group)) {
                holder = data.getSubCommandGroupData(group);
            }

            return (CommandNodeData) holder.getNode(subCommandName);
        } catch (Exception e) {
            LOGGER.error("Failed to find command node for interaction event", e);
            return null;
        }
    }

}
