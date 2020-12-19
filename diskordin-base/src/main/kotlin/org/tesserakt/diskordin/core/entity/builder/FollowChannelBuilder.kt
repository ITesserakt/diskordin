package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.FollowRequest

class FollowChannelBuilder(
    /**
     * Id of target channel
     */
    private val webhookChannelId: Snowflake
) : BuilderBase<FollowRequest>() {
    override fun create(): FollowRequest = FollowRequest(webhookChannelId)
}