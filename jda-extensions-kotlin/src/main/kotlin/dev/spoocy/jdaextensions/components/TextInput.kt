package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class TextInputBuilder(
    id: String,
    style: TextInputStyle,
) : ComponentBuilder<TextInput> {

    val builder: TextInput.Builder = TextInput.create(id, style)

    var id: String
        get() = builder.customId
        set(value) {
            builder.customId = value
        }

    var style: TextInputStyle
        get() = builder.style
        set(value) {
            builder.style = value
        }

    override var uniqueId: Int
        get() = builder.uniqueId
        set(value) {
            builder.uniqueId = value
        }

    var required: Boolean
        get() = builder.isRequired
        set(value) {
            builder.isRequired = value
        }

    var value: String?
        get() = builder.value
        set(value) {
            builder.value = value
        }

    var placeholder: String?
        get() = builder.placeholder
        set(value) {
            builder.placeholder = value
        }

    var range: IntRange
        get() = builder.minLength..builder.maxLength
        set(value) {
            builder.setRequiredRange(value.first, value.last)
        }

    override fun build() = builder.build()

}

fun TextInput(
    id: String,
    style: TextInputStyle,
    uniqueId: Int = -1,
    value: String? = null,
    placeholder: String? = null,
    required: Boolean = false,
    valueRange: IntRange = 1..1,
    block: TextInputBuilder.() -> Unit = {}
): TextInput = TextInputBuilder(id, style).apply {
    if (uniqueId > 0) this.uniqueId = uniqueId
    if (value != null) this.value = value
    if (placeholder != null) this.placeholder = placeholder

    this.required = required
    this.range = valueRange
    block()
}.build()