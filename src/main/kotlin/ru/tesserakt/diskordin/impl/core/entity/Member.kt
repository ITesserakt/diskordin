package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.k
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.fix
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.MemberResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import java.time.Instant

class Member constructor(
    raw: MemberResponse<*>,
    guildId: Snowflake
) : User(raw.user), IMember {
    override val guild: Identified<IGuild> = guildId identify { client.getGuild(it).bind() }

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
    }.flatMap { IO.fx { guild().bind().members.bind().first { it.id == id } } }

    override fun toString(): String {
        return StringBuilder("Member(")
            .appendln("guild=$guild, ")
            .appendln("nickname=$nickname, ")
            .appendln("roles=$roles, ")
            .appendln("joinTime=$joinTime, ")
            .appendln("mention='$mention'")
            .appendln(") ${super.toString()}")
            .toString()
    }

    override val nickname: String? = raw.nick

    override val roles = IO.fx {
        raw.roles.map { roleId ->
            guild().bind().getRole(roleId).bind()
        }.k()
    }

    override val joinTime: Instant = raw.joinedAt
    override val mention: String = "<@!$id>"
}