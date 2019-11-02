package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.core.some
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.applicativeError
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.fix
import arrow.integrations.retrofit.adapter.unwrapBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.GuildResponse
import ru.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import ru.tesserakt.diskordin.core.entity.query.MemberQuery
import ru.tesserakt.diskordin.core.entity.query.PruneQuery
import ru.tesserakt.diskordin.core.entity.query.query
import ru.tesserakt.diskordin.impl.core.entity.`object`.Region
import ru.tesserakt.diskordin.rest.call
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@Suppress("UNCHECKED_CAST")
class Guild(raw: GuildResponse) : IGuild {
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

    override val widgetChannel: Identified<IGuildChannel>? =
        raw.widget_channel_id?.combine { getChannel<IGuildChannel>(it) }

    override val systemChannel: Identified<IGuildChannel>? =
        raw.system_channel_id?.combine { getChannel<IGuildChannel>(it) }

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

    override suspend fun getRole(id: Snowflake): IRole =
        roles.first { it.id == id }

    override suspend fun getEmoji(emojiId: Snowflake) = rest.call(id.some(), Id.functor()) {
        emojiService.getGuildEmoji(id, emojiId)
    }.fix().suspended().extract()

    override suspend fun createEmoji(builder: EmojiCreateBuilder.() -> Unit) = rest.call(id.some(), Id.functor()) {
        emojiService.createGuildEmoji(id, builder.build())
    }.fix().suspended().extract()

    override suspend fun editOwnNickname(builder: NicknameEditBuilder.() -> Unit) = rest.guildService
        .editCurrentNickname(id, builder.build())
        .async(IO.async())
        .flatMap { it.unwrapBody(IO.applicativeError()) }
        .suspended().extract()

    override suspend fun addTextChannel(builder: TextChannelCreateBuilder.() -> Unit): ITextChannel =
        addChannelJ(builder)

    override suspend fun addVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel =
        addChannelJ(builder)

    private suspend inline fun <C : IGuildChannel, reified B : GuildChannelCreateBuilder<out C>> addChannelJ(
        noinline builder: B.() -> Unit
    ): C = rest.call(Id.functor()) {
        val inst = builder.instance()
        guildService.createGuildChannel(id, inst.create(), inst.reason)
    }.fix().suspended().extract() as C

    override suspend fun moveChannels(vararg builder: PositionEditBuilder.() -> Unit) = rest.effect {
        guildService.editGuildChannelPositions(id, builder.map { it.build() }.toTypedArray())
    }.fix().suspended()

    override suspend fun addMember(userId: Snowflake, builder: MemberAddBuilder.() -> Unit) =
        rest.call(id, Id.functor()) {
            guildService.newMember(id, userId, builder.build())
        }.fix().suspended().extract()

    override suspend fun kick(member: IMember, reason: String?) = kick(member.id, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = rest.effect {
        guildService.removeMember(id, memberId, reason)
    }.fix().suspended()

    override suspend fun addRole(builder: RoleCreateBuilder.() -> Unit) = rest.call(id, Id.functor()) {
        val inst = builder.instance()
        guildService.createRole(id, inst.create(), inst.reason)
    }.fix().suspended().extract()

    override suspend fun moveRoles(vararg builder: PositionEditBuilder.() -> Unit) = rest.call(id, ListK.functor()) {
        guildService.editRolePositions(id, builder.map { it.build() }.toTypedArray())
    }.fix().suspended().fix()

    override suspend fun findBan(userId: Snowflake): IBan? = rest.call(Id.functor()) {
        guildService.getBan(id, userId)
    }.attempt().suspended().toOption().orNull()?.extract()

    override suspend fun ban(member: IMember, builder: BanQuery.() -> Unit) = ban(member.id, builder)

    override suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) = rest.effect {
        guildService.ban(id, memberId, builder.query())
    }.fix().suspended()

