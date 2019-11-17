package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.impl.core.entity.`object`.Ban


data class BanResponse(
    val reason: String?,
    val user: UserResponse<IUser>
) : DiscordResponse<IBan, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IBan = Ban(this)
}
