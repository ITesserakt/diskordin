@file:Suppress("unused", "UNUSED_PARAMETER")

package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import arrow.core.ListK
import arrow.core.Option
import arrow.fx.ForIO
import arrow.fx.coroutines.stream.Stream
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.IdentifiedIO
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.*
import org.tesserakt.diskordin.core.entity.query.BanQuery
import org.tesserakt.diskordin.core.entity.query.MemberQuery
import org.tesserakt.diskordin.core.entity.query.PruneQuery
import java.awt.Color
import java.io.File
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface IGuild : IEntity, INamed, IDeletable, IEditable<IGuild, GuildEditBuilder> {
    val iconHash: String?
    val splashHash: String?
    val owner: IdentifiedF<ForIO, IMember>
    val afkChannel: IdentifiedIO<IVoiceChannel>?

    @ExperimentalTime
    val afkChannelTimeout: Duration
    val verificationLevel: VerificationLevel
    val region: IRegion
    val isEmbedEnabled: Boolean
    val defaultMessageNotificationLevel: DefaultMessageNotificationLevel
    val explicitContentFilter: ExplicitContentFilter
    val mfaLevel: MFALevel
    val isWidgetEnabled: Boolean
    val widgetChannel: IdentifiedF<ForId, IGuildChannel>?
    val systemChannel: IdentifiedF<ForId, IGuildChannel>?
    val maxMembers: Long?
    val maxPresences: Long
    val description: String?
    val bannerHash: String?
    val premiumTier: PremiumTier
    val premiumSubscriptions: Int?
    val features: EnumSet<Feature>

    enum class Feature {
        INVITE_SPLASH,
        VIP_REGIONS,
        VANITY_URL,
        VERIFIED,
        PARTNERED,
        PUBLIC,
        COMMERCE,
        NEWS,
        DISCOVERABLE,
        FEATURABLE,
        ANIMATED_ICON,
        BANNER
    }

    enum class PremiumTier {
        None,
        Tier1,
        Tier2,
        Tier3
    }

    enum class MFALevel {
        None, Elevated
    }

    fun getRole(id: Snowflake): Option<IRole>
    suspend fun getEmoji(emojiId: Snowflake): ICustomEmoji
    suspend fun createEmoji(name: String, image: File, roles: Array<Snowflake>): ICustomEmoji
    suspend fun editOwnNickname(newNickname: String): String?
    suspend fun addTextChannel(name: String, builder: TextChannelCreateBuilder.() -> Unit): ITextChannel
    suspend fun addVoiceChannel(name: String, builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel
    suspend fun addMember(userId: Snowflake, accessToken: String, builder: MemberAddBuilder.() -> Unit): IMember
    suspend fun kick(member: IMember, reason: String?)
    suspend fun kick(memberId: Snowflake, reason: String?)
    suspend fun moveRoles(vararg builder: Pair<Snowflake, Int>): ListK<IRole>
    suspend fun getBan(userId: Snowflake): IBan

    @OptIn(ExperimentalTime::class)
    suspend fun ban(member: IMember, builder: BanQuery.() -> Unit)

    suspend fun getMembers(query: MemberQuery.() -> Unit): ListK<IMember>

    @OptIn(ExperimentalTime::class)
    suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit)

    suspend fun pardon(userId: Snowflake, reason: String?)
    suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int
    suspend fun addIntegration(id: Snowflake, type: String)
    fun getEveryoneRole(): IdentifiedF<ForId, IRole>
    fun <C : IGuildChannel> getChannel(id: Snowflake): C

    val invites: Stream<IGuildInvite>
    val emojis: Stream<ICustomEmoji>
    val bans: Stream<IBan>
    val integrations: Stream<IIntegration>
    val roles: List<IRole>
    val channels: List<IGuildChannel>

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

    suspend fun addRole(name: String, color: Color, builder: RoleCreateBuilder.() -> Unit): IRole
    suspend fun moveChannels(vararg builder: Pair<Snowflake, Int>)
}