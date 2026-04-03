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
import java.util.Collections;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ImmutableBotSettings implements BotSettings {

    private final String token;
    private final Collection<GatewayIntent> intents;
    private final boolean autoLogin;
    private final Set<Long> owners;
    private final Set<Long> guilds;
    private final int shards;
    private final IntFunction<OnlineStatus> onlineStatus;
    private final IntFunction<Activity> activity;
    private final IntFunction<? extends IEventManager> eventManager;
    private final EventWaiter eventWaiter;
    private final boolean autoCommitCommands;
    private final CommandManager commandManager;

    public ImmutableBotSettings(@NotNull BotSettings settings) {
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

    public ImmutableBotSettings(
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
        this.autoLogin = autoLogin;
        this.owners = Collections.unmodifiableSet(owners);
        this.guilds = guilds != null ? Collections.unmodifiableSet(guilds) : null;
        this.shards = shards;
        this.onlineStatus = onlineStatus;
        this.activity = activity;
        this.eventManager = eventManager;
        this.eventWaiter = eventWaiter;
        this.autoCommitCommands = autoCommitCommands;
        this.commandManager = commandManager;
    }

    @Override
    @NotNull
    public String token() {
        return this.token;
    }

    @Override
    @NotNull
    public Collection<GatewayIntent> intents() {
        return this.intents;
    }

    @Override
    public boolean autoLogin() {
        return this.autoLogin;
    }

    @Override
    public @Nullable Set<Long> guilds() {
        return this.guilds;
    }

    @Override
    @NotNull
    public Set<Long> owners() {
        return this.owners;
    }

    @Override
    public int shards() {
        return this.shards;
    }

    @Override
    @NotNull
    public IntFunction<OnlineStatus> onlineStatus() {
        return this.onlineStatus;
    }

    @Override
    @NotNull
    public IntFunction<Activity> activity() {
        return this.activity;
    }

    @Override
    @NotNull
    public IntFunction<? extends IEventManager> eventManager() {
        return this.eventManager;
    }

    @Override
    @NotNull
    public EventWaiter eventWaiter() {
        return this.eventWaiter;
    }

    @Override
    public boolean autoCommitCommands() {
        return this.autoCommitCommands;
    }

    @Override
    @Nullable
    public CommandManager commandManager() {
        return this.commandManager;
    }

}
