package dev.spoocy.jdaextensions.core;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.event.EventWaiter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface BotSettings {

    /**
     * Creates a new {@link BotSettingsBuilder} instance.
     *
     * @return a new {@link BotSettingsBuilder} instance.
     */
    static BotSettingsBuilder builder() {
        return new BotSettingsBuilder();
    }

    /**
     * Creates a new {@link BotSettingsBuilder} instance with the specified token.
     *
     * @param token the token of the bot.
     * @return a new {@link BotSettingsBuilder} instance.
     */
    static BotSettingsBuilder builder(@NotNull String token) {
        return new BotSettingsBuilder(token);
    }

    /**
     * Gets the token of the bot.
     *
     * @return the token of the bot.
     */
    @NotNull
    String token();

    /**
     * Gets the gateway intents to enable for the bot.
     *
     * @return the gateway intents to enable for the bot.
     */
    @NotNull
    Collection<GatewayIntent> intents();

    /**
     * Gets whether the bot should automatically log in on startup.
     *
     * @return {@code true} if the bot should automatically log in on startup, {@code false} otherwise.
     */
    boolean autoLogin();

    /**
     * Gets the IDs of the bot owners.
     *
     * @return the IDs of the bot owners.
     */
    @NotNull
    Set<Long> owners();

    /**
     * Locks the bot to the specified guilds.
     * This is useful for testing as commands will only be registered in these guilds.
     *
     * @return the IDs of the guilds to lock the bot to, or {@code null} to not lock the bot to any guilds.
     */
    @Nullable
    Set<Long> guilds();

    /**
     * Gets the number of shards to use for the bot.
     *
     * @return the number of shards to use for the bot.
     */
    int shards();

    /**
     * Gets the online status provider to use for the bot.
     *
     * @return the online status provider to use for the bot.
     */
    @NotNull
    IntFunction<OnlineStatus> onlineStatus();

    /**
     * Gets the activity provider to use for the bot.
     *
     * @return the activity provider to use for the bot.
     */
    @NotNull
    IntFunction<Activity> activity();

    /**
     * Gets the event manager provider to use for the bot.
     *
     * @return the event manager provider to use for the bot.
     */
    @NotNull
    IntFunction<? extends IEventManager> eventManager();

    /**
     * Gets the global event waiter to use for the bot.
     *
     * @return the event waiter to use for the bot, or null if no event waiter should be used.
     */
    @Nullable
    EventWaiter eventWaiter();

    /**
     * Gets whether the bot should automatically commit commands on startup.
     *
     * @return {@code true} if the bot should automatically commit commands on startup, {@code false} otherwise.
     */
    boolean autoCommitCommands();

    /**
     * Gets the command manager to use for the bot.
     *
     * @return the command manager to use for the bot, or null if no command manager should be used.
     */
    @Nullable
    CommandManager commandManager();

    /**
     * Creates an immutable copy of this BotSettings instance.
     *
     * @return an immutable copy of this BotSettings instance.
     */
    @NotNull
    default ImmutableBotSettings immutable() {
        return new ImmutableBotSettings(this);
    }

}
