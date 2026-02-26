package dev.spoocy.jdaextensions.components

import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.entities.emoji.Emoji

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class ActionRowBuilder : ComponentBuilder<ActionRow> {

    val components: MutableList<ActionRowChildComponent> = mutableListOf()
    override var uniqueId: Int = -1

    fun button(
        customId: String,
        label: String? = null,
        style: ButtonStyle,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        uniqueId: Int = -1,
    ): ActionRowBuilder {
        components += Button.of(style, customId, label, emoji)
            .withDisabled(disabled)
            .let { if (uniqueId > 0) it.withUniqueId(uniqueId) else it }
        return this
    }

    fun primaryButton(
        customId: String,
        label: String? = null,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        uniqueId: Int = -1,
    ) = button(customId, label, ButtonStyle.PRIMARY, emoji, disabled, uniqueId)

    fun secondaryButton(
        customId: String,
        label: String? = null,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        uniqueId: Int = -1,
    ) = button(customId, label, ButtonStyle.SECONDARY, emoji, disabled, uniqueId)

    fun successButton(
        customId: String,
        label: String? = null,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        uniqueId: Int = -1,
    ): ActionRowBuilder = button(customId, label, ButtonStyle.SUCCESS, emoji, disabled, uniqueId)

    fun dangerButton(
        customId: String,
        label: String? = null,
        emoji: Emoji? = null,
        disabled: Boolean = false,
        uniqueId: Int = -1,
    ): ActionRowBuilder = button(customId, label, ButtonStyle.DANGER, emoji, disabled, uniqueId)

    fun linkButton(
        url: String,
        label: String? = null,
        emoji: Emoji? = null,
        disabled: Boolean = false,
    ): ActionRowBuilder = button(url, label, ButtonStyle.LINK, emoji, disabled)

    inline fun selectString(
        customId: String,
        uniqueId: Int = -1,
        placeholder: String? = null,
        disabled: Boolean = false,
        valueRange: IntRange = 1..1,
        options: Collection<SelectOption> = emptyList(),
        builder: StringSelectMenu.Builder.() -> Unit = {},
    ): ActionRowBuilder {
        components += StringSelectMenu(customId, uniqueId, placeholder, disabled, valueRange, options, builder)
        return this
    }

    inline fun selectEntity(
        customId: String,
        targets: Collection<EntitySelectMenu.SelectTarget>,
        uniqueId: Int = -1,
        placeholder: String? = null,
        disabled: Boolean = false,
        valueRange: IntRange = 1..1,
        builder: EntitySelectMenu.Builder.() -> Unit = {},
    ): ActionRowBuilder {
        components += EntitySelectMenu(customId, targets, uniqueId, placeholder, disabled, valueRange, builder)
        return this
    }

    inline fun selectRole(
        customId: String,
        uniqueId: Int = -1,
        placeholder: String? = null,
        disabled: Boolean = false,
        valueRange: IntRange = 1..1,
        builder: EntitySelectMenu.Builder.() -> Unit = {},
    ): ActionRowBuilder = selectEntity(
        customId,
        listOf(EntitySelectMenu.SelectTarget.ROLE),
        uniqueId,
        placeholder,
        disabled,
        valueRange,
        builder
    )

    inline fun selectUser(
        customId: String,
        uniqueId: Int = -1,
        placeholder: String? = null,
        disabled: Boolean = false,
        valueRange: IntRange = 1..1,
        builder: EntitySelectMenu.Builder.() -> Unit = {},
    ): ActionRowBuilder = selectEntity(
        customId,
        listOf(EntitySelectMenu.SelectTarget.USER),
        uniqueId,
        placeholder,
        disabled,
        valueRange,
        builder
    )

    inline fun selectChannel(
        customId: String,
        uniqueId: Int = -1,
        placeholder: String? = null,
        disabled: Boolean = false,
        valueRange: IntRange = 1..1,
        builder: EntitySelectMenu.Builder.() -> Unit = {},
    ): ActionRowBuilder = selectEntity(
        customId,
        listOf(EntitySelectMenu.SelectTarget.CHANNEL),
        uniqueId,
        placeholder,
        disabled,
        valueRange,
        builder
    )

    override fun build(): ActionRow = ActionRow.of(components)
        .let { if (uniqueId > 0) it.withUniqueId(uniqueId) else it }

}

inline fun ActionRow(
    uniqueId: Int = -1,
    block: ActionRowBuilder.() -> Unit
): ActionRow = ActionRowBuilder().apply {
    if (uniqueId > 0) this.uniqueId = uniqueId
    block()
}.build()