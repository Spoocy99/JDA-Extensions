package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.container.ContainerChildComponent
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.utils.FileUpload
import java.awt.Color

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

private val SEPARATOR_LARGE = Separator.create(false, Separator.Spacing.LARGE)
private val SEPARATOR_SMALL = Separator.create(false, Separator.Spacing.SMALL)
private val DIVIDER_LARGE = Separator.create(true, Separator.Spacing.LARGE)
private val DIVIDER_SMALL = Separator.create(true, Separator.Spacing.SMALL)

class ContainerBuilder : ComponentBuilder<Container> {

    val components: MutableList<ContainerChildComponent> = mutableListOf()
    override var uniqueId: Int = -1
    var color: Color? = null
    var spoiler: Boolean = false

    var rgb: Int
        get() = color?.rgb ?: 0
        set(value) {
            color = Color(value)
        }

    fun separatorLarge(): ContainerBuilder {
        components += SEPARATOR_LARGE
        return this
    }

    fun separatorSmall(): ContainerBuilder {
        components += SEPARATOR_SMALL
        return this
    }

    fun dividerLarge(): ContainerBuilder {
        components += DIVIDER_LARGE
        return this
    }

    fun dividerSmall(): ContainerBuilder {
        components += DIVIDER_SMALL
        return this
    }

    inline fun actionRow(
        uniqueId: Int = -1,
        block: ActionRowBuilder.() -> Unit,
    ): ContainerBuilder {
        components += ActionRow(uniqueId, block)
        return this
    }

    inline fun section(
        uniqueId: Int = -1,
        block: SectionBuilder.() -> Unit
    ): ContainerBuilder {
        components += Section(uniqueId, block)
        return this
    }

    inline fun gallery(
        uniqueId: Int = -1,
        block: MediaGalleryBuilder.() -> Unit
    ): ContainerBuilder {
        components += MediaGallery(uniqueId, block)
        return this
    }

    inline fun file(
        file: FileUpload,
        uniqueId: Int = -1,
        spoiler: Boolean = false,
        block: FileDisplayBuilder.() -> Unit = {}
    ): ContainerBuilder {
        components += FileDisplay(file, uniqueId, spoiler, block)
        return this
    }

    inline fun file(
        fileName: String,
        uniqueId: Int = -1,
        spoiler: Boolean = false,
        block: FileDisplayBuilder.() -> Unit = {}
    ): ContainerBuilder {
        components += FileDisplay(fileName, uniqueId, spoiler, block)
        return this
    }

    inline fun text(
        text: String,
        uniqueId: Int = -1,
        block: TextDisplayBuilder.() -> Unit = {}
    ): ContainerBuilder {
        components += TextDisplay(text, uniqueId, block)
        return this
    }

    override fun build(): Container = Container.of(components)
        .let { if (uniqueId > 0) it.withUniqueId(uniqueId) else it }
        .let { if (color != null) it.withAccentColor(color) else it }
        .let { if (spoiler) it.withSpoiler(true) else it }
}

inline fun Container(
    uniqueId: Int = -1,
    color: Color? = null,
    spoiler: Boolean = false,
    block: ContainerBuilder.() -> Unit
): Container =
    ContainerBuilder().apply {
        if (uniqueId > 0) this.uniqueId = uniqueId
        if (color != null) this.color = color
        this.spoiler = spoiler
        block()
    }.build()
