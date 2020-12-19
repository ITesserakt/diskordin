package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.MessageUser


data class MessageUserResponse(
    val username: String,
    val id: Snowflake,
    val discriminator: Short,
    val avatar: String?,
    val bot: Boolean = false,
    val system: Boolean = false,
    val mfa_enabled: Boolean? = null,
    val locale: String? = null,
    val publicFlags: Int = 0,
    val premium_type: Int = 0,
    val member: MessageMemberResponse? = null
) : DiscordResponse<IUser, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IUser = MessageUser(this)
}
