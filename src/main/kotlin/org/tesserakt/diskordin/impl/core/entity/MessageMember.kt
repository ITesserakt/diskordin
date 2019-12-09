package org.tesserakt.diskordin.impl.core.entity

import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.listk.monadFilter.filterMap
import arrow.core.identity
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.applicative.map
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.MessageMemberResponse
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant

class MessageMember(raw: MessageMemberResponse, guildId: Snowflake) : IMember {
    override val avatar: String? by lazy { delegate.avatar }
    override val mfaEnabled: Boolean by lazy { delegate.mfaEnabled }
    override val locale: String? by lazy { delegate.locale }
    override val verified: Boolean by lazy { delegate.verified }
    override val email: String? by lazy { delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Short> by lazy { delegate.flags }
    override val premiumType: IUser.Type? by lazy { delegate.premiumType }
    private val delegate by lazy { client.getMember(id, guildId).unsafeRunSync() }
    override val guild: IdentifiedF<ForIO, IGuild> = guildId identify { client.getGuild(it) }
    override val nickname: String? = raw.nick

    override val roles = raw.roles.map { id ->
        guild()
            .map { it.getRole(id) }
    }
        .sequence(IO.applicative())
        .map { it.filterMap(::identity) }

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