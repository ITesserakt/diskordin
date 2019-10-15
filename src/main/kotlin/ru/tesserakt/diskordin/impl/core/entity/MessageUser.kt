package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.flow.first
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.MessageUserResponse
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.client

class MessageUser(private val raw: MessageUserResponse) : IUser {
    override val username: String = raw.username
    override val discriminator: Short = raw.discriminator
    override val isBot: Boolean = raw.bot ?: false

    override suspend fun asMember(guildId: Snowflake): IMember = raw.member?.unwrap(guildId)
        ?: client.findGuild(guildId)!!.members.first { it.id == id }

    override val id: Snowflake = raw.id.asSnowflake()
    override val mention: String = "<@$id>"
}