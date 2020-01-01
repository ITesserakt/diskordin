package org.tesserakt.diskordin.core.data.event.channel

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache

class ChannelDeleteEvent(raw: ChannelResponse<*>) : IChannelEvent<ForId> {
    override val channel = raw.id identify { raw.unwrap().just() }

    init {
        cache -= channel.id
    }
}
