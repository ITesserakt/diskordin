package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.ISelf
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.Self

data class UserResponse<out U : IUser>(
    val id: Snowflake,
    val username: String,
    val discriminator: Short,
    val avatar: String?,
    val bot: Boolean = false,
    val system: Boolean = false,
    val mfa_enabled: Boolean? = null,
    val locale: String? = null,
    val publicFlags: Int = 0,
    val premium_type: Int = 0
) : DiscordResponse<U, UnwrapContext.EmptyContext>() {
    @Suppress("UNCHECKED_CAST")
    override fun unwrap(ctx: UnwrapContext.EmptyContext): U = Self(this as UserResponse<ISelf>) as? U
        ?: throw IllegalArgumentException("Illegal type parameter. Allowed {User, Self}.")
}