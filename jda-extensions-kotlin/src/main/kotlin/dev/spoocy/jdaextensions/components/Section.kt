package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent
import net.dv8tion.jda.api.components.section.SectionContentComponent
import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.utils.FileUpload

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class SectionBuilder : ComponentBuilder<Section> {

    val content: MutableList<SectionContentComponent> = mutableListOf()
    var accessory: SectionAccessoryComponent? = null
    override var uniqueId: Int = -1

    fun thumbnail(url: String): SectionBuilder {
        accessory = Thumbnail.fromUrl(url)
        return this
    }

    fun thumbnail(file: FileUpload): SectionBuilder {
        accessory = Thumbnail.fromFile(file)
        return this
    }

    inline fun text(block: TextDisplayBuilder.() -> Unit): SectionBuilder {
        content += TextDisplayBuilder().apply(block).build()
        return this
    }

    inline fun text(
        text: String,
        uniqueId: Int = -1,
        block: TextDisplayBuilder.() -> Unit = {}
    ): SectionBuilder {
        content += TextDisplay(text, uniqueId, block)
        return this
    }


    override fun build(): Section {
        accessory ?: throw IllegalStateException("Section must have an accessory component")
        return Section.of(accessory!!, content)
            .let { if (uniqueId > 0) it.withUniqueId(uniqueId) else it }
    }

}

inline fun Section(
    uniqueId: Int = -1,
    block: SectionBuilder.() -> Unit
): Section = SectionBuilder().apply {
    if (uniqueId > 0) this.uniqueId = uniqueId
    block()
}.build()