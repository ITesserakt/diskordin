package org.tesserakt.diskordin.core.data.event.channel

import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.identified
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IChannel

class ChannelUpdateEvent(raw: ChannelResponse<*>) : IChannelEvent.Eager {
    override val channel: EagerIdentified<IChannel> = raw.unwrap().identified()
}
