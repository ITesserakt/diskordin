package org.tesserakt.diskordin.impl.core.entity

import arrow.fx.ForIO
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.filterOption
import org.tesserakt.diskordin.core.data.*
import org.tesserakt.diskordin.core.data.json.response.MemberResponse
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import org.tesserakt.diskordin.core.entity.builder.instance
import java.time.Instant

internal class Member constructor(
    raw: MemberResponse<*>,
    guildId: Snowflake
) : User(raw.user), IMember {
    override val guild: IdentifiedF<ForIO, IGuild> = guildId.identify<IGuild> { client.getGuild(it) }

    override suspend fun asMember(guildId: Snowflake): IMember = this

    override suspend fun addRole(role: IRole, reason: String?) = rest.effect {
        guildService.addMemberRole(guild.id, id, role.id, reason)
    }

    override suspend fun addRole(roleId: Snowflake, reason: String?) = rest.effect {
        guildService.addMemberRole(guild.id, id, roleId, reason)
    }

    override suspend fun removeRole(role: IRole, reason: String?) = rest.effect {
        guildService.deleteMemberRole(guild.id, id, role.id, reason)
    }

    override suspend fun removeRole(roleId: Snowflake, reason: String?) = rest.effect {
        guildService.deleteMemberRole(guild.id, id, roleId, reason)
    }

    override suspend fun edit(builder: MemberEditBuilder.() -> Unit) = rest.effect {
        val inst = builder.instance(::MemberEditBuilder)
        guildService.editMember(guild.id, id, inst.create(), inst.reason)
    }.let { client.getMember(id, guild.id) }

    override fun toString(): String {
        return "Member(guild=$guild, nickname=$nickname, roles=$roles, joinTime=$joinTime, mention='$mention') " +
                "   ${super.toString()}"
    }

    override val nickname: String? = raw.nick

    override val roles = Stream.chunk(Chunk.array(raw.roles))
        .effectMap { guild().getRole(it) }
        .filterOption()

    override val joinTime: Instant = raw.joinedAt
    override val mention: String = "<@!$id>"
}