package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake

data class GuildWidgetEditRequest(
    val enabled: Boolean,
    val channelId: Snowflake?
) : JsonRequest()