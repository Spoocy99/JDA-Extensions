package dev.spoocy.jdaextensions.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.IntFunction;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class SingleShardDiscordBot extends DiscordBot {

    private JDA shard;

    public SingleShardDiscordBot(@NotNull BotSettings settings) {
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

        if (shards != 1) {
            LOGGER.warn("The provided shard count is not 1, but SingleShardDiscordBot only supports a single shard. Did you mean to use ShardedDiscordBot instead?");
        }

        JDABuilder builder = JDABuilder.createDefault(token)
                .setEnabledIntents(intents)
                .setEventManager(eventManager.apply(0))
                .setStatus(onlineStatus.apply(0))
                .setActivity(activity.apply(0))
                .setAutoReconnect(true);

        this.configure(builder);

        this.shard = builder.build();

    }

    protected abstract void configure(@NotNull JDABuilder builder);

    @Override
    public int getShardCount() {
        return 1;
    }

    @Override
    public boolean isReady() {
        return this.shard != null && this.shard.getStatus() == JDA.Status.CONNECTED;
    }

    @Override
    public void registerListener(@NotNull Object listener) {
        this.shard.addEventListener(listener);
    }

    @Override
    public void unregisterListener(@NotNull Object listener) {
        this.shard.removeEventListener(listener);
    }

    /**
     * Gets the {@link JDA} instance of this bot.
     *
     * @return the JDA instance of this bot.
     *
     * @throws IllegalStateException if the bot has not been started yet.
     */
    @NotNull
    public JDA getJDA() {
        if (this.shard == null) {
            throw new IllegalStateException("The bot has not been started yet!");
        }
        return this.shard;
    }

    /**
     * Gets the {@link User} representation of the bot itself.
     *
     * @return the self user
     */
    public User getSelfUser() {
        return getJDA().getSelfUser();
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
        return guild.getMember(getSelfUser());
    }

    /**
     * Updates the commands of all shards of this bot.
     *
     * @throws IllegalStateException if the bot has not been started yet.
     */
    public void updateCommands() {
        getJDA().updateCommands()
                .queue();
    }

}
