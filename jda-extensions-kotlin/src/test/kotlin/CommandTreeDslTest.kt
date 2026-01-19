import dev.spoocy.jdaextensions.commandTree
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData
import dev.spoocy.jdaextensions.group
import dev.spoocy.jdaextensions.sub
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CommandTreeDslTest {

    @Test
    fun buildCreatesCommandData() {
        val tree = commandTree("name", "desc") {
            withContext(InteractionContextType.GUILD)
            withNsfw(true)
        }

        val data = tree.build() as CommandData

        assertEquals("name", data.name())
        assertEquals("desc", data.description())
        assertTrue(data.nsfw())
        assertTrue(data.context().contains(InteractionContextType.GUILD))
    }

    @Test
    fun testJdaConversion() {
        val tree = commandTree("test", "A test command") {
            withContext(InteractionContextType.GUILD, InteractionContextType.BOT_DM)
            executes { /* Command execution logic */ }

            sub("sub", "A subcommand")  {
                executes { /* Subcommand execution logic */ }
            }

            group("group", "A command group") {

                sub("nested", "A nested command") {
                    executes { /* Nested command execution logic */ }
                }
            }

            withNsfw(false)
        }

        val data = tree.build() as CommandData
        val jdaData: SlashCommandData = data.buildJDA()

        assertNotNull(jdaData)
        assertEquals("test", jdaData.name)
        assertEquals("A test command", jdaData.description)
        assertEquals(1, jdaData.subcommands.size)
        assertEquals(1, jdaData.subcommandGroups.size)
        assertFalse(data.nsfw())
        assertTrue(data.context().contains(InteractionContextType.GUILD))
        assertTrue(data.context().contains(InteractionContextType.BOT_DM))
    }

    @Test
    fun toStringContainsKeyProperties() {
        val tree = commandTree("n", "d") { }
        val s = tree.toString()
        assertTrue(s.contains("n"))
        assertTrue(s.contains("d"))
    }
}