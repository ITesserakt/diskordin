package ru.tesserakt.diskordin.core.data.event.message

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse

class MessageUpdateEvent(raw: MessageResponse) : IEvent {
    val message = raw.id combine { raw.unwrap() }
}
