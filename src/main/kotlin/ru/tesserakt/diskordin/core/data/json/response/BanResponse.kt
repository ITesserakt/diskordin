package ru.tesserakt.diskordin.core.data.json.response


data class BanResponse(
    val reason: String?,
    val user: UserResponse
) : DiscordResponse()
