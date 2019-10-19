package ru.tesserakt.diskordin.core.data.event.message

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.util.combine

class MessageCreateEvent(raw: MessageResponse) : IEvent {
    val message = raw.id combine { raw.unwrap() }
}
