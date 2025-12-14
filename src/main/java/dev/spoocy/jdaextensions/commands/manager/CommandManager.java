package dev.spoocy.jdaextensions.commands.manager;

import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandManager {

    /**
     * Gets the current {@link CommandListener} assigned to this manager.
     *
     * @return the command listener instance.
     */
    @NotNull
    CommandListener getListener();

    /**
     * Sets the {@link CommandListener} for this manager.
     * If no listener is set, a default empty listener will be used.
     *
     * @param listener
     *        the command listener to set.
     *
     * @return the current command manager instance.
     */
    CommandManager setListener(@NotNull CommandListener listener);

    /**
     * Gets all registered commands.
     *
     * @return a collection of all registered commands.
     */
    @NotNull
    Collection<DiscordCommand> getCommands();

    /**
     * Gets a registered command by its name.
     *
     * @param name
     *        the name of the command.
     *
     * @return the command instance, or null if not found.
     */
    @Nullable
    DiscordCommand getCommand(@NotNull String name);

    /**
     * Adds multiple commands to the manager.
     *
     * @param command
     *        the commands to add.
     */
    void register(@NotNull DiscordCommand... command);

    /**
     * Adds multiple commands to the manager.
     *
     * @param command
     *        the commands to add.
     */
    void register(@NotNull Collection<DiscordCommand> command);

    /**
     * Scans and registers commands from the given annotated class.
     * <br>
     * This class should be annotated with {@link dev.spoocy.jdaextensions.commands.annotations.Command}
     * and have static methods annotated with {@link dev.spoocy.jdaextensions.commands.annotations.Command.Default}
     * and {@link dev.spoocy.jdaextensions.commands.annotations.Command.Sub}.
     *
     * @param annotatedClass
     *        the class to scan for commands.
     *
     * @throws IllegalArgumentException
     *         if the class is not properly annotated or contains invalid command methods.
     */
    void registerClasses(@NotNull Class<?>... annotatedClass);

    /**
     * Scans and registers commands from the given collection of annotated classes.
     * <br>
     * Each class should be annotated with {@link dev.spoocy.jdaextensions.commands.annotations.Command}
     * and have static methods annotated with {@link dev.spoocy.jdaextensions.commands.annotations.Command.Default}
     * and {@link dev.spoocy.jdaextensions.commands.annotations.Command.Sub}.
     *
     * @param annotatedClasses
     *        the collection of classes to scan for commands.
     *
     * @throws IllegalArgumentException
     *         if any class is not properly annotated or contains invalid command methods.
     */
    void registerClasses(@NotNull Collection<Class<?>> annotatedClasses);

    /**
     * Removes a command from the manager by its name.
     *
     * @param name
     *        the name of the command to remove.
     *
     * @return the current command manager instance for chaining.
     */
    @NotNull
    CommandManager removeCommand(@NotNull String name);

    /**
     * Updates the commands in the given JDA instance.
     * This will commit all registered commands to Discord.
     *
     * @param jda
     *        the JDA instance to update commands for.
     *
     * @see JDA#updateCommands()
     */
    void updateCommands(@NotNull JDA jda);
}
