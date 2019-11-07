package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.ListK
import arrow.fx.IO
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import ru.tesserakt.diskordin.core.entity.query.PruneQuery
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class PartialGuild(raw: UserGuildResponse) : IGuild {
    override val region: IRegion by lazy { delegate.region }
    override val isEmbedEnabled: Boolean by lazy { delegate.isEmbedEnabled }
    override val defaultMessageNotificationLevel: IGuild.DefaultMessageNotificationLevel
            by lazy { delegate.defaultMessageNotificationLevel }
    override val explicitContentFilter: IGuild.ExplicitContentFilter by lazy { delegate.explicitContentFilter }
    override val mfaLevel: IGuild.MFALevel by lazy { delegate.mfaLevel }
    override val isWidgetEnabled: Boolean by lazy { delegate.isWidgetEnabled }
    override val widgetChannel: Identified<IGuildChannel>? by lazy { delegate.widgetChannel }
    override val systemChannel: Identified<IGuildChannel>? by lazy { delegate.systemChannel }
    override val maxMembers: Long? by lazy { delegate.maxMembers }
    override val maxPresences: Long by lazy { delegate.maxPresences }
    override val description: String? by lazy { delegate.description }
    override val bannerHash: String? by lazy { delegate.bannerHash }
    override val premiumTier: IGuild.PremiumTier by lazy { delegate.premiumTier }
    override val premiumSubscriptions: Int? by lazy { delegate.premiumSubscriptions }
    override val features: EnumSet<IGuild.Feature> by lazy { delegate.features }

    private val delegate by lazy { client.getGuild(id).unsafeRunSync() }

    override val iconHash: String? = raw.icon
    override val splashHash: String? by lazy { delegate.splashHash }
    override val owner: Identified<IMember> by lazy { delegate.owner }
    override val afkChannel: Identified<IVoiceChannel>? by lazy { delegate.afkChannel }
    @ExperimentalTime
    override val afkChannelTimeout: Duration by lazy { delegate.afkChannelTimeout }
    override val verificationLevel: IGuild.VerificationLevel by lazy { delegate.verificationLevel }

    override fun getRole(id: Snowflake): IO<IRole> = delegate.getRole(id)

    override fun getEmoji(emojiId: Snowflake): IO<ICustomEmoji> = delegate.getEmoji(emojiId)

    override fun createEmoji(builder: EmojiCreateBuilder.() -> Unit): IO<ICustomEmoji> =
        delegate.createEmoji(builder)

    override fun editOwnNickname(builder: NicknameEditBuilder.() -> Unit): IO<String?> =
        delegate.editOwnNickname(builder)

    override fun addTextChannel(builder: TextChannelCreateBuilder.() -> Unit): IO<TextChannel> =
        delegate.addTextChannel(builder)

    override fun addVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): IO<VoiceChannel> =
        delegate.addVoiceChannel(builder)

    override fun moveChannels(vararg builder: PositionEditBuilder.() -> Unit) = delegate.moveChannels(*builder)

    override fun addMember(userId: Snowflake, builder: MemberAddBuilder.() -> Unit): IO<IMember> =
        delegate.addMember(userId, builder)

    override fun kick(member: IMember, reason: String?) = delegate.kick(member, reason)

    override fun kick(memberId: Snowflake, reason: String?) = delegate.kick(memberId, reason)

    override fun addRole(builder: RoleCreateBuilder.() -> Unit): IO<IRole> = delegate.addRole(builder)

    override fun moveRoles(vararg builder: PositionEditBuilder.() -> Unit): IO<ListK<IRole>> =
        delegate.moveRoles(*builder)

    override fun getBan(userId: Snowflake): IO<IBan> = delegate.getBan(userId)

    override fun ban(member: IMember, builder: BanQuery.() -> Unit) = delegate.ban(member, builder)

    override fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) = delegate.ban(memberId, builder)

    override fun pardon(userId: Snowflake, reason: String?) = delegate.pardon(userId, reason)

    override fun getPruneCount(builder: PruneQuery.() -> Unit): IO<Int> = delegate.getPruneCount(builder)

    override fun addIntegration(builder: IntegrationCreateBuilder.() -> Unit) = delegate.addIntegration(builder)

    override fun getEveryoneRole(): IO<IRole> = delegate.getEveryoneRole()

    override fun <C : IGuildChannel> getChannel(id: Snowflake): IO<C> = delegate.getChannel(id)

    override val members: IO<ListK<IMember>> by lazy { delegate.members }
    override val invites: IO<ListK<IGuildInvite>> by lazy { delegate.invites }
    override val emojis: IO<ListK<ICustomEmoji>> by lazy { delegate.emojis }
    override val bans: IO<ListK<IBan>> by lazy { delegate.bans }
    override val integrations: IO<ListK<IIntegration>> by lazy { delegate.integrations }
    override val roles: IO<ListK<IRole>> by lazy { delegate.roles }
    override val channels: IO<ListK<IGuildChannel>> by lazy { delegate.channels }
    override val id: Snowflake = raw.id
    override val name: String = raw.name

    override fun delete(reason: String?) = delegate.delete(reason)

    override fun edit(builder: GuildEditBuilder.() -> Unit): IO<IGuild> = delegate.edit(builder)

    override fun toString(): String {
        return StringBuilder("PartialGuild(")
            .appendln("iconHash=$iconHash, ")
            .appendln("id=$id, ")
            .appendln("name='$name'")
            .appendln(")")
            .toString()
    }
}