package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.filedisplay.FileDisplay
import net.dv8tion.jda.api.utils.FileUpload

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class FileDisplayBuilder(
    private var fileDisplay: FileDisplay,
) : ComponentBuilder<FileDisplay> {

    override var uniqueId: Int
        get() = fileDisplay.uniqueId
        set(value) {
            fileDisplay = fileDisplay.withUniqueId(value)
        }

    var spoiler: Boolean
        get() = fileDisplay.isSpoiler
        set(value) {
            fileDisplay = fileDisplay.withSpoiler(value)
        }

    override fun build(): FileDisplay = fileDisplay
}

inline fun FileDisplay(
    fileDisplay: FileDisplay,
    uniqueId: Int = -1,
    spoiler: Boolean = false,
    block: FileDisplayBuilder.() -> Unit = {}
): FileDisplay =
    FileDisplayBuilder(fileDisplay).apply {
        if (uniqueId > 0) this.uniqueId = uniqueId
        this.spoiler = spoiler
        block()
    }.build()

inline fun FileDisplay(
    file: FileUpload,
    uniqueId: Int = -1,
    spoiler: Boolean = false,
    block: FileDisplayBuilder.() -> Unit = {}
): FileDisplay = FileDisplay(FileDisplay.fromFile(file), uniqueId, spoiler, block)

inline fun FileDisplay(
    fileName: String,
    uniqueId: Int = -1,
    spoiler: Boolean = false,
    block: FileDisplayBuilder.() -> Unit = {}
): FileDisplay = FileDisplay(FileDisplay.fromFileName(fileName), uniqueId, spoiler, block)