package ru.tesserakt.diskordin.core.data.json.response

import kotlinx.coroutines.runBlocking
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import ru.tesserakt.diskordin.util.typeclass.integral

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
    val flags: Short? = null,
    val premiumType: Int? = null
) : DiscordResponse<IUser, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IUser = object : IUser {
        private val raw = this@IDUserResponse
        private val delegate by lazy { runBlocking { client.getUser(raw.id) } }

        override val avatar: String? by lazy { raw.avatar ?: delegate.avatar }
        override val mfaEnabled: Boolean by lazy { raw.mfaEnabled ?: delegate.mfaEnabled }
        override val locale: String? by lazy { raw.locale ?: delegate.locale }
        override val verified: Boolean by lazy { raw.verified ?: delegate.verified }
        override val email: String? by lazy { raw.email ?: delegate.email }
        override val flags: ValuedEnum<IUser.Flags, Short> by lazy {
            raw.flags?.let { ValuedEnum<IUser.Flags, Short>(it, Short.integral()) } ?: delegate.flags
        }
        override val premiumType: IUser.Type? by lazy {
            raw.premiumType?.let { type -> IUser.Type.values().find { it.ordinal == type } } ?: delegate.premiumType
        }
        override val username: String by lazy { raw.username ?: delegate.username }
        override val discriminator: Short by lazy { raw.discriminator?.toShort() ?: delegate.discriminator }
        override val isBot: Boolean by lazy { raw.bot ?: delegate.isBot }

        override suspend fun asMember(guildId: Snowflake): IMember = delegate.asMember(guildId)

        override val id: Snowflake = raw.id
        override val mention: String = "<@$id>"
    }
}
