package org.tesserakt.diskordin.impl.core.entity

import arrow.fx.ForIO
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.filterNotNull
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.data.*
import org.tesserakt.diskordin.core.data.json.response.MessageMemberResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.time.Instant

@Deprecated("Dead code", level = DeprecationLevel.ERROR)
internal class MessageMember(override val raw: MessageMemberResponse, guildId: Snowflake) : IMember,
    ICacheable<IMember, UnwrapContext.GuildContext, MessageMemberResponse> {
    override val avatar: String? by lazy { delegate.avatar }
    override val mfaEnabled: Boolean by lazy { delegate.mfaEnabled }
    override val locale: String? by lazy { delegate.locale }
    override val verified: Boolean by lazy { delegate.verified }
    override val email: String? by lazy { delegate.email }
    override val flags: ValuedEnum<IUser.Flags, Int> by lazy { delegate.flags }
    override val premiumType: IUser.Type by lazy { delegate.premiumType }
    private val delegate by lazy { runBlocking { client.getMember(id, guildId) } }
    override val guild: IdentifiedF<ForIO, IGuild> = guildId.identify<IGuild> { client.getGuild(it) }
    override val nickname: String? = raw.nick

    override val roles = Stream.iterable(raw.roles)
        .effectMap { guild().getRole(it) }.filterNotNull()

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

    override fun fromCache(): IMember = cache[id] as IMember

    override fun copy(changes: (MessageMemberResponse) -> MessageMemberResponse): IMember =
        raw.run(changes).unwrap(guild.id)
}