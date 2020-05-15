package org.tesserakt.diskordin.core.data.event.message

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.fx.ForIO
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.cache

class MessageCreateEvent(raw: MessageResponse) : IMessageEvent<ForId>, IChannelEvent<ForIO> {
    override val message = raw.id identify { raw.unwrap().just() }
    override val channel = message().extract().channel
    val author = message().extract().author
    val guild: IdentifiedF<ForIO, IGuild>? = channel().fix().unsafeRunSync().takeIf { it is IGuildChannel }?.let {
        it as IGuildChannel
        it.guild
    }

    init {
        cache[message.id] = message().extract()
        if (author != null)
            cache[author.id] = author.invoke().extract()
    }
}