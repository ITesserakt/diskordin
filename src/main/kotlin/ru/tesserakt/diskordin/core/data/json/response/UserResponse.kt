package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.ISelf
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.Self

data class UserResponse<out U : IUser>(
    val id: Snowflake,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: Boolean? = null,
    val mfa_enabled: Boolean? = null,
    val locale: String? = null,
    val verified: Boolean? = null,
    val email: String? = null,
    val flags: Short? = null,
    val premium_type: Int? = null
) : DiscordResponse<U>() {
    @Suppress("UNCHECKED_CAST")
    override fun unwrap(vararg params: Any): U = Self(this as UserResponse<ISelf>) as? U
        ?: throw IllegalArgumentException("Illegal type parameter. Allowed {User, Self}.")
}