package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake

data class FollowRequest(
    val webhookChannelId: Snowflake
) : JsonRequest()