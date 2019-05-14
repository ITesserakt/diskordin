package ru.tesserakt.diskordin.core.data.json.response


data class AccountResponse(
    val id: Long,
    val name: String
) : DiscordResponse()
