package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.arguments.impl.StringArgument;
import dev.spoocy.jdaextensions.commands.event.CommandPreProcessContext;
import dev.spoocy.jdaextensions.commands.manager.CommandListener;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.commands.tree.CommandTree;
import dev.spoocy.jdaextensions.core.DiscordBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DefaultCommandManagerTest {

    @Test
    void registerAndGetCommand() {
        DefaultCommandManager mgr = DefaultCommandManager.builder().build();

        DiscordCommand cmd = new CommandTree("foo", "desc").build();
        mgr.register(cmd);

        assertNotNull(mgr.getCommand("foo"));
        assertTrue(mgr.getCommands().stream().anyMatch(c -> c == cmd));
    }

    @Test
    void removeCommandRemoves() {
        DefaultCommandManager mgr = DefaultCommandManager.builder().build();
        DiscordCommand cmd = new CommandTree("foo", "desc").build();
        mgr.register(cmd);

        mgr.removeCommand("foo");
        assertNull(mgr.getCommand("foo"));
    }

    @Test
    void setAndGetListener() {
        CommandListenerImpl l = new CommandListenerImpl();

        DefaultCommandManager mgr = DefaultCommandManager.builder()
                .listener(l)
                .build();

        assertSame(l, mgr.getListener());
    }

    @Test
    void preProcessCancellationCancelsExecution() {
        AtomicBoolean preProcessCalled = new AtomicBoolean(false);

        CommandListener listener = new CommandListener() {
            @Override
            public void onPreProcess(CommandPreProcessContext event) {
                preProcessCalled.set(true);
                event.setCancelled(true);
            }
        };

        DefaultCommandManager mgr = DefaultCommandManager.builder()
                .listener(listener)
                .build();

        DiscordCommand cmd = new CommandTree("test", "desc")
                .executes(ctx -> {})
                .build();

        mgr.register(cmd);
        mgr.shutdown();
    }

    @Test
    void handlePrefixCommandExecutesMatchingCommand() {
        AtomicBoolean executed = new AtomicBoolean(false);
        AtomicReference<String> stringArg = new AtomicReference<>();

        DefaultCommandManager mgr = DefaultCommandManager.builder()
                .messagePrefix("!")
                .build();

        DiscordCommand cmd = new CommandTree("echo", "echoes text")
                .arg(new StringArgument("text", "text to echo", true, false))
                .executes(ctx -> {
                    executed.set(true);
                    stringArg.set(ctx.getArgument("text").getAsString());
                })
                .build();

        mgr.register(cmd);

        User author = Mockito.mock(User.class);
        Mockito.when(author.isBot()).thenReturn(false);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getContentRaw()).thenReturn("!echo hello world");
        Mockito.when(message.getAttachments()).thenReturn(Collections.emptyList());

        MessageReceivedEvent event = Mockito.mock(MessageReceivedEvent.class);
        Mockito.when(event.getAuthor()).thenReturn(author);
        Mockito.when(event.isWebhookMessage()).thenReturn(false);
        Mockito.when(event.getMessage()).thenReturn(message);

        DiscordBot bot = Mockito.mock(DiscordBot.class);

        mgr.handlePrefixCommand(event, bot);

        assertTrue(executed.get());
        assertEquals("hello world", stringArg.get());

        mgr.shutdown();
    }

    static class CommandListenerImpl implements CommandListener {
    }
}
