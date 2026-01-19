package dev.spoocy.jdaextensions.commands.cooldown;

import dev.spoocy.jdaextensions.commands.event.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class GlobalCooldown implements Cooldown {

    private long cooldown;
    private final Duration duration;

    public GlobalCooldown(@NotNull Duration duration) {
        this.duration = duration;
    }

    @Override
    public CooldownScope scope() {
        return CooldownScope.GLOBAL;
    }

    @Override
    public Duration duration() {
        return this.duration;
    }

    @Override
    public void clearAll() {
        this.cooldown = 0;
    }

    @Override
    public boolean shouldExecute(@NotNull CommandContext context) {
        long currentTime = System.currentTimeMillis();
        if(currentTime >= cooldown) {
            this.cooldown = currentTime + duration.toMillis();
            return true;
        }
        return false;
    }
}
