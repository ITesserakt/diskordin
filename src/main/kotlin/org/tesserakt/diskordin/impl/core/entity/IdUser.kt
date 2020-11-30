package org.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.IDUserResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.ICacheable
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.ValuedEnum

internal class IdUser(override val raw: IDUserResponse) : IUser,
    ICacheable<IUser, UnwrapContext.EmptyContext, IDUserResponse> {
    private val delegate by lazy {
        runBlocking { client.rest.call { userService.getUser(id) } }
    }

    override val avatar: String? by lazy { raw.avatar ?: delegate.avatar }
    override val mfaEnabled: Boolean by lazy { raw.mfaEnabled ?: delegate.mfaEnabled }
    override val locale: String? by lazy { raw.locale ?: delegate.locale }
    override val verified: Boolean by lazy { raw.verified ?: delegate.verified }
    override val email: String? by lazy { raw.email ?: delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Int> by lazy {
        raw.flags?.let { ValuedEnum(it, Int.integral()) } ?: delegate.flags
    }
    override val premiumType: IUser.Type by lazy {
        when (raw.premiumType) {
            0 -> IUser.Type.NitroClassic
            1 -> IUser.Type.Nitro
            null -> null
            else -> IUser.Type.None
        } ?: delegate.premiumType
    }
    override val username: String by lazy { raw.username ?: delegate.username }
    override val discriminator: Short by lazy { raw.discriminator?.toShort() ?: delegate.discriminator }
    override val isBot: Boolean by lazy { raw.bot ?: delegate.isBot }

    override suspend fun asMember(guildId: Snowflake) = delegate.asMember(guildId)

    override fun toString(): String {
        return "IdUser(id=$id, mention='$mention')"
    }

    override val id: Snowflake = raw.id
    override val mention: String = "<@${id.asString()}>"

    override fun fromCache(): IUser = cache[id] as IUser

    override fun copy(changes: (IDUserResponse) -> IDUserResponse): IUser = raw.run(changes).unwrap()
}