package dev.spoocy.jdaextensions.commands.tree;

import dev.spoocy.jdaextensions.commands.arguments.impl.AbstractArgument;
import dev.spoocy.jdaextensions.commands.cooldown.CooldownScope;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.permission.CommandPermission;
import dev.spoocy.jdaextensions.commands.structure.CommandNodeHolder;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandNodeData;
import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static net.dv8tion.jda.api.interactions.commands.build.CommandData.MAX_NAME_LENGTH;
import static net.dv8tion.jda.api.interactions.commands.build.CommandData.MAX_DESCRIPTION_LENGTH;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractCommandTree<Impl extends AbstractCommandTree<Impl>> implements Chainable<Impl> {

    protected String name;
    protected String description;
    protected CommandPermission[] permissions = new CommandPermission[0];
    protected boolean async = false;
    protected boolean sendTyping = false;
    protected boolean acknowledge = true;
    protected boolean ephemeral = false;
    protected List<AbstractArgument> arguments = new ArrayList<>();
    protected CooldownScope cooldown = null;
    protected Duration cooldownDuration = Duration.ZERO;
    protected Consumer<CommandContext> executor;

    public AbstractCommandTree(@NotNull String name,
                               @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public Impl withName(@NotNull String name) {
        this.name = name;
        return instance();
    }

    public Impl withDescription(@NotNull String description) {
        this.description = description;
        return instance();
    }

    public Impl withPermissions(@NotNull CommandPermission... permissions) {
        this.permissions = permissions;
        return instance();
    }

    public Impl async(boolean async) {
        this.async = async;
        return instance();
    }

    public Impl typing(boolean sendTyping) {
        this.sendTyping = sendTyping;
        return instance();
    }

    /**
     * Sets whether the command should be acknowledged automatically upon execution.
     * <p>
     * This should only be disabled if you plan acknowledge the command using a {@link net.dv8tion.jda.api.modals.Modal}.
     *
     * @param acknowledge
     *          true to acknowledge the command automatically, false to disable it
     *
     * @return the current instance for chaining
     */
    public Impl acknowledge(boolean acknowledge) {
        this.acknowledge = acknowledge;
        return instance();
    }

    public Impl ephemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
        return instance();
    }

    public Impl arg(@NotNull AbstractArgument argument) {
        this.arguments.add(argument);
        return instance();
    }

    public Impl withCooldown(@NotNull CooldownScope scope, long duration, @NotNull TimeUnit unit) {
        this.cooldownDuration = Duration.ofMillis(unit.toMillis(duration));
        this.cooldown = scope;
        return instance();
    }

    public Impl withCooldown(@NotNull CooldownScope scope, @NotNull Duration duration) {
        this.cooldownDuration = duration;
        this.cooldown = scope;
        return instance();
    }

    public Impl executes(@NotNull Consumer<CommandContext> executor) {
        this.executor = executor;
        return instance();
    }

    protected void validate() {
        Objects.requireNonNull(this.name, "Command name cannot be null!");
        Objects.requireNonNull(this.description, "Command description cannot be null!");
        Objects.requireNonNull(this.permissions, "Command permissions cannot be null!");
        Objects.requireNonNull(this.executor, "Command executor cannot be null!");
        if(StringUtils.isNullOrEmpty(this.name) || this.name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Command name must be between 1 and " + MAX_NAME_LENGTH + " characters!");
        }
        if(StringUtils.isNullOrEmpty(this.description) || this.description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Command description must be between 1 and " + MAX_DESCRIPTION_LENGTH + " characters!");
        }
    }

    @NotNull
    protected CommandNodeData buildNodeData(@NotNull CommandNodeHolder parent) {
        validate();

        dev.spoocy.jdaextensions.commands.cooldown.Cooldown cooldown =
                dev.spoocy.jdaextensions.commands.cooldown.Cooldown.NONE;

        return new CommandNodeData(
                parent,
                this.name,
                this.description,
                this.permissions,
                this.async,
                this.sendTyping,
                this.acknowledge,
                this.ephemeral,
                this.arguments,
                cooldown,
                this.executor
        );
    }

}
