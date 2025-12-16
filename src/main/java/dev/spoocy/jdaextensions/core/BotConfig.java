package dev.spoocy.jdaextensions.core;

import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.LogLevel;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.documents.JsonConfig;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BotConfig implements BotSettings {

    private final HashMap<String, Object> defaults = new HashMap<>();
    private final Document file;

    public BotConfig(@NotNull File jsonFile) {
        this.file = Document.readFile(JsonConfig.class, jsonFile);
        this.loadDefaults();
        this.load();
    }

    public BotConfig(@NotNull Document config) {
        this.file = config;
        this.loadDefaults();
        this.load();
    }

    protected void loadDefaults() {
        setDefault("log-level", "INFO");
        setDefault("token", "DISCORD_BOT_TOKEN");
        setDefault("shards", 1);
        setDefault("online-status", "online");
        setDefault("owner", Collector.of(0L).asList());
    }

    protected void load() {
        for (String key : defaults.keySet()) {
            if (!file.isSet(key)) {
                file.set(key, getDefault(key));
            }
        }
        file.saveSafely();
    }

    public BotConfig setDefault(@NotNull String key, @Nullable Object value) {
        defaults.put(key, value);
        return this;
    }

    public Object getDefault(@NotNull String key) {
        return defaults.get(key);
    }

    @Override
    public List<Long> getOwners() {
        return file.getList("owner", long.class, new ArrayList<>());
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.byName(file.getString("log-level"));
    }

    @Override
    public String getToken() {
        return file.getString("token");
    }

    @Override
    public int getShards() {
        return file.getInt("shards");
    }

    @Override
    public OnlineStatus getOnlineStatus() {
        return OnlineStatus.fromKey(file.getString("online-status"));
    }

    @NotNull
    public Document getDocument() {
        return this.file;
    }

    @Override
    public String toString() {
        return "BotConfig{" +
                "defaults=" + defaults.size() +
                ", file=" + file.getPath() +
                '}';
    }
}
