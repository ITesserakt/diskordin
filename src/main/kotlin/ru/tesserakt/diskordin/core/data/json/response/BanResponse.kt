package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.impl.core.entity.`object`.Ban


data class BanResponse(
    val reason: String?,
    val user: UserResponse<IUser>
) : DiscordResponse<IBan>() {
    override fun unwrap(vararg params: Any): IBan = Ban(this)
}
