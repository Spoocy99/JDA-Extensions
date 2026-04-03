package dev.spoocy.jdaextensions

import dev.spoocy.jdaextensions.commands.manager.CommandManager
import dev.spoocy.jdaextensions.core.BotSettings
import dev.spoocy.jdaextensions.core.BotSettingsBuilder
import dev.spoocy.jdaextensions.event.EventWaiter
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.IEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import java.util.function.IntFunction

/**
 * Kotlin DSL extensions for [BotSettingsBuilder].
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

inline fun BotSettings(block: BotSettingsBuilder.() -> Unit): BotSettings {
    return BotSettingsBuilder().apply(block).build()
}

/**
 * The token for the bot.
 */
var BotSettingsBuilder.token: String
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setToken(value)
    }

/**
 * The gateway intents to enable for the bot.
 */
var BotSettingsBuilder.intents: Collection<GatewayIntent>
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setIntents(value)
    }

var BotSettingsBuilder.autoLogin: Boolean
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setAutoLogin(value)
    }

/**
 * The IDs of the bot owners.
 */
var BotSettingsBuilder.owners: Set<Long>
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setOwners(value)
    }

/**
 * The IDs of the guilds to lock to bot to.
 */
var BotSettingsBuilder.guilds: Set<Long>
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setGuilds(value)
    }

/**
 * The number of shards to use for the bot.
 */
var BotSettingsBuilder.shards: Int
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setShards(value)
    }

/**
 * The online status for the bot (constant across all shards).
 */
var BotSettingsBuilder.onlineStatus: OnlineStatus
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setOnlineStatus(value)
    }

/**
 * The online status provider for the bot (can vary by shard).
 */
var BotSettingsBuilder.onlineStatusProvider: IntFunction<OnlineStatus>
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setOnlineStatus(value)
    }

/**
 * The activity for the bot (constant across all shards).
 */
var BotSettingsBuilder.activity: Activity?
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setActivity(value)
    }

/**
 * The activity provider for the bot (can vary by shard).
 */
var BotSettingsBuilder.activityProvider: IntFunction<Activity>
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setActivity(value)
    }

/**
 * The event manager for the bot (constant across all shards).
 */
var BotSettingsBuilder.eventManager: IEventManager
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setEventManager(value)
    }

/**
 * The event manager provider for the bot (can vary by shard).
 */
var BotSettingsBuilder.eventManagerProvider: IntFunction<out IEventManager>
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setEventManager(value)
    }

/**
 * The global event waiter to use for the bot.
 */
var BotSettingsBuilder.eventWaiter: EventWaiter?
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setEventWaiter(value)
    }

/**
 * Whether the bot should automatically commit commands on startup.
 */
var BotSettingsBuilder.autoCommitCommands: Boolean
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setAutoCommitCommands(value)
    }

/**
 * The command manager to use for the bot.
 */
var BotSettingsBuilder.commandManager: CommandManager?
    @Deprecated("Write-only property", level = DeprecationLevel.HIDDEN)
    get() = throw UnsupportedOperationException("Write-only property")
    set(value) {
        this.setCommandManager(value)
    }

/**
 * Sets the online status provider using a Kotlin lambda.
 *
 * @param provider a lambda that takes a shard ID and returns an [OnlineStatus].
 */
fun BotSettingsBuilder.onlineStatus(provider: (shardId: Int) -> OnlineStatus) {
    this.setOnlineStatus { provider(it) }
}

/**
 * Sets the activity provider using a Kotlin lambda.
 *
 * @param provider a lambda that takes a shard ID and returns an [Activity].
 */
fun BotSettingsBuilder.activity(provider: (shardId: Int) -> Activity?) {
    this.setActivity { provider(it) }
}

/**
 * Sets the event manager provider using a Kotlin lambda.
 *
 * @param provider a lambda that takes a shard ID and returns an [IEventManager].
 */
fun BotSettingsBuilder.eventManager(provider: (shardId: Int) -> IEventManager) {
    this.setEventManager { provider(it) }
}
