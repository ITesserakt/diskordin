package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.GuildWidgetEditRequest

@Suppress("NOTHING_TO_INLINE", "unused")
class GuildWidgetEditBuilder(private val prevEnabled: Boolean) : BuilderBase<GuildWidgetEditRequest>() {
    private var enabled: Boolean? = null
    private var channelId: Snowflake? = null

    operator fun Boolean.unaryPlus() {
        enabled = this
    }

    operator fun Snowflake.unaryPlus() {
        channelId = this
    }

    inline fun GuildWidgetEditBuilder.disable() = false
    inline fun GuildWidgetEditBuilder.enable() = true
    inline fun GuildWidgetEditBuilder.onChannel(id: Snowflake) = id

    override fun create(): GuildWidgetEditRequest = GuildWidgetEditRequest(
        enabled ?: prevEnabled,
        channelId
    )
}