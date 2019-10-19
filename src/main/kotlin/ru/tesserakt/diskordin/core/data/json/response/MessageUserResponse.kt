package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.MessageUser


data class MessageUserResponse(
    val username: String,
    val id: Snowflake,
    val discriminator: Short,
    val avatar: String?,
    val bot: Boolean? = null,
    val member: MessageMemberResponse? = null
) : DiscordResponse<IUser>() {
    override fun unwrap(vararg params: Any): IUser = MessageUser(this)
}
