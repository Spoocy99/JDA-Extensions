package dev.spoocy.jdaextensions.coroutines

import kotlinx.coroutines.future.await
import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.concurrent.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

suspend fun <T> RestAction<T>.await(): T = submit().await()

suspend fun <T> Task<T>.await() = suspendCancellableCoroutine<T> {
    it.invokeOnCancellation { cancel() }
    onSuccess { r -> it.resume(r) }
    onError { e -> it.resumeWithException(e) }
}