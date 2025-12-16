package dev.spoocy.jdaextensions.commands.manager;

import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandManager {

    /**
     * Checks if slash commands are enabled in this manager.
     *
     * @return {@code true} if slash commands are enabled, {@code false} otherwise.
     */
    boolean useSlashCommands();

    /**
     * Checks if prefix commands are enabled in this manager.
     *
     * @return {@code true} if prefix commands are enabled, {@code false} otherwise.
     */
    boolean usePrefixCommands();

    /**
     * Gets the command prefix used for prefix commands.
     *
     * @return the command prefix, or null if prefix commands are disabled.
     *
     * @see #usePrefixCommands()
     */
    @Nullable
    String getPrefix();

    /**
     * Gets the {@link CommandListener} assigned to this manager.
     * This listener handles command events such as errors.
     *
     * @return the command listener instance.
     */
    @NotNull
    CommandListener getListener();

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

    /**
     * Handles a slash command interaction event.
     *
     * @param event
     *        the slash command interaction event to handle.
     */
    @ApiStatus.Internal
    void handleCommand(@NotNull SlashCommandInteractionEvent event);

    /**
     * Executes a command if the message received event
     * would trigger a prefix command.
     *
     * @param event
     *        the message received event to handle.
     */
    @ApiStatus.Internal
    void handlePrefixCommand(@NotNull MessageReceivedEvent event);
}
