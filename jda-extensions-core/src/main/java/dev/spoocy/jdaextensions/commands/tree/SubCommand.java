package dev.spoocy.jdaextensions.commands.tree;

import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SubCommand extends AbstractCommandTree<SubCommand> {

    public SubCommand(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    @Override
    public SubCommand instance() {
        return this;
    }
}
