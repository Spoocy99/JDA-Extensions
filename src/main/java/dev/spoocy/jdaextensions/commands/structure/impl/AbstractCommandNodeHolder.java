package dev.spoocy.jdaextensions.commands.structure.impl;

import dev.spoocy.jdaextensions.commands.structure.CommandNode;
import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractCommandNodeHolder implements CommandNodeHolder {

    protected final String name;
    protected final String description;
    protected final Map<String, CommandNodeData> subCommands = new ConcurrentHashMap<>();

    protected AbstractCommandNodeHolder(
            @NotNull String name,
            @NotNull String description
    ) {
        this.name = name;
        this.description = description;
    }

    public void addSubCommandData(@NotNull CommandNodeData subCommand) {
        subCommands.put(subCommand.name(), subCommand);
    }

    public void addSubCommandData(@NotNull Collection<CommandNodeData> subCommand) {
        for (CommandNodeData cmd : subCommand) {
            subCommands.put(cmd.name(), cmd);
        }
    }

    @NotNull
    public CommandNodeData getSubCommandData(@NotNull String name) {
        if (!this.subCommands.containsKey(name)) {
            throw new IllegalArgumentException("Sub-command with name '" + name + "' does not exist!");
        }

        return this.subCommands.get(name);
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @NotNull String description() {
        return this.description;
    }

    @Override
    public @NotNull CommandNode getNode(@NotNull String name) {
        return this.getSubCommandData(name);
    }
}
