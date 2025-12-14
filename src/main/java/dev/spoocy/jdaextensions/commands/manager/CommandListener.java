package dev.spoocy.jdaextensions.commands.manager;

import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.event.CommandPreProcessContext;
import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandListener {

    default void onPreProcess(@NotNull CommandPreProcessContext event) { }

    default void onUnknownCommand(@NotNull CommandContext event) {
        event.reply(new EmbedBuilder().setDescription("Unknown Command.").setColor(Color.RED)).queue();
    }

    default void onNoPermissions(@NotNull CommandContext event) {
        event.reply(new EmbedBuilder().setDescription("You don't have permission to use this command.").setColor(Color.RED)).queue();
    }

    default void onCooldown(@NotNull CommandContext event) {
        event.reply(new EmbedBuilder().setDescription("Please wait a bit before executing this command again.").setColor(Color.RED)).queue();
    }

    default void onException(@NotNull CommandContext event, Throwable error) {
        event.reply(new EmbedBuilder().setDescription("An error occurred while executing the command. Please try again later.").setColor(Color.RED)).queue();
        ILogger.forThisClass().error("An error occurred while executing the command: " + event.getCommand(), error);
    }

}
