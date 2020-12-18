package org.tesserakt.diskordin.core.data.event.channel

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class ChannelCreateEvent(raw: ChannelResponse<*>) : IChannelEvent<ForId> {
    override val channel = raw.id.identifyId { raw.unwrap() }
}
