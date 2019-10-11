package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.Integration


data class AccountResponse(
    val id: Long,
    val name: String
) : DiscordResponse() {
    fun unwrap() = Integration.Account(this)
}
