package dev.spoocy.jdaextensions.commands.manager;

import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandAnnotationProcessor {

    /**
     * Parses a class annotated with command annotations into CommandData
     *
     * @param clazz The class to parse
     * @return The parsed CommandData
     */
    CommandData parseCommand(@NotNull Class<?> clazz);

}
