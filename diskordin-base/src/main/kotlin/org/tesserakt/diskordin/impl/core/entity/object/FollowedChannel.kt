package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.IdentifiedIO
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.FollowedChannelResponse
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.IWebhook
import org.tesserakt.diskordin.core.entity.`object`.IFollowedChannel
import org.tesserakt.diskordin.core.entity.client

class FollowedChannel(raw: FollowedChannelResponse) : IFollowedChannel {
    override val channel: IdentifiedIO<ITextChannel> = raw.channelId.identify<ITextChannel> {
        client.getChannel(it) as ITextChannel
    }
    override val webhook: IdentifiedIO<IWebhook> get() = TODO("Add webhooks support")
}