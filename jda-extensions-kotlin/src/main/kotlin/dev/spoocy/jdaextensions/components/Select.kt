package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.entities.emoji.Emoji

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

/**
 *
 */
fun SelectOption(
    label: String,
    value: String,
    description: String? = null,
    emoji: Emoji? = null,
    default: Boolean = false
) = SelectOption.of(label, value)
    .withDescription(description)
    .withEmoji(emoji)
    .withDefault(default)

fun StringSelectMenu.Builder.option(
    label: String,
    value: String,
    description: String? = null,
    emoji: Emoji? = null,
    default: Boolean = false
) = addOptions(SelectOption(label, value, description, emoji, default))

inline fun StringSelectMenu(
    customId: String,
    uniqueId: Int = -1,
    placeholder: String? = null,
    disabled: Boolean = false,
    valueRange: IntRange = 1..1,
    options: Collection<SelectOption> = emptyList(),
    block: StringSelectMenu.Builder.() -> Unit = {},
) = StringSelectMenu.create(customId).let {
    if (uniqueId > 0) it.uniqueId = uniqueId
    if (placeholder != null) it.placeholder = placeholder

    it.isDisabled = disabled
    it.setRequiredRange(valueRange.first, valueRange.last)
    it.options.addAll(options)
    it.apply(block)
    it.build()
}

inline fun EntitySelectMenu(
    customId: String,
    targets: Collection<EntitySelectMenu.SelectTarget>,
    uniqueId: Int = -1,
    placeholder: String? = null,
    disabled: Boolean = false,
    valueRange: IntRange = 1..1,
    block: EntitySelectMenu.Builder.() -> Unit = {},
) = EntitySelectMenu.create(customId, targets).let {
    if (uniqueId > 0) it.uniqueId = uniqueId
    if (placeholder != null) it.placeholder = placeholder

    it.isDisabled = disabled
    it.setRequiredRange(valueRange.first, valueRange.last)
    it.apply(block)
    it.build()
}