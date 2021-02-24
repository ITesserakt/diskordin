package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.WebhooksUpdate

class WebhooksUpdateEvent(raw: WebhooksUpdate) : IGuildEvent.Deferred, IChannelEvent.Deferred {
    override val guild = raw.guildId deferred { client.getGuild(it) }
    override val channel = raw.channelId deferred { client.getChannel(it) }
}
