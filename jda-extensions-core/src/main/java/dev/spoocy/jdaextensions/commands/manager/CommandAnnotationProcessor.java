package dev.spoocy.jdaextensions.commands.manager;

import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandAnnotationProcessor {

    /**
     * Parses a class annotated with command annotations into {@link CommandData}.
     *
     * @param clazz The class to parse
     * @return The parsed CommandData.
     */
    CommandData parseCommand(@NotNull Class<?> clazz);

    /**
     * Parses an instance of a class annotated with command annotations into {@link CommandData}.
     *
     * @param clazz The instance to parse
     * @return The parsed CommandData.
     */
    CommandData parseCommand(@NotNull Object clazz);

}
