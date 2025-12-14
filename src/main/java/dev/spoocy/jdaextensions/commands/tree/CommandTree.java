package dev.spoocy.jdaextensions.commands.tree;

import com.google.common.collect.ImmutableSet;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.core.DiscordBot;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandTree extends AbstractCommandTree<CommandTree> implements GroupChainable<CommandTree>, CommandChainable<CommandTree> {

    public static SubCommand command(@NotNull String name, @NotNull String description) {
        return new SubCommand(name, description);
    }

    public static SubCommandGroup group(@NotNull String name, @NotNull String description) {
        return new SubCommandGroup(name, description);
    }

    private final List<SubCommand> subCommands = new ArrayList<>();
    private final List<SubCommandGroup> subCommandGroups = new ArrayList<>();
    private Set<InteractionContextType> context = InteractionContextType.ALL;
    private DefaultMemberPermissions defaultPermissions = DefaultMemberPermissions.ENABLED;
    private boolean nsfw = false;

    public CommandTree(@NotNull String name,
                       @NotNull String description) {
        super(name, description);
    }

    public CommandTree withContext(@NotNull InteractionContextType... context) {
        this.context = ImmutableSet.copyOf(context);
        return this;
    }

    public CommandTree withDefaultPermissions(@NotNull DefaultMemberPermissions permissions) {
        this.defaultPermissions = permissions;
        return this;
    }

    public CommandTree withNsfw(boolean nsfw) {
        this.nsfw = nsfw;
        return this;
    }

    @Override
    public CommandTree then(@NotNull SubCommandGroup group) {
        this.subCommandGroups.add(group);
        return this;
    }

    @Override
    public CommandTree then(@NotNull SubCommand subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    public DiscordCommand build() {
        CommandData data = new CommandData(
                this.name,
                this.description,
                this.nsfw,
                this.defaultPermissions,
                this.context
        );

        if(super.executor != null) {
            data.setRootCommand(buildNodeData(data));
        }

        for (SubCommand cmd : this.subCommands) {
            data.addSubCommandData(cmd.buildNodeData(data));
        }

        for (SubCommandGroup group : this.subCommandGroups) {
            data.getOrCreateSubCommandGroup(group.name, group.description)
                    .addSubCommandData(group.buildNodeData(data));
        }

        return data;
    }

    public void register() {
        DiscordBot.getInstance()
                .getCommandManager()
                .register(this.build());
    }

    @Override
    public CommandTree instance() {
        return this;
    }

    @Override
    public String toString() {
        return "CommandTree{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subCommands=" + subCommands +
                ", subCommandGroups=" + subCommandGroups +
                ", context=" + context +
                ", defaultPermissions=" + defaultPermissions +
                ", nsfw=" + nsfw +
                '}';
    }
}
