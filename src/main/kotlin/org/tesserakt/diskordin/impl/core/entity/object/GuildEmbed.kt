package org.tesserakt.diskordin.impl.core.entity.`object`


import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.GuildEmbedResponse
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import org.tesserakt.diskordin.core.entity.client

internal class GuildEmbed(raw: GuildEmbedResponse) : IGuildEmbed {
    override val enabled: Boolean = raw.enabled

    override val channel: IdentifiedF<ForIO, IGuildChannel>? = raw.channel_id?.identify<IGuildChannel> {
        client.getChannel(it) as IGuildChannel
    }

    override fun toString(): String {
        return StringBuilder("GuildEmbed(")
            .appendLine("enabled=$enabled, ")
            .appendLine("channel=$channel")
            .appendLine(")")
            .toString()
    }
}
