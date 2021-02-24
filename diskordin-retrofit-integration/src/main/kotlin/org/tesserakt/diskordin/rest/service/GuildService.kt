package org.tesserakt.diskordin.rest.service


import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.query.Query
import retrofit2.http.*

interface GuildServiceImpl : GuildService {
    @POST("/api/v6/guilds")
    override suspend fun createGuild(@Body request: GuildCreateRequest): GuildResponse

    @GET("/api/v6/guilds/{id}")
    override suspend fun getGuild(@Path("id") id: Snowflake): GuildResponse

    @GET("/api/v6/guilds/{id}/preview")
    override suspend fun getGuildPreview(@Path("id") id: Snowflake): GuildPreviewResponse

    @PATCH("/api/v6/guilds/{id}")
    override suspend fun editGuild(@Path("id") id: Snowflake, @Body request: GuildEditRequest): GuildResponse

    @PATCH("/api/v6/guilds/{id}/members/@me/nick")
    override suspend fun editCurrentNickname(@Path("id") id: Snowflake, @Body request: NicknameEditRequest): String?

    @GET("/api/v6/guilds/{id}/invites")
    override suspend fun getInvites(@Path("id") id: Snowflake): List<InviteResponse<IGuildInvite>>

    @GET("/api/v6/guilds/{id}/channels")
    override suspend fun getGuildChannels(@Path("id") id: Snowflake): List<ChannelResponse<IGuildChannel>>

    @POST("/api/v6/guilds/{id}/channels")
    override suspend fun createGuildChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): ChannelResponse<IGuildChannel>

    @PATCH("/api/v6/guilds/{id}/channels")
    override suspend fun editGuildChannelPositions(@Path("id") id: Snowflake, @Body request: Array<PositionEditRequest>)

    @GET("/api/v6/guilds/{guildId}/members/{userId}")
    override suspend fun getMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake
    ): GuildMemberResponse

    @GET("/api/v6/guilds/{id}/members")
    override suspend fun getMembers(@Path("id") id: Snowflake, @QueryMap query: Query): List<GuildMemberResponse>

    @PUT("/api/v6/guilds/{guildId}/members/{userId}")
    override suspend fun newMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: MemberAddRequest
    ): GuildMemberResponse

    @PATCH("/api/v6/guilds/{guildId}/members/{userId}")
    override suspend fun editMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: MemberEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @DELETE("/api/v6/guilds/{guildId}/members/{userId}")
    override suspend fun removeMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @PUT("/api/v6/guilds/{guildId}/members/{userId}/roles/{roleId}")
    override suspend fun addMemberRole(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @DELETE("/api/v6/guilds/{guildId}/members/{userId}/roles/{roleId}")
    override suspend fun deleteMemberRole(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @GET("/api/v6/guilds/{id}/roles")
    override suspend fun getRoles(@Path("id") id: Snowflake): List<RoleResponse>

    @POST("/api/v6/guilds/{id}/roles")
    override suspend fun createRole(
        @Path("id") id: Snowflake,
        @Body request: GuildRoleCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): RoleResponse

    @PATCH("/api/v6/guilds/{id}/roles")
    override suspend fun editRolePositions(
        @Path("id") id: Snowflake,
        @Body request: Array<PositionEditRequest>
    ): List<RoleResponse>

    @PATCH("/api/v6/guilds/{guildId}/roles/{roleId}")
    override suspend fun editRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Body request: GuildRoleEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): RoleResponse

    @DELETE("/api/v6/guilds/{guildId}/roles/{roleId}")
    override suspend fun deleteRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @GET("/api/v6/guilds/{id}/bans")
    override suspend fun getBans(@Path("id") id: Snowflake): List<BanResponse>

    @GET("/api/v6/guilds/{guildId}/bans/{userId}")
    override suspend fun getBan(@Path("guildId") guildId: Snowflake, @Path("userId") userId: Snowflake): BanResponse

    @PUT("/api/v6/guilds/{guildId}/bans/{userId}")
    override suspend fun ban(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @QueryMap query: Query
    )

    @DELETE("/api/v6/guilds/{guildId}/bans/{userId}")
    override suspend fun removeBan(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @GET("/api/v6/guilds/{id}/prune")
    override suspend fun getPruneCount(@Path("id") id: Snowflake, @QueryMap query: Query): Int

    @POST("/api/v6/guilds/{id}/prune")
    override suspend fun startPrune(
        @Path("id") id: Snowflake,
        @QueryMap query: Query,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Int

    @GET("/api/v6/guilds/{id}/integrations")
    override suspend fun getIntegrations(@Path("id") id: Snowflake): List<GuildIntegrationResponse>

    @POST("/api/v6/guilds/{id}/integrations")
    override suspend fun createIntegration(@Path("id") id: Snowflake, @Body request: IntegrationCreateRequest)

    @PATCH("/api/v6/guilds/{guildId}/integrations/{intId}")
    override suspend fun editIntegration(
        @Path("guildId") guildId: Snowflake,
        @Path("intId") integrationId: Snowflake,
        @Body request: IntegrationEditRequest
    )

    @DELETE("/api/v6/guilds/{guildId}/integrations/{intId}")
    override suspend fun deleteIntegration(@Path("guildId") guildId: Snowflake, @Path("intId") integrationId: Snowflake)

    @POST("/api/v6/guilds/{guildId}/integrations/{intId}")
    override suspend fun syncIntegration(@Path("guildId") guildId: Snowflake, @Path("intId") integrationId: Snowflake)

    @GET("/api/v6/guilds/{id}/embed")
    override suspend fun getEmbed(@Path("id") id: Snowflake): GuildEmbedResponse

    @PATCH("/api/v6/guilds/{id}/embed")
    override suspend fun editEmbed(@Path("id") id: Snowflake, @Body request: GuildEmbedEditRequest): GuildEmbedResponse

    @GET("/api/v6/guilds/{id}/widget")
    override suspend fun getGuildWidgetSettings(@Path("id") id: Snowflake): GuildWidgetSettingsResponse

    @PATCH("/api/v6/guilds/{id}/widget")
    override suspend fun modifyGuildWidget(
        @Path("id") id: Snowflake,
        @Body request: GuildWidgetEditRequest
    ): GuildWidgetSettingsResponse

    @GET("/api/v6/guilds/{id}/widget.json")
    override suspend fun getGuildWidget(@Path("id") id: Snowflake): GuildWidgetResponse

    @GET("/api/v6/guilds/{id}/vanity-url")
    override suspend fun getVanityUrl(@Path("id") id: Snowflake): InviteResponse<IGuildInvite>
}