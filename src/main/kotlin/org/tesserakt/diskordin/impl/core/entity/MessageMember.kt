package org.tesserakt.diskordin.impl.core.entity

import arrow.core.k
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.just
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.MessageMemberResponse
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant

class MessageMember(private val raw: MessageMemberResponse, guildId: Snowflake) : IMember {
    override val avatar: String? by lazy { delegate.avatar }
    override val mfaEnabled: Boolean by lazy { delegate.mfaEnabled }
    override val locale: String? by lazy { delegate.locale }
    override val verified: Boolean by lazy { delegate.verified }
    override val email: String? by lazy { delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Short> by lazy { delegate.flags }
    override val premiumType: IUser.Type? by lazy { delegate.premiumType }
    private val delegate by lazy {
        IO.fx { guild().bind().members.bind().first { it.nickname == raw.nick } }.unsafeRunSync()
    }
    override val guild: Identified<IGuild> = guildId identify { client.getGuild(it).bind() }
    override val nickname: String? = raw.nick
    override val roles = IO.fx { raw.roles.map { guild().bind().getRole(it).bind() }.k() }
    override val joinTime: Instant = raw.joined_at

    override fun addRole(role: IRole, reason: String?) =
        delegate.addRole(role, reason)

    override fun addRole(roleId: Snowflake, reason: String?) =
        delegate.addRole(roleId, reason)

    override fun removeRole(role: IRole, reason: String?) =
        delegate.removeRole(role, reason)

    override fun removeRole(roleId: Snowflake, reason: String?) =
        delegate.removeRole(roleId, reason)

    override val username: String by lazy { delegate.username }

    override val discriminator: Short by lazy { delegate.discriminator }

    override val isBot: Boolean by lazy { delegate.isBot }

    override fun asMember(guildId: Snowflake): IO<IMember> = delegate.just()

    override val id: Snowflake by lazy { delegate.id }
    override val mention: String by lazy { delegate.mention }

    override fun edit(builder: MemberEditBuilder.() -> Unit): IO<IMember> = delegate.edit(builder)

    override fun toString(): String {
        return StringBuilder("MessageMember(")
            .appendln("guild=$guild, ")
            .appendln("nickname=$nickname, ")
            .appendln("roles=$roles, ")
            .appendln("joinTime=$joinTime")
            .appendln(")")
            .toString()
    }
}