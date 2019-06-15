package ru.tesserakt.diskordin.core.entity.builder

import kotlin.properties.Delegates

class GuildEmbedEditBuilder : BuilderBase<GuildEmbedEditRequest>() {
    var enabled: Boolean by Delegates.notNull()
    var channelId: Snowflake? = null

    override fun create(): GuildEmbedEditRequest = GuildEmbedEditRequest(
        enabled, channelId?.asLong()
    )
}
