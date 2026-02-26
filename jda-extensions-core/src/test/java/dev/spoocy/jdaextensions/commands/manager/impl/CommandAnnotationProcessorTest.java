package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Choice;
import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.jdaextensions.commands.annotations.Cooldown;
import dev.spoocy.jdaextensions.commands.annotations.Permissions;
import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.arguments.impl.StringArgument;
import dev.spoocy.jdaextensions.commands.cooldown.CooldownScope;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandGroupData;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandAnnotationProcessorTest {

    @Command(name = "testcmd", description = "A test command", nsfw = false,
            context = {InteractionContextType.GUILD})
    public static class SimpleCommandFixture {

        @Command.Default
        @Cooldown(value = 2, unit = TimeUnit.SECONDS, scope = CooldownScope.USER)
        @Permissions({Permission.MESSAGE_SEND})
        public static void execute(
                CommandContext ctx,
                @Arguments.Bool(name = "flag", description = "a flag") boolean flag,
                @Arguments.Integer(name = "count", description = "Count arg", minValue = 0, maxValue = 5) int count
        ) {
            // noop
        }

    }

    @Command(name = "parent", description = "Parent command")
    public static class SubcommandContainerFixture {

        @Command.Sub(name = "alone", description = "Alone sub", ephemeral = true)
        public static void alone(
                CommandContext ctx,
                @Arguments.Number(name = "n", description = "number", minValue = 0.0, maxValue = 1.0) double n
        ) {
            // noop
        }

        @Command.Sub(name = "sub2", description = "Sub in group")
        @Command.Group(name = "group1", description = "Group 1")
        @Choice.Text(argument = "text", name = "option1", value = "value1")
        @Choice.Text(argument = "text", name = "option2", value = "value2")
        public static void inGroup(
                CommandContext ctx,
                @Arguments.Text(name = "text", description = "Text arg", minLength = 1, maxLength = 10) String text
        ) {
            // noop
        }

    }

    @Test
    void defaultMethodsExist() {
        MethodAccessor defaultMethod = CommandResolver.getRootCommandMethod(SimpleCommandFixture.class, true);
        assertNotNull(defaultMethod);

        MethodAccessor defaultMethod2 = CommandResolver.getRootCommandMethod(SubcommandContainerFixture.class, true);
        assertNull(defaultMethod2);
    }

    @Test
    void subcommandMethodsExist() {
        Set<MethodAccessor> subcommandMethods = CommandResolver.getSubCommandMethods(SimpleCommandFixture.class, true);
        assertNotNull(subcommandMethods);
        assertEquals(0, subcommandMethods.size());

        Set<MethodAccessor> subcommandMethods2 = CommandResolver.getSubCommandMethods(SubcommandContainerFixture.class, true);
        assertNotNull(subcommandMethods2);
        assertEquals(2, subcommandMethods2.size());
    }

    @Test
    void testParseDefaultCommand() {

        CommandData data = DefaultCommandAnnotationProcessor.DEFAULT.parseCommand(SimpleCommandFixture.class);

        assertNotNull(data);
        assertEquals("testcmd", data.name());
        assertEquals("A test command", data.description());
        assertTrue(data.context()
                .contains(InteractionContextType.GUILD));

        CommandNodeData root = data.rootNode();
        assertNotNull(root);
        assertEquals("testcmd", root.name());
        assertEquals("A test command", root.description());

        // arguments
        List<AbstractArgument> args = root.getArgumentData();
        assertEquals(2, args.size());
        assertEquals("flag", args.get(0)
                .name());
        assertEquals("count", args.get(1)
                .name());

        // cooldown
        assertNotNull(root.cooldown());
        assertEquals(TimeUnit.SECONDS.toMillis(2), root.cooldown()
                .duration()
                .toMillis());

        // permissions should contain at least one
        assertTrue(root.permissions().length >= 1);
    }

    @Test
    void testParseSubcommandsAndGroups() {
        CommandData data = DefaultCommandAnnotationProcessor.DEFAULT.parseCommand(SubcommandContainerFixture.class);

        assertNotNull(data);
        assertEquals("parent", data.name());

        // direct subcommand
        CommandNodeData alone = data.getSubCommandData("alone");
        assertNotNull(alone);
        assertEquals("alone", alone.name());
        assertTrue(alone.ephemeral());
        assertEquals(1, alone.getArgumentData()
                .size());
        assertEquals("n", alone.getArgumentData()
                .get(0)
                .name());

        // grouped subcommand
        CommandGroupData group = data.getSubCommandGroupData("group1");
        assertNotNull(group);
        CommandNodeData sub2 = (CommandNodeData) group.getNode("sub2");
        assertNotNull(sub2);
        assertEquals("sub2", sub2.name());
        assertFalse(sub2.ephemeral());
        assertEquals(1, sub2.getArgumentData()
                .size());
        assertEquals("text", sub2.getArgumentData()
                .get(0)
                .name());
        assertEquals(2, ((StringArgument) sub2.getArgumentData()
                .get(0))
                .choices()
                .size());
    }

}
