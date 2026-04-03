package dev.spoocy.jdaextensions.core;

import dev.spoocy.jdaextensions.event.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class ShardedDiscordBot extends DiscordBot {

    private ShardManager shardManager;
    private int expectedShardCount;

    public ShardedDiscordBot(@NotNull BotSettings settings) {
        super(settings);
    }

    @Override
    protected void prepareLogin(
            int shards,
            @NotNull String token,
            @NotNull Collection<GatewayIntent> intents,
            @NotNull IntFunction<OnlineStatus> onlineStatus,
            @NotNull IntFunction<Activity> activity,
            @NotNull IntFunction<? extends IEventManager> eventManager
    ) {
        this.expectedShardCount = shards;

        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.createDefault(token, intents)
                .setShardsTotal(shards)
                .setEventManagerProvider(eventManager)
                .addEventListeners(this)
                .setStatusProvider(onlineStatus)
                .setActivityProvider(activity)
                .setAutoReconnect(true);

        this.configure(shardManagerBuilder);

        this.shardManager = shardManagerBuilder.build();
    }

    protected abstract void configure(@NotNull DefaultShardManagerBuilder builder);

    @Override
    public void registerListener(@NotNull Object listener) {
        getShardManager().addEventListener(listener);
    }

    @Override
    public void unregisterListener(@NotNull Object listener) {
        getShardManager().removeEventListener(listener);
    }

    @Override
    public int getShardCount() {
        return this.shardManager != null ? this.shardManager.getShardsTotal() : expectedShardCount;
    }

    /**
     * Gets the {@link ShardManager} instance managing the shards of this bot.
     *
     * @return the ShardManager instance managing the shards of this bot.
     *
     * @throws IllegalStateException if the bot has not been started yet.
     */
    @NotNull
    public ShardManager getShardManager() {
        if (this.shardManager == null) {
            throw new IllegalStateException("The bot has not been started yet!");
        }
        return this.shardManager;
    }

    /**
     * Gets a list of all {@link JDA} instances representing the shards of this bot.
     *
     * @return a list of all JDA instances representing the shards of this bot.
     *
     * @throws IllegalStateException if the bot has not been started yet.
     */
    @NotNull
    public List<JDA> getShards() {
        if (this.shardManager == null) {
            throw new IllegalStateException("The bot has not been started yet!");
        }
        return this.shardManager.getShards();
    }

    /**
     * Executes the given action for each shard of this bot.
     *
     * @param action the action to execute for each shard
     *
     * @throws IllegalStateException if the bot has not been started yet.
     */
    public void onAllShards(@NotNull Consumer<JDA> action) {
        for (JDA shard : getShards()) {
            action.accept(shard);
        }
    }

    /**
     * Gets the {@link User} representation of the bot itself.
     *
     * @param shard the shard to get the self user from
     *
     * @return the self user
     */
    public User getSelfUser(@NotNull JDA shard) {
        return shard.getSelfUser();
    }

    /**
     * Gets the {@link Member} representation of the bot itself in a specific guild.
     *
     * @param guild the guild to get the self member from
     *
     * @return the self member in the specified guild
     *
     * @throws IllegalStateException if the bot is not a member of the specified guild
     */
    public Member getSelfMember(@NotNull Guild guild) {
        return guild.getMember(getSelfUser(guild.getJDA()));
    }

    /**
     * Updates the commands of all shards of this bot.
     *
     * @throws IllegalStateException if the bot has not been started yet.
     */
    public void updateCommands() {
        for (JDA shard : getShards()) {
            shard.updateCommands().queue();
        }
    }

}
