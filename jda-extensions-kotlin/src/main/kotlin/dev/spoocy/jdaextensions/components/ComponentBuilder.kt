package dev.spoocy.jdaextensions.components

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

interface ComponentBuilder<C> {

    var uniqueId: Int

    fun build(): C

}