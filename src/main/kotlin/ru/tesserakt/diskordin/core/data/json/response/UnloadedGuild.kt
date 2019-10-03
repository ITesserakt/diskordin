package ru.tesserakt.diskordin.core.data.json.response

data class UnloadedGuild(
    val id: Long,
    val unavailable: Boolean
) : DiscordResponse()