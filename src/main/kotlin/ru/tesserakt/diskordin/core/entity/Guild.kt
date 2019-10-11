@file:Suppress("unused", "UNUSED_PARAMETER")

package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import ru.tesserakt.diskordin.core.entity.query.PruneQuery
import ru.tesserakt.diskordin.util.Identified
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface IGuild : IEntity, INamed, IDeletable, IEditable<IGuild, GuildEditBuilder> {
    val iconHash: String?
    val splashHash: String?
    val owner: Identified<IMember>
    val afkChannel: Identified<IVoiceChannel>?
    @ExperimentalTime
    val afkChannelTimeout: Duration
    val verificationLevel: VerificationLevel

    suspend fun getRole(id: Snowflake): IRole
    suspend fun getEmoji(emojiId: Snowflake): ICustomEmoji
    suspend fun createEmoji(builder: EmojiCreateBuilder.() -> Unit): ICustomEmoji
    suspend fun editOwnNickname(builder: NicknameEditBuilder.() -> Unit): String?
    suspend fun addTextChannel(builder: TextChannelCreateBuilder.() -> Unit): ITextChannel
    suspend fun addVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel
    suspend fun moveChannels(vararg builder: PositionEditBuilder.() -> Unit)
    suspend fun addMember(userId: Snowflake, builder: MemberAddBuilder.() -> Unit): IMember
    suspend fun kick(member: IMember, reason: String?)
    suspend fun kick(memberId: Snowflake, reason: String?)
    suspend fun addRole(builder: RoleCreateBuilder.() -> Unit): IRole
    suspend fun moveRoles(vararg builder: PositionEditBuilder.() -> Unit): List<IRole>
    suspend fun findBan(userId: Snowflake): IBan?
    suspend fun ban(member: IMember, builder: BanQuery.() -> Unit)
    suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit)
    suspend fun pardon(userId: Snowflake, reason: String?)
    suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int
    suspend fun addIntegration(builder: IntegrationCreateBuilder.() -> Unit)
    suspend fun getEveryoneRole(): IRole
    suspend fun <C : IGuildChannel> getChannel(id: Snowflake): C

    val members: Flow<IMember>
    val invites: Flow<IGuildInvite>
    val emojis: Flow<ICustomEmoji>
    val bans: Flow<IBan>
    val integrations: Flow<IIntegration>
    val roles: Flow<IRole>
    val channels: Flow<IGuildChannel>

    enum class VerificationLevel {
        None,
        Low,
        Medium,
        High,
        VeryHigh;
    }

    enum class DefaultMessageNotificationLevel {
        AllMessages,
        OnlyMentions;
    }

    enum class ExplicitContentFilter {
        Disabled,
        MembersWithoutRoles,
        AllMembers;
    }
}