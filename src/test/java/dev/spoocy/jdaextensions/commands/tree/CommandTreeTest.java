package dev.spoocy.jdaextensions.commands.tree;

import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTreeTest {

    @Test
    public void buildCreatesCommandData() {
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
    public void toStringContainsKeyProperties() {
        CommandTree tree = new CommandTree("n", "d");
        String s = tree.toString();
        assertTrue(s.contains("n"));
        assertTrue(s.contains("d"));
    }
}

