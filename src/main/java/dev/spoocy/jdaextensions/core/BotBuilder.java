package dev.spoocy.jdaextensions.core;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BotBuilder {

    private static final ILogger LOGGER = ILogger.forThisClass();

	protected final List<Consumer<? super DefaultShardManagerBuilder>> builderActions;
	protected final List<Consumer<? super ShardManager>> shardManActions;
	protected final List<Consumer<? super JDA>> shardActions;
    protected final List<Object> listeners;
    protected final List<Supplier<? extends Activity>> activities;
    protected int activityUpdateRate;
    protected CommandManager commandManager;
    protected MemberCachePolicy memberCachePolicy;
    protected EnumSet<CacheFlag> cacheFlags;
    protected List<GatewayIntent> intents;

    public BotBuilder() {
        this.builderActions = new ArrayList<>();
        this.shardManActions = new ArrayList<>();
        this.shardActions = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.activities = new ArrayList<>();
        this.activityUpdateRate = -1;

        this.memberCachePolicy = MemberCachePolicy.DEFAULT;
        this.cacheFlags = EnumSet.allOf(CacheFlag.class);
        this.intents = new ArrayList<>();
    }

    public BotBuilder forShardManagerBuilder(@NotNull Consumer<? super DefaultShardManagerBuilder> action) {
		builderActions.add(action);
		return this;
	}

    public BotBuilder forShardManager(@NotNull Consumer<? super ShardManager> action) {
        shardManActions.add(action);
        return this;
    }

    public BotBuilder forShards(@NotNull Consumer<? super JDA> action) {
        shardActions.add(action);
        return this;
    }

    public BotBuilder addListener(@NotNull Object listener) {
        this.listeners.add(listener);
        return this;
    }

    public BotBuilder addActivity(@NotNull Supplier<? extends Activity> activity) {
        this.activities.add(activity);
        return this;
    }

    public BotBuilder setActivityUpdateRate(int seconds) {
        if (seconds < 1) {
            throw new IllegalArgumentException("Activity update rate must be at least 1 second.");
        }
        this.activityUpdateRate = seconds;
        return this;
    }

    public BotBuilder setMemberCachePolicy(@NotNull MemberCachePolicy policy) {
        this.memberCachePolicy = policy;
        return this;
    }

    public BotBuilder setCacheFlags(@NotNull CacheFlag... flags) {
        this.cacheFlags = EnumSet.copyOf(Collector.of(flags).asList());
        return this;
    }

    public BotBuilder setIntents(@NotNull GatewayIntent... intents) {
        this.intents = Collector.of(intents).asList();
        return this;
    }

    public BotBuilder setAllIntents() {
        LOGGER.warn("All intents enabled.");
		return setIntents(GatewayIntent.values());
	}

    public BotBuilder setCommandManager(@NotNull CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    public void validate() {
        if (intents.isEmpty()) {
			LOGGER.info("Intents not defined. Using all...");
			setAllIntents();
		}

		if (!intents.contains(GatewayIntent.DIRECT_MESSAGES)) {
            LOGGER.warn("Missing GatewayIntent 'DIRECT_MESSAGES'. No direct messages can be read!");
        }

		if (!intents.contains(GatewayIntent.GUILD_MESSAGES)) {
            LOGGER.warn("Missing GatewayIntent 'GUILD_MESSAGES'. No guild messages can be read!");
        }

        if (activityUpdateRate < 1 && activities.size() >= 2) {
            LOGGER.warn("Activity update rate not defined. Using default (15 seconds).");
            setActivityUpdateRate(15);
        }
    }

    public String formatInstanceInfo() {
        return "BotBuilder Configuration \n" +
                " - Command Manager:    " + (this.commandManager == null ? "NONE" : this.commandManager.getClass().getSimpleName() + " (" + commandManager.getCommands().size() + " commands)") + "\n" +
                " - Listeners:          " + formatList(listeners, true) + "\n" +
                " - Activities:         " + activities.size() + "\n" +
                " - Intents:            " + formatList(intents, false) + "\n" +
                " - CacheFlags:         " + formatList(cacheFlags, false) + "\n";
    }

    private String formatList(@NotNull Collection<?> list, boolean useClassNames) {
        if (list.isEmpty()) {
            return "None";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {

            Object obj = list.toArray()[i];

            if (useClassNames) {

                if (obj instanceof Class<?>) {
                    sb.append(((Class<?>) obj).getSimpleName());
                } else {
                    sb.append(obj.getClass().getSimpleName());
                }

            } else {
                sb.append(obj.toString());
            }

            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "BotBuilder{" +
                "activities=" + activities +
                ", builder=" + builderActions +
                ", manager=" + shardManActions +
                ", shards=" + shardActions +
                ", commands=" + commandManager +
                ", listeners=" + listeners +
                ", activityUpdateRate=" + activityUpdateRate +
                ", memberCachePolicy=" + memberCachePolicy +
                ", cacheFlags=" + cacheFlags +
                ", intents=" + intents +
                '}';
    }
}
