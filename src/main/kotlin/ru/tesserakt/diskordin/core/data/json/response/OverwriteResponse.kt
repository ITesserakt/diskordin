package ru.tesserakt.diskordin.core.data.json.response


data class OverwriteResponse(
    val id: Long,
    val type: String,
    val allow: Long,
    val deny: Long
) : DiscordResponse()