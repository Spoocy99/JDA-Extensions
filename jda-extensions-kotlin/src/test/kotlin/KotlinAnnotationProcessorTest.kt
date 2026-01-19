import dev.spoocy.jdaextensions.commands.annotations.Choice
import dev.spoocy.jdaextensions.commands.annotations.Command
import dev.spoocy.jdaextensions.commands.annotations.Cooldown
import dev.spoocy.jdaextensions.commands.annotations.Permissions
import dev.spoocy.jdaextensions.commands.arguments.Arguments
import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument
import dev.spoocy.jdaextensions.commands.cooldown.CooldownScope
import dev.spoocy.jdaextensions.commands.event.CommandContext
import dev.spoocy.jdaextensions.commands.manager.impl.DefaultCommandAnnotationProcessor
import dev.spoocy.jdaextensions.commands.manager.impl.KotlinAnnotationProcessor
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Command(name = "ktcmd", description = "A kotlin-like command", nsfw = false,
        context = [InteractionContextType.GUILD])
object ObjectLikeSimple {

    @Command.Default
    @Cooldown(value = 3, unit = TimeUnit.SECONDS, scope = CooldownScope.USER)
    @Permissions(Permission.MESSAGE_SEND)
    fun execute(ctx: CommandContext,
                @Arguments.Text(name = "text", description = "Text arg", minLength = 1, maxLength = 10) text: String,
                @Arguments.Integer(name = "count", description = "Count arg", minValue = 0, maxValue = 5) count: Int) {
        // noop
    }

}

@Command(name = "parentkt", description = "Parent kotlin-like command")
object ObjectLikeSubcommandContainer {

    @Command.Sub(name = "alone", description = "Alone sub")
    fun alone(ctx: CommandContext,
              @Arguments.Number(name = "n", description = "number", minValue = 0.0, maxValue = 1.0) n: Double) {
        // noop
    }

    @Command.Sub(name = "sub2", description = "Sub in group")
    @Command.Group(name = "group1", description = "Group 1")
    @Choice.Text(argument = "text", name = "option1", value = "value1")
    fun inGroup(ctx: CommandContext,
                @Arguments.Text(name = "text", description = "Text arg", minLength = 1, maxLength = 10) text: String) {
        // noop
    }

}

class KotlinAnnotationProcessorTest {

    @Test
    fun defaultMethodsExist() {
        val proc = KotlinAnnotationProcessor()
        val defaultMethod = proc.getDefaultMethod(ObjectLikeSimple::class.java)
        assertNotNull(defaultMethod)

        val defaultMethod2 = proc.getDefaultMethod(ObjectLikeSubcommandContainer::class.java)
        assertNull(defaultMethod2)
    }

    @Test
    fun subcommandMethodsExist() {
        val proc = KotlinAnnotationProcessor()
        val subcommandMethods = proc.getSubCommandMethods(ObjectLikeSimple::class.java)
        assertNotNull(subcommandMethods)
        assertEquals(0, subcommandMethods.size)

        val subcommandMethods2 = proc.getSubCommandMethods(ObjectLikeSubcommandContainer::class.java)
        assertNotNull(subcommandMethods2)
        assertEquals(2, subcommandMethods2.size)
    }

    @Test
    fun testParseDefaultCommand() {
        val proc = KotlinAnnotationProcessor()
        val data: CommandData = proc.parseCommand(ObjectLikeSimple::class.java)

        assertNotNull(data)
        assertEquals("ktcmd", data.name())
        assertEquals("A kotlin-like command", data.description())
        assertTrue(data.context().contains(InteractionContextType.GUILD))

        val root = data.rootNode()
        assertNotNull(root)
        assertEquals("ktcmd", root.name())
        assertEquals("A kotlin-like command", root.description())

        val args: List<AbstractArgument> = root.argumentData
        assertEquals(2, args.size)
        assertEquals("text", args[0].name())
        assertEquals("count", args[1].name())

        assertNotNull(root.cooldown())
        assertEquals(TimeUnit.SECONDS.toMillis(3), root.cooldown().duration().toMillis())

        assertTrue(root.permissions().isNotEmpty())
    }

    @Test
    fun testParseSubcommandsAndGroups() {
        val proc = KotlinAnnotationProcessor()
        val data: CommandData = proc.parseCommand(ObjectLikeSubcommandContainer::class.java)

        assertNotNull(data)
        assertEquals("parentkt", data.name())

        val alone = data.getSubCommandData("alone")
        assertNotNull(alone)
        assertEquals("alone", alone.name())
        assertEquals(1, alone.argumentData.size)
        assertEquals("n", alone.argumentData[0].name())

        val group = data.getSubCommandGroupData("group1")
        assertNotNull(group)
        val sub2 = group.getNode("sub2") as CommandNodeData
        assertNotNull(sub2)
        assertEquals("sub2", sub2.name())
        assertEquals(1, sub2.argumentData.size)
        assertEquals("text", sub2.argumentData[0].name())
    }

}
