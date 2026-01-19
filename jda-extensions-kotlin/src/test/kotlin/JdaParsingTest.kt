import dev.spoocy.jdaextensions.commands.annotations.Choice
import dev.spoocy.jdaextensions.commands.annotations.Command
import dev.spoocy.jdaextensions.commands.arguments.Arguments
import dev.spoocy.jdaextensions.commands.event.CommandContext
import dev.spoocy.jdaextensions.commands.manager.impl.KotlinAnnotationProcessor
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Command(name = "parent", description = "Parent command")
object SubcommandContainerFixture {

    @Command.Sub(name = "alone", description = "Alone sub")
    @Choice.Text(argument = "text", name = "option1", value = "value1")
    @Choice.Text(argument = "text", name = "option2", value = "value2")
    fun alone(
        ctx: CommandContext?,
        @Arguments.Text(name = "text", description = "Text arg", minLength = 1, maxLength = 10) text: String?
    ) {
        // noop
    }

    @Command.Sub(name = "sub2", description = "Sub in group")
    @Command.Group(name = "group1", description = "Group 1")
    fun inGroup(
        ctx: CommandContext?,
        @Arguments.Number(name = "n", description = "number", minValue = 0.0, maxValue = 1.0) n: Double
    ) {
        // noop
    }
}

class JdaParsingTest {

    @Test
    fun testJdaConversionWithKotlinAnnotations() {
        val data = KotlinAnnotationProcessor().parseCommand(SubcommandContainerFixture::class.java)
        assertNotNull(data)

        val jdaData = data.buildJDA()

        // Verify main command properties
        assertNotNull(jdaData)
        assertEquals(data.name(), jdaData.name)
        assertEquals(data.description(), jdaData.description)
        assertEquals(data.context(), jdaData.contexts)
        assertEquals(data.nsfw(), jdaData.isNSFW)
        assertEquals(data.defaultPermissions(), jdaData.defaultPermissions)
        assertEquals(data.subCommandGroups.size, jdaData.subcommandGroups.size)
        assertEquals(data.nodeCount, jdaData.subcommands.size)

        // Verify "alone" subcommand
        val aloneSub = jdaData.subcommands.stream()
            .filter { sub: SubcommandData? -> sub!!.getName() == "alone" }
            .findFirst()
            .orElse(null)
        assertNotNull(aloneSub)
        assertEquals(1, aloneSub.options.size)
        val aloneOption = aloneSub.options[0]
        assertEquals("text", aloneOption.name)
        assertEquals(OptionType.STRING, aloneOption.getType())
        assertEquals(2, aloneOption.getChoices().size)
        assertEquals("option1", aloneOption.getChoices().get(0).getName())
        assertEquals("option2", aloneOption.getChoices().get(1).getName())

        // Verify "group1" subcommand group and its "sub2" subcommand
        val group1 = jdaData.subcommandGroups.stream()
            .filter { group: SubcommandGroupData? -> group!!.getName() == "group1" }
            .findFirst()
            .orElse(null)
        assertNotNull(group1)
        val sub2 = group1.subcommands.stream()
            .filter { sub: SubcommandData? -> sub!!.getName() == "sub2" }
            .findFirst()
            .orElse(null)
        assertNotNull(sub2)
        assertEquals(1, sub2.options.size)
        val sub2Option = sub2.options[0]
        assertEquals("n", sub2Option.getName())
        assertEquals(OptionType.NUMBER, sub2Option.getType())
    }
}