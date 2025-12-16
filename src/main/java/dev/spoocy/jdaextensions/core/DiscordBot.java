package dev.spoocy.jdaextensions.core;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.event.AdvancedEventManager;
import dev.spoocy.jdaextensions.event.EventWaiter;
import dev.spoocy.jdaextensions.commands.manager.impl.DefaultCommandManager;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.FactoryHolder;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.log.LogLevel;
import dev.spoocy.utils.common.text.FormatUtils;
import dev.spoocy.utils.common.scheduler.Scheduler;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.CloseCode;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class DiscordBot<S extends BotSettings> {

    private static final ILogger LOGGER = ILogger.forThisClass();

    private final BotBuilder builder;
    private final S config;
    private final long startupTime;
    private final IEventManager eventManager;
    private final ShardManager shardManager;
    private ApplicationInfo applicationInfo;
    private final EventWaiter eventWaiter;

    @Nullable
    private final CommandManager commandManager;

    private final ScheduledExecutorService scheduler = Scheduler.newScheduledThreadPool(1);

    public DiscordBot(@NotNull S config, @NotNull BotBuilder builder) {
        this.builder = builder;
        this.config = config;
        this.startupTime = System.currentTimeMillis();
        this.eventManager = new AdvancedEventManager();
        this.eventWaiter = new EventWaiter(Scheduler.newScheduledThreadPool(1), true);

        LogLevel level = config.getLogLevel();
        FactoryHolder.setLevel(level);
        LOGGER.info("Logging level set to: " + level);

        builder.validate();
        LOGGER.info("Initializing bot with: "
                + "\n\t" + config.toString()
                + "\n\t" + builder.formatInstanceInfo()
        );

        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.createDefault(config.getToken(), builder.getIntents())
                .setHttpClient(new OkHttpClient())
                .setStatus(config.getOnlineStatus())
                .setStatusProvider(v -> config.getOnlineStatus())
                .setShardsTotal(config.getShards())
                .setMemberCachePolicy(builder.getMemberCachePolicy())
                .enableCache(builder.getCacheFlags())
                .setEventManagerProvider(v -> eventManager);

        initActivities(builder, shardManagerBuilder);

        this.commandManager = builder.getCommandManager();

        shardManagerBuilder.addEventListeners(this, this.eventWaiter);
        shardManagerBuilder.addEventListeners(builder.getListeners().toArray());

        builder.getBuilderActions().forEach(action -> action.accept(shardManagerBuilder));
        this.shardManager = shardManagerBuilder.build();
        builder.getManagerActions().forEach(action -> action.accept(shardManager));

        getJDA().retrieveApplicationInfo().queue(applicationInfo -> this.applicationInfo = applicationInfo);
        LOGGER.info("Successfully launched Discord Bot in {}!", FormatUtils.formatDuration(System.currentTimeMillis() - startupTime));

        onStart();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down Bot...");

            if(!this.scheduler.isShutdown()) {
                this.scheduler.shutdownNow();
            }

            this.onShutdown();
        }));
    }

    protected abstract void onStart();

    protected abstract void onReady();

    protected abstract void onShutdown();

    public long getBotStartupTime() {
        return this.startupTime;
    }

    @NotNull
    public S getConfig() {
        return this.config;
    }

    @NotNull
    public IEventManager getEventManager() {
        return this.eventManager;
    }

    @NotNull
    public ShardManager getShardManager() {
        return this.shardManager;
    }

    @NotNull
    public ApplicationInfo getApplicationInfo() {
        return this.applicationInfo;
    }

    @NotNull
    public EventWaiter getEventWaiter() {
        return this.eventWaiter;
    }

    public boolean useCommandManager() {
        return this.commandManager != null;
    }

    @NotNull
    public CommandManager getCommandManager() {
        if (this.commandManager == null) {
            throw new IllegalStateException("This bot instance does not use a CommandManager!");
        }
        return this.commandManager;
    }

    public JDA getJDA() {
        return Collector.of(shardManager.getShardCache().stream()).findFirst().orElseThrow(() -> new IllegalStateException("No JDA is ready yet!"));
    }

    public boolean isReady() {
        return shardManager != null && Collector.of(shardManager.getShardCache().stream()).allMatch(jda -> jda.getStatus() == JDA.Status.CONNECTED);
    }

    public int getShardCount() {
        return shardManager == null ? 0 : Collector.of(shardManager.getShardCache().stream()).filter(jda -> jda.getStatus() == JDA.Status.CONNECTED).count();
    }

    public List<JDA> getShards() {
        return shardManager == null ? Collections.emptyList() : Collector.of(shardManager.getShardCache().stream()).filter(jda -> jda.getStatus() == JDA.Status.CONNECTED).asList();
    }

    public void onEachShard(@NotNull Consumer<? super JDA> action) {
        shardManager.getShardCache().forEach(action);
    }

    public User getSelfUser() {
        return getJDA().getSelfUser();
    }

    public Member getSelfMember(@NotNull Guild guild) {
        return guild.getMember(getSelfUser());
    }

    public boolean isOwner(long id) {
        for (long ownerId : config.getOwners()) {
            if (ownerId == id) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        LOGGER.info("Shard {} is ready!", jda.getShardInfo().getShardId());
        if (config.getShards() == 1 || isReady()) {
            onReady();
        }

        this.builder.getShardActions().forEach(action -> action.accept(jda));
    }

    @SubscribeEvent
    public void onDisconnect(@NotNull SessionDisconnectEvent event) {
        CloseCode code = event.getCloseCode();
        String reason = (code == null) ? null : code.getMeaning();
        LOGGER.info("Lost connection. Reason: {}", reason);
    }

    @SubscribeEvent
    public void onReconnect(@NotNull SessionResumeEvent event) {
        LOGGER.info("Reconnected successfully. RN: {}", event.getResponseNumber());
    }

    public void addListener(@NotNull Object listener) {
        if (!isReady()) {
            throw new IllegalStateException("Cannot add listeners before the bot is ready!");
        }
        shardManager.addEventListener(listener);
    }

    public void removeListener(@NotNull Object listener) {
        if (!isReady()) {
            throw new IllegalStateException("Cannot remove listeners before the bot is ready!");
        }
        shardManager.removeEventListener(listener);
    }

    private void initActivities(@NotNull BotBuilder builder, @NotNull DefaultShardManagerBuilder shardManagerBuilder) {
        if (builder.getActivities().isEmpty()) {
            LOGGER.debug("No activities provided, skipping activity setup...");
            return;
        }

        if (builder.getActivities().size() == 1) {
            shardManagerBuilder.setActivity(builder.getActivities().get(0).get());
        } else {

            AtomicInteger index = new AtomicInteger();

            try {

                this.scheduler.scheduleAtFixedRate(() -> {
                    shardManagerBuilder.setActivity(builder.getActivities().get(index.getAndIncrement()).get());

                    if (index.get() >= builder.getActivities().size()) {
                        index.set(0);
                    }
                }, 0, builder.getActivityUpdateRate(), TimeUnit.SECONDS);

            } catch (Throwable e) {
                LOGGER.error("Failed to schedule activity updates!", e);
                shardManagerBuilder.setActivity(builder.getActivities().get(0).get());
            }
        }
    }

    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (this.commandManager == null) return;
        this.commandManager.handleCommand(event);
    }

    @SubscribeEvent
    public void onMessage(@NotNull MessageReceivedEvent event) {
        if (this.commandManager == null) return;
        this.commandManager.handlePrefixCommand(event);
    }

}
