package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*


import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import ru.tesserakt.diskordin.core.entity.query.PruneQuery
import ru.tesserakt.diskordin.impl.core.rest.resource.GuildResource
import ru.tesserakt.diskordin.impl.core.service.EmojiService
import ru.tesserakt.diskordin.impl.core.service.GuildService
import ru.tesserakt.diskordin.util.Identified
import java.time.Duration

class Guild(raw: GuildResponse) : IGuild {
    override suspend fun findEmoji(emojiId: Snowflake): ICustomEmoji? = EmojiService.getEmoji(id, emojiId)

    override suspend fun createEmoji(builder: EmojiCreateBuilder.() -> Unit): ICustomEmoji =
        EmojiService.createEmoji(id, builder)

    override suspend fun editOwnNickname(builder: NicknameEditBuilder.() -> Unit) {
        GuildService.editOwnNickname(id, builder)
    }

    override suspend fun addTextChannel(builder: TextChannelCreateBuilder.() -> Unit): ITextChannel =
        addChannelJ(builder)

    override suspend fun addVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): IVoiceChannel =
        addChannelJ(builder)

    private suspend inline fun <C : IGuildChannel, reified B : GuildChannelCreateBuilder<out C>> addChannelJ(
        noinline builder: B.() -> Unit
    ): C = GuildService.createChannel(id, builder, B::class)

    override suspend fun moveChannels(vararg builder: PositionEditBuilder.() -> Unit) =
        GuildService.editChannelPositions(id, builder as Array<PositionEditBuilder.() -> Unit>)

    override suspend fun addMember(userId: Snowflake, builder: MemberAddBuilder.() -> Unit): IMember =
        GuildService.addMember(id, userId, builder)

    override suspend fun kick(member: IMember, reason: String?) = kick(member.id, reason)

    override suspend fun kick(memberId: Snowflake, reason: String?) = GuildService.kickMember(id, memberId, reason)

    override suspend fun addRole(builder: RoleCreateBuilder.() -> Unit): IRole = GuildService.createRole(id, builder)

    override suspend fun moveRoles(vararg builder: PositionEditBuilder.() -> Unit): List<IRole> =
        GuildService.editRolePositions(id, builder as Array<PositionEditBuilder.() -> Unit>)

    override suspend fun findBan(userId: Snowflake): IBan? = GuildService.getBan(id, userId)

    override suspend fun ban(member: IMember, builder: BanQuery.() -> Unit) = ban(member.id, builder)

    override suspend fun ban(memberId: Snowflake, builder: BanQuery.() -> Unit) =
        GuildService.ban(id, memberId, builder)

    override suspend fun pardon(userId: Snowflake, reason: String?) = GuildService.unban(id, userId, reason)

    override suspend fun getPruneCount(builder: PruneQuery.() -> Unit): Int = GuildService.getPruneCount(id, builder)

    override suspend fun addIntegration(builder: IntegrationCreateBuilder.() -> Unit) =
        GuildService.createIntegration(id, builder)

    @ExperimentalCoroutinesApi
    override val invites: Flow<IGuildInvite>
        get() = flow {
            GuildService.getInvites(id).forEach { emit(it) }
        }
    @ExperimentalCoroutinesApi
    override val emojis: Flow<ICustomEmoji>
        get() = flow {
            EmojiService.getEmojis(id).forEach { emit(it) }
        }
    @ExperimentalCoroutinesApi
    override val bans: Flow<IBan>
        get() = flow {
            GuildService.getBans(id).forEach { emit(it) }
        }
    @ExperimentalCoroutinesApi
    override val integrations: Flow<IIntegration>
        get() = flow {
            GuildService.getIntegrations(id).forEach { emit(it) }
        }

    override suspend fun edit(builder: GuildEditBuilder.() -> Unit): IGuild =
        GuildService.editGuild(id, builder)

    override val id: Snowflake = raw.id.asSnowflake()

    @ExperimentalCoroutinesApi
    override suspend fun findRole(id: Snowflake): IRole? = roles
        .filter { it.id == id }
        .singleOrNull()


    override val iconHash: String? = raw.icon
    override val splashHash: String? = raw.splash

    @ExperimentalCoroutinesApi
    override val owner: Identified<IMember> = Identified(raw.owner_id.asSnowflake()) { id ->
        members.filter { it.id == id }.single()
    }

    @ExperimentalCoroutinesApi
    override val afkChannel: Identified<IVoiceChannel>? = raw.afk_channel_id?.asSnowflake()?.let {
        Identified(it) { id ->
            channels.filter { channel -> channel.id == id }.single() as VoiceChannel
        }
    }

    override val afkChannelTimeout: Duration = Duration.ofSeconds(raw.afk_timeout.toLong())

    override val verificationLevel = IGuild.VerificationLevel.of(raw.verification_level)

    @ExperimentalCoroutinesApi
    override val roles: Flow<IRole> =
        raw.roles
            .map { Role(it, id) }
            .asFlow()

    @ExperimentalCoroutinesApi
    override val members: Flow<IMember> = flow {
        GuildService.getMembers(id) { limit = 1000 }.forEach { emit(it) }
    }

    @ExperimentalCoroutinesApi
    override val channels: Flow<IGuildChannel> = flow {
        GuildService.getChannels(id).forEach { emit(it) }
    }

    override val name: String = raw.name

    override suspend fun delete(reason: String?) {
        GuildResource.General.deleteGuild(id.asLong())
    }
}