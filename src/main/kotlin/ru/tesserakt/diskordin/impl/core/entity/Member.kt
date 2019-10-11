package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.util.Identified
import java.time.Instant
import java.time.format.DateTimeFormatter

class Member constructor(
    raw: GuildMemberResponse,
    guildId: Snowflake
) : IMember {
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

    override val joinTime: Instant = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(raw.joinedAt, Instant::from)

    override val username: String = raw.user.username

    override val discriminator: Short = raw.user.discriminator.toShort()

    override val isBot: Boolean = raw.user.bot ?: false

    override val id: Snowflake = raw.user.id.asSnowflake()

    override val guild: Identified<IGuild> = Identified(guildId) {
        client.findGuild(it) ?: throw IllegalArgumentException("Guild id isn`t right!")
    }

    override val mention: String = "<@!$id>"
}