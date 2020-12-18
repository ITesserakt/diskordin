@file:Suppress("unused", "UNUSED_PARAMETER")

package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import arrow.core.ListK
import arrow.fx.ForIO
import kotlinx.coroutines.flow.Flow
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

interface IGuild : IEntity, INamed, IEditable<IGuild, GuildEditBuilder> {
    @ExperimentalTime
    val afkChannelTimeout: Duration
    val iconHash: String?
    val splashHash: String?
    val owner: IdentifiedF<ForIO, IMember>
    val afkChannel: IdentifiedIO<IVoiceChannel>?
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
    val invites: Flow<IGuildInvite>
    val emojis: Flow<ICustomEmoji>
    val bans: Flow<IBan>
    val integrations: Flow<IIntegration>
    val roles: List<IRole>
    val cachedChannels: List<IGuildChannel>
    val members: List<IMember>
    val isFullyLoaded: Boolean

    fun getRole(id: Snowflake): IRole?
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
    suspend fun ban(member: IMember, builder: BanQuery.() -> Unit)
    suspend fun getMembers(query: MemberQuery.() -> Unit): ListK<IMember>
    suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit)
    suspend fun pardon(userId: Snowflake, reason: String?)
    suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int
    suspend fun addIntegration(id: Snowflake, type: String)
    fun getEveryoneRole(): IdentifiedF<ForId, IRole>
    fun <C : IGuildChannel> getChannel(channelId: Snowflake): C
    suspend fun getWidget(): IGuildWidget
    suspend fun getVanityUrl(): IGuildInvite?
    suspend fun addRole(name: String, color: Color, builder: RoleCreateBuilder.() -> Unit): IRole
    suspend fun moveChannels(vararg builder: Pair<Snowflake, Int>)

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

    enum class Feature {

        /**
         * Guild has access to set an invite splash background
         */
        INVITE_SPLASH,

        /**
         * Guild has access to set 384kbps bitrate in voice (previously VIP voice servers)
         */
        VIP_REGIONS,

        /**
         * Guild has access to set a vanity URL
         */
        VANITY_URL,

        /**
         * Guild is verified
         */
        VERIFIED,

        /**
         * Guild is partnered
         */
        PARTNERED,

        /**
         * Guild can enable welcome screen and discovery, and receives community updates
         */
        COMMUNITY,

        /**
         * Guild has access to use commerce features (i.e. create store channels)
         */
        COMMERCE,

        /**
         * Guild has access to create news channels
         */
        NEWS,

        /**
         * Guild is lurkable and able to be discovered in the directory
         */
        DISCOVERABLE,

        /**
         * Guild is able to be featured in the directory
         */
        FEATURABLE,

        /**
         * 	Guild has access to set an animated guild icon
         */
        ANIMATED_ICON,

        /**
         * Guild has access to set a guild banner image
         */
        BANNER,

        /**
         * Guild has enabled the welcome screen
         */
        WELCOME_SCREEN_ENABLED,

        /**
         * Guild has enabled the preview
         */
        PREVIEW_ENABLED,
        ENABLED_DISCOVERABLE_BEFORE,
        MORE_EMOJI
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
}