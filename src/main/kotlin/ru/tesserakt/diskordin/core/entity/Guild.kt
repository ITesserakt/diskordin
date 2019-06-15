@file:Suppress("unused", "UNUSED_PARAMETER")

package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import kotlin.reflect.KClass

interface IGuild : IEntity, INamed, IDeletable, IEditable<IGuild, GuildEditBuilder> {
    val iconHash: String?
    val splashHash: String?
    val owner: Identified<IMember>
    val afkChannel: Identified<IVoiceChannel>?
    val afkChannelTimeout: Duration
    val verificationLevel: VerificationLevel

    suspend fun findRole(id: Snowflake): IRole?
    suspend fun findEmoji(emojiId: Snowflake): ICustomEmoji?
    suspend fun createEmoji(builder: EmojiCreateBuilder.() -> Unit): ICustomEmoji
    suspend fun editOwnNickname(builder: NicknameEditBuilder.() -> Unit)
    suspend fun <C : IGuildChannel, B : GuildChannelCreateBuilder<C>> addChannel(
        builder: B.() -> Unit,
        clazz: KClass<B>
    ): C

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

    @ExperimentalCoroutinesApi
    val members: Flow<IMember>
    @ExperimentalCoroutinesApi
    val invites: Flow<IGuildInvite>
    @ExperimentalCoroutinesApi
    val emojis: Flow<ICustomEmoji>
    @ExperimentalCoroutinesApi
    val bans: Flow<IBan>
    @ExperimentalCoroutinesApi
    val integrations: Flow<IIntegration>
    @ExperimentalCoroutinesApi
    val roles: Flow<IRole>
    @ExperimentalCoroutinesApi
    val channels: Flow<IGuildChannel>

    enum class VerificationLevel {
        None,
        Low,
        Medium,
        High,
        VeryHigh;

        companion object {
            fun of(value: Int) = when (value) {
                0 -> None
                1 -> Low
                2 -> Medium
                3 -> High
                4 -> VeryHigh
                else -> throw NoSuchElementException()
            }
        }
    }
}