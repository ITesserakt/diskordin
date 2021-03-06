package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.GuildEmbedResponse
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import org.tesserakt.diskordin.core.entity.client

internal class GuildEmbed(raw: GuildEmbedResponse) : IGuildEmbed {
    override val enabled: Boolean = raw.enabled

    override val channel: DeferredIdentified<IGuildChannel>? = raw.channel_id?.deferred {
        client.getChannel(it) as IGuildChannel
    }

    override fun toString(): String {
        return "GuildEmbed(enabled=$enabled, channel=$channel)"
    }
}
