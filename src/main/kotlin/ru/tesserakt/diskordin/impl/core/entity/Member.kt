package ru.tesserakt.diskordin.impl.core.entity


import arrow.fx.IO
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.fix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.MemberResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import java.time.Instant

class Member constructor(
    raw: MemberResponse<*>,
    guildId: Snowflake
) : User(raw.user), IMember {
    override val guild: Identified<IGuild> = guildId combine { client.getGuild(it) }

    override suspend fun asMember(guildId: Snowflake): IMember = this

    override suspend fun addRole(role: IRole, reason: String?) = rest.effect {
        guildService.addMemberRole(guild.id, id, role.id, reason)
    }.fix().suspended()

    override suspend fun addRole(roleId: Snowflake, reason: String?) = rest.effect {
        guildService.addMemberRole(guild.id, id, roleId, reason)
    }.fix().suspended()

    override suspend fun removeRole(role: IRole, reason: String?) = rest.effect {
        guildService.deleteMemberRole(guild.id, id, role.id, reason)
    }.fix().suspended()

    override suspend fun removeRole(roleId: Snowflake, reason: String?) = rest.effect {
        guildService.deleteMemberRole(guild.id, id, roleId, reason)
    }.fix().suspended()

    override suspend fun edit(builder: MemberEditBuilder.() -> Unit) = rest.effect {
        guildService.editMember(guild.id, id, builder.build(), null)
    }.flatMap { IO { guild().members.first { it.id == id } } }.suspended()

    override fun toString(): String {
        return "Member(guild=$guild, nickname=$nickname, roles=$roles, joinTime=$joinTime, mention='$mention') ${super.toString()}"
    }

    override val nickname: String? = raw.nick

    override val roles: Flow<IRole> = flow {
        raw.roles
            .map(Snowflake.Companion::of)
            .map {
                guild().getRole(it)
            }.forEach { emit(it) }
    }

    override val joinTime: Instant = raw.joinedAt
    override val mention: String = "<@!$id>"
}