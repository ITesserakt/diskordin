@file:Suppress("DEPRECATION")

package org.tesserakt.diskordin.impl.core.entity


import org.tesserakt.diskordin.core.cache.CacheProcessor
import org.tesserakt.diskordin.core.data.*
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
import org.tesserakt.diskordin.rest.flow
import java.awt.Color
import java.io.File
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("UNCHECKED_CAST", "CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
internal class Guild(override val raw: GuildResponse) : IGuild,
    ICacheable<IGuild, UnwrapContext.EmptyContext, GuildResponse> {
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

    override val widgetChannel: LazyIdentified<IGuildChannel>? = raw.widget_channel_id?.lazy {
        getChannel(it)
    }

    override val systemChannel: LazyIdentified<IGuildChannel>? = raw.system_channel_id?.lazy {
        getChannel(it)
    }

    override val maxMembers: Long? = raw.max_members

    override val maxPresences: Long = raw.max_presences ?: 5000

    override val description: String? = raw.description

    override val bannerHash: String? = raw.banner

    override val premiumTier: IGuild.PremiumTier =
        IGuild.PremiumTier.values().first { it.ordinal == (raw.premium_tier ?: 0) }

    override val premiumSubscriptions: Int? = raw.premiumSubscribersCount

    override val features: EnumSet<IGuild.Feature> = if (raw.features.isEmpty())
        EnumSet.noneOf(IGuild.Feature::class.java)
    else EnumSet.copyOf(raw.features.map { IGuild.Feature.valueOf(it) })

    override val id: Snowflake = raw.id

    override fun getRole(id: Snowflake) = roles.find { it.id == id }

    override suspend fun getEmoji(emojiId: Snowflake) =
        rest.callRaw { emojiService.getGuildEmoji(id, emojiId) }.unwrap(id)

    override suspend fun createEmoji(name: String, image: File, roles: Array<Snowflake>): ICustomEmoji = rest.callRaw {
        emojiService.createGuildEmoji(id, EmojiCreateBuilder().apply {
            this.name = name
            this.image = image
            this.roles = roles
        }.create())
    }.unwrap(id)

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
        rest.callRaw {
            guildService.newMember(id, userId, MemberAddBuilder(accessToken).apply(builder).create())
        }.unwrap(id)

    override suspend fun kick(member: IMember, reason: String?) = kick(member.id, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = rest.effect {
        guildService.removeMember(id, memberId, reason)
    }

    override suspend fun addRole(name: String, color: Color, builder: RoleCreateBuilder.() -> Unit) =
        rest.callRaw {
            val inst = builder.instance { RoleCreateBuilder(name, color) }
            guildService.createRole(id, inst.create(), inst.reason)
        }.unwrap(id)

    override suspend fun moveRoles(vararg builder: Pair<Snowflake, Int>): List<IRole> =
        rest.callRaw {
            guildService.editRolePositions(id, builder.map { (id, pos) ->
                PositionEditBuilder(id, pos).create()
            }.toTypedArray())
        }.map { it.unwrap(id) }

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

    override fun getEveryoneRole(): LazyIdentified<IRole> = id lazy { getRole(it)!! } //everyone role id == guild id

    override fun <C : IGuildChannel> getChannel(channelId: Snowflake) =
        cacheSnapshot.getGuildChannel(id, channelId) as C

    override suspend fun getWidget(): IGuildWidget = rest.call {
        guildService.getGuildWidget(id)
    }

    override suspend fun getVanityUrl(): IGuildInvite? =
        if (IGuild.Feature.VANITY_URL !in features) null
        else rest.call {
            guildService.getVanityUrl(id)
        }

    override val invites = rest.flow {
        guildService.getInvites(id)
    }

    override val emojis = rest.flow<ICustomEmoji, EmojiResponse<ICustomEmoji>>(id) {
        emojiService.getGuildEmojis(id)
    }

    override val bans = rest.flow {
        guildService.getBans(id)
    }

    override val integrations = rest.flow<IIntegration, GuildIntegrationResponse>(id) {
        guildService.getIntegrations(id)
    }

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild = rest.call {
        guildService.editGuild(id, builder.build(::GuildEditBuilder))
    }

    override fun copy(changes: (GuildResponse) -> GuildResponse): IGuild =
        raw.let(changes).unwrap()

    override val iconHash: String? = raw.icon
    override val splashHash: String? = raw.splash

    override val owner: DeferredIdentified<IMember> =
        raw.owner_id deferred { userId -> client.getMember(userId, id) }

    override val afkChannel = raw.afk_channel_id?.deferred { id ->
        (cachedChannels.find { it.id == id } ?: client.getChannel(id)) as IVoiceChannel
    }

    @ExperimentalTime
    override val afkChannelTimeout: Duration = Duration.seconds(raw.afk_timeout)

    override val verificationLevel =
        IGuild.VerificationLevel.values().first { it.ordinal == raw.verification_level }

    override val roles = raw.roles.map { it.unwrap(id) }

    override suspend fun getMembers(query: MemberQuery.() -> Unit) = rest.callRaw {
        guildService.getMembers(id, query.query(::MemberQuery))
    }.map { it.unwrap(id) }.onEach { client.context[CacheProcessor].updateData(it) }

    override val cachedChannels = raw.channels.filter { it.guild_id != null }.map { it.unwrap() }

    override val name: String = raw.name

    override val members: List<IMember> = raw.members.map { it.unwrap(id) }

    override val isFullyLoaded: Boolean = true

    @ExperimentalTime
    override fun toString(): String {
        return "Guild(region=$region, isEmbedEnabled=$isEmbedEnabled, defaultMessageNotificationLevel=$defaultMessageNotificationLevel, explicitContentFilter=$explicitContentFilter, mfaLevel=$mfaLevel, isWidgetEnabled=$isWidgetEnabled, widgetChannel=$widgetChannel, systemChannel=$systemChannel, maxMembers=$maxMembers, maxPresences=$maxPresences, description=$description, bannerHash=$bannerHash, premiumTier=$premiumTier, premiumSubscriptions=$premiumSubscriptions, features=$features, id=$id, invites=$invites, emojis=$emojis, bans=$bans, integrations=$integrations, iconHash=$iconHash, splashHash=$splashHash, owner=$owner, afkChannel=$afkChannel, afkChannelTimeout=$afkChannelTimeout, verificationLevel=$verificationLevel, name='$name')"
    }
}