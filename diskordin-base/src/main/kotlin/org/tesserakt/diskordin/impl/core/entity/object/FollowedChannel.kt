package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.FollowedChannelResponse
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.IWebhook
import org.tesserakt.diskordin.core.entity.`object`.IFollowedChannel
import org.tesserakt.diskordin.core.entity.client

class FollowedChannel(raw: FollowedChannelResponse) : IFollowedChannel {
    override val channel: DeferredIdentified<ITextChannel> = raw.channelId deferred {
        client.getChannel(it) as ITextChannel
    }
    override val webhook: DeferredIdentified<IWebhook> get() = TODO("Add webhooks support")
}