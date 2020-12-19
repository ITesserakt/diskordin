package org.tesserakt.diskordin.core.data.event.message

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class MessageUpdateEvent(raw: MessageResponse) : IMessageEvent<ForId> {
    override val message = raw.id identifyId { raw.unwrap() }
}
