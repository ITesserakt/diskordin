package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.GuildEmbedEditRequest
import kotlin.properties.Delegates

class GuildEmbedEditBuilder : BuilderBase<GuildEmbedEditRequest>() {
    var enabled: Boolean by Delegates.notNull()
    var channelId: Snowflake? = null

    override fun create(): GuildEmbedEditRequest = GuildEmbedEditRequest(
        enabled, channelId
    )
}
