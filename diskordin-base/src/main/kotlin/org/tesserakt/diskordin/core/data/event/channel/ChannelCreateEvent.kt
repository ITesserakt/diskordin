package org.tesserakt.diskordin.core.data.event.channel

import org.tesserakt.diskordin.core.data.identified
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class ChannelCreateEvent(raw: ChannelResponse<*>) : IChannelEvent.Eager {
    override val channel = raw.unwrap().identified()
}
