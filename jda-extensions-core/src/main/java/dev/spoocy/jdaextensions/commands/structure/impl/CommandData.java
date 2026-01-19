package dev.spoocy.jdaextensions.commands.structure.impl;

import com.google.common.collect.ImmutableSet;
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.structure.CommandNode;
import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
public class CommandData extends AbstractCommandNodeHolder implements DiscordCommand {

    private final Map<String, CommandGroupData> subCommandGroups = new ConcurrentHashMap<>();

    @Nullable
    private CommandNodeData rootCommand;
    private final Set<InteractionContextType> context;
    private final DefaultMemberPermissions defaultPermissions;
    private final boolean nsfw;

    public CommandData(
            @NotNull String name,
            @NotNull String description,
            boolean nsfw,
            @NotNull DefaultMemberPermissions defaultPermissions,
            @NotNull InteractionContextType... context
    ) {
        super(name, description);
        this.nsfw = nsfw;
        this.defaultPermissions = defaultPermissions;
        this.context = ImmutableSet.copyOf(context);
    }

    public CommandData(
            @NotNull String name,
            @NotNull String description,
            boolean nsfw,
            @NotNull DefaultMemberPermissions defaultPermissions,
            @NotNull Set<InteractionContextType> context
    ) {
        super(name, description);
        this.nsfw = nsfw;
        this.defaultPermissions = defaultPermissions;
        this.context = ImmutableSet.copyOf(context);
    }

    public void setRootCommand(@Nullable CommandNodeData rootCommand) {
        this.rootCommand = rootCommand;
    }

    @NotNull
    public CommandGroupData getOrCreateSubCommandGroup(@NotNull String name, @NotNull String description) {
        return this.subCommandGroups.computeIfAbsent(name, k -> new CommandGroupData(this, name, description));
    }

    @NotNull
    public CommandGroupData getSubCommandGroupData(@NotNull String name) {
        if (!this.subCommandGroups.containsKey(name)) {
            throw new IllegalArgumentException("Sub-command group with name '" + name + "' does not exist!");
        }
        return this.subCommandGroups.get(name);
    }

    @Override
    public String[] getGroupNames() {
        return this.subCommandGroups
                .keySet()
                .toArray(String[]::new);
    }

    @Override
    public boolean hasGroup(String name) {
        return this.subCommandGroups.containsKey(name);
    }

    @Override
    public @NotNull CommandNodeHolder getGroup(@NotNull String name) {
        return this.getSubCommandGroupData(name);
    }

    @Override
    public @NotNull Set<InteractionContextType> context() {
        return this.context;
    }

    @Override
    public @NotNull DefaultMemberPermissions defaultPermissions() {
        return this.defaultPermissions;
    }

    @Override
    public boolean nsfw() {
        return this.nsfw;
    }

    @Override
    public @NotNull CommandNodeData rootNode() {
        if (this.rootCommand == null) {
            throw new IllegalStateException("This command does not have a root command node!");
        }
        return this.rootCommand;
    }

    public SlashCommandData buildJDA() {
        SlashCommandData data = Commands.slash(this.name(), this.description());
        data.setContexts(this.context);
        data.setDefaultPermissions(this.defaultPermissions);
        data.setNSFW(this.nsfw);

        for (CommandGroupData group : this.subCommandGroups.values()) {
            data.addSubcommandGroups(group.buildJDA());
        }

        for (CommandNodeData node : this.subCommands.values()) {
            data.addSubcommands(node.buildJDA());
        }

        if (this.rootCommand != null) {

            for (AbstractArgument argument : this.rootCommand.getArgumentData()) {
                data.addOptions(argument.buildJDA());
            }

        }

        return data;
    }

    @NotNull
    public static CommandData extract(@NotNull CommandNode node) {
       return extract(node.parent());
    }

    @NotNull
    public static CommandData extract(@NotNull CommandNodeHolder node) {
        CommandNodeHolder holder = node.parent();
        if (!(holder instanceof CommandData)) {
            throw new IllegalArgumentException("The provided node is not part of a DiscordCommand!");
        }
        return (CommandData) holder;
    }
}
