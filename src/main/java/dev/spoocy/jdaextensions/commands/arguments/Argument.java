package dev.spoocy.jdaextensions.commands.arguments;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Argument {

    @NotNull
    OptionType type();

    @NotNull
    String name();

    @NotNull
    String description();

    boolean required();

    boolean autoComplete();
}
