package org.tesserakt.diskordin.impl.core.entity

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.MessageUserResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*

internal class MessageUser(override val raw: MessageUserResponse) : IUser, User(
    UserResponse(
        raw.id,
        raw.username,
        raw.discriminator,
        raw.avatar,
        raw.bot,
        raw.system,
        raw.mfa_enabled,
        raw.locale,
        raw.publicFlags,
        raw.premium_type
    )
), ICacheable<IUser, UnwrapContext.EmptyContext, MessageUserResponse> {
    override suspend fun asMember(guildId: Snowflake): IMember =
        raw.member?.unwrap(guildId) ?: client.getMember(id, guildId)

    override val mention: String = "<@$id>"

    override fun fromCache(): IUser = cache[id] as IUser

    override fun copy(changes: (MessageUserResponse) -> MessageUserResponse): IUser =
        raw.run(changes).unwrap()
}