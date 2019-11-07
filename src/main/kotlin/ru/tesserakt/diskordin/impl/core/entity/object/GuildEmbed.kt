package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.GuildEmbedResponse
import ru.tesserakt.diskordin.core.entity.IGuildChannel
import ru.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import ru.tesserakt.diskordin.core.entity.client

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
