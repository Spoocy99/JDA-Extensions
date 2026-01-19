package dev.spoocy.jdaextensions.commands.permission;

import dev.spoocy.jdaextensions.core.DiscordBot;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandPermission {

    CommandPermission ADMIN = event -> event.hasPermission(Permission.ADMINISTRATOR);
    CommandPermission OWNER = event -> DiscordBot.getInstance().isOwner(event.getUser().getIdLong());

    static CommandPermission channel(@NotNull Permission permission) {
        return new ChannelPermission(permission);
    }

    static List<CommandPermission> channel(@NotNull Permission... permission) {
        return ChannelPermission.mapPermission(permission);
    }

    static CommandPermission guild(@NotNull Permission permission) {
        return new GlobalPermission(permission);
    }

    static List<CommandPermission> guild(@NotNull Permission... permission) {
        return GlobalPermission.mapPermission(permission);
    }

    boolean isCovered(@NotNull CommandContext event);

}
