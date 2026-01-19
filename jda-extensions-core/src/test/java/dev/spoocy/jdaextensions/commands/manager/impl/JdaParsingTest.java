package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Choice;
import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.jdaextensions.commands.tree.CommandTree;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JdaParsingTest {

    @Command(name = "parent", description = "Parent command")
    public static class SubcommandContainerFixture {

        @Command.Sub(name = "alone", description = "Alone sub")
        @Choice.Text(argument = "text", name = "option1", value = "value1")
        @Choice.Text(argument = "text", name = "option2", value = "value2")
        public static void alone(
                CommandContext ctx,
                @Arguments.Text(name = "text", description = "Text arg", minLength = 1, maxLength = 10) String text
        ) {
            // noop
        }

        @Command.Sub(name = "sub2", description = "Sub in group")
        @Command.Group(name = "group1", description = "Group 1")
        public static void inGroup(CommandContext ctx,
                                   @Arguments.Number(name = "n", description = "number", minValue = 0.0, maxValue = 1.0) double n) {
            // noop
        }

    }

    @Test
    void testJdaConversionWithAnnotations() {
        CommandData data = DefaultCommandAnnotationProcessor.INSTANCE.parseCommand(SubcommandContainerFixture.class);
        assertNotNull(data);

        var jdaData = data.buildJDA();

        // Verify main command properties
        assertNotNull(jdaData);
        assertEquals(data.name(), jdaData.getName());
        assertEquals(data.description(), jdaData.getDescription());
        assertEquals(data.context(), jdaData.getContexts());
        assertEquals(data.nsfw(), jdaData.isNSFW());
        assertEquals(data.defaultPermissions(), jdaData.getDefaultPermissions());
        assertEquals(data.getSubCommandGroups().size(), jdaData.getSubcommandGroups().size());
        assertEquals(data.getNodeCount(), jdaData.getSubcommands().size());

        // Verify "alone" subcommand
        SubcommandData aloneSub = jdaData.getSubcommands().stream()
                .filter(sub -> sub.getName().equals("alone"))
                .findFirst()
                .orElse(null);
        assertNotNull(aloneSub);
        assertEquals(1, aloneSub.getOptions().size());
        OptionData aloneOption = aloneSub.getOptions().get(0);
        assertEquals("text", aloneOption.getName());
        assertEquals(OptionType.STRING, aloneOption.getType());
        assertEquals(2, aloneOption.getChoices().size());
        assertEquals("option1", aloneOption.getChoices().get(0).getName());
        assertEquals("option2", aloneOption.getChoices().get(1).getName());

        // Verify "group1" subcommand group and its "sub2" subcommand
        SubcommandGroupData group1 = jdaData.getSubcommandGroups().stream()
                .filter(group -> group.getName().equals("group1"))
                .findFirst()
                .orElse(null);
        assertNotNull(group1);
        SubcommandData sub2 = group1.getSubcommands().stream()
                .filter(sub -> sub.getName().equals("sub2"))
                .findFirst()
                .orElse(null);
        assertNotNull(sub2);
        assertEquals(1, sub2.getOptions().size());
        OptionData sub2Option = sub2.getOptions().get(0);
        assertEquals("n", sub2Option.getName());
        assertEquals(OptionType.NUMBER, sub2Option.getType());

    }

    @Test
    void testJdaConversionWithCommandTree() {
        CommandData data = (CommandData) new CommandTree("asd", "asd")
                .then(CommandTree.command("alone", "Alone sub")
                        .arg(Arguments.number("n", "number", true, false)
                                .minValue(0.0)
                                .maxValue(1.0))
                        .executes(ctx -> {
                            // noop
                        })
                )
                .then(CommandTree.group("group1", "Group 1")
                        .then(CommandTree.command("sub2", "Sub in group")
                                .arg(Arguments.string("text", "Text arg", true, false)
                                        .minLength(1)
                                        .maxLength(10)
                                        .choice("option1", "value1"))
                                .executes(ctx -> {
                                    // noop
                                })
                        )
                )
                .build();

        assertNotNull(data);
        var jdaData = data.buildJDA();
        assertNotNull(jdaData);

        // Verify "alone" subcommand
        SubcommandData aloneSub = jdaData.getSubcommands().stream()
                .filter(sub -> sub.getName().equals("alone"))
                .findFirst()
                .orElse(null);
        assertNotNull(aloneSub);
        assertEquals(1, aloneSub.getOptions().size());
        OptionData aloneOption = aloneSub.getOptions().get(0);
        assertEquals("n", aloneOption.getName());
        assertEquals(OptionType.NUMBER, aloneOption.getType());

        // Verify "group1" subcommand group and its "sub2" subcommand
        SubcommandGroupData group1 = jdaData.getSubcommandGroups().stream()
                .filter(group -> group.getName().equals("group1"))
                .findFirst()
                .orElse(null);
        assertNotNull(group1);
        SubcommandData sub2 = group1.getSubcommands().stream()
                .filter(sub -> sub.getName().equals("sub2"))
                .findFirst()
                .orElse(null);
        assertNotNull(sub2);
        assertEquals(1, sub2.getOptions().size());
        OptionData sub2Option = sub2.getOptions().get(0);
        assertEquals("text", sub2Option.getName());
        assertEquals(OptionType.STRING, sub2Option.getType());
    }

}
