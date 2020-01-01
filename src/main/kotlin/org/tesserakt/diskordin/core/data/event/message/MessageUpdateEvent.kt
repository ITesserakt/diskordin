package org.tesserakt.diskordin.core.data.event.message

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache

class MessageUpdateEvent(raw: MessageResponse) : IMessageEvent<ForId> {
    override val message = raw.id identify { raw.unwrap().just() }

    init {
        cache[message.id] = message().extract()
    }
}
