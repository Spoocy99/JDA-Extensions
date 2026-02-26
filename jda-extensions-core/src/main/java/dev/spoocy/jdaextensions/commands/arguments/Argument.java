package dev.spoocy.jdaextensions.commands.arguments;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Argument {

    /**
     * Gets the type of this argument.
     *
     * @return The type of this argument.
     */
    @NotNull
    OptionType type();

    /**
     * Gets the name of this argument.
     *
     * @return The name of this argument.
     */
    @NotNull
    String name();

    /**
     * Gets the description of this argument.
     *
     * @return The description of this argument.
     */
    @NotNull
    String description();

    /**
     * Checks if this argument is required.
     *
     * @return {@code true} if this argument is required, {@code false} otherwise.
     */
    boolean required();

    /**
     * Checks if this argument has auto-complete enabled.
     *
     * @return {@code true} if this argument has auto-complete enabled, {@code false} otherwise.
     */
    boolean autoComplete();

}
