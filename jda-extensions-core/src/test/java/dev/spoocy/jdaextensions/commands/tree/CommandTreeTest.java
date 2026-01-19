package dev.spoocy.jdaextensions.commands.tree;

import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandTreeTest {

    @Test
    void buildCreatesCommandData() {
        CommandTree tree = new CommandTree("name", "desc")
                .withContext(InteractionContextType.GUILD)
                .withNsfw(true);

        CommandData data = (CommandData) tree.build();

        assertEquals("name", data.name());
        assertEquals("desc", data.description());
        assertTrue(data.nsfw());
        assertTrue(data.context().contains(InteractionContextType.GUILD));
    }

    @Test
    void testJdaConversion() {
        CommandTree tree = new CommandTree("test", "A test command")
                .withContext(InteractionContextType.GUILD, InteractionContextType.BOT_DM)
                .executes(e -> {
                    // Command execution logic
                })
                .then(CommandTree.command("sub", "A subcommand")
                        .executes(e -> {
                            // Subcommand execution logic
                        })
                )
                .then(CommandTree.group("group", "A command group")
                        .then(CommandTree.command("nested", "A nested command")
                                .executes(e -> {
                                    // Nested command execution logic
                                })
                        )
                )
                .withNsfw(false);

        CommandData data = (CommandData) tree.build();
        SlashCommandData jdaData = data.buildJDA();

        assertNotNull(jdaData);
        assertEquals("test", jdaData.getName());
        assertEquals("A test command", jdaData.getDescription());
        assertEquals(1, jdaData.getSubcommands().size());
        assertEquals(1, jdaData.getSubcommandGroups().size());
        assertFalse(data.nsfw());
        assertTrue(data.context().contains(InteractionContextType.GUILD));
        assertTrue(data.context().contains(InteractionContextType.BOT_DM));
        assertEquals("test", jdaData.getName());
    }

    @Test
    void toStringContainsKeyProperties() {
        CommandTree tree = new CommandTree("n", "d");
        String s = tree.toString();
        assertTrue(s.contains("n"));
        assertTrue(s.contains("d"));
    }
}

