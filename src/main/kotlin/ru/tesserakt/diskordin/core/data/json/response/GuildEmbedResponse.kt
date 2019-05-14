package ru.tesserakt.diskordin.core.data.json.response


data class GuildEmbedResponse(
    val enabled: Boolean,
    val channel_id: Long
) : DiscordResponse()
