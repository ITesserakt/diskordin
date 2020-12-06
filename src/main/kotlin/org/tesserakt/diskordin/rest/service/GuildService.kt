@file:Suppress("unused", "DEPRECATION")

package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.query.Query

interface GuildService {
    suspend fun createGuild(request: GuildCreateRequest): GuildResponse

    suspend fun getGuild(id: Snowflake): GuildResponse

    suspend fun getGuildPreview(id: Snowflake): GuildPreviewResponse

    suspend fun editGuild(
        id: Snowflake,
        request: GuildEditRequest
    ): GuildResponse

    suspend fun editCurrentNickname(
        id: Snowflake,
        request: NicknameEditRequest
    ): String?

    suspend fun getInvites(id: Snowflake): ListK<InviteResponse<IGuildInvite>>

    suspend fun getGuildChannels(id: Snowflake): ListK<ChannelResponse<IGuildChannel>>

    suspend fun createGuildChannel(
        id: Snowflake,
        request: ChannelCreateRequest,
        reason: String?
    ): ChannelResponse<IGuildChannel>

    suspend fun editGuildChannelPositions(
        id: Snowflake,
        request: Array<PositionEditRequest>
    )

    suspend fun getMember(
        guildId: Snowflake,
        userId: Snowflake
    ): GuildMemberResponse

    suspend fun getMembers(
        id: Snowflake,
        query: Query
    ): ListK<GuildMemberResponse>

    suspend fun newMember(
        guildId: Snowflake,
        userId: Snowflake,
        request: MemberAddRequest
    ): GuildMemberResponse

    suspend fun editMember(
        guildId: Snowflake,
        userId: Snowflake,
        request: MemberEditRequest,
        reason: String?
    )

    suspend fun removeMember(
        guildId: Snowflake,
        userId: Snowflake,
        reason: String?
    )

    suspend fun addMemberRole(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String?
    )

    suspend fun deleteMemberRole(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String?
    )

    suspend fun getRoles(id: Snowflake): ListK<RoleResponse>

    suspend fun createRole(
        id: Snowflake,
        request: GuildRoleCreateRequest,
        reason: String?
    ): RoleResponse

    suspend fun editRolePositions(
        id: Snowflake,
        request: Array<PositionEditRequest>
    ): ListK<RoleResponse>

    suspend fun editRole(
        guildId: Snowflake,
        roleId: Snowflake,
        request: GuildRoleEditRequest,
        reason: String?
    ): RoleResponse

    suspend fun deleteRole(
        guildId: Snowflake,
        roleId: Snowflake,
        reason: String?
    )

    suspend fun getBans(id: Snowflake): ListK<BanResponse>

    suspend fun getBan(
        guildId: Snowflake,
        userId: Snowflake
    ): BanResponse

    suspend fun ban(
        guildId: Snowflake,
        userId: Snowflake,
        query: Query
    )

    suspend fun removeBan(
        guildId: Snowflake,
        userId: Snowflake,
        reason: String?
    )

    suspend fun getPruneCount(
        id: Snowflake,
        query: Query
    ): Int

    suspend fun startPrune(
        id: Snowflake,
        query: Query,
        reason: String?
    ): Int

    suspend fun getIntegrations(id: Snowflake): ListK<GuildIntegrationResponse>

    suspend fun createIntegration(
        id: Snowflake,
        request: IntegrationCreateRequest
    )

    suspend fun editIntegration(
        guildId: Snowflake,
        integrationId: Snowflake,
        request: IntegrationEditRequest
    )

    suspend fun deleteIntegration(
        guildId: Snowflake,
        integrationId: Snowflake
    )

    suspend fun syncIntegration(
        guildId: Snowflake,
        integrationId: Snowflake
    )

    suspend fun getEmbed(id: Snowflake): GuildEmbedResponse

    suspend fun editEmbed(
        id: Snowflake,
        request: GuildEmbedEditRequest
    ): GuildEmbedResponse

    suspend fun getGuildWidgetSettings(id: Snowflake): GuildWidgetSettingsResponse

    suspend fun modifyGuildWidget(
        id: Snowflake,
        request: GuildWidgetEditRequest
    ): GuildWidgetSettingsResponse

    suspend fun getGuildWidget(id: Snowflake): GuildWidgetResponse

    suspend fun getVanityUrl(id: Snowflake): InviteResponse<IGuildInvite>
}