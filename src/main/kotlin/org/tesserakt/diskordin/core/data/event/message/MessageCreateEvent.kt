package org.tesserakt.diskordin.core.data.event.message

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.monad.monad
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.cache

class MessageCreateEvent(raw: MessageResponse) : IMessageEvent<ForId>, IChannelEvent<ForIO> {
    override val message = raw.id identify { raw.unwrap().just() }
    override val channel = message().extract().channel
    val author = message().extract().author
    val guild = channel.takeIf { it is IGuildChannel }?.flatMap(IO.monad()) {
        (it as IGuildChannel).guild
    }

    init {
        cache[message.id] = message().extract()
    }
}
