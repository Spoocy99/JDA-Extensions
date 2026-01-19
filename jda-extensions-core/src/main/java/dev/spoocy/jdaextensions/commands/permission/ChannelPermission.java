package dev.spoocy.jdaextensions.commands.permission;

import dev.spoocy.jdaextensions.commands.event.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ChannelPermission implements CommandPermission {

    public static List<CommandPermission> mapPermission(@NotNull Permission[] permission) {
        return Arrays.stream(permission)
                .map(ChannelPermission::new)
                .collect(Collectors.toList());
    }

    private final Permission permission;

    public ChannelPermission(@NotNull Permission permission) {
        this.permission = permission;
    }

    @Override
    public boolean isCovered(@NotNull CommandContext event) {
        Member member = event.getMember();
        GuildChannel channel = event.getTextChannel();

        return member.hasPermission(channel, permission);
    }

    @Override
    public String toString() {
        return "ChannelPermission{" +
                "permission=" + permission +
                '}';
    }
}
