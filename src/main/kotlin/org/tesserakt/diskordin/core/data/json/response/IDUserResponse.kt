package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.IdUser

data class IDUserResponse(
    val id: Snowflake,
    val username: String? = null,
    val discriminator: Short? = null,
    val avatar: String? = null,
    val bot: Boolean? = null,
    val mfaEnabled: Boolean? = null,
    val locale: String? = null,
    val verified: Boolean? = null,
    val email: String? = null,
    val publicFlags: Int? = null,
    val premiumType: Int? = null,
    val system: Boolean? = null
) : DiscordResponse<IUser, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IUser = IdUser(this)
}
