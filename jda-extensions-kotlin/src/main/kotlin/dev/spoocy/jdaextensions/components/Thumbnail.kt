package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.utils.FileUpload

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class ThumbnailBuilder(
    private var thumbnail: Thumbnail,
) : ComponentBuilder<Thumbnail> {

    override var uniqueId: Int
        get() = thumbnail.uniqueId
        set(value) {
            thumbnail = value?.let { thumbnail.withUniqueId(it) } ?: thumbnail
        }

    var description: String?
        get() = thumbnail.description
        set(value) {
            thumbnail = thumbnail.withDescription(value)
        }

    var spoiler: Boolean
        get() = thumbnail.isSpoiler
        set(value) {
            thumbnail = thumbnail.withSpoiler(value)
        }

    override fun build(): Thumbnail = thumbnail
}

inline fun Thumbnail(
    thumbnail: Thumbnail,
    uniqueId: Int = -1,
    description: String? = null,
    spoiler: Boolean = false,
    block: ThumbnailBuilder.() -> Unit = {}
): Thumbnail = ThumbnailBuilder(thumbnail).apply {
    if (uniqueId > 0) this.uniqueId = uniqueId
    if (description != null) this.description = description

    this.spoiler = spoiler
    block()
}.build()

inline fun Thumbnail(
    url: String,
    uniqueId: Int = -1,
    description: String? = null,
    spoiler: Boolean = false,
    block: ThumbnailBuilder.() -> Unit = {}
): Thumbnail = Thumbnail(Thumbnail.fromUrl(url), uniqueId, description, spoiler, block)

inline fun Thumbnail(
    file: FileUpload,
    uniqueId: Int = -1,
    description: String? = null,
    spoiler: Boolean = false,
    block: ThumbnailBuilder.() -> Unit = {}
): Thumbnail = Thumbnail(Thumbnail.fromFile(file), uniqueId, description, spoiler, block)