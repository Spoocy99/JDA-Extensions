package dev.spoocy.jdaextensions.commands.structure.impl;

import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandGroupData extends AbstractCommandNodeHolder {

    private final DiscordCommand parent;

    public CommandGroupData(@Nullable DiscordCommand parent,
                               @NotNull String name,
                               @NotNull String description) {
        super(name, description);
        this.parent = parent;
    }


    @Override
    public DiscordCommand parent() {
        return this.parent;
    }

    public SubcommandGroupData buildJDA() {
        SubcommandGroupData groupData = new SubcommandGroupData(this.name, this.description);

        for (CommandNodeData node : this.subCommands.values()) {
            groupData.addSubcommands(node.buildJDA());
        }

        return groupData;
    }

}
