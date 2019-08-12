@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.BanQuery
import ru.tesserakt.diskordin.core.entity.query.MemberQuery
import ru.tesserakt.diskordin.core.entity.query.PruneQuery
import ru.tesserakt.diskordin.core.entity.query.query
import ru.tesserakt.diskordin.impl.core.entity.Guild
import ru.tesserakt.diskordin.impl.core.entity.Integration
import ru.tesserakt.diskordin.impl.core.entity.Member
import ru.tesserakt.diskordin.impl.core.entity.Role
import ru.tesserakt.diskordin.impl.core.entity.`object`.Ban
import ru.tesserakt.diskordin.impl.core.entity.`object`.GuildEmbed
import ru.tesserakt.diskordin.impl.core.rest.resource.GuildResource
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

internal object GuildService {
//    private val guildCache = genericCache<IGuild>()
//    private val channelCache = genericCache<IChannel>()
//    private val memberCache = genericCache<IMember>()

    suspend fun createGuild(builder: GuildCreateBuilder.() -> Unit): IGuild =
        Guild(GuildResource.General.createGuild(builder.build()))

    suspend fun getGuild(guildId: Snowflake): IGuild? = runCatching {
        GuildResource.General.getGuild(guildId.asLong())
    }.map { Guild(it) }.getOrNull()

    suspend fun editGuild(guildId: Snowflake, builder: GuildEditBuilder.() -> Unit): IGuild =
        Guild(GuildResource.General.modifyGuild(guildId.asLong(), builder.build(), builder.extractReason()))

    @Deprecated("Bot can use this only 10 times")
    suspend fun deleteGuild(guildId: Snowflake) =
        GuildResource.General.deleteGuild(guildId.asLong())

    suspend fun editOwnNickname(guildId: Snowflake, builder: NicknameEditBuilder.() -> Unit) =
        GuildResource.General.modifyOwnNickname(guildId.asLong(), builder.build())

    suspend fun getInvites(guildId: Snowflake) =
        GuildResource.General
            .getInvites(guildId.asLong())
            .map { IInvite.typed<IGuildInvite>(it) }

    suspend fun getChannels(guildId: Snowflake) =
        GuildResource.Channels.getGuildChannels(guildId.asLong())
            .map { IChannel.typed<IGuildChannel>(it) }

    suspend fun <C : IGuildChannel, B : GuildChannelCreateBuilder<out C>> createChannel(
        guildId: Snowflake,
        builder: B.() -> Unit,
        reifiedClass: KClass<B>
    ): C {
        val builderInstance = reifiedClass.createInstance().apply(builder)
        return GuildResource.Channels.createGuildChannel(
            guildId.asLong(),
            builderInstance.create(),
            builderInstance.reason
        ).let { IChannel.typed(it) }
    }

    suspend fun editChannelPositions(guildId: Snowflake, builder: Array<PositionEditBuilder.() -> Unit>) =
        GuildResource.Channels.modifyGuildChannelPositions(guildId.asLong(), builder.map { it.build() }.toTypedArray())

    suspend fun getMember(guildId: Snowflake, userId: Snowflake): IMember =
        Member(GuildResource.Members.getMember(guildId.asLong(), userId.asLong()), guildId)

    suspend fun getMembers(guildId: Snowflake, query: MemberQuery.() -> Unit): List<IMember> =
        GuildResource.Members
            .getMembers(guildId.asLong(), query.query())
            .map { Member(it, guildId) }

    suspend fun addMember(guildId: Snowflake, userId: Snowflake, builder: MemberAddBuilder.() -> Unit): IMember =
        Member(GuildResource.Members.newMember(guildId.asLong(), userId.asLong(), builder.build()), guildId)

    suspend fun editMember(guildId: Snowflake, userId: Snowflake, builder: MemberEditBuilder.() -> Unit) =
        GuildResource.Members.modifyMember(guildId.asLong(), userId.asLong(), builder.build(), builder.extractReason())

    suspend fun kickMember(guildId: Snowflake, userId: Snowflake, reason: String?) =
        GuildResource.Members.removeMember(guildId.asLong(), userId.asLong(), reason)

