package dev.spoocy.jdaextensions.commands.structure;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@ApiStatus.NonExtendable
public interface CommandNodeHolder {

    DiscordCommand parent();

    /**
     * Get the name of this command holder.
     *
     * @return the name
     */
    @NotNull
    String name();

    /**
     * Get the description of this command holder.
     *
     * @return the description
     */
    @NotNull
    String description();

    /**
     * Get the names of all sub-commands in
     * this command holder.
     *
     * @return an array of sub-command names
     */
    String[] getNodeNames();

    /**
     * Checks if this command holder has a sub-command with the provided name.
     *
     * @param name
     *          the name of the node
     *
     * @return {@code true} if the node exists, {@code false} otherwise
     */
    boolean hasNode(@NotNull String name);

    /**
     * Get a sub-command by name.
     *
     * @param name
     *        the name of the node
     *
     * @return the node with the provided name
     *
     * @throws IllegalArgumentException
     *         if no such node exists
     */
    @NotNull
    CommandNode getNode(@NotNull String name);

}
