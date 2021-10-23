package org.tesserakt.diskordin.impl.core.entity

import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.rest
import org.tesserakt.diskordin.rest.flow

internal sealed class Channel(raw: ChannelResponse<IChannel>) : IChannel {
    private val logger = KotlinLogging.logger { }

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id

    final override val mention: String = "<#$id>"

    final override suspend fun delete(reason: String?) = rest.call {
        channelService.deleteChannel(id, reason)
    }.let { }

    override fun toString(): String {
        return "Channel(type=$type, id=$id, mention='$mention', invites=$invites)"
    }

    override val invites = rest.flow {
        channelService.getChannelInvites(id)
    }
}