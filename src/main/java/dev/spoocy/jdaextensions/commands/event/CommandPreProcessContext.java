package dev.spoocy.jdaextensions.commands.event;
import dev.spoocy.jdaextensions.commands.structure.CommandNode;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandPreProcessContext implements Cancellable {

    private final DiscordCommand command;
    private final CommandNode node;
    private final CommandContext event;

    private boolean cancelled;

    public CommandPreProcessContext(@NotNull DiscordCommand command,
                                    @NotNull CommandNode node,
                                    @NotNull CommandContext event) {
        this.command = command;
        this.node = node;
        this.event = event;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        this.cancelled = canceled;
    }

    @NotNull
    public DiscordCommand getCommand() {
        return this.command;
    }

    @NotNull
    public CommandNode getNode() {
        return this.node;
    }

    @NotNull
    public CommandContext getContext() {
        return event;
    }
}
