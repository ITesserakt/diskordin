package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.GuildEmbedResponse
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import org.tesserakt.diskordin.core.entity.client

class GuildEmbed(raw: GuildEmbedResponse) : IGuildEmbed {
    override val enabled: Boolean = raw.enabled

    override val channel: Identified<IGuildChannel>? = raw.channel_id?.identify {
        client.getChannel(it).bind() as IGuildChannel
    }

    override fun toString(): String {
        return StringBuilder("GuildEmbed(")
            .appendln("enabled=$enabled, ")
            .appendln("channel=$channel")
            .appendln(")")
            .toString()
    }
}
