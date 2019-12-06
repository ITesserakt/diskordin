package org.tesserakt.diskordin.core.data.event.message

import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class MessageCreateEvent(raw: MessageResponse) : IEvent {
    val message = raw.id identify { raw.unwrap().just() }
}
