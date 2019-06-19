package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildEmbedResponse
import ru.tesserakt.diskordin.core.entity.IGuildChannel
import ru.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import ru.tesserakt.diskordin.util.Identified

class GuildEmbed(raw: GuildEmbedResponse) : IGuildEmbed {
    override val enabled: Boolean = raw.enabled

    override val channel: Identified<IGuildChannel>? = raw.channel_id?.let { id ->
        Identified(id.asSnowflake()) {
            (client.findChannel(it) as IGuildChannel?) ?: throw NoSuchElementException("")
        }
    }


}
