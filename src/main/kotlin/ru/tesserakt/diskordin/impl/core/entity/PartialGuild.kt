package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import ru.tesserakt.diskordin.core.entity.query.PruneQuery
import ru.tesserakt.diskordin.util.Identified
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class PartialGuild(raw: UserGuildResponse) : IGuild {
    private val delegate by lazy { runBlocking { client.findGuild(id)!! } }

    override val iconHash: String? = raw.icon
    override val splashHash: String? by lazy { delegate.splashHash }
    override val owner: Identified<IMember> by lazy { delegate.owner }
    override val afkChannel: Identified<IVoiceChannel>? by lazy { delegate.afkChannel }
    @ExperimentalTime
    override val afkChannelTimeout: Duration by lazy { delegate.afkChannelTimeout }
    override val verificationLevel: IGuild.VerificationLevel by lazy { delegate.verificationLevel }

    override suspend fun getRole(id: Snowflake): IRole = delegate.getRole(id)

    override suspend fun getEmoji(emojiId: Snowflake): ICustomEmoji = delegate.getEmoji(emojiId)

    override suspend fun createEmoji(builder: EmojiCreateBuilder.() -> Unit): ICustomEmoji =
        delegate.createEmoji(builder)

    override suspend fun editOwnNickname(builder: NicknameEditBuilder.() -> Unit): String? =
        delegate.editOwnNickname(builder)

    override suspend fun addTextChannel(builder: TextChannelCreateBuilder.() -> Unit): ITextChannel =
        delegate.addTextChannel(builder)

    override suspend fun addVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel =
        delegate.addVoiceChannel(builder)

    override suspend fun moveChannels(vararg builder: PositionEditBuilder.() -> Unit) = delegate.moveChannels(*builder)

    override suspend fun addMember(userId: Snowflake, builder: MemberAddBuilder.() -> Unit): IMember =
        delegate.addMember(userId, builder)

    override suspend fun kick(member: IMember, reason: String?) = delegate.kick(member, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = delegate.kick(memberId, reason)

    override suspend fun addRole(builder: RoleCreateBuilder.() -> Unit): IRole = delegate.addRole(builder)

    override suspend fun moveRoles(vararg builder: PositionEditBuilder.() -> Unit): List<IRole> =
        delegate.moveRoles(*builder)

    override suspend fun findBan(userId: Snowflake): IBan? = delegate.findBan(userId)

    override suspend fun ban(member: IMember, builder: BanQuery.() -> Unit) = delegate.ban(member, builder)

    override suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) = delegate.ban(memberId, builder)

    override suspend fun pardon(userId: Snowflake, reason: String?) = delegate.pardon(userId, reason)

    override suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int = delegate.getPruneCount(builder)

    override suspend fun addIntegration(builder: IntegrationCreateBuilder.() -> Unit) = delegate.addIntegration(builder)

    override suspend fun getEveryoneRole(): IRole = delegate.getEveryoneRole()

    override suspend fun <C : IGuildChannel> getChannel(id: Snowflake): C = delegate.getChannel(id)

    override val members: Flow<IMember> by lazy { delegate.members }
    override val invites: Flow<IGuildInvite> by lazy { delegate.invites }
    override val emojis: Flow<ICustomEmoji> by lazy { delegate.emojis }
    override val bans: Flow<IBan> by lazy { delegate.bans }
    override val integrations: Flow<IIntegration> by lazy { delegate.integrations }
    override val roles: Flow<IRole> by lazy { delegate.roles }
    override val channels: Flow<IGuildChannel> by lazy { delegate.channels }
    override val id: Snowflake = raw.id
    override val name: String = raw.name

    override suspend fun delete(reason: String?) = delegate.delete(reason)

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild = delegate.edit(builder)
}