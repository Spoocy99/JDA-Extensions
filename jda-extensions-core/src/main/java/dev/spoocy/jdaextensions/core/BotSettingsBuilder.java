package dev.spoocy.jdaextensions.core;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.event.AdvancedEventManager;
import dev.spoocy.jdaextensions.event.EventWaiter;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.misc.Args;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntFunction;

/**
 * A builder class for creating {@link BotSettings} instances.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class BotSettingsBuilder {

    private static final ILogger LOGGER = ILogger.forThisClass();

    private String token;
    private Collection<GatewayIntent> intents = Collections.emptyList();
    private boolean autoLogin = true;
    private Set<Long> owners = Collections.emptySet();
    private Set<Long> guilds = null;
    private int shards = 1;
    private IntFunction<OnlineStatus> onlineStatus = shardId -> OnlineStatus.ONLINE;
    private IntFunction<Activity> activity = shardId -> null;
    private IntFunction<? extends IEventManager> eventManager = shardId -> new AdvancedEventManager();
    private EventWaiter eventWaiter;
    private boolean autoCommitCommands = true;
    private CommandManager commandManager;

    /**
     * Creates a new BotSettingsBuilder.
     */
    public BotSettingsBuilder() {
    }

    /**
     * Creates a new BotSettingsBuilder with the specified token.
     *
     * @param token the token of the bot.
     */
    public BotSettingsBuilder(@NotNull String token) {
        this.token = Args.notNull(token, "Token cannot be null");
    }

    /**
     * Sets the token of the bot.
     *
     * @param token the token of the bot.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setToken(@NotNull String token) {
        this.token = Args.notNull(token, "Token cannot be null");
        return this;
    }

    /**
     * Sets the gateway intents to enable for the bot.
     *
     * @param intents the gateway intents to enable.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setIntents(@NotNull Collection<GatewayIntent> intents) {
        this.intents = Args.notNull(intents, "Intents cannot be null");
        return this;
    }

    /**
     * Sets the gateway intents to enable for the bot.
     *
     * @param intents the gateway intents to enable.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setIntents(@NotNull GatewayIntent... intents) {
        this.intents = Arrays.asList(Args.notNull(intents, "Intents cannot be null"));
        return this;
    }

    /**
     * Sets whether the bot should automatically log in when passed to a {@link DiscordBot} constructor.
     * <p>
     * If set to {@code false}, you must manually call {@link DiscordBot#login()} to start the bot.
     * This is useful when you need to perform additional configuration in subclass constructors before logging in.
     * <p>
     * Default is {@code true}.
     *
     * @param autoLogin {@code true} to automatically log in, {@code false} to defer login.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
        return this;
    }

    /**
     * Sets the IDs of the bot owners.
     *
     * @param owners the IDs of the bot owners.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setOwners(@NotNull Set<Long> owners) {
        this.owners = Args.notNull(owners, "Owners cannot be null");
        return this;
    }

    /**
     * Sets the IDs of the bot owners.
     *
     * @param owners the IDs of the bot owners.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setOwners(@NotNull Long... owners) {
        this.owners = new HashSet<>(Arrays.asList(Args.notNull(owners, "Owners cannot be null")));
        return this;
    }

    /**
     * Locks the bot to the specified guilds.
     *
     * @param guilds the IDs of the guilds to lock the bot to, or null to not lock the bot to any guilds.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setGuilds(@Nullable Set<Long> guilds) {
        this.guilds = guilds != null ? Set.copyOf(guilds) : null;
        return this;
    }

    /**
     * Locks the bot to the specified guilds.
     *
     * @param guilds the IDs of the guilds to lock the bot to, or null to not lock the bot to any guilds.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setGuilds(@Nullable Long... guilds) {
        this.guilds = guilds != null ? new HashSet<>(Arrays.asList(guilds)) : null;
        return this;
    }

    /**
     * Sets the number of shards to use for the bot.
     *
     * @param shards the number of shards.
     *
     * @return this builder instance for chaining.
     *
     * @throws IllegalArgumentException if shards is less than 1.
     */
    @NotNull
    public BotSettingsBuilder setShards(int shards) {
        if (shards < 1) {
            throw new IllegalArgumentException("Shards must be at least 1");
        }
        this.shards = shards;
        return this;
    }

    /**
     * Sets the online status for the bot (constant across all shards).
     *
     * @param onlineStatus the online status.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setOnlineStatus(@NotNull OnlineStatus onlineStatus) {
        Args.notNull(onlineStatus, "Online status cannot be null");
        this.onlineStatus = shardId -> onlineStatus;
        return this;
    }

    /**
     * Sets the online status provider for the bot (can vary by shard).
     *
     * @param onlineStatus the online status provider function.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setOnlineStatus(@NotNull IntFunction<OnlineStatus> onlineStatus) {
        this.onlineStatus = Args.notNull(onlineStatus, "Online status provider cannot be null");
        return this;
    }

    /**
     * Sets the activity for the bot (constant across all shards).
     *
     * @param activity the activity, or null for no activity.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setActivity(@Nullable Activity activity) {
        this.activity = shardId -> activity;
        return this;
    }

    /**
     * Sets the activity provider for the bot (can vary by shard).
     *
     * @param activity the activity provider function.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setActivity(@NotNull IntFunction<Activity> activity) {
        this.activity = Args.notNull(activity, "Activity provider cannot be null");
        return this;
    }

    /**
     * Sets the event manager provider for the bot.
     *
     * @param eventManager the event manager provider function.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setEventManager(@NotNull IntFunction<? extends IEventManager> eventManager) {
        this.eventManager = Args.notNull(eventManager, "Event manager provider cannot be null");
        return this;
    }

    /**
     * Sets the event manager for the bot (constant across all shards).
     *
     * @param eventManager the event manager.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setEventManager(@NotNull IEventManager eventManager) {
        Args.notNull(eventManager, "Event manager cannot be null");
        this.eventManager = shardId -> eventManager;
        return this;
    }

    /**
     * Sets the global event waiter to use for the bot.
     *
     * @param eventWaiter the event waiter, or null if no event waiter should be used.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setEventWaiter(@Nullable EventWaiter eventWaiter) {
        this.eventWaiter = eventWaiter;
        return this;
    }

    /**
     * Sets whether the bot should automatically commit commands on startup.
     *
     * @param autoCommitCommands {@code true} if the bot should automatically commit commands on startup, {@code false} otherwise.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setAutoCommitCommands(boolean autoCommitCommands) {
        this.autoCommitCommands = autoCommitCommands;
        return this;
    }

    /**
     * Sets the command manager to use for the bot.
     *
     * @param commandManager the command manager, or null if no command manager should be used.
     *
     * @return this builder instance for chaining.
     */
    @NotNull
    public BotSettingsBuilder setCommandManager(@Nullable CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    /**
     * Builds the {@link BotSettings} instance with the configured settings.
     *
     * @return a new {@link BotSettings} instance.
     *
     * @throws IllegalStateException if the token has not been set.
     */
    @NotNull
    public BotSettings build() {
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Token must be set");
        }

        if (this.intents.isEmpty()) {
            LOGGER.warn("No gateway intents have been set! This may cause the bot to not receive any events. Make sure to set the required gateway intents for your bot using BotSettingsBuilder.setIntents()!");
        }

        return new ImmutableBotSettings(
                this.token,
                this.intents,
                this.autoLogin,
                this.owners,
                this.guilds,
                this.shards,
                this.onlineStatus,
                this.activity,
                this.eventManager,
                this.eventWaiter,
                this.autoCommitCommands,
                this.commandManager
        );
    }
}

