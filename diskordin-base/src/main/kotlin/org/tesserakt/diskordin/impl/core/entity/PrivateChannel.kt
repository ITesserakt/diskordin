package org.tesserakt.diskordin.impl.core.entity

import arrow.core.NonEmptyList
import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.ImageResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*

internal class PrivateChannel(override val raw: ChannelResponse<IPrivateChannel>) : Channel(raw), IPrivateChannel,
    ICacheable<IPrivateChannel, UnwrapContext.EmptyContext, ChannelResponse<IPrivateChannel>> {
    override val recipient = NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })
    override val owner: DeferredIdentified<IUser> = raw.owner_id?.deferred { client.getUser(it) } ?: client.self

    override fun toString(): String {
        return "PrivateChannel(recipient=$recipient)\n    ${super.toString()}"
    }

    override fun copy(changes: (ChannelResponse<IPrivateChannel>) -> ChannelResponse<IPrivateChannel>) =
        raw.run(changes).unwrap()
}

internal class GroupPrivateChannel(override val raw: ChannelResponse<IGroupPrivateChannel>) : Channel(raw),
    IGroupPrivateChannel,
    ICacheable<IGroupPrivateChannel, UnwrapContext.EmptyContext, ChannelResponse<IGroupPrivateChannel>> {
    override val icon = raw.icon?.let { ImageResponse(it, null) }?.unwrap()

    override fun toString(): String {
        return "GroupPrivateChannel(icon=$icon)\n   ${super.toString()}"
    }

    override val owner = raw.owner_id?.deferred { client.getUser(it) } ?: client.self
    override val recipient: NonEmptyList<IUser> = NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override fun copy(changes: (ChannelResponse<IGroupPrivateChannel>) -> ChannelResponse<IGroupPrivateChannel>) =
        raw.run(changes).unwrap()
}