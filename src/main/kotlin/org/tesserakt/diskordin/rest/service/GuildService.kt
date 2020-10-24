@file:Suppress("unused", "DEPRECATION")

package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.query.Query
import retrofit2.http.*

interface GuildService {
    @POST("/api/v6/guilds")
    suspend fun createGuild(@Body request: GuildCreateRequest): Id<GuildResponse>

    @GET("/api/v6/guilds/{id}")
    suspend fun getGuild(@Path("id") id: Snowflake): Id<GuildResponse>

    @GET("/api/v6/guilds/{id}/preview")
    suspend fun getGuildPreview(@Path("id") id: Snowflake): Id<GuildPreviewResponse>

    @PATCH("/api/v6/guilds/{id}")
    suspend fun editGuild(
        @Path("id") id: Snowflake,
        @Body request: GuildEditRequest
    ): Id<GuildResponse>

    @Deprecated("Bots can use this only 10 times")
    @DELETE("/api/v6/guilds/{id}")
    suspend fun deleteGuild(@Path("id") id: Snowflake)

    @PATCH("/api/v6/guilds/{id}/members/@me/nick")
    suspend fun editCurrentNickname(
        @Path("id") id: Snowflake,
        @Body request: NicknameEditRequest
    ): Id<String?>

    @GET("/api/v6/guilds/{id}/invites")
    suspend fun getInvites(@Path("id") id: Snowflake): ListK<InviteResponse<IGuildInvite>>

    @GET("/api/v6/guilds/{id}/channels")
    suspend fun getGuildChannels(@Path("id") id: Snowflake): ListK<ChannelResponse<IGuildChannel>>

    @POST("/api/v6/guilds/{id}/channels")
    suspend fun createGuildChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<ChannelResponse<IGuildChannel>>

    @PATCH("/api/v6/guilds/{id}/channels")
    suspend fun editGuildChannelPositions(
        @Path("id") id: Snowflake,
        @Body request: Array<PositionEditRequest>
    )

    @GET("/api/v6/guilds/{guildId}/members/{userId}")
    suspend fun getMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake
    ): Id<GuildMemberResponse>

    @GET("/api/v6/guilds/{id}/members")
    suspend fun getMembers(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): ListK<GuildMemberResponse>

    @PUT("/api/v6/guilds/{guildId}/members/{userId}")
    suspend fun newMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: MemberAddRequest
    ): Id<GuildMemberResponse>

    @PATCH("/api/v6/guilds/{guildId}/members/{userId}")
    suspend fun editMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: MemberEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @DELETE("/api/v6/guilds/{guildId}/members/{userId}")
    suspend fun removeMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @PUT("/api/v6/guilds/{guildId}/members/{userId}/roles/{roleId}")
    suspend fun addMemberRole(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @DELETE("/api/v6/guilds/{guildId}/members/{userId}/roles/{roleId}")
    suspend fun deleteMemberRole(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @GET("/api/v6/guilds/{id}/roles")
    suspend fun getRoles(@Path("id") id: Snowflake): ListK<RoleResponse>

    @POST("/api/v6/guilds/{id}/roles")
    suspend fun createRole(
        @Path("id") id: Snowflake,
        @Body request: GuildRoleCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<RoleResponse>

    @PATCH("/api/v6/guilds/{id}/roles")
    suspend fun editRolePositions(
        @Path("id") id: Snowflake,
        @Body request: Array<PositionEditRequest>
    ): ListK<RoleResponse>

    @PATCH("/api/v6/guilds/{guildId}/roles/{roleId}")
    suspend fun editRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Body request: GuildRoleEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<RoleResponse>

    @DELETE("/api/v6/guilds/{guildId}/roles/{roleId}")
    suspend fun deleteRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @GET("/api/v6/guilds/{id}/bans")
    suspend fun getBans(@Path("id") id: Snowflake): ListK<BanResponse>

    @GET("/api/v6/guilds/{guildId}/bans/{userId}")
    suspend fun getBan(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake
    ): Id<BanResponse>

    @PUT("/api/v6/guilds/{guildId}/bans/{userId}")
    suspend fun ban(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @QueryMap query: Query
    )

    @DELETE("/api/v6/guilds/{guildId}/bans/{userId}")
    suspend fun removeBan(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @GET("/api/v6/guilds/{id}/prune")
    suspend fun getPruneCount(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): Id<Int>

    @POST("/api/v6/guilds/{id}/prune")
    suspend fun startPrune(
        @Path("id") id: Snowflake,
        @QueryMap query: Query,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<Int>

    @GET("/api/v6/guilds/{id}/integrations")
    suspend fun getIntegrations(@Path("id") id: Snowflake): ListK<GuildIntegrationResponse>

    @POST("/api/v6/guilds/{id}/integrations")
    suspend fun createIntegration(
        @Path("id") id: Snowflake,
        @Body request: IntegrationCreateRequest
    )

    @PATCH("/api/v6/guilds/{guildId}/integrations/{intId}")
    suspend fun editIntegration(
        @Path("guildId") guildId: Snowflake,
        @Path("intId") integrationId: Snowflake,
        @Body request: IntegrationEditRequest
    )

    @DELETE("/api/v6/guilds/{guildId}/integrations/{intId}")
    suspend fun deleteIntegration(
        @Path("guildId") guildId: Snowflake,
        @Path("intId") integrationId: Snowflake
    )

    @POST("/api/v6/guilds/{guildId}/integrations/{intId}")
    suspend fun syncIntegration(
        @Path("guildId") guildId: Snowflake,
        @Path("intId") integrationId: Snowflake
    )

    @GET("/api/v6/guilds/{id}/embed")
    suspend fun getEmbed(@Path("id") id: Snowflake): Id<GuildEmbedResponse>

    @PATCH("/api/v6/guilds/{id}/embed")
    suspend fun editEmbed(
        @Path("id") id: Snowflake,
        @Body request: GuildEmbedEditRequest
    ): Id<GuildEmbedResponse>
}