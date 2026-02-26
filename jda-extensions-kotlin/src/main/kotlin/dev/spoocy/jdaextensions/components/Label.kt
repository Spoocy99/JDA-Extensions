package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.label.LabelChildComponent

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class LabelBuilder : ComponentBuilder<Label> {

    var text: String? = null
    var child: LabelChildComponent? = null
    override var uniqueId: Int = -1
    var description: String? = null

    override fun build(): Label {
        text ?: throw IllegalStateException("Label must have text")
        child ?: throw IllegalStateException("Label must have a child component")

        return Label.of(text!!, child!!)
            .let { if (uniqueId > 0) it.withUniqueId(uniqueId) else it }
            .let { description?.let { desc -> it.withDescription(desc) } ?: it }
    }

}

inline fun Label(
    text: String? = null,
    uniqueId: Int = -1,
    description: String? = null,
    child: LabelChildComponent? = null,
    block: LabelBuilder.() -> Unit
): Label = LabelBuilder().apply {
    if (text != null) this.text = text
    if (uniqueId > 0) this.uniqueId = uniqueId
    if (description != null) this.description = description
    if (child != null) this.child = child
    block()
}.build()