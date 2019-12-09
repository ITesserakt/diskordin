package org.tesserakt.diskordin.core.data.event.channel

import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class ChannelUpdateEvent(raw: ChannelResponse<*>) : IEvent {
    val channel = raw.id identify { raw.unwrap().just() }

    init {
        GlobalEntityCache[channel.id] = channel().extract()
    }
}
