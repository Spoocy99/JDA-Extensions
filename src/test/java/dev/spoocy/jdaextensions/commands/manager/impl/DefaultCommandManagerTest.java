package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.commands.tree.CommandTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultCommandManagerTest {

    @Test
    public void registerAndGetCommand() {
        DefaultCommandManager mgr = new DefaultCommandManager();

        DiscordCommand cmd = new CommandTree("foo", "desc").build();
        mgr.register(cmd);

        assertNotNull(mgr.getCommand("foo"));
        assertTrue(mgr.getCommands().stream().anyMatch(c -> c == cmd));
    }

    @Test
    public void removeCommandRemoves() {
        DefaultCommandManager mgr = new DefaultCommandManager();
        DiscordCommand cmd = new CommandTree("foo", "desc").build();
        mgr.register(cmd);

        mgr.removeCommand("foo");
        assertNull(mgr.getCommand("foo"));
    }

    @Test
    public void setAndGetListener() {
        DefaultCommandManager mgr = new DefaultCommandManager();
        CommandListenerImpl l = new CommandListenerImpl();
        mgr.setListener(l);
        assertSame(l, mgr.getListener());
    }

    static class CommandListenerImpl implements dev.spoocy.jdaextensions.commands.manager.CommandListener {
    }
}

