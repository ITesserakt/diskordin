package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.IdUser

class IDUserResponse(
    val id: Snowflake,
    val username: String? = null,
    val discriminator: String? = null,
    val avatar: String? = null,
    val bot: Boolean? = null,
    val mfaEnabled: Boolean? = null,
    val locale: String? = null,
    val verified: Boolean? = null,
    val email: String? = null,
    val flags: Short? = null,
    val premiumType: Int? = null
) : DiscordResponse<IUser, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IUser = IdUser(this)
}
