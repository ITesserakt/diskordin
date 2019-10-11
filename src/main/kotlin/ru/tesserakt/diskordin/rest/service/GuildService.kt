@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import retrofit2.http.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.*
import ru.tesserakt.diskordin.core.data.json.response.*
import ru.tesserakt.diskordin.core.entity.query.Query

interface GuildService {
    @POST("/api/v6/guilds")
    suspend fun createGuild(@Body request: GuildCreateRequest): GuildResponse

    @GET("/api/v6/guilds/{id}")
    suspend fun getGuild(@Path("id") id: Snowflake): GuildResponse

    @PATCH("/api/v6/guilds/{id}")
    suspend fun editGuild(
        @Path("id") id: Snowflake,
        @Body request: GuildEditRequest
    ): GuildResponse

    @Deprecated("Bots can use this only 10 times")
    @DELETE("/api/v6/guilds/{id}")
    suspend fun deleteGuild(@Path("id") id: Snowflake)

    @PATCH("/api/v6/guilds/{id}/members/@me/nick")
    suspend fun editCurrentNickname(
        @Path("id") id: Snowflake,
        @Body request: NicknameEditRequest
    ): NicknameModifyResponse

    @GET("/api/v6/guilds/{id}/invites")
    suspend fun getInvites(@Path("id") id: Snowflake): Array<InviteResponse>

    @GET("/api/v6/guilds/{id}/channels")
    suspend fun getGuildChannels(@Path("id") id: Snowflake): Array<ChannelResponse>

    @POST("/api/v6/guilds/{id}/channels")
    suspend fun createGuildChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): ChannelResponse

    @PATCH("/api/v6/guilds/{id}/channels")
    suspend fun editGuildChannelPositions(
        @Path("id") id: Snowflake,
        @Body request: Array<PositionEditRequest>
    )

    @GET("/api/v6/guilds/{guildId}/members/{userId}")
    suspend fun getMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake
    ): GuildMemberResponse

    @GET("/api/v6/guilds/{id}/members")
    suspend fun getMembers(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): Array<GuildMemberResponse>

    @PUT("/api/v6/guilds/{guildId}/members/{userId}")
    suspend fun newMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: MemberAddRequest
    ): GuildMemberResponse

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
    suspend fun getRoles(@Path("id") id: Snowflake): Array<RoleResponse>

    @POST("/api/v6/guilds/{id}/roles")
    suspend fun createRole(
        @Path("id") id: Snowflake,
        @Body request: GuildRoleCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): RoleResponse

    @PATCH("/api/v6/guilds/{id}/roles")
    suspend fun editRolePositions(
        @Path("id") id: Snowflake,
        @Body request: Array<PositionEditRequest>
    ): Array<RoleResponse>

    @PATCH("/api/v6/guilds/{guildId}/roles/{roleId}")
    suspend fun editRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Body request: GuildRoleEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): RoleResponse

    @DELETE("/api/v6/guilds/{guildId}/roles/{roleId}")
    suspend fun deleteRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @GET("/api/v6/guilds/{id}/bans")
    suspend fun getBans(@Path("id") id: Snowflake): Array<BanResponse>

    @GET("/api/v6/guilds/{guildId}/bans/{userId}")
    suspend fun getBan(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake
    ): BanResponse

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
    ): PruneResponse

    @POST("/api/v6/guilds/{id}/prune")
    suspend fun startPrune(
        @Path("id") id: Snowflake,
        @QueryMap query: Query,
        @Header("X-Audit-Log-Reason") reason: String?
    ): PruneResponse

    @GET("/api/v6/guilds/{id}/integrations")
    suspend fun getIntegrations(@Path("id") id: Snowflake): Array<GuildIntegrationResponse>

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
    suspend fun getEmbed(@Path("id") id: Snowflake): GuildEmbedResponse

    @PATCH("/api/v6/guilds/{id}/embed")
    suspend fun editEmbed(
        @Path("id") id: Snowflake,
        @Body request: GuildEmbedEditRequest
    ): GuildEmbedResponse
}