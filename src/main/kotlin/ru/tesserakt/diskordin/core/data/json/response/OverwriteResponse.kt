package ru.tesserakt.diskordin.core.data.json.response


data class OverwriteResponse(
    val id: Long,
    val type: String,
    val allow: Int,
    val deny: Int
) : DiscordResponse()