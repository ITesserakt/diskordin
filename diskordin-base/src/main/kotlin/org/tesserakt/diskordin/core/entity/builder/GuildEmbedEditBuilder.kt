package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.GuildEmbedEditRequest

@RequestBuilder
class GuildEmbedEditBuilder(val enabled: Boolean) : BuilderBase<GuildEmbedEditRequest>() {
    var channelId: Snowflake? = null

    override fun create(): GuildEmbedEditRequest = GuildEmbedEditRequest(
        enabled, channelId
    )

    operator fun Snowflake.unaryPlus() {
        channelId = this
    }

    @Suppress("NOTHING_TO_INLINE", "unused")
    inline fun GuildEmbedEditBuilder.channel(id: Snowflake) = id
}
