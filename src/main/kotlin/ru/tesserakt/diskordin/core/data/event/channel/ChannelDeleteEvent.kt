package ru.tesserakt.diskordin.core.data.event.channel

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap

class ChannelDeleteEvent(raw: ChannelResponse<*>) : IEvent {
    val channel = raw.unwrap()
}
