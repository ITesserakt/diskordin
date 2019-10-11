package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.Self
import ru.tesserakt.diskordin.impl.core.entity.User


data class UserResponse(
    val id: Long,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: Boolean? = null,
    val mfa_enabled: Boolean? = null,
    val locale: String? = null,
    val verified: Boolean? = null,
    val email: String? = null,
    val flags: Int? = null,
    val premium_type: Int? = null
) : DiscordResponse() {
    @Suppress("UNCHECKED_CAST")
    fun <U : IUser> unwrap() = when (email) {
        null -> User(this)
        else -> Self(this)
    } as U
}