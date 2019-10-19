package ru.tesserakt.diskordin.core.data.json.request

import ru.tesserakt.diskordin.core.data.Snowflake


data class GuildEmbedEditRequest(
    val enabled: Boolean,
    val channel_id: Snowflake?
) : JsonRequest()