    suspend fun addRoleToMember(guildId: Snowflake, userId: Snowflake, roleId: Snowflake, reason: String?) =
        GuildResource.Roles.addMemberRole(guildId.asLong(), userId.asLong(), roleId.asLong(), reason)

    suspend fun removeRoleFromMember(guildId: Snowflake, userId: Snowflake, roleId: Snowflake, reason: String?) =
        GuildResource.Roles.deleteMemberRole(guildId.asLong(), userId.asLong(), roleId.asLong(), reason)

    suspend fun getRoles(guildId: Snowflake): List<IRole> =
        GuildResource.Roles.getRoles(guildId.asLong())
            .map { Role(it, guildId) }

    suspend fun createRole(guildId: Snowflake, builder: RoleCreateBuilder.() -> Unit): IRole =
        Role(GuildResource.Roles.createRole(guildId.asLong(), builder.build(), builder.extractReason()), guildId)

    suspend fun editRolePositions(guildId: Snowflake, builder: Array<PositionEditBuilder.() -> Unit>): List<IRole> =
        GuildResource.Roles.editRolePositions(guildId.asLong(), builder.map { it.build() }.toTypedArray())
            .map { Role(it, guildId) }

    suspend fun editRole(guildId: Snowflake, roleId: Snowflake, builder: RoleEditBuilder.() -> Unit): IRole =
        Role(
            GuildResource.Roles.editRole(guildId.asLong(), roleId.asLong(), builder.build(), builder.extractReason()),
            guildId
        )

    suspend fun deleteRole(guildId: Snowflake, roleId: Snowflake, reason: String?) =
        GuildResource.Roles.deleteRole(guildId.asLong(), roleId.asLong(), reason)

    suspend fun getBans(guildId: Snowflake): List<IBan> =
        GuildResource.Bans.getBans(guildId.asLong()).map { Ban(it) }

    suspend fun getBan(guildId: Snowflake, userId: Snowflake): IBan? = runCatching {
        GuildResource.Bans.getBan(guildId.asLong(), userId.asLong())
    }.map { Ban(it) }.getOrNull()

    suspend fun ban(guildId: Snowflake, userId: Snowflake, query: BanQuery.() -> Unit) =
        GuildResource.Bans.createBan(guildId.asLong(), userId.asLong(), query.query())

    suspend fun unban(guildId: Snowflake, userId: Snowflake, reason: String?) =
        GuildResource.Bans.removeBan(guildId.asLong(), userId.asLong(), reason)

    suspend fun getPruneCount(guildId: Snowflake, builder: PruneQuery.() -> Unit) =
        GuildResource.Prunes.getPruneCount(guildId.asLong(), builder.query()).pruned

    suspend fun getIntegrations(guildId: Snowflake): List<IIntegration> =
        GuildResource.Integrations.getIntegrations(guildId.asLong())
            .map { Integration(it, guildId) }

    suspend fun createIntegration(guildId: Snowflake, builder: IntegrationCreateBuilder.() -> Unit) =
        GuildResource.Integrations.createIntegration(guildId.asLong(), builder.build())

    suspend fun editIntegration(
        guildId: Snowflake,
        integrationId: Snowflake,
        builder: IntegrationEditBuilder.() -> Unit
    ) = GuildResource.Integrations
        .editIntegration(guildId.asLong(), integrationId.asLong(), builder.build())

    suspend fun deleteIntegration(guildId: Snowflake, integrationId: Snowflake) =
        GuildResource.Integrations.deleteIntegration(guildId.asLong(), integrationId.asLong())

    suspend fun syncIntegration(guildId: Snowflake, integrationId: Snowflake) =
        GuildResource.Integrations.syncIntegration(guildId.asLong(), integrationId.asLong())

    suspend fun getEmbed(guildId: Snowflake) =
        GuildEmbed(GuildResource.Embeds.getEmbed(guildId.asLong()))

    suspend fun editEmbed(guildId: Snowflake, builder: GuildEmbedEditBuilder.() -> Unit) =
        GuildEmbed(GuildResource.Embeds.editEmbed(guildId.asLong(), builder.build()))
}