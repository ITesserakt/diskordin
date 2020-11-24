package org.tesserakt.diskordin.impl.core.entity

import arrow.core.ForId
import arrow.core.ListK
import arrow.fx.ForIO
import arrow.fx.coroutines.stream.Stream
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import org.tesserakt.diskordin.core.entity.*
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

internal class PartialGuild(raw: UserGuildResponse) : IGuild {
    override val region: IRegion by lazy { delegate.region }
    override val isEmbedEnabled: Boolean by lazy { delegate.isEmbedEnabled }
    override val defaultMessageNotificationLevel: IGuild.DefaultMessageNotificationLevel
            by lazy { delegate.defaultMessageNotificationLevel }
    override val explicitContentFilter: IGuild.ExplicitContentFilter by lazy { delegate.explicitContentFilter }
    override val mfaLevel: IGuild.MFALevel by lazy { delegate.mfaLevel }
    override val isWidgetEnabled: Boolean by lazy { delegate.isWidgetEnabled }
    override val widgetChannel: IdentifiedF<ForId, IGuildChannel>? by lazy { delegate.widgetChannel }
    override val systemChannel: IdentifiedF<ForId, IGuildChannel>? by lazy { delegate.systemChannel }
    override val maxMembers: Long? by lazy { delegate.maxMembers }
    override val maxPresences: Long by lazy { delegate.maxPresences }
    override val description: String? by lazy { delegate.description }
    override val bannerHash: String? by lazy { delegate.bannerHash }
    override val premiumTier: IGuild.PremiumTier by lazy { delegate.premiumTier }
    override val premiumSubscriptions: Int? by lazy { delegate.premiumSubscriptions }
    override val features: EnumSet<IGuild.Feature> by lazy { delegate.features }

    private val delegate by lazy { runBlocking { client.getGuild(id) } }

    override val iconHash: String? = raw.icon
    override val splashHash: String? by lazy { delegate.splashHash }
    override val owner: IdentifiedF<ForIO, IMember> by lazy { delegate.owner }
    override val afkChannel by lazy { delegate.afkChannel }

    @ExperimentalTime
    override val afkChannelTimeout: Duration by lazy { delegate.afkChannelTimeout }
    override val verificationLevel: IGuild.VerificationLevel by lazy { delegate.verificationLevel }

    override fun getRole(id: Snowflake) = delegate.getRole(id)

    override suspend fun getEmoji(emojiId: Snowflake): ICustomEmoji = delegate.getEmoji(emojiId)

    override suspend fun createEmoji(name: String, image: File, roles: Array<Snowflake>): ICustomEmoji =
        delegate.createEmoji(name, image, roles)

    override suspend fun editOwnNickname(newNickname: String): String? =
        delegate.editOwnNickname(newNickname)

    override suspend fun addTextChannel(name: String, builder: TextChannelCreateBuilder.() -> Unit): ITextChannel =
        delegate.addTextChannel(name, builder)

    override suspend fun addVoiceChannel(name: String, builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel =
        delegate.addVoiceChannel(name, builder)

    override suspend fun addMember(
        userId: Snowflake,
        accessToken: String,
        builder: MemberAddBuilder.() -> Unit
    ): IMember =
        delegate.addMember(userId, accessToken, builder)

    override suspend fun kick(member: IMember, reason: String?) = delegate.kick(member, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = delegate.kick(memberId, reason)

    override suspend fun moveRoles(vararg builder: Pair<Snowflake, Int>): ListK<IRole> =
        delegate.moveRoles(*builder)

    override suspend fun getBan(userId: Snowflake): IBan = delegate.getBan(userId)

    @ExperimentalTime
    override suspend fun ban(member: IMember, builder: BanQuery.() -> Unit) = delegate.ban(member, builder)

    @ExperimentalTime
    override suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) = delegate.ban(memberId, builder)

    override suspend fun pardon(userId: Snowflake, reason: String?) = delegate.pardon(userId, reason)

    override suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int = delegate.getPruneCount(builder)

    override suspend fun addIntegration(id: Snowflake, type: String) = delegate.addIntegration(id, type)

    override fun getEveryoneRole() = delegate.getEveryoneRole()

    override fun <C : IGuildChannel> getChannel(id: Snowflake) = delegate.getChannel<C>(id)

    override suspend fun getWidget(): IGuildWidget = delegate.getWidget()

    override suspend fun getVanityUrl(): IGuildInvite? = delegate.getVanityUrl()

    override val invites: Stream<IGuildInvite> by lazy { delegate.invites }
    override val emojis: Stream<ICustomEmoji> by lazy { delegate.emojis }
    override val bans: Stream<IBan> by lazy { delegate.bans }
    override val integrations: Stream<IIntegration> by lazy { delegate.integrations }
    override val roles by lazy { delegate.roles }
    override val channels by lazy { delegate.channels }
    override val id: Snowflake = raw.id
    override val name: String = raw.name

    override suspend fun delete(reason: String?) = delegate.delete(reason)

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild = delegate.edit(builder)

    override suspend fun getMembers(query: MemberQuery.() -> Unit): ListK<IMember> = delegate.getMembers(query)

    override suspend fun addRole(name: String, color: Color, builder: RoleCreateBuilder.() -> Unit): IRole =
        delegate.addRole(name, color, builder)

    override suspend fun moveChannels(vararg builder: Pair<Snowflake, Int>): Unit = delegate.moveChannels(*builder)

    override fun toString(): String {
        return "PartialGuild(iconHash=$iconHash, id=$id, name='$name')"
    }
}