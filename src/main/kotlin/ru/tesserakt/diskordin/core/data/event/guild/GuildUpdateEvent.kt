package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.GuildResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap

class GuildUpdateEvent(raw: GuildResponse) : IEvent {
    val guild = raw.unwrap()
}
