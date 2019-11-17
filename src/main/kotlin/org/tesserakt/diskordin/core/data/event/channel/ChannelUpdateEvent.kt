package org.tesserakt.diskordin.core.data.event.channel

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class ChannelUpdateEvent(raw: ChannelResponse<*>) : IEvent {
    val channel = raw.unwrap()
}
