package ru.tesserakt.diskordin.core.data.json.request


data class GuildEmbedEditRequest(
    val enabled: Boolean,
    val channel_id: Long?
) : JsonRequest()
