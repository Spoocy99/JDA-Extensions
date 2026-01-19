package dev.spoocy.jdaextensions.commands.tree;

import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandChainable<T> {

    T then(@NotNull SubCommand subCommand);

}
