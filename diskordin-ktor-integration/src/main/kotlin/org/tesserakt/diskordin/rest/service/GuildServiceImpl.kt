package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.query.Query
import org.tesserakt.diskordin.rest.integration.parameters
import org.tesserakt.diskordin.rest.integration.reasonHeader

class GuildServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : GuildService {
    override suspend fun createGuild(request: GuildCreateRequest): GuildResponse =
        ktor.get("$discordApiUrl/api/v6/guilds")

    override suspend fun getGuild(id: Snowflake): GuildResponse = ktor.get("$discordApiUrl/api/v6/guilds/$id")

    override suspend fun getGuildPreview(id: Snowflake): GuildPreviewResponse =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/preview")

    override suspend fun editGuild(id: Snowflake, request: GuildEditRequest): GuildResponse =
        ktor.patch("$discordApiUrl//api/v6/guilds/$id")

    override suspend fun deleteGuild(id: Snowflake): Unit = ktor.delete("$discordApiUrl/api/v6/guilds/$id")

    override suspend fun editCurrentNickname(id: Snowflake, request: NicknameEditRequest): String? =
        ktor.patch("$discordApiUrl/api/v6/guilds/$id/members/@me/nick") {
            body = request
        }

    override suspend fun getInvites(id: Snowflake): ListK<InviteResponse<IGuildInvite>> =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/invites")

    override suspend fun getGuildChannels(id: Snowflake): ListK<ChannelResponse<IGuildChannel>> =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/channels")

    override suspend fun createGuildChannel(
        id: Snowflake,
        request: ChannelCreateRequest,
        reason: String?
    ): ChannelResponse<IGuildChannel> = ktor.post("$discordApiUrl/api/v6/guilds/{id}/channels") {
        body = request
        reasonHeader(reason)
    }

