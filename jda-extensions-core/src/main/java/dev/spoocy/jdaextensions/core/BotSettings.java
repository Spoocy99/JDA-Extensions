package dev.spoocy.jdaextensions.core;

import dev.spoocy.utils.common.log.LogLevel;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface BotSettings {

    static Builder builder() {
        return new Builder();
    }

    List<Long> owners();

    LogLevel logLevel();

    String token();

    int shards();

    OnlineStatus onlineStatus();

    class Builder implements BotSettings {

        private Collection<Long> owners = new ArrayList<>();
        private LogLevel logLevel = LogLevel.INFO;
        private String token = "DISCORD_BOT_TOKEN";
        private int shards = 1;
        private OnlineStatus onlineStatus = OnlineStatus.ONLINE;

        public Builder owners(@NotNull Collection<Long> ownerIds) {
            this.owners = ownerIds;
            return this;
        }

        public Builder owners(long @NotNull ... ownerIds) {
            for (long ownerId : ownerIds) {
                this.owners.add(ownerId);
            }
            return this;
        }

        public Builder addOwner(long ownerId) {
            this.owners.add(ownerId);
            return this;
        }

        public Builder logLevel(@NotNull LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder shards(int shards) {
            this.shards = shards;
            return this;
        }

        public Builder onlineStatus(@NotNull OnlineStatus onlineStatus) {
            this.onlineStatus = onlineStatus;
            return this;
        }

        @Override
        public List<Long> owners() {
            return List.copyOf(this.owners);
        }

        @Override
        public LogLevel logLevel() {
            return this.logLevel;
        }

        @Override
        public String token() {
            return this.token;
        }

        @Override
        public int shards() {
            return this.shards;
        }

        @Override
        public OnlineStatus onlineStatus() {
            return this.onlineStatus;
        }
    }

}
