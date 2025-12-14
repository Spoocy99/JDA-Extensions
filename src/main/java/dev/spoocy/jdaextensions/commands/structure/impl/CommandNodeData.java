package dev.spoocy.jdaextensions.commands.structure.impl;

import com.google.common.collect.ImmutableList;
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.cooldown.Cooldown;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.arguments.Argument;
import dev.spoocy.jdaextensions.commands.structure.CommandNode;
import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.utils.common.scheduler.task.Task;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static dev.spoocy.utils.common.scheduler.Scheduler.runAsync;
import static dev.spoocy.utils.common.scheduler.Scheduler.runSync;
import static net.dv8tion.jda.api.interactions.commands.build.CommandData.MAX_NAME_LENGTH;
import static net.dv8tion.jda.api.interactions.commands.build.CommandData.MAX_DESCRIPTION_LENGTH;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandNodeData implements CommandNode {

    private final CommandNodeHolder holder;
    private final String name;
    private final String description;
    private final CommandPermission[] permissions;
    private final boolean async;
    private final boolean sendTyping;
    private final boolean acknowledge;
    private final boolean ephemeral;
    private final List<AbstractArgument> arguments;
    private final Cooldown cooldown;
    private final Consumer<CommandContext> executor;

    public CommandNodeData(
            @NotNull CommandNodeHolder holder,
            @NotNull String name,
            @NotNull String description,
            @NotNull CommandPermission[] permissions,
            boolean async,
            boolean sendTyping,
            boolean acknowledge,
            boolean ephemeral,
            @NotNull List<AbstractArgument> arguments,
            @NotNull Cooldown cooldown,
            @NotNull Consumer<CommandContext> executor
    ) {

        if(name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Command name cannot be longer than " + MAX_NAME_LENGTH + " characters!");
        }

        if(description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Command description cannot be longer than " + MAX_DESCRIPTION_LENGTH + " characters!");
        }

        this.holder = holder;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.async = async;
        this.sendTyping = sendTyping;
        this.acknowledge = acknowledge;
        this.ephemeral = ephemeral;
        this.arguments = ImmutableList.copyOf(arguments);
        this.cooldown = cooldown;
        this.executor = executor;

    }

    @Override
    public @NotNull CommandNodeHolder parent() {
        return this.holder;
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
    public CommandPermission[] permissions() {
        return this.permissions;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    @Override
    public boolean sendTyping() {
        return this.sendTyping;
    }

    @Override
    public boolean acknowledge() {
        return this.acknowledge;
    }

    @Override
    public boolean ephemeral() {
        return this.ephemeral;
    }

    @Override
    public @NotNull List<Argument> arguments() {
        return ImmutableList.copyOf(this.arguments);
    }

    @Override
    public @NotNull Cooldown cooldown() {
        return this.cooldown;
    }

    @Override
    public Task<Void> execute(@NotNull CommandContext context) {
        return runSync(() -> this.executor.accept(context));
    }

    @Override
    public Task<Void> executeAsync(@NotNull CommandContext context) {
        return runAsync(() -> this.executor.accept(context));
    }

    public @NotNull List<AbstractArgument> getArgumentData() {
        return this.arguments;
    }

    public SubcommandData buildJDA() {
        SubcommandData subcommandData = new SubcommandData(this.name(), this.description());

        for (AbstractArgument argument : this.arguments) {
            subcommandData.addOptions(argument.buildJDA());
        }

        return subcommandData;
    }

    @Override
    public String toString() {
        return "CommandNodeData{" +
                "holder=" + holder +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + (permissions == null ? "null" : permissions.length + " permissions") +
                ", async=" + async +
                ", sendTyping=" + sendTyping +
                ", ephemeral=" + ephemeral +
                ", arguments=" + arguments +
                ", cooldown=" + cooldown +
                ", executor=" + executor +
                '}';
    }
}
