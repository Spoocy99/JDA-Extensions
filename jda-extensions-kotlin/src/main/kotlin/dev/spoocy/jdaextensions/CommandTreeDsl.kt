package dev.spoocy.jdaextensions

import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument
import dev.spoocy.jdaextensions.commands.cooldown.CooldownScope
import dev.spoocy.jdaextensions.commands.event.CommandContext
import dev.spoocy.jdaextensions.commands.manager.CommandManager
import dev.spoocy.jdaextensions.commands.permission.CommandPermission
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand
import dev.spoocy.jdaextensions.commands.tree.CommandTree
import dev.spoocy.jdaextensions.commands.tree.SubCommand
import dev.spoocy.jdaextensions.commands.tree.SubCommandGroup
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Kotlin DSL for building command trees.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

fun commandTree(name: String, description: String, block: CommandTree.() -> Unit): CommandTree =
    CommandTree(name, description).apply(block)

fun buildCommand(name: String, description: String, block: CommandTree.() -> Unit): DiscordCommand =
    commandTree(name, description, block).build()

fun registerCommand(manager: CommandManager, name: String, description: String, block: CommandTree.() -> Unit) {
    commandTree(name, description, block).register(manager)
}

fun CommandTree.registerInto(manager: CommandManager) = this.register(manager)

fun CommandTree.sub(name: String, description: String, block: SubCommand.() -> Unit = {}) {
    this.then(SubCommand(name, description).apply(block))
}

fun CommandTree.group(name: String, description: String, block: SubCommandGroup.() -> Unit = {}) {
    this.then(SubCommandGroup(name, description).apply(block))
}

fun SubCommandGroup.sub(name: String, description: String, block: SubCommand.() -> Unit = {}) {
    this.then(SubCommand(name, description).apply(block))
}

fun CommandTree.executes(block: (CommandContext) -> Unit): CommandTree = this.executes(Consumer(block))
fun SubCommand.executes(block: (CommandContext) -> Unit): SubCommand = this.executes(Consumer(block))

fun CommandTree.withCooldown(scope: CooldownScope, duration: Duration): CommandTree =
    this.withCooldown(scope, duration)

fun CommandTree.withCooldown(scope: CooldownScope, duration: Long, unit: TimeUnit): CommandTree =
    this.withCooldown(scope, duration, unit)

fun CommandTree.buildCommand(): DiscordCommand = this.build()

fun CommandTree.withPermissions(vararg permissions: CommandPermission): CommandTree =
    this.withPermissions(*permissions)

fun SubCommand.withPermissions(vararg permissions: CommandPermission): SubCommand =
    this.withPermissions(*permissions)

fun CommandTree.arg(argument: AbstractArgument): CommandTree = this.arg(argument)
fun SubCommand.arg(argument: AbstractArgument): SubCommand = this.arg(argument)

fun registerCommandTree(manager: CommandManager, tree: CommandTree) {
    manager.register(tree.build())
}
