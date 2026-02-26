package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.ModalTopLevelComponent
import net.dv8tion.jda.api.components.label.LabelChildComponent
import net.dv8tion.jda.api.modals.Modal

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class ModalBuilder {

    val components: MutableList<ModalTopLevelComponent> = mutableListOf()
    var id: String? = null
    var title: String? = null

    inline fun label(
        text: String? = null,
        uniqueId: Int = -1,
        description: String? = null,
        child: LabelChildComponent? = null,
        block: LabelBuilder.() -> Unit = {}
    ): ModalBuilder {
        components += Label(text, uniqueId, description, child, block)
        return this
    }

    inline fun text(
        text: String,
        uniqueId: Int = -1,
        block: TextDisplayBuilder.() -> Unit = {}
    ): ModalBuilder {
        components += TextDisplay(text, uniqueId, block)
        return this
    }

    fun build(): Modal {
        id ?: throw IllegalStateException("Modal must have an id")
        title ?: throw IllegalStateException("Modal must have a title")

        return Modal.create(id!!, title!!)
            .addComponents(components)
            .build()
    }

}

inline fun Modal(
    id: String? = null,
    title: String? = null,
    block: ModalBuilder.() -> Unit
): Modal = ModalBuilder().apply {
    if (id != null) this.id = id
    if (title != null) this.title = title
    block()
}.build()

