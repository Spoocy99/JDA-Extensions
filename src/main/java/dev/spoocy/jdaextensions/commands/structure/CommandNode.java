package dev.spoocy.jdaextensions.commands.structure;

import dev.spoocy.jdaextensions.commands.arguments.Argument;
import dev.spoocy.jdaextensions.commands.cooldown.Cooldown;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.utils.common.scheduler.task.Task;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@ApiStatus.NonExtendable
public interface CommandNode {

    @NotNull
    CommandNodeHolder parent();

    @NotNull
    String name();

    @NotNull
    String description();

    @NotNull
    CommandPermission[] permissions();

    boolean async();

    boolean sendTyping();

    boolean acknowledge();

    boolean ephemeral();

    @NotNull
    List<Argument> arguments();

    default boolean hasCooldown() {
        return cooldown() != Cooldown.NONE;
    }

    @NotNull
    Cooldown cooldown();

    Task<Void> execute(@NotNull CommandContext context);

    Task<Void> executeAsync(@NotNull CommandContext context);

}
