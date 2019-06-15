package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.core.entity.IRole
import ru.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import ru.tesserakt.diskordin.impl.core.service.GuildService
import ru.tesserakt.diskordin.util.Identified
import java.time.Instant
import java.time.format.DateTimeFormatter

class Member constructor(
    raw: GuildMemberResponse,
    guildId: Snowflake,
    override val kodein: Kodein = Diskordin.kodein
) : IMember {
    override suspend fun addRole(role: IRole, reason: String?) =
        GuildService.addRoleToMember(guild.state, id, role.id, reason)

    override suspend fun addRole(roleId: Snowflake, reason: String?) =
        GuildService.addRoleToMember(guild.state, id, roleId, reason)

    override suspend fun removeRole(role: IRole, reason: String?) =
        GuildService.removeRoleFromMember(guild.state, id, role.id, reason)

    override suspend fun removeRole(roleId: Snowflake, reason: String?) =
        GuildService.removeRoleFromMember(guild.state, id, roleId, reason)

    @ExperimentalCoroutinesApi
    override suspend fun edit(builder: MemberEditBuilder.() -> Unit) =
        GuildService.editMember(guild.state, id, builder).run {
            guild.extract().await().members.first { it.id == id }
        }

    override val client: IDiscordClient by instance()

    override val nickname: String? = raw.nick

    @ExperimentalCoroutinesApi
    override val roles: Flow<IRole> = flow {
        raw.roles
            .map(Snowflake.Companion::of)
            .map {
                guild.extract().await()
                    .findRole(it) ?: throw IllegalStateException("An error occurred")
            }.forEach { emit(it) }
    }

    override val joinTime: Instant = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(raw.joinedAt, Instant::from)

    override val username: String = raw.user.username

    override val discriminator: Short = raw.user.discriminator.toShort()

    override val isBot: Boolean = raw.user.bot ?: false

    override val id: Snowflake = raw.user.id.asSnowflake()

    override val guild: Identified<IGuild> = Identified(guildId) {
        client.coroutineScope.async {
            client.findGuild(it) ?: throw IllegalArgumentException("Guild id isn`t right!")
        }
    }

    override val mention: String = "<@!$id>"
}