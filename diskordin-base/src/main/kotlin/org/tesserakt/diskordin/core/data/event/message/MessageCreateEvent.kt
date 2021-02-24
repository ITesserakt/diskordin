package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client

class MessageCreateEvent(raw: MessageResponse) : IMessageEvent.Eager, IChannelEvent.Deferred {
    override val message = raw.id eager { raw.unwrap() }
    override val channel = message().channel
    val author = message().author
    val guild: DeferredIdentified<IGuild>? = raw.guild_id?.deferred(client::getGuild)
}