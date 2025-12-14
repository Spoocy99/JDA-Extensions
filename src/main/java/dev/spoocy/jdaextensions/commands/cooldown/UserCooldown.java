package dev.spoocy.jdaextensions.commands.cooldown;

import dev.spoocy.jdaextensions.commands.event.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class UserCooldown implements Cooldown {

    private final Map<Long, Long> cooldowns = new ConcurrentHashMap<>();
    private final Duration duration;

    public UserCooldown(@NotNull Duration duration) {
        this.duration = duration;
    }

    @Override
    public CooldownScope scope() {
        return CooldownScope.USER;
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
        long userId = context.getUser().getIdLong();
        long currentTime = System.currentTimeMillis();

        if (!cooldowns.containsKey(userId)) {
            cooldowns.put(userId, currentTime + duration.toMillis());
            return true;
        }

        long expiryTime = cooldowns.get(userId);
        if (currentTime >= expiryTime) {
            cooldowns.put(userId, currentTime + duration.toMillis());
            return true;
        }

        return false;
    }
}
