package org.tesserakt.diskordin.impl.core.entity

import arrow.core.ForId
import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.coroutines.stream.Stream
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.*
import org.tesserakt.diskordin.core.entity.query.BanQuery
import org.tesserakt.diskordin.core.entity.query.MemberQuery
import org.tesserakt.diskordin.core.entity.query.PruneQuery
import org.tesserakt.diskordin.core.entity.query.query
import org.tesserakt.diskordin.impl.core.entity.`object`.Region
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.rest.stream
import java.awt.Color
import java.io.File
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@Suppress("UNCHECKED_CAST", "CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
internal class Guild(override val raw: GuildResponse) : IGuild,
    ICacheable<IGuild, UnwrapContext.EmptyContext, GuildResponse> {
    override val members: List<IMember> = raw.members.map {
        when (it) {
            is GuildMemberResponse -> it.unwrap(id)
            is JoinMemberResponse -> it.unwrap()
        }
    }

    override val region: IRegion = Region(
        VoiceRegionResponse(
            raw.region,
            raw.region,
            vip = false,
            optimal = false,
            deprecated = false,
            custom = false
        )
    )

    override val isEmbedEnabled: Boolean = raw.embed_enabled ?: false

    override val defaultMessageNotificationLevel: IGuild.DefaultMessageNotificationLevel =
        IGuild.DefaultMessageNotificationLevel.values().first { raw.default_message_notifications == it.ordinal }

    override val explicitContentFilter: IGuild.ExplicitContentFilter =
        IGuild.ExplicitContentFilter.values().first { raw.explicit_content_filter == it.ordinal }

    override val mfaLevel: IGuild.MFALevel =
        IGuild.MFALevel.values().first { it.ordinal == raw.mfa_level }

    override val isWidgetEnabled: Boolean = raw.widget_enabled ?: false

    override val widgetChannel = raw.widget_channel_id?.identifyId {
        getChannel<IGuildChannel>(it)
    }

    override val systemChannel = raw.system_channel_id?.identifyId {
        getChannel<IGuildChannel>(it)
    }

    override val maxMembers: Long? = raw.max_members

    override val maxPresences: Long = raw.max_presences ?: 5000

    override val description: String? = raw.description

    override val bannerHash: String? = raw.banner

    override val premiumTier: IGuild.PremiumTier =
        IGuild.PremiumTier.values().first { it.ordinal == raw.premium_tier ?: 0 }

    override val premiumSubscriptions: Int? = raw.premiumSubscribersCount

    override val features: EnumSet<IGuild.Feature> = if (raw.features.isEmpty())
        EnumSet.noneOf(IGuild.Feature::class.java)
    else EnumSet.copyOf(raw.features.map { IGuild.Feature.valueOf(it) })

    override val id: Snowflake = raw.id

    override fun getRole(id: Snowflake) = roles.find { it.id == id }

    override suspend fun getEmoji(emojiId: Snowflake) =
        rest.call<ForId, ICustomEmoji, EmojiResponse<ICustomEmoji>>(id, Id.functor()) {
            emojiService.getGuildEmoji(id, emojiId).just()
        }.extract()

    override suspend fun createEmoji(name: String, image: File, roles: Array<Snowflake>): ICustomEmoji =
        rest.call<ForId, ICustomEmoji, EmojiResponse<ICustomEmoji>>(id, Id.functor()) {
            emojiService.createGuildEmoji(id, EmojiCreateBuilder().apply {
                this.name = name
                this.image = image
                this.roles = roles
            }.create()).just()
        }.extract()

    override suspend fun editOwnNickname(newNickname: String): String? = rest.callRaw {
        guildService.editCurrentNickname(id, NicknameEditBuilder(newNickname).create())
    }

    override suspend fun addTextChannel(name: String, builder: TextChannelCreateBuilder.() -> Unit): ITextChannel =
        addChannelJ(TextChannelCreateBuilder(name), builder)

    override suspend fun addVoiceChannel(name: String, builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel =
        addChannelJ(VoiceChannelCreateBuilder(name), builder)

    private suspend inline fun <C : IGuildChannel, reified B : GuildChannelCreateBuilder<out C>> addChannelJ(
        instance: B,
        noinline builder: B.() -> Unit
    ): C = rest.call {
        instance.apply(builder)
        guildService.createGuildChannel(id, instance.create(), instance.reason)
    } as C

    override suspend fun moveChannels(vararg builder: Pair<Snowflake, Int>) = rest.effect {
        guildService.editGuildChannelPositions(
            id, builder.map { PositionEditBuilder(it.first, it.second).create() }.toTypedArray()
        )
    }

    override suspend fun addMember(userId: Snowflake, accessToken: String, builder: MemberAddBuilder.() -> Unit) =
        rest.call(id, Id.functor()) {
            guildService.newMember(id, userId, MemberAddBuilder(accessToken).apply(builder).create()).just()
        }.extract()

    override suspend fun kick(member: IMember, reason: String?) = kick(member.id, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = rest.effect {
        guildService.removeMember(id, memberId, reason)
    }

    override suspend fun addRole(name: String, color: Color, builder: RoleCreateBuilder.() -> Unit) =
        rest.call(id, Id.functor()) {
            val inst = builder.instance { RoleCreateBuilder(name, color) }
            guildService.createRole(id, inst.create(), inst.reason).just()
        }.extract()

    override suspend fun moveRoles(vararg builder: Pair<Snowflake, Int>): ListK<IRole> =
        rest.call(id, ListK.functor()) {
            guildService.editRolePositions(id, builder.map { (id, pos) ->
                PositionEditBuilder(id, pos).create()
            }.toTypedArray())
        }.fix()

    override suspend fun getBan(userId: Snowflake): IBan = rest.call {
        guildService.getBan(id, userId)
    }

    @ExperimentalTime
    override suspend fun ban(member: IMember, builder: BanQuery.() -> Unit) = ban(member.id, builder)

    @ExperimentalTime
    override suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) = rest.effect {
        guildService.ban(id, memberId, builder.query(::BanQuery))
    }

    override suspend fun pardon(userId: Snowflake, reason: String?) = rest.effect {
        guildService.removeBan(id, userId, reason)
    }

    override suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int = rest.callRaw {
        guildService.getPruneCount(id, builder.query(::PruneQuery))
    }

    override suspend fun addIntegration(id: Snowflake, type: String): Unit = rest.effect {
        guildService.createIntegration(id, IntegrationCreateBuilder(id, type).create())
    }

    override fun getEveryoneRole() = id identifyId { getRole(it)!! } //everyone role id == guild id

    override fun <C : IGuildChannel> getChannel(id: Snowflake): C = cache[id] as C

    override suspend fun getWidget(): IGuildWidget = rest.call {
        guildService.getGuildWidget(id)
    }

    override suspend fun getVanityUrl(): IGuildInvite? =
        if (IGuild.Feature.VANITY_URL !in features) null
        else rest.call {
            guildService.getVanityUrl(id)
        }

    override val invites: Stream<IGuildInvite> = rest.stream {
        guildService.getInvites(id)
    }

    override val emojis = rest.stream<ICustomEmoji, EmojiResponse<ICustomEmoji>>(id) {
        emojiService.getGuildEmojis(id)
    }

    override val bans = rest.stream {
        guildService.getBans(id)
    }

    override val integrations = rest.stream(id) {
        guildService.getIntegrations(id)
    }

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild = rest.call {
        guildService.editGuild(id, builder.build(::GuildEditBuilder))
    }

    override fun fromCache(): IGuild = cache[id] as IGuild

    override fun copy(changes: (GuildResponse) -> GuildResponse): IGuild =
        raw.let(changes).unwrap()

    override val iconHash: String? = raw.icon
    override val splashHash: String? = raw.splash

    override val owner = raw.owner_id.identify<IMember> { userId -> client.getMember(userId, id) }

    override val afkChannel = raw.afk_channel_id?.identify<IVoiceChannel> { id ->
        (channels.find { it.id == id } ?: client.getChannel(id)) as IVoiceChannel
    }

    @ExperimentalTime
    override val afkChannelTimeout: Duration = raw.afk_timeout.seconds

    override val verificationLevel =
        IGuild.VerificationLevel.values().first { it.ordinal == raw.verification_level }

    override val roles = raw.roles.map { it.unwrap(id) }

    override suspend fun getMembers(query: MemberQuery.() -> Unit) = rest.call(id, ListK.functor()) {
        guildService.getMembers(id, query.query(::MemberQuery))
    }.fix().also { list -> cache += list.associateBy { it.id } }

    override val channels get() = cache.values.filterIsInstance<IGuildChannel>().filter { it.guild.id == id }

    override val name: String = raw.name

    @ExperimentalTime
    override fun toString(): String {
        return "Guild(region=$region, isEmbedEnabled=$isEmbedEnabled, defaultMessageNotificationLevel=$defaultMessageNotificationLevel, explicitContentFilter=$explicitContentFilter, mfaLevel=$mfaLevel, isWidgetEnabled=$isWidgetEnabled, widgetChannel=$widgetChannel, systemChannel=$systemChannel, maxMembers=$maxMembers, maxPresences=$maxPresences, description=$description, bannerHash=$bannerHash, premiumTier=$premiumTier, premiumSubscriptions=$premiumSubscriptions, features=$features, id=$id, invites=$invites, emojis=$emojis, bans=$bans, integrations=$integrations, iconHash=$iconHash, splashHash=$splashHash, owner=$owner, afkChannel=$afkChannel, afkChannelTimeout=$afkChannelTimeout, verificationLevel=$verificationLevel, name='$name')"
    }
}