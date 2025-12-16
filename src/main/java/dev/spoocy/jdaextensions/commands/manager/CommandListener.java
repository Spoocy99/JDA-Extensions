package dev.spoocy.jdaextensions.commands.manager;

import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.event.CommandPreProcessContext;
import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandListener {

    /**
     * Called before processing a command.
     *
     * @param event the command pre-process context
     */
    default void onPreProcess(@NotNull CommandPreProcessContext event) { }

    /**
     * Called when an unknown command is received.
     * <br> This is only called for message-based commands.
     *
     * @param event the message received event
     */
    default void onUnknownCommand(@NotNull MessageReceivedEvent event) {
        event.getMessage().replyEmbeds(new EmbedBuilder().setDescription("Unknown Command.").setColor(Color.RED).build()).queue();
    }

    /**
     * Called when a user lacks the necessary permissions to execute a command.
     * This is based on required {@link dev.spoocy.jdaextensions.commands.permission.CommandPermission}s.
     *
     *
     * @param event the command context
     */
    default void onNoPermissions(@NotNull CommandContext event) {
        event.reply(new EmbedBuilder().setDescription("You don't have permission to use this command.").setColor(Color.RED)).queue();
    }

    /**
     * Called when a command is on cooldown for a user.
     *
     * @param event the command context
     */
    default void onCooldown(@NotNull CommandContext event) {
        event.reply(new EmbedBuilder().setDescription("Please wait a bit before executing this command again.").setColor(Color.RED)).queue();
    }

    /**
     * Called when an exception occurs during command execution.
     *
     * @param event the command context
     * @param error the thrown exception
     */
    default void onException(@NotNull CommandContext event, @NotNull Throwable error) {
        event.reply(new EmbedBuilder().setDescription("An error occurred while executing the command. Please try again later.").setColor(Color.RED)).queue();
        ILogger.forThisClass().error("An error occurred while executing the command: " + event.getCommand(), error);
    }

}
