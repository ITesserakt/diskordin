package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.GuildResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import ru.tesserakt.diskordin.core.entity.query.MemberQuery
import ru.tesserakt.diskordin.core.entity.query.PruneQuery
import ru.tesserakt.diskordin.core.entity.query.query
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@Suppress("UNCHECKED_CAST")
class Guild(raw: GuildResponse) : IGuild {
    override val region: IRegion = object : IRegion {
        override val isOptimal: Boolean = false
        override val isVIP: Boolean = false
        override val isDeprecated: Boolean = false
        override val name: String = raw.region
        override val id: String = raw.region
        override val isCustom: Boolean = false
    }

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

    override val features: EnumSet<IGuild.Feature> =
        EnumSet.copyOf(raw.features.map { IGuild.Feature.valueOf(it) })

    override val id: Snowflake = raw.id

    override suspend fun getRole(id: Snowflake): IRole =
        roles.first { it.id == id }

    override suspend fun getEmoji(emojiId: Snowflake): ICustomEmoji = emojiService.getGuildEmoji(id, emojiId).unwrap()

    override suspend fun createEmoji(builder: EmojiCreateBuilder.() -> Unit): ICustomEmoji =
        emojiService.createGuildEmoji(id, builder.build()).unwrap()

    override suspend fun editOwnNickname(builder: NicknameEditBuilder.() -> Unit) =
        guildService.editCurrentNickname(id, builder.build())

    override suspend fun addTextChannel(builder: TextChannelCreateBuilder.() -> Unit): ITextChannel =
        addChannelJ(builder)

    override suspend fun addVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel =
        addChannelJ(builder)

    private suspend inline fun <C : IGuildChannel, reified B : GuildChannelCreateBuilder<out C>> addChannelJ(
        noinline builder: B.() -> Unit
    ): C = guildService.createGuildChannel(id, builder.build(), null).unwrap() as C

    override suspend fun moveChannels(vararg builder: PositionEditBuilder.() -> Unit) =
        guildService.editGuildChannelPositions(id, builder.map { it.build() }.toTypedArray())

    override suspend fun addMember(userId: Snowflake, builder: MemberAddBuilder.() -> Unit): IMember =
        guildService.newMember(id, userId, builder.build()).unwrap()

    override suspend fun kick(member: IMember, reason: String?) = kick(member.id, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = guildService.removeMember(id, memberId, reason)

    override suspend fun addRole(builder: RoleCreateBuilder.() -> Unit): IRole =
        guildService.createRole(id, builder.build(), null).unwrap()

    override suspend fun moveRoles(vararg builder: PositionEditBuilder.() -> Unit): List<IRole> =
        guildService.editRolePositions(id, builder.map { it.build() }.toTypedArray())
            .map { it.unwrap() }

    override suspend fun findBan(userId: Snowflake): IBan? = guildService.getBan(id, userId).unwrap()

    override suspend fun ban(member: IMember, builder: BanQuery.() -> Unit) = ban(member.id, builder)

    override suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) =
        guildService.ban(id, memberId, builder.query())

    override suspend fun pardon(userId: Snowflake, reason: String?) = guildService.removeBan(id, userId, reason)

    override suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int =
        guildService.getPruneCount(id, builder.query())

    override suspend fun addIntegration(builder: IntegrationCreateBuilder.() -> Unit) =
        guildService.createIntegration(id, builder.build())

    override suspend fun getEveryoneRole(): IRole = getRole(id) //everyone role id == guild id

    override suspend fun <C : IGuildChannel> getChannel(id: Snowflake): C =
        channels.first { it.id == id } as C

    override val invites: Flow<IGuildInvite> = flow {
        guildService.getInvites(id).map { it.unwrap() }.forEach { emit(it) }
    }

    override val emojis: Flow<ICustomEmoji> = flow {
        emojiService.getGuildEmojis(id).map { it.unwrap() }.forEach { emit(it) }
    }

    override val bans: Flow<IBan> = flow {
        guildService.getBans(id).map { it.unwrap() }.forEach { emit(it) }
    }

    override val integrations: Flow<IIntegration> = flow {
        guildService.getIntegrations(id).map { it.unwrap(id) }.forEach { emit(it) }
    }

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild =
        guildService.editGuild(id, builder.build()).unwrap()

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
        guildService.getMembers(id, MemberQuery().apply {
            limit = 1000
        }.create()).map { it.unwrap(id) }.forEach { emit(it) }
    }

    override val channels: Flow<IGuildChannel> = flow {
        guildService.getGuildChannels(id).map { it.unwrap() }.forEach { emit(it) }
    }

    override val name: String = raw.name

    @Suppress("DEPRECATION")
    @Deprecated(
        "Bots can use this only 10 times!",
        ReplaceWith("guildService.deleteGuild(id)", "ru.tesserakt.diskordin.core.entity.guildService")
    )
    override suspend fun delete(reason: String?) = guildService.deleteGuild(id)
}