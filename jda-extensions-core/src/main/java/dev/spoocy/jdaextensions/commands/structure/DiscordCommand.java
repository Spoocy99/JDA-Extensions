package dev.spoocy.jdaextensions.commands.structure;

import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@ApiStatus.NonExtendable
public interface DiscordCommand extends CommandNodeHolder {

    @Override
    default DiscordCommand parent() {
        return this;
    }

    /**
     * Get the name of this command.
     *
     * @return the name
     */
    @Override
    @NotNull String name();

    /**
     * Get the description of this command.
     *
     * @return the description
     */
    @Override
    @NotNull String description();

    /**
     * Get the context type of this command.
     *
     * @return the context type
     */
    @NotNull
    Set<InteractionContextType> context();

    /**
     * Get the default member permissions required to use this command.
     *
     * @return the default permissions
     *
     * @see DefaultMemberPermissions
     */
    @NotNull
    DefaultMemberPermissions defaultPermissions();

    /**
     * Check if this command is marked as NSFW.
     *
     * @return {@code true} if NSFW, {@code false} otherwise
     */
    boolean nsfw();

    /**
     * Get the root command node.
     *
     * @return the root node
     *
     * @throws IllegalStateException
     *         if this command has no root node
     */
    @NotNull
    CommandNode rootNode();

    /**
     * Get the names of all sub-command groups.
     *
     * @return an array of sub-command group names
     */
    String[] getGroupNames();

    /**
     * Check if this command has a sub-command group with the provided name.
     *
     * @param name
     *          the name of the group
     *
     * @return {@code true} if the group exists, {@code false} otherwise
     */
    boolean hasGroup(String name);

    /**
     * Get a sub-command group by name.
     *
     * @param name
     *        the name of the group
     *
     * @return the group with the provided name.
     *
     * @throws IllegalArgumentException
     *         if no such group exists.
     */
    @NotNull
    CommandNodeHolder getGroup(@NotNull String name);

}
