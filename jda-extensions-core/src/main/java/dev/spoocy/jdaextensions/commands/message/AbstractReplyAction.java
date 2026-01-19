package dev.spoocy.jdaextensions.commands.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractReplyAction<T extends RestAction<R>, R> implements ReplyAction {

    protected final T action;

	public AbstractReplyAction(@NotNull T action) {
		this.action = action;
	}

	@NotNull
    @Override
    public ReplyAction setCheck(@Nullable BooleanSupplier checks) {
        this.action.setCheck(checks);
        return this;
    }

    @NotNull
    @Override
    public ReplyAction deadline(long timestamp) {
        this.action.deadline(timestamp);
        return this;
    }

	@NotNull
	@Override
	public JDA getJDA() {
		return action.getJDA();
	}

	@Override
	public void queue(@Nullable Consumer<? super Message> success, @Nullable Consumer<? super Throwable> failure) {
		if (success == null) {
			action.queue(null, failure);
			return;
		}
		action.queue(r -> success.accept(getMapper().apply(r)), failure);
	}

	@Override
	public Message complete(boolean shouldQueue) throws RateLimitedException {
		R result = action.complete(shouldQueue);
		return result == null ? null : getMapper().apply(result);
	}

	@Override
	public @NotNull CompletableFuture<Message> submit(boolean shouldQueue) {
		return action.submit(shouldQueue).thenApply(getMapper());
	}

	@NotNull
	public abstract Function<R, Message> getMapper();

}