    override suspend fun pardon(userId: Snowflake, reason: String?) = rest.effect {
        guildService.removeBan(id, userId, reason)
    }.fix().suspended()

    override suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int =
        rest.guildService.getPruneCount(id, builder.query())
            .async(IO.async())
            .flatMap { it.unwrapBody(IO.applicativeError()) }
            .suspended().extract()

    override suspend fun addIntegration(builder: IntegrationCreateBuilder.() -> Unit) = rest.effect {
        guildService.createIntegration(id, builder.build())
    }.fix().suspended()

    override suspend fun getEveryoneRole(): IRole = getRole(id) //everyone role id == guild id

    override suspend fun <C : IGuildChannel> getChannel(id: Snowflake): C =
        channels.first { it.id == id } as C

    override val invites: Flow<IGuildInvite> = flow {
        rest.call(ListK.functor()) {
            guildService.getInvites(id)
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override val emojis: Flow<ICustomEmoji> = flow {
        rest.call(id.some(), ListK.functor()) {
            emojiService.getGuildEmojis(id)
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override val bans: Flow<IBan> = flow {
        rest.call(ListK.functor()) {
            guildService.getBans(id)
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override val integrations: Flow<IIntegration> = flow {
        rest.call(id, ListK.functor()) {
            guildService.getIntegrations(id)
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild = rest.call(Id.functor()) {
        guildService.editGuild(id, builder.build())
    }.fix().suspended().extract()

    override val iconHash: String? = raw.icon
    override val splashHash: String? = raw.splash

    override val owner: Identified<IMember> =
        Identified(raw.owner_id) { id ->
            members.first { it.id == id }
        }

    override val afkChannel: Identified<IVoiceChannel>? = raw.afk_channel_id?.combine { id ->
        channels.first { channel -> channel.id == id } as VoiceChannel
    }

    @ExperimentalTime
    override val afkChannelTimeout: Duration = raw.afk_timeout.seconds

    override val verificationLevel =
        IGuild.VerificationLevel.values().first { it.ordinal == raw.verification_level }

    override val roles: Flow<IRole> =
        raw.roles
            .map { Role(it, id) }
            .asFlow()

    override val members: Flow<IMember> = flow {
        rest.call(id, ListK.functor()) {
            guildService.getMembers(id, MemberQuery().apply {
                this.limit = 1000
            }.create())
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override val channels: Flow<IGuildChannel> = flow {
        rest.call(ListK.functor()) {
            guildService.getGuildChannels(id)
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override val name: String = raw.name

    @Suppress("DEPRECATION")
    @Deprecated(
        "Bots can use this only 10 times!",
        ReplaceWith("guildService.deleteGuild(id)", "ru.tesserakt.diskordin.core.entity.guildService")
    )
    override suspend fun delete(reason: String?) = rest.effect {
        guildService.deleteGuild(id)
    }.fix().suspended()

    @UseExperimental(ExperimentalTime::class)
    override fun toString(): String {
        return "Guild(region=$region, isEmbedEnabled=$isEmbedEnabled, defaultMessageNotificationLevel=$defaultMessageNotificationLevel, explicitContentFilter=$explicitContentFilter, mfaLevel=$mfaLevel, isWidgetEnabled=$isWidgetEnabled, widgetChannel=$widgetChannel, systemChannel=$systemChannel, maxMembers=$maxMembers, maxPresences=$maxPresences, description=$description, bannerHash=$bannerHash, premiumTier=$premiumTier, premiumSubscriptions=$premiumSubscriptions, features=$features, id=$id, invites=$invites, emojis=$emojis, bans=$bans, integrations=$integrations, iconHash=$iconHash, splashHash=$splashHash, owner=$owner, afkChannel=$afkChannel, afkChannelTimeout=$afkChannelTimeout, verificationLevel=$verificationLevel, roles=$roles, members=$members, channels=$channels, name='$name')"
    }
}