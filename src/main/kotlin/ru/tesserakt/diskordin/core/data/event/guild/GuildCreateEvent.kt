package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.GuildResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap

class GuildCreateEvent(raw: GuildResponse) : IEvent {
    val guild = raw.id identify { raw.unwrap() }
}
