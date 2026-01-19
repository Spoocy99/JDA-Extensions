package dev.spoocy.jdaextensions.commands.cooldown;

import dev.spoocy.jdaextensions.commands.event.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class GuildCooldown implements Cooldown {

    private final Map<Long, Long> cooldowns = new ConcurrentHashMap<>();
    private final Duration duration;

    public GuildCooldown(@NotNull Duration duration) {
        this.duration = duration;
    }

    @Override
    public CooldownScope scope() {
        return CooldownScope.GUILD;
    }

    @Override
    public Duration duration() {
        return this.duration;
    }

    @Override
    public void clearAll() {
        this.cooldowns.clear();
    }

    @Override
    public boolean shouldExecute(@NotNull CommandContext context) {
        if(!context.isGuild()) {
            return true;
        }

        long guildId = context.getGuild().getIdLong();
        long currentTime = System.currentTimeMillis();

        if (!cooldowns.containsKey(guildId)) {
            cooldowns.put(guildId, currentTime + duration.toMillis());
            return true;
        }

        long expiryTime = cooldowns.get(guildId);
        if (currentTime >= expiryTime) {
            cooldowns.put(guildId, currentTime + duration.toMillis());
            return true;
        }

        return false;
    }
}
