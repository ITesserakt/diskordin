package ru.tesserakt.diskordin.core.data.json.response


data class UserGuildResponse(
    val id: Long,
    val name: String,
    val icon: String,
    val owner: Boolean,
    val permissions: Int
) : DiscordResponse()