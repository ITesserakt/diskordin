package org.tesserakt.diskordin.impl.core.entity


import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.listk.monadFilter.filterMap
import arrow.core.identity
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicative.map
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.MemberResponse
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import java.time.Instant

internal class Member constructor(
    raw: MemberResponse<*>,
    guildId: Snowflake
) : User(raw.user), IMember {
    override val guild: IdentifiedF<ForIO, IGuild> = guildId identify { client.getGuild(it) }

    override fun asMember(guildId: Snowflake): IO<IMember> = IO.just(this)

    override fun addRole(role: IRole, reason: String?) = rest.effect {
        guildService.addMemberRole(guild.id, id, role.id, reason)
    }.fix()

    override fun addRole(roleId: Snowflake, reason: String?) = rest.effect {
        guildService.addMemberRole(guild.id, id, roleId, reason)
    }.fix()

    override fun removeRole(role: IRole, reason: String?) = rest.effect {
        guildService.deleteMemberRole(guild.id, id, role.id, reason)
    }.fix()

    override fun removeRole(roleId: Snowflake, reason: String?) = rest.effect {
        guildService.deleteMemberRole(guild.id, id, roleId, reason)
    }.fix()

    override fun edit(builder: MemberEditBuilder.() -> Unit) = rest.effect {
        guildService.editMember(guild.id, id, builder.build(), null)
    }.flatMap { client.getMember(id, guild.id) }

    override fun toString(): String {
        return "Member(guild=$guild, nickname=$nickname, roles=$roles, joinTime=$joinTime, mention='$mention') " +
                "   ${super.toString()}"
    }

    override val nickname: String? = raw.nick

    override val roles = raw.roles.map { id ->
        guild().map { it.getRole(id) }
    }.sequence(IO.applicative()).map { it.filterMap(::identity) }

    override val joinTime: Instant = raw.joinedAt
    override val mention: String = "<@!$id>"
}