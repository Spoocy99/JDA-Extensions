package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.mediagallery.MediaGallery
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem
import net.dv8tion.jda.api.utils.FileUpload

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class MediaGalleryBuilder : ComponentBuilder<MediaGallery> {

    val content: MutableList<MediaGalleryItem> = mutableListOf()
    override var uniqueId: Int = -1

    fun fromUrl(url: String) : MediaGalleryBuilder {
        content += MediaGalleryItem.fromUrl(url)
        return this
    }

    fun fromFile(file: FileUpload) : MediaGalleryBuilder {
        content += MediaGalleryItem.fromFile(file)
        return this
    }

    override fun build(): MediaGallery = MediaGallery.of(content)
        .let { if (uniqueId > 0) it.withUniqueId(uniqueId) else it }

}

inline fun MediaGallery(
    uniqueId: Int? = -1,
    block: MediaGalleryBuilder.() -> Unit
): MediaGallery =
    MediaGalleryBuilder().apply {
        if (uniqueId != null) this.uniqueId = uniqueId
        block()
    }.build()