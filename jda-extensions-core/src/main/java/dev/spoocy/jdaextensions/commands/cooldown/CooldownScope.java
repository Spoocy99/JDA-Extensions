package dev.spoocy.jdaextensions.commands.cooldown;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public enum CooldownScope {
    /**
     * No cooldown will be applied.
     */
    NONE,

    /**
     * Cooldown will be applied per user.
     */
    USER,

    /**
     * Cooldown will be applied per guild.
     */
    GUILD,

    /**
     * Cooldown will be applied globally.
     */
    GLOBAL;

    public Cooldown cooldown(@NotNull Duration duration) {
        return cooldown(this, duration);
    }

     public static Cooldown cooldown(@NotNull CooldownScope scope, @NotNull Duration duration) {
        switch (scope) {
            case USER:
                return new UserCooldown(duration);
            case GUILD:
                return new GuildCooldown(duration);
            case GLOBAL:
                return new GlobalCooldown(duration);
            default:
                return Cooldown.NONE;
        }
    }

}
