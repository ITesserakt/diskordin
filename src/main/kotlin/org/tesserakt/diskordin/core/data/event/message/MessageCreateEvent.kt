package org.tesserakt.diskordin.core.data.event.message

import arrow.core.ForId
import arrow.fx.ForIO
import arrow.fx.extensions.io.monad.map
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.cache

class MessageCreateEvent(raw: MessageResponse) : IMessageEvent<ForId>, IChannelEvent<ForIO> {
    override val message = raw.id identifyId { raw.unwrap() }
    override val channel = message().channel
    val author = message().author
    val guild = channel.extract().map {
        if (it is IGuildChannel)
            it.guild.id.identify<IGuild> { _ ->
                it.guild()
            }
        else null
    }.unsafeRunSync()

    init {
        cache[message.id] = message()
        if (author != null)
            cache[author.id] = author.invoke()
    }
}