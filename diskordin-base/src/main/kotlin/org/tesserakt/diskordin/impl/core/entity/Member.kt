package org.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import org.tesserakt.diskordin.core.entity.builder.instance

internal class Member<C : UnwrapContext> constructor(
    override val raw: MemberResponse<C>,
    guildId: Snowflake
) : User(raw.user), IMember, ICacheable<IMember, C, MemberResponse<C>> {
    override val guild = guildId deferred { client.getGuild(it) }

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

    override val roles = raw.roles.asFlow().mapNotNull { guild().getRole(it) }

    override val joinTime: Instant = raw.joinedAt
    override val mention: String = "<@!$id>"

    override fun copy(changes: (MemberResponse<C>) -> MemberResponse<C>): IMember = when (val copy = raw.run(changes)) {
        is JoinMemberResponse -> copy.unwrap()
        is GuildMemberResponse -> copy.unwrap(guild.id)
    }

}