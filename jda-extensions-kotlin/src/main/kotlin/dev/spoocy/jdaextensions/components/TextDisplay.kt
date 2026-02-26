package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.textdisplay.TextDisplay

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class TextDisplayBuilder : ComponentBuilder<TextDisplay> {

    override var uniqueId: Int = -1
    var text: String? = null

    override fun build(): TextDisplay {
        text ?: throw IllegalStateException("TextDisplay must have text")
        return TextDisplay.of(text!!)
            .let { if (uniqueId > 0) it.withUniqueId(uniqueId) else it }
    }

}

inline fun TextDisplay(
    text: String? = null,
    uniqueId: Int = -1,
    block: TextDisplayBuilder.() -> Unit = {}
): TextDisplay = TextDisplayBuilder().apply {
    if (text != null) this.text = text
    if (uniqueId > 0) this.uniqueId = uniqueId
    block()
}.build()