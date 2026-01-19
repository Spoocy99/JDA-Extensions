package dev.spoocy.jdaextensions

import dev.spoocy.jdaextensions.commands.manager.impl.DefaultCommandManager
import dev.spoocy.jdaextensions.commands.manager.impl.KotlinAnnotationProcessor
import kotlin.reflect.KClass

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

fun DefaultCommandManager.Builder.kotlinAnnotationProcessing(): DefaultCommandManager.Builder {
    return this.annotationProcessor(KotlinAnnotationProcessor())
}

inline fun <reified C> DefaultCommandManager.Builder.register(): DefaultCommandManager.Builder {
    return this.register(C::class.java)
}