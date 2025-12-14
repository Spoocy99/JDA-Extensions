package dev.spoocy.jdaextensions.commands.structure;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@ApiStatus.NonExtendable
public interface CommandNodeHolder {

    DiscordCommand parent();

    @NotNull
    String name();

    @NotNull
    String description();

    @NotNull
    CommandNode getNode(@NotNull String name);

}
