package org.tesserakt.diskordin.impl.core.entity


import arrow.core.*
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.monad.flatTap
import arrow.fx.extensions.io.monad.map
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
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
import arrow.core.extensions.id.applicative.just as idJust

@Suppress("UNCHECKED_CAST")
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

    override val widgetChannel = raw.widget_channel_id?.identify {
        getChannel<IGuildChannel>(it).idJust()
    }

    override val systemChannel = raw.system_channel_id?.identify {
        getChannel<IGuildChannel>(it).idJust()
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

    override fun getEmoji(emojiId: Snowflake) = rest.call(id.some(), Id.functor()) {
        emojiService.getGuildEmoji(id, emojiId)
    }.map { it.extract() }

    override fun createEmoji(name: String, image: File, roles: Array<Snowflake>): IO<ICustomEmoji> =
        rest.call(id.some(), Id.functor()) {
            emojiService.createGuildEmoji(id, EmojiCreateBuilder().apply {
                this.name = name
                this.image = image
                this.roles = roles
            }.create())
        }.map { it.extract() }

    override fun editOwnNickname(newNickname: String): IO<String?> = rest.callRaw {
        guildService.editCurrentNickname(id, NicknameEditBuilder(newNickname).create())
    }.map { it.extract() }

    override fun addTextChannel(name: String, builder: TextChannelCreateBuilder.() -> Unit): IO<ITextChannel> =
        addChannelJ(TextChannelCreateBuilder(name), builder)

    override fun addVoiceChannel(name: String, builder: VoiceChannelCreateBuilder.() -> Unit): IO<IVoiceChannel> =
        addChannelJ(VoiceChannelCreateBuilder(name), builder)

    private inline fun <C : IGuildChannel, reified B : GuildChannelCreateBuilder<out C>> addChannelJ(
        instance: B,
        noinline builder: B.() -> Unit
    ): IO<C> = rest.call {
        instance.apply(builder)
        guildService.createGuildChannel(id, instance.create(), instance.reason)
    }.map { it as C }

    override fun moveChannels(vararg builder: Pair<Snowflake, Int>) = rest.effect {
        guildService.editGuildChannelPositions(
            id, builder.map { PositionEditBuilder(it.first, it.second).create() }.toTypedArray()
        )
    }.fix()

    override fun addMember(userId: Snowflake, accessToken: String, builder: MemberAddBuilder.() -> Unit): IO<IMember> =
        rest.call(id, Id.functor()) {
            guildService.newMember(id, userId, MemberAddBuilder(accessToken).apply(builder).create())
        }.map { it.extract() }

    override fun kick(member: IMember, reason: String?) = kick(member.id, reason)

    override fun kick(memberId: Snowflake, reason: String?) = rest.effect {
        guildService.removeMember(id, memberId, reason)
    }.fix()

    override fun addRole(name: String, color: Color, builder: RoleCreateBuilder.() -> Unit) =
        rest.call(id, Id.functor()) {
            val inst = builder.instance { RoleCreateBuilder(name, color) }
            guildService.createRole(id, inst.create(), inst.reason)
        }.map { it.extract() }

    override fun moveRoles(vararg builder: Pair<Snowflake, Int>): IO<ListK<IRole>> = rest.call(id, ListK.functor()) {
        guildService.editRolePositions(id, builder.map { (id, pos) ->
            PositionEditBuilder(id, pos).create()
        }.toTypedArray())
    }.map { it.fix() }

    override fun getBan(userId: Snowflake): IO<IBan> = rest.call(Id.functor()) {
        guildService.getBan(id, userId)
    }.map { it.extract() }

    @ExperimentalTime
    override fun ban(member: IMember, builder: BanQuery.() -> Unit) = ban(member.id, builder)

    @ExperimentalTime
    override fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) = rest.effect {
        guildService.ban(id, memberId, builder.query(::BanQuery))
    }.fix()

    override fun pardon(userId: Snowflake, reason: String?) = rest.effect {
        guildService.removeBan(id, userId, reason)
    }.fix()

    override fun getPruneCount(builder: PruneQuery.() -> Unit): IO<Int> = rest.callRaw {
        guildService.getPruneCount(id, builder.query(::PruneQuery))
    }.map { it.extract() }

    override fun addIntegration(id: Snowflake, type: String): IO<Unit> = rest.effect {
        guildService.createIntegration(id, IntegrationCreateBuilder(id, type).create())
    }.fix()

    override fun getEveryoneRole() = id identify { getRole(it).orNull()!!.idJust() } //everyone role id == guild id

    override fun <C : IGuildChannel> getChannel(id: Snowflake): C = cache[id] as C

    override val invites: IO<ListK<IGuildInvite>> = rest.call(ListK.functor()) {
        guildService.getInvites(id)
    }.map { it.fix() }

    override val emojis: IO<ListK<ICustomEmoji>> = rest.call(id.some(), ListK.functor()) {
        emojiService.getGuildEmojis(id)
    }.map { it.fix() }

    override val bans: IO<ListK<IBan>> = rest.call(ListK.functor()) {
        guildService.getBans(id)
    }.map { it.fix() }

    override val integrations: IO<ListK<IIntegration>> = rest.call(id, ListK.functor()) {
        guildService.getIntegrations(id)
    }.map { it.fix() }

    override fun edit(builder: GuildEditBuilder.() -> Unit): IO<IGuild> = rest.call(Id.functor()) {
        guildService.editGuild(id, builder.build(::GuildEditBuilder))
    }.map { it.extract() }

    override val iconHash: String? = raw.icon
    override val splashHash: String? = raw.splash

    override val owner = raw.owner_id identify { userId -> client.getMember(userId, id) }

    override val afkChannel = raw.afk_channel_id?.identify { id ->
        ((channels.find { it.id == id } ?: client.getChannel(id).unsafeRunSync()) as IVoiceChannel).idJust()
    }

    @ExperimentalTime
    override val afkChannelTimeout: Duration = raw.afk_timeout.seconds

    override val verificationLevel =
        IGuild.VerificationLevel.values().first { it.ordinal == raw.verification_level }

    override val roles
        get() = cache
            .values.filterIsInstance<IRole>()
            .filter { it.guild.id == id }

    override fun getMembers(query: MemberQuery.() -> Unit) = rest.call(id, ListK.functor()) {
        guildService.getMembers(id, query.query(::MemberQuery))
    }.map { it.fix() }.flatTap { list -> cache += list.associateBy { it.id }; just() }

    override val channels get() = cache.values.filterIsInstance<IGuildChannel>().filter { it.guild.id == id }

    override val name: String = raw.name

    @Suppress("DEPRECATION")
    @Deprecated(
        "Bots can use this only 10 times!",
        ReplaceWith("guildService.deleteGuild(id)", "ru.tesserakt.diskordin.core.entity.guildService")
    )
    override fun delete(reason: String?): IO<Unit> = rest.effect {
        guildService.deleteGuild(id)
    }.fix()

    @ExperimentalTime
    override fun toString(): String {
        return StringBuilder("Guild(")
            .appendln("region=$region, ")
            .appendln("isEmbedEnabled=$isEmbedEnabled, ")
            .appendln("defaultMessageNotificationLevel=$defaultMessageNotificationLevel, ")
            .appendln("explicitContentFilter=$explicitContentFilter, ")
            .appendln("mfaLevel=$mfaLevel, ")
            .appendln("isWidgetEnabled=$isWidgetEnabled, ")
            .appendln("widgetChannel=$widgetChannel, ")
            .appendln("systemChannel=$systemChannel, ")
            .appendln("maxMembers=$maxMembers, ")
            .appendln("maxPresences=$maxPresences, ")
            .appendln("description=$description, ")
            .appendln("bannerHash=$bannerHash, ")
            .appendln("premiumTier=$premiumTier, ")
            .appendln("premiumSubscriptions=$premiumSubscriptions, ")
            .appendln("features=$features, ")
            .appendln("id=$id, ")
            .appendln("invites=$invites, ")
            .appendln("emojis=$emojis, ")
            .appendln("bans=$bans, ")
            .appendln("integrations=$integrations, ")
            .appendln("iconHash=$iconHash, ")
            .appendln("splashHash=$splashHash, ")
            .appendln("owner=$owner, ")
            .appendln("afkChannel=$afkChannel, ")
            .appendln("afkChannelTimeout=$afkChannelTimeout, ")
            .appendln("verificationLevel=$verificationLevel, ")
            .appendln("roles=$roles, ")
            .appendln("channels=$channels, ")
            .appendln("name='$name'")
            .appendln(")")
            .toString()
    }

    init {
        cache += rest.call(ListK.functor()) { guildService.getGuildChannels(id) }
            .fix().unsafeRunSync()
            .fix().associateBy { it.id }

        cache += raw.roles.map { it.unwrap(id) }.associateBy { it.id }
    }
}