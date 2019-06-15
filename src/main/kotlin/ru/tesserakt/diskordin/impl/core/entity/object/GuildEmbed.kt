package ru.tesserakt.diskordin.impl.core.entity.`object`

import kotlinx.coroutines.async
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildEmbedResponse
import ru.tesserakt.diskordin.core.entity.IGuildChannel
import ru.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import ru.tesserakt.diskordin.util.Identified

class GuildEmbed(raw: GuildEmbedResponse, override val kodein: Kodein = Diskordin.kodein) : IGuildEmbed {
    override val enabled: Boolean = raw.enabled

    override val channel: Identified<IGuildChannel>? = raw.channel_id?.let {
        Identified(it.asSnowflake()) {
            client.coroutineScope.async {
                (client.findChannel(it) as IGuildChannel?) ?: throw NoSuchElementException("")
            }
        }
    }

    override val client: IDiscordClient by instance()

}
