package org.tesserakt.diskordin.core.data.event.message

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache

class MessageUpdateEvent(raw: MessageResponse) : IMessageEvent<ForId> {
    override val message = raw.id identifyId { raw.unwrap() }

    init {
        cache[message.id] = message()
    }
}
