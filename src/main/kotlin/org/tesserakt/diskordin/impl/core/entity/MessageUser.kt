package org.tesserakt.diskordin.impl.core.entity

import arrow.fx.IO
import arrow.fx.extensions.io.applicative.just
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.MessageUserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.util.enums.ValuedEnum

internal class MessageUser(private val raw: MessageUserResponse) : IUser {
    private val delegate by lazy { client.getUser(raw.id).unsafeRunSync() }

    override val avatar: String? = raw.avatar
    override val mfaEnabled: Boolean by lazy { delegate.mfaEnabled }
    override val locale: String? by lazy { delegate.locale }
    override val verified: Boolean by lazy { delegate.verified }
    override val email: String? by lazy { delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Int> by lazy { delegate.flags }
    override val premiumType: IUser.Type? by lazy { delegate.premiumType }
    override val username: String = raw.username
    override val discriminator: Short = raw.discriminator
    override val isBot: Boolean = raw.bot ?: false

    override fun asMember(guildId: Snowflake): IO<IMember> =
        raw.member?.unwrap(guildId)?.just() ?: client.getMember(id, guildId)

    override fun toString(): String {
        return StringBuilder("MessageUser(")
            .appendln("avatar=$avatar, ")
            .appendln("username='$username', ")
            .appendln("discriminator=$discriminator, ")
            .appendln("isBot=$isBot, ")
            .appendln("id=$id, ")
            .appendln("mention='$mention'")
            .appendln(")")
            .toString()
    }

    override val id: Snowflake = raw.id
    override val mention: String = "<@$id>"
}