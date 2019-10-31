package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.MessageUserResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.util.enums.ValuedEnum

class MessageUser(private val raw: MessageUserResponse) : IUser {
    private val delegate by lazy { runBlocking { client.getUser(raw.id) } }

    override val avatar: String? = raw.avatar
    override val mfaEnabled: Boolean by lazy { delegate.mfaEnabled }
    override val locale: String? by lazy { delegate.locale }
    override val verified: Boolean by lazy { delegate.verified }
    override val email: String? by lazy { delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Short> by lazy { delegate.flags }
    override val premiumType: IUser.Type? by lazy { delegate.premiumType }
    override val username: String = raw.username
    override val discriminator: Short = raw.discriminator
    override val isBot: Boolean = raw.bot ?: false

    override suspend fun asMember(guildId: Snowflake): IMember = raw.member?.unwrap(guildId)
        ?: client.getGuild(guildId).members.first { it.id == id }

    override fun toString(): String {
        return "MessageUser(avatar=$avatar, username='$username', discriminator=$discriminator, isBot=$isBot, id=$id, mention='$mention')"
    }

    override val id: Snowflake = raw.id
    override val mention: String = "<@$id>"
}