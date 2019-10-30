package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import java.time.Instant

class Member constructor(
    raw: GuildMemberResponse,
    guildId: Snowflake
) : User(raw.user), IMember {
    override val guild: Identified<IGuild> = guildId combine { client.getGuild(it) }

    override suspend fun asMember(guildId: Snowflake): IMember = this

    override suspend fun addRole(role: IRole, reason: String?) =
        guildService.addMemberRole(guild.id, id, role.id, reason)

    override suspend fun addRole(roleId: Snowflake, reason: String?) =
        guildService.addMemberRole(guild.id, id, roleId, reason)

    override suspend fun removeRole(role: IRole, reason: String?) =
        guildService.deleteMemberRole(guild.id, id, role.id, reason)

    override suspend fun removeRole(roleId: Snowflake, reason: String?) =
        guildService.deleteMemberRole(guild.id, id, roleId, reason)

    override suspend fun edit(builder: MemberEditBuilder.() -> Unit) =
        guildService.editMember(guild.id, id, builder.build(), null).run {
            guild().members.first { it.id == id }
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