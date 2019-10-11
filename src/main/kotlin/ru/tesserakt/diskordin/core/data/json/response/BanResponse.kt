package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.`object`.Ban


data class BanResponse(
    val reason: String?,
    val user: UserResponse
) : DiscordResponse() {
    fun unwrap() = Ban(this)
}
