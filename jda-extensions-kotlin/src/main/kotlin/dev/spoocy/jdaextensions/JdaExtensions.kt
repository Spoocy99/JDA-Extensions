package dev.spoocy.jdaextensions

import dev.spoocy.jdaextensions.commands.manager.impl.DefaultCommandManager
import net.dv8tion.jda.api.requests.restaction.CacheRestAction

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

inline fun <reified C> DefaultCommandManager.Builder.register(): DefaultCommandManager.Builder {
    return this.registerCommand(C::class.java)
}

inline fun <T> CacheRestAction<T>.queue(crossinline onSuccess: (T) -> Unit, crossinline onFailure: (Throwable) -> Unit = {}) {
    this.queue({ onSuccess(it) }, { onFailure(it) })
}