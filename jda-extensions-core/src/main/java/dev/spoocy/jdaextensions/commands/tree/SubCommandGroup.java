package dev.spoocy.jdaextensions.commands.tree;

import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SubCommandGroup implements CommandChainable<SubCommandGroup> {

    protected final String name;
    protected final String description;
    protected final List<SubCommand> subCommands = new ArrayList<>();

    public SubCommandGroup(@NotNull String name,
                           @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public SubCommandGroup then(@NotNull SubCommand subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    protected List<CommandNodeData> buildNodeData(@NotNull CommandNodeHolder parent) {
        List<CommandNodeData> nodes = new ArrayList<>();
        for (SubCommand cmd : this.subCommands) {
            nodes.add(cmd.buildNodeData(parent));
        }
        return nodes;
    }

    @Override
    public String toString() {
        return "SubCommandGroup{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subCommands=" + subCommands +
                '}';
    }
}
