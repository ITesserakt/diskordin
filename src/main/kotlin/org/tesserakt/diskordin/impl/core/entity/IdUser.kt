package org.tesserakt.diskordin.impl.core.entity

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.IDUserResponse
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.ValuedEnum

class IdUser(raw: IDUserResponse) : IUser {
    private val delegate by lazy { client.getUser(raw.id).unsafeRunSync() }

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

    override fun asMember(guildId: Snowflake) = delegate.asMember(guildId)

    override fun toString(): String {
        return StringBuilder("IdUser(")
            .appendln("id=$id, ")
            .appendln("mention='$mention'")
            .appendln(")")
            .toString()
    }

    override val id: Snowflake = raw.id
    override val mention: String = "<@${id.asString()}>"
}