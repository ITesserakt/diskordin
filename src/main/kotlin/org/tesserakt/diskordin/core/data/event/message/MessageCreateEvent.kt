package org.tesserakt.diskordin.core.data.event.message

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class MessageCreateEvent(raw: MessageResponse) : IMessageEvent<ForId>, IChannelEvent<ForIO> {
    override val message = raw.id identify { raw.unwrap().just() }
    override val channel = message().extract().channel
    val author = message().extract().author

    init {
        GlobalEntityCache[message.id] = message().extract()
    }
}
