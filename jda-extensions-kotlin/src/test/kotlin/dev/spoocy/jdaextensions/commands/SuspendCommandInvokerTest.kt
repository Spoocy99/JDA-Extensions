package dev.spoocy.jdaextensions.commands

import dev.spoocy.jdaextensions.commands.annotations.Command
import dev.spoocy.jdaextensions.commands.annotations.Cooldown
import dev.spoocy.jdaextensions.commands.annotations.Permissions
import dev.spoocy.jdaextensions.commands.arguments.Arguments
import dev.spoocy.jdaextensions.commands.cooldown.CooldownScope
import dev.spoocy.jdaextensions.commands.event.CommandContext
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.InteractionContextType
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test command with suspend functions.
 */
@Command(
    name = "suspendcmd",
    description = "A command with suspend functions",
    context = [InteractionContextType.GUILD]
)
object SuspendCommandTest {

    @Suppress("UNUSED_PARAMETER")
    @Command.Default
    @Cooldown(value = 5, unit = TimeUnit.SECONDS, scope = CooldownScope.USER)
    @Permissions(Permission.MESSAGE_SEND)
    suspend fun execute(
        ctx: CommandContext,
        @Arguments.Text(name = "message", description = "The message to send") message: String
    ) {
        // This is a suspend function that would be invoked in a coroutine
        // In a real scenario, you could use await() on JDA RestActions here
        delay(1) // Use a coroutine function to ensure this is truly a suspend function
    }

    @Suppress("UNUSED_PARAMETER")
    @Command.Sub(name = "async", description = "An async subcommand")
    suspend fun asyncSub(
        ctx: CommandContext,
        @Arguments.Integer(name = "count", description = "Number of items") count: Int
    ) {
        // Another suspend function
        delay(1)
    }

    @Suppress("UNUSED_PARAMETER")
    @Command.Sub(name = "sync", description = "A synchronous subcommand")
    fun syncSub(ctx: CommandContext) {
        // This is a regular function, not suspend
    }
}

/**
 * Mixed command with both suspend and regular functions.
 */
@Command(name = "mixedcmd", description = "A mixed command")
object MixedCommandTest {

    @Suppress("UNUSED_PARAMETER")
    @Command.Default
    suspend fun defaultSuspend(ctx: CommandContext) {
        // Suspend default command
        delay(1)
    }

    @Suppress("UNUSED_PARAMETER")
    @Command.Sub(name = "regular", description = "Regular function")
    fun regularSub(ctx: CommandContext) {
        // Regular function
    }

    @Suppress("UNUSED_PARAMETER")
    @Command.Sub(name = "suspended", description = "Suspend function")
    suspend fun suspendedSub(
        ctx: CommandContext,
        @Arguments.Text(name = "text", description = "Text input") text: String
    ) {
        // Suspend function with arguments
        delay(1)
    }
}

class SuspendCommandInvokerTest {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val processor = KotlinCommandAnnotationProcessor.builder()
        .withScope(scope)
        .build()

    @Test
    fun `test suspend command parsing`() {
        val data: CommandData = processor.parseCommand(SuspendCommandTest::class.java)

        assertNotNull(data)
        assertEquals("suspendcmd", data.name())
        assertEquals("A command with suspend functions", data.description())

        // Check root command
        val root = data.rootNode()
        assertNotNull(root)
        assertEquals("suspendcmd", root.name())

        // Verify arguments are parsed correctly (suspend functions have a Continuation param that should be ignored)
        val args = root.argumentData
        assertEquals(1, args.size)
        assertEquals("message", args[0].name())
    }

    @Test
    fun `test suspend subcommand parsing`() {
        val data: CommandData = processor.parseCommand(SuspendCommandTest::class.java)

        // Check async subcommand (suspend)
        val asyncSub = data.getSubCommandData("async")
        assertNotNull(asyncSub)
        assertEquals("async", asyncSub.name())
        assertEquals(1, asyncSub.argumentData.size)
        assertEquals("count", asyncSub.argumentData[0].name())

        // Check sync subcommand (regular)
        val syncSub = data.getSubCommandData("sync")
        assertNotNull(syncSub)
        assertEquals("sync", syncSub.name())
        assertTrue(syncSub.argumentData.isEmpty())
    }

    @Test
    fun `test mixed command parsing`() {
        val data: CommandData = processor.parseCommand(MixedCommandTest::class.java)

        assertNotNull(data)
        assertEquals("mixedcmd", data.name())

        // Check default suspend command
        val root = data.rootNode()
        assertNotNull(root)
        assertTrue(root.argumentData.isEmpty())

        // Check regular subcommand
        val regularSub = data.getSubCommandData("regular")
        assertNotNull(regularSub)
        assertTrue(regularSub.argumentData.isEmpty())

        // Check suspend subcommand with arguments
        val suspendedSub = data.getSubCommandData("suspended")
        assertNotNull(suspendedSub)
        assertEquals(1, suspendedSub.argumentData.size)
        assertEquals("text", suspendedSub.argumentData[0].name())
    }
}

