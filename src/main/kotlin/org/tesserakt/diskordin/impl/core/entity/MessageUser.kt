package org.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.MessageUserResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.util.enums.ValuedEnum

internal class MessageUser(override val raw: MessageUserResponse) : IUser,
    ICacheable<IUser, UnwrapContext.EmptyContext, MessageUserResponse> {
    private val delegate by lazy { runBlocking { client.getUser(raw.id) } }

    override val avatar: String? = raw.avatar
    override val mfaEnabled: Boolean by lazy { delegate.mfaEnabled }
    override val locale: String? by lazy { delegate.locale }
    override val verified: Boolean by lazy { delegate.verified }
    override val email: String? by lazy { delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Int> by lazy { delegate.flags }
    override val premiumType: IUser.Type by lazy { delegate.premiumType }
    override val username: String = raw.username
    override val discriminator: Short = raw.discriminator
    override val isBot: Boolean = raw.bot ?: false

    override suspend fun asMember(guildId: Snowflake): IMember =
        raw.member?.unwrap(guildId) ?: client.getMember(id, guildId)

    override fun toString(): String {
        return "MessageUser(avatar=$avatar, username='$username', discriminator=$discriminator, isBot=$isBot, id=$id, mention='$mention')"
    }

    override val id: Snowflake = raw.id
    override val mention: String = "<@$id>"

    override fun fromCache(): IUser = cache[id] as IUser

    override fun copy(changes: (MessageUserResponse) -> MessageUserResponse): IUser =
        raw.run(changes).unwrap()
}