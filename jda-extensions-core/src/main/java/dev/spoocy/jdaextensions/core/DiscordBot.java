package dev.spoocy.jdaextensions.core;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.event.EventWaiter;
import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.CloseCode;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class DiscordBot {

    protected static final ILogger LOGGER = ILogger.forThisClass();

    private final String token;
    private final Collection<GatewayIntent> intents;
    private final Set<Long> owners;
    private final Set<Long> guilds;
    private final int shards;
    private final IntFunction<OnlineStatus> onlineStatus;
    private final IntFunction<Activity> activity;
    private final IntFunction<? extends IEventManager> eventManager;
    private final EventWaiter eventWaiter;
    private final boolean autoCommitCommands;
    private final CommandManager commandManager;

    private boolean loggedIn;
    private long startupTime;
    private ApplicationInfo applicationInfo;

    /**
     * Creates a new bot instance with the given settings.
     * <p>
     * If {@link BotSettings#autoLogin()} is {@code true} (default), the bot will automatically log in.
     * If {@code false}, the settings are stored and you must call {@link #login()} to start the bot.
     * This allows subclasses to perform additional configuration in their constructors before the bot logs in.
     * <p>
     * Example usage with deferred login:
     * <pre>{@code
     * public class MyBot extends DiscordBot {
     *     private final MyService service;
     *
     *     public MyBot(BotSettings settings, MyService service) {
     *         super(settings); // autoLogin = false
     *         this.service = service;
     *         // Additional setup using constructor args...
     *         login(); // Now login after setup is complete
     *     }
     *
     *     @Override
     *     protected void configure() {
     *         // Called before login - can use this.service here
     *         registerCommand(new MyCommand(service));
     *     }
     * }
     * }</pre>
     *
     * @param settings the settings to use for this bot.
     */
    public DiscordBot(@NotNull BotSettings settings) {
        this(
                settings.token(),
                settings.intents(),
                settings.autoLogin(),
                settings.owners(),
                settings.guilds(),
                settings.shards(),
                settings.onlineStatus(),
                settings.activity(),
                settings.eventManager(),
                settings.eventWaiter(),
                settings.autoCommitCommands(),
                settings.commandManager()
        );
    }

    public DiscordBot(
            @NotNull String token,
            @NotNull Collection<GatewayIntent> intents,
            boolean autoLogin,
            @NotNull Set<Long> owners,
            @Nullable Set<Long> guilds,
            int shards,
            @NotNull IntFunction<OnlineStatus> onlineStatus,
            @NotNull IntFunction<Activity> activity,
            @NotNull IntFunction<? extends IEventManager> eventManager,
            @Nullable EventWaiter eventWaiter,
            boolean autoCommitCommands,
            @Nullable CommandManager commandManager
    ) {
        this.token = token;
        this.intents = Collections.unmodifiableCollection(intents);
        this.owners = Collections.unmodifiableSet(owners);
        this.guilds = guilds != null ? Collections.unmodifiableSet(guilds) : null;
        this.shards = shards;
        this.onlineStatus = onlineStatus;
        this.activity = activity;
        this.eventManager = eventManager;
        this.eventWaiter = eventWaiter;
        this.autoCommitCommands = autoCommitCommands;
        this.commandManager = commandManager;

        if (autoLogin) {
            this.login();
        }
    }

    /**
     * Starts this bot using the settings provided in the constructor.
     * <p>
     * This method should only be called when the bot was created with {@link BotSettings#autoLogin()}
     * set to {@code false}.
     *
     * @throws IllegalStateException if the bot has already been logged in or no settings were provided.
     */
    public void login() {
        if (this.loggedIn) {
            throw new IllegalStateException("This bot has already been logged in!");
        }
        this.loggedIn = true;

        this.configure();

        this.prepareLogin(
                this.shards,
                this.token,
                this.intents,
                this.onlineStatus,
                this.activity,
                this.eventManager
        );

        this.registerListener(this);

        if (this.eventWaiter != null) {
            this.registerListener(this.eventWaiter);
        }

        Runtime.getRuntime()
                .addShutdownHook(new Thread(this::onShutdown));

        LOGGER.info(
                "Bot[{}] started successfully in {} ms!",
                this.getClass()
                        .getSimpleName(),
                System.currentTimeMillis() - startupTime
        );
        this.onStart();
    }

    protected abstract void prepareLogin(
            int shards,
            @NotNull String token,
            @NotNull Collection<GatewayIntent> intents,
            @NotNull IntFunction<OnlineStatus> onlineStatus,
            @NotNull IntFunction<Activity> activity,
            @NotNull IntFunction<? extends IEventManager> eventManager
    );

    /**
     * Called during the login process, before any listeners are registered.
     * <p>
     * Override this method to perform configuration that depends on constructor arguments
     * or other setup that must occur before the bot starts receiving events.
     * <p>
     * At this point, the command manager and other settings are already initialized
     * and can be accessed via getter methods.
     * <p>
     * Example:
     * <pre>{@code
     * @Override
     * protected void configure() {
     *     registerCommand(new MyCommand(myService));
     * }
     * }</pre>
     * <p>
     * Default implementation does nothing.
     */
    protected void configure() {
        // Default implementation - subclasses can override
    }

    protected abstract void onStart();

    protected abstract void onReady();

    protected abstract void onShutdown();

    /**
     * Gets the time (in milliseconds) when this bot was started.
     *
     * @return the time (in milliseconds) when this bot was started.
     */
    public long getLoginTime() {
        requireLoggedIn();
        return this.startupTime;
    }

    /**
     * Gets the set of user IDs that belong to the owners of this bot.
     *
     * @return the set of user IDs that belong to the owners of this bot.
     */
    @NotNull
    public Set<Long> getOwners() {
        return this.owners;
    }

    /**
     * Checks if this bot is locked to any guilds.
     *
     * @return {@code true} if this bot is locked to any guilds, {@code false} otherwise.
     */
    public boolean lockedToGuilds() {
        return this.guilds != null && !this.guilds.isEmpty();
    }

    /**
     * Gets the set of guild IDs that this bot is locked.
     *
     * @return the set of guild IDs, or {@code null} if this bot is not locked to any guilds.
     */
    @Nullable
    public Set<Long> getGuilds() {
        return this.guilds;
    }

    /**
     * Gets the event waiter used by this bot.
     *
     * @return the event waiter used by this bot.
     *
     * @throws IllegalStateException if this bot is not using an event waiter.
     */
    @NotNull
    public EventWaiter getEventWaiter() {
        return notNull(this.eventWaiter, "This bot is not using an event waiter!");
    }


    /**
     * Gets the set of user IDs that belong to the owners of this bot.
     *
     * @return the set of user IDs that belong to the owners of this bot.
     */
    @NotNull
    public ApplicationInfo getApplicationInfo() {
        requireLoggedIn();
        return notNull(this.applicationInfo, "Application info has not been retrieved yet!");
    }

    /**
     * Gets the command manager used by this bot.
     *
     * @return the command manager used by this bot.
     *
     * @throws IllegalStateException if this bot is not using a command manager.
     */
    public CommandManager getCommandManager() {
        requireLoggedIn();
        return notNull(this.commandManager, "This bot is not using a command manager!");
    }

    /**
     * Checks if the given user ID belongs to an owner of this bot.
     *
     * @param userId the user ID to check
     *
     * @return {@code true} if the given user ID belongs to an owner of this bot, {@code false} otherwise.
     */
    public boolean isOwner(long userId) {
        return getOwners().contains(userId);
    }

    /**
     * Gets the total number of shards this bot is running with.
     *
     * @throws IllegalStateException if the bot has not been started yet.
     */
    public abstract int getShardCount();

    /**
     * Checks if all shards of this bot are ready.
     *
     * @return {@code true} if all shards of this bot are ready, {@code false} otherwise.
     */
    public abstract boolean isReady();

    /**
     * Registers the given listener to this bot's event manager.
     *
     * @param listener the listener to register
     */
    public abstract void registerListener(@NotNull Object listener);

    /**
     * Unregisters the given listener from this bot's event manager.
     *
     * @param listener the listener to unregister
     */
    public abstract void unregisterListener(@NotNull Object listener);

    /**
     * Registers the given annotated class to this bot's command manager.
     *
     * @param command the annotated class to register
     *
     * @throws IllegalStateException if this bot is not using a command manager.
     */
    public void registerCommand(@NotNull Object command) {
        if (command instanceof DiscordCommand) {
            getCommandManager().register((DiscordCommand) command);
            return;
        }

        if (command instanceof Class<?>) {
            getCommandManager().addCommand((Class<?>) command);
            return;
        }

        getCommandManager().addCommand(command);
    }

    @SubscribeEvent
    private void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        LOGGER.debug("Shard {} is ready!", jda.getShardInfo()
                .getShardId());

        jda.retrieveApplicationInfo()
                .queue(
                        info -> this.applicationInfo = info,
                        error -> LOGGER.error("Failed to retrieve bot application info!", error)
                );

        if (this.commandManager != null && this.autoCommitCommands) {
            this.commandManager.commitCommands(jda, this.guilds);
        }

        if (getShardCount() == 1 || isReady()) {
            this.onReady();
        }
    }

    @SubscribeEvent
    private void onDisconnect(@NotNull SessionDisconnectEvent event) {
        CloseCode code = event.getCloseCode();
        String reason = (code == null) ? "Unknown" : code.getMeaning();
        LOGGER.debug("Lost connection. Reason: {}", reason);
    }

    @SubscribeEvent
    private void onReconnect(@NotNull SessionResumeEvent event) {
        LOGGER.debug("Reconnected successfully. RN: {}", event.getResponseNumber());
    }

    @SubscribeEvent
    private void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (this.commandManager == null) return;
        this.commandManager.handleCommand(event, this);
    }

    @SubscribeEvent
    public void onMessage(@NotNull MessageReceivedEvent event) {
        if (this.commandManager == null) return;
        this.commandManager.handlePrefixCommand(event, this);
    }

    private void requireLoggedIn() {
        if (!this.loggedIn) {
            throw new IllegalStateException("This bot has not been logged in yet!");
        }
    }

    private <T> T notNull(@Nullable T obj, @NotNull String message) {
        if (obj == null) {
            throw new IllegalStateException(message);
        }
        return obj;
    }

}
