package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.MessageMemberResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant

class MessageMember(private val raw: MessageMemberResponse, guildId: Snowflake) : IMember {
    override val avatar: String? by lazy { delegate.avatar }
    override val mfaEnabled: Boolean by lazy { delegate.mfaEnabled }
    override val locale: String? by lazy { delegate.locale }
    override val verified: Boolean by lazy { delegate.verified }
    override val email: String? by lazy { delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Short> by lazy { delegate.flags }
    override val premiumType: IUser.Type? by lazy { delegate.premiumType }
    private val delegate by lazy { runBlocking { guild().members.first { it.nickname == raw.nick } } }
    override val guild: Identified<IGuild> = guildId combine { client.getGuild(it) }
    override val nickname: String? = raw.nick
    override val roles: Flow<IRole> = raw.roles.map { it.asSnowflake() }.asFlow().map { guild().getRole(it) }
    override val joinTime: Instant = raw.joined_at

    override suspend fun addRole(role: IRole, reason: String?) =
        delegate.addRole(role, reason)

    override suspend fun addRole(roleId: Snowflake, reason: String?) =
        delegate.addRole(roleId, reason)

    override suspend fun removeRole(role: IRole, reason: String?) =
        delegate.removeRole(role, reason)

    override suspend fun removeRole(roleId: Snowflake, reason: String?) =
        delegate.removeRole(roleId, reason)

    override val username: String by lazy { delegate.username }

    override val discriminator: Short by lazy { delegate.discriminator }

    override val isBot: Boolean by lazy { delegate.isBot }

    override suspend fun asMember(guildId: Snowflake): IMember = delegate

    override val id: Snowflake by lazy { delegate.id }
    override val mention: String by lazy { delegate.mention }

    override suspend fun edit(builder: MemberEditBuilder.() -> Unit): IMember = delegate.edit(builder)

    override fun toString(): String {
        return "MessageMember(guild=$guild, nickname=$nickname, roles=$roles, joinTime=$joinTime)"
    }
}