package dev.spoocy.jdaextensions.core;

import dev.spoocy.jdaextensions.commands.manager.CommandManager;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import lombok.Getter;
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

	private final List<Consumer<? super DefaultShardManagerBuilder>> builder;
	private final List<Consumer<? super ShardManager>> manager;
	private final List<Consumer<? super JDA>> shards;

    @Getter
    private final List<DiscordCommand> commands;
    @Getter
    private final List<Class<?>> commandAnnotationClasses;
    @Getter
    private final List<Object> listeners;
    @Getter
    private final List<Supplier<? extends Activity>> activities;
    @Getter
    private int activityUpdateRate;
    @Getter
    private CommandManager commandManager;
    @Getter
    protected MemberCachePolicy memberCachePolicy;
    @Getter
    private EnumSet<CacheFlag> cacheFlags;
    @Getter
    private List<GatewayIntent> intents;

    public BotBuilder() {
        this.builder = new ArrayList<>();
        this.manager = new ArrayList<>();
        this.shards = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.commandAnnotationClasses = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.activities = new ArrayList<>();
        this.activityUpdateRate = -1;

        this.memberCachePolicy = MemberCachePolicy.DEFAULT;
        this.cacheFlags = EnumSet.allOf(CacheFlag.class);
        this.intents = new ArrayList<>();
    }

    public List<Consumer<? super DefaultShardManagerBuilder>> getBuilderActions() {
        return builder;
    }

    public List<Consumer<? super ShardManager>> getManagerActions() {
        return manager;
    }

    public List<Consumer<? super JDA>> getShardActions() {
        return shards;
    }

    public BotBuilder forShardManagerBuilder(@NotNull Consumer<? super DefaultShardManagerBuilder> action) {
		builder.add(action);
		return this;
	}

    public BotBuilder forShardManager(@NotNull Consumer<? super ShardManager> action) {
        manager.add(action);
        return this;
    }

    public BotBuilder forShards(@NotNull Consumer<? super JDA> action) {
        shards.add(action);
        return this;
    }

    public BotBuilder addCommand(@NotNull DiscordCommand command) {
        this.commands.add(command);
        return this;
    }

    public BotBuilder addCommand(@NotNull Class<?> annotatedClass) {
        this.commandAnnotationClasses.add(annotatedClass);
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

    public void setCommandManager(@NotNull CommandManager commandManager) {
        this.commandManager = commandManager;
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
                " - [I] Commands:   " + formatList(commands, true) + "\n" +
                " - [A] Commands:   " + formatList(commandAnnotationClasses, true) + "\n" +
                " - Listeners:      " + formatList(listeners, true) + "\n" +
                " - Activities:     " + activities.size() + "\n" +
                " - Intents:        " + formatList(intents, false) + "\n" +
                " - CacheFlags:     " + formatList(cacheFlags, false) + "\n";
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
                ", builder=" + builder +
                ", manager=" + manager +
                ", shards=" + shards +
                ", commands=" + commands +
                ", listeners=" + listeners +
                ", activityUpdateRate=" + activityUpdateRate +
                ", memberCachePolicy=" + memberCachePolicy +
                ", cacheFlags=" + cacheFlags +
                ", intents=" + intents +
                '}';
    }
}
