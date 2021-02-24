package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class MessageUpdateEvent(raw: MessageResponse) : IMessageEvent.Eager {
    override val message = raw.id eager { raw.unwrap() }
}
