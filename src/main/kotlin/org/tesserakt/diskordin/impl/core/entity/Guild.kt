package org.tesserakt.diskordin.impl.core.entity

import arrow.core.*
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.callback
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
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
import java.awt.Color
import java.io.File
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@Suppress("UNCHECKED_CAST", "CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
internal class Guild(raw: GuildResponse) : IGuild {
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

    override fun getRole(id: Snowflake) = roles.find { it.id == id }.toOption()

    override suspend fun getEmoji(emojiId: Snowflake) = rest.call(id.some(), Id.functor()) {
        emojiService.getGuildEmoji(id, emojiId)
    }.extract()

    override suspend fun createEmoji(name: String, image: File, roles: Array<Snowflake>): ICustomEmoji =
        rest.call(id.some(), Id.functor()) {
            emojiService.createGuildEmoji(id, EmojiCreateBuilder().apply {
                this.name = name
                this.image = image
                this.roles = roles
            }.create())
        }.extract()

    override suspend fun editOwnNickname(newNickname: String): String? = rest.callRaw {
        guildService.editCurrentNickname(id, NicknameEditBuilder(newNickname).create())
    }.extract()

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
            guildService.newMember(id, userId, MemberAddBuilder(accessToken).apply(builder).create())
        }.extract()

    override suspend fun kick(member: IMember, reason: String?) = kick(member.id, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = rest.effect {
        guildService.removeMember(id, memberId, reason)
    }

    override suspend fun addRole(name: String, color: Color, builder: RoleCreateBuilder.() -> Unit) =
        rest.call(id, Id.functor()) {
            val inst = builder.instance { RoleCreateBuilder(name, color) }
            guildService.createRole(id, inst.create(), inst.reason)
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
    }.extract()

    override suspend fun addIntegration(id: Snowflake, type: String): Unit = rest.effect {
        guildService.createIntegration(id, IntegrationCreateBuilder(id, type).create())
    }

    override fun getEveryoneRole() = id identifyId { getRole(it).orNull()!! } //everyone role id == guild id

    override fun <C : IGuildChannel> getChannel(id: Snowflake): C = cache[id] as C

    override val invites: Stream<IGuildInvite> = Stream.callback {
        rest.call(ListK.functor()) {
            guildService.getInvites(id)
        }.fix().forEach(::emit)
    }

    override val emojis: Stream<ICustomEmoji> = Stream.callback {
        rest.call(id.some(), ListK.functor()) {
            emojiService.getGuildEmojis(id)
        }.fix().forEach(::emit)
    }

    override val bans = Stream.callback {
        rest.call(ListK.functor()) {
            guildService.getBans(id)
        }.fix().forEach(::emit)
    }

    override val integrations = Stream.callback {
        rest.call(id, ListK.functor()) {
            guildService.getIntegrations(id)
        }.fix().forEach(::emit)
    }

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild = rest.call(Id.functor()) {
        guildService.editGuild(id, builder.build(::GuildEditBuilder))
    }.extract()

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

    override val roles
        get() = cache
            .values.filterIsInstance<IRole>()
            .filter { it.guild.id == id }

    override suspend fun getMembers(query: MemberQuery.() -> Unit) = rest.call(id, ListK.functor()) {
        guildService.getMembers(id, query.query(::MemberQuery))
    }.fix().also { list -> cache += list.associateBy { it.id } }

    override val channels get() = cache.values.filterIsInstance<IGuildChannel>().filter { it.guild.id == id }

    override val name: String = raw.name

    @Suppress("DEPRECATION")
    @Deprecated(
        "Bots can use this only 10 times!",
        ReplaceWith("guildService.deleteGuild(id)", "ru.tesserakt.diskordin.core.entity.guildService")
    )
    override suspend fun delete(reason: String?) = rest.effect {
        guildService.deleteGuild(id)
    }

    @ExperimentalTime
    override fun toString(): String {
        return StringBuilder("Guild(")
            .appendLine("region=$region, ")
            .appendLine("isEmbedEnabled=$isEmbedEnabled, ")
            .appendLine("defaultMessageNotificationLevel=$defaultMessageNotificationLevel, ")
            .appendLine("explicitContentFilter=$explicitContentFilter, ")
            .appendLine("mfaLevel=$mfaLevel, ")
            .appendLine("isWidgetEnabled=$isWidgetEnabled, ")
            .appendLine("widgetChannel=$widgetChannel, ")
            .appendLine("systemChannel=$systemChannel, ")
            .appendLine("maxMembers=$maxMembers, ")
            .appendLine("maxPresences=$maxPresences, ")
            .appendLine("description=$description, ")
            .appendLine("bannerHash=$bannerHash, ")
            .appendLine("premiumTier=$premiumTier, ")
            .appendLine("premiumSubscriptions=$premiumSubscriptions, ")
            .appendLine("features=$features, ")
            .appendLine("id=$id, ")
            .appendLine("invites=$invites, ")
            .appendLine("emojis=$emojis, ")
            .appendLine("bans=$bans, ")
            .appendLine("integrations=$integrations, ")
            .appendLine("iconHash=$iconHash, ")
            .appendLine("splashHash=$splashHash, ")
            .appendLine("owner=$owner, ")
            .appendLine("afkChannel=$afkChannel, ")
            .appendLine("afkChannelTimeout=$afkChannelTimeout, ")
            .appendLine("verificationLevel=$verificationLevel, ")
            .appendLine("roles=$roles, ")
            .appendLine("channels=$channels, ")
            .appendLine("name='$name'")
            .appendLine(")")
            .toString()
    }

    init {
        cache += runBlocking { rest.call(ListK.functor()) { guildService.getGuildChannels(id) } }
            .fix().associateBy { it.id }

        cache += raw.roles.map { it.unwrap(id) }.associateBy { it.id }
    }
}