    override suspend fun editGuildChannelPositions(id: Snowflake, request: Array<PositionEditRequest>): Unit =
        ktor.patch("$discordApiUrl/api/v6/guilds/{id}/channels") {
            body = request
        }

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): GuildMemberResponse =
        ktor.get("$discordApiUrl/api/v6/guilds/$guildId/members/$userId")

    override suspend fun getMembers(id: Snowflake, query: Query): ListK<GuildMemberResponse> =
        ktor.get("$discordApiUrl/api/v6/guilds/{guildId}/members") {
            parameters(query)
        }

    override suspend fun newMember(
        guildId: Snowflake,
        userId: Snowflake,
        request: MemberAddRequest
    ): GuildMemberResponse = ktor.put("$discordApiUrl/api/v6/guilds/$guildId/members/$userId") {
        body = request
    }

    override suspend fun editMember(
        guildId: Snowflake,
        userId: Snowflake,
        request: MemberEditRequest,
        reason: String?
    ) = ktor.patch<Unit>("$discordApiUrl/api/v6/guilds/$guildId/members/$userId") {
        body = request
        reasonHeader(reason)
    }

    override suspend fun removeMember(guildId: Snowflake, userId: Snowflake, reason: String?): Unit =
        ktor.delete("$discordApiUrl/api/v6/guilds/$guildId/members/$userId") {
            reasonHeader(reason)
        }

    override suspend fun addMemberRole(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String?
    ): Unit =
        ktor.put("$discordApiUrl/api/v6/guilds/$guildId/members/$userId/roles/$roleId") {
            reasonHeader(reason)
        }

    override suspend fun deleteMemberRole(
        guildId: Snowflake,
        userId: Snowflake,
        roleId: Snowflake,
        reason: String?
    ): Unit =
        ktor.delete("$discordApiUrl/api/v6/guilds/$guildId/members/$userId/roles/$roleId") {
            reasonHeader(reason)
        }

    override suspend fun getRoles(id: Snowflake): ListK<RoleResponse> =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/roles")

    override suspend fun createRole(id: Snowflake, request: GuildRoleCreateRequest, reason: String?): RoleResponse =
        ktor.post("$discordApiUrl/api/v6/guilds/$id/roles") {
            body = request
            reasonHeader(reason)
        }

    override suspend fun editRolePositions(id: Snowflake, request: Array<PositionEditRequest>): ListK<RoleResponse> =
        ktor.patch("$discordApiUrl/api/v6/guilds/$id/roles") {
            body = request
        }

    override suspend fun editRole(
        guildId: Snowflake,
        roleId: Snowflake,
        request: GuildRoleEditRequest,
        reason: String?
    ): RoleResponse = ktor.patch("$discordApiUrl/api/v6/guilds/$guildId/roles/$roleId") {
        body = request
        reasonHeader(reason)
    }

    override suspend fun deleteRole(guildId: Snowflake, roleId: Snowflake, reason: String?): Unit =
        ktor.delete("$discordApiUrl/api/v6/guilds/$guildId/roles/$roleId") {
            reasonHeader(reason)
        }

    override suspend fun getBans(id: Snowflake): ListK<BanResponse> = ktor.get("$discordApiUrl/api/v6/guilds/$id/bans")

    override suspend fun getBan(guildId: Snowflake, userId: Snowflake): BanResponse =
        ktor.get("$discordApiUrl/api/v6/guilds/$guildId/bans/$userId")

    override suspend fun ban(guildId: Snowflake, userId: Snowflake, query: Query): Unit =
        ktor.put("$discordApiUrl/api/v6/guilds/$guildId/bans/$userId") {
            parameters(query)
        }

    override suspend fun removeBan(guildId: Snowflake, userId: Snowflake, reason: String?): Unit =
        ktor.delete("$discordApiUrl/api/v6/guilds/$guildId/bans/$userId") {
            reasonHeader(reason)
        }

    override suspend fun getPruneCount(id: Snowflake, query: Query): Int =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/prune") {
            parameters(query)
        }

    override suspend fun startPrune(id: Snowflake, query: Query, reason: String?): Int =
        ktor.post("$discordApiUrl/api/v6/guilds/$id/prune") {
            reasonHeader(reason)
        }

    override suspend fun getIntegrations(id: Snowflake): ListK<GuildIntegrationResponse> =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/integrations")

    override suspend fun createIntegration(id: Snowflake, request: IntegrationCreateRequest): Unit =
        ktor.post("$discordApiUrl/api/v6/guilds/$id/integrations") {
            body = request
        }

    override suspend fun editIntegration(
        guildId: Snowflake,
        integrationId: Snowflake,
        request: IntegrationEditRequest
    ) = ktor.patch<Unit>("$discordApiUrl/api/v6/guilds/$guildId/integrations/$integrationId") {
        body = request
    }

    override suspend fun deleteIntegration(guildId: Snowflake, integrationId: Snowflake) =
        ktor.delete<Unit>("$discordApiUrl/api/v6/guilds/$guildId/integrations/$integrationId")

    override suspend fun syncIntegration(guildId: Snowflake, integrationId: Snowflake): Unit =
        ktor.post("$discordApiUrl/api/v6/guilds/$guildId/integrations/$integrationId")

    override suspend fun getEmbed(id: Snowflake): GuildEmbedResponse =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/embed")

    override suspend fun editEmbed(id: Snowflake, request: GuildEmbedEditRequest): GuildEmbedResponse =
        ktor.patch("$discordApiUrl/api/v6/guilds/$id/embed") {
            body = request
        }

    override suspend fun getGuildWidgetSettings(id: Snowflake): GuildWidgetSettingsResponse =
        ktor.get("$discordApiUrl/api/v6/$id/widget")

    override suspend fun modifyGuildWidget(
        id: Snowflake,
        request: GuildWidgetEditRequest
    ): GuildWidgetSettingsResponse = ktor.patch("$discordApiUrl/api/v6/guilds/$id/widget") {
        body = request
    }

    override suspend fun getGuildWidget(id: Snowflake): GuildWidgetResponse =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/widget.json")

    override suspend fun getVanityUrl(id: Snowflake): InviteResponse<IGuildInvite> =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/vanity-url")
}