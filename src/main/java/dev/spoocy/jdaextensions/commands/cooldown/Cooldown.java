package dev.spoocy.jdaextensions.commands.cooldown;

import dev.spoocy.jdaextensions.commands.event.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Cooldown {

    /**
     * Gets the {@link CooldownScope} of the cooldown.
     *
     * @return the scope of the cooldown.
     */
    CooldownScope scope();

    /**
     * Gets the d{@link Duration} of the cooldown.
     *
     * @return the duration of the cooldown.
     */
    Duration duration();

    /**
     * Clears all cooldowns.
     */
    void clearAll();

    /**
     * Checks whether the command should be executed based on the cooldown.
     *
     * @param context
     *        the command event context
     *
     * @return {@code true} if the command should be executed, {@code false} otherwise.
     */
    boolean shouldExecute(@NotNull CommandContext context);

    /**
     * A cooldown instance that represents no cooldown.
     */
    Cooldown NONE = new Cooldown() {

        @Override
        public CooldownScope scope() {
            return CooldownScope.NONE;
        }

        @Override
        public Duration duration() {
            return Duration.ZERO;
        }

        @Override
        public void clearAll() {
            // No-op
        }

        @Override
        public boolean shouldExecute(@NotNull CommandContext context) {
            return true;
        }

        @Override
        public String toString() {
            return "Cooldown.NONE";
        }

    };

}
