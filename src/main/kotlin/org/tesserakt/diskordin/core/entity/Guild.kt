@file:Suppress("unused", "UNUSED_PARAMETER")

package org.tesserakt.diskordin.core.entity

import arrow.core.ListK
import arrow.fx.IO
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.*
import org.tesserakt.diskordin.core.entity.query.BanQuery
import org.tesserakt.diskordin.core.entity.query.PruneQuery
import org.tesserakt.diskordin.impl.core.entity.TextChannel
import org.tesserakt.diskordin.impl.core.entity.VoiceChannel
import java.io.File
import java.util.*
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
    val region: IRegion
    val isEmbedEnabled: Boolean
    val defaultMessageNotificationLevel: DefaultMessageNotificationLevel
    val explicitContentFilter: ExplicitContentFilter
    val mfaLevel: MFALevel
    val isWidgetEnabled: Boolean
    val widgetChannel: Identified<IGuildChannel>?
    val systemChannel: Identified<IGuildChannel>?
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

    fun getRole(id: Snowflake): IO<IRole>
    fun getEmoji(emojiId: Snowflake): IO<ICustomEmoji>
    fun createEmoji(name: String, image: File, roles: Array<Snowflake>): IO<ICustomEmoji>
    fun editOwnNickname(newNickname: String): IO<String?>
    fun addTextChannel(name: String, builder: TextChannelCreateBuilder.() -> Unit): IO<TextChannel>
    fun addVoiceChannel(name: String, builder: VoiceChannelCreateBuilder.() -> Unit): IO<VoiceChannel>
    fun moveChannels(vararg builder: PositionEditBuilder.() -> Unit): IO<Unit>
    fun addMember(userId: Snowflake, accessToken: String, builder: MemberAddBuilder.() -> Unit): IO<IMember>
    fun kick(member: IMember, reason: String?): IO<Unit>
    fun kick(memberId: Snowflake, reason: String?): IO<Unit>
    fun addRole(builder: RoleCreateBuilder.() -> Unit): IO<IRole>
    fun moveRoles(vararg builder: Pair<Snowflake, Int>): IO<ListK<IRole>>
    fun getBan(userId: Snowflake): IO<IBan>
    fun ban(member: IMember, builder: BanQuery.() -> Unit): IO<Unit>
    fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit): IO<Unit>
    fun pardon(userId: Snowflake, reason: String?): IO<Unit>
    fun getPruneCount(builder: PruneQuery.() -> Unit): IO<Int>
    fun addIntegration(id: Snowflake, type: String): IO<Unit>
    fun getEveryoneRole(): IO<IRole>
    fun <C : IGuildChannel> getChannel(id: Snowflake): IO<C>

    val members: IO<ListK<IMember>>
    val invites: IO<ListK<IGuildInvite>>
    val emojis: IO<ListK<ICustomEmoji>>
    val bans: IO<ListK<IBan>>
    val integrations: IO<ListK<IIntegration>>
    val roles: IO<ListK<IRole>>
    val channels: IO<ListK<IGuildChannel>>

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