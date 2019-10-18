package ru.tesserakt.diskordin.core.data.json.response

import kotlinx.coroutines.runBlocking
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.client

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
    val flags: Long? = null,
    val premiumType: Int? = null
) : DiscordResponse<IUser>() {
    override fun unwrap(vararg params: Any) = object : IUser {
        private val raw = this@IDUserResponse
        private val delegate by lazy { runBlocking { client.findUser(raw.id)!! } }
        override val username: String by lazy { raw.username ?: delegate.username }
        override val discriminator: Short by lazy { raw.discriminator?.toShort() ?: delegate.discriminator }
        override val isBot: Boolean by lazy { raw.bot ?: delegate.isBot }

        override suspend fun asMember(guildId: Snowflake): IMember = delegate.asMember(guildId)

        override val id: Snowflake = raw.id
        override val mention: String = "<@$id>"
    }
}
