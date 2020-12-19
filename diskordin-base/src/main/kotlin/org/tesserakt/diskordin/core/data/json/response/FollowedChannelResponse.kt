package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IFollowedChannel
import org.tesserakt.diskordin.impl.core.entity.`object`.FollowedChannel

data class FollowedChannelResponse(
    val channelId: Snowflake,
    val webhookId: Snowflake
) : DiscordResponse<IFollowedChannel, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IFollowedChannel = FollowedChannel(this)
}