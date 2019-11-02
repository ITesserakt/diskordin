@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
import retrofit2.http.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.*
import ru.tesserakt.diskordin.core.data.json.response.*
import ru.tesserakt.diskordin.core.entity.IGuildChannel
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.query.Query

interface GuildService {
    @POST("/api/v6/guilds")
    fun createGuild(@Body request: GuildCreateRequest): CallK<Id<GuildResponse>>

    @GET("/api/v6/guilds/{id}")
    fun getGuild(@Path("id") id: Snowflake): CallK<Id<GuildResponse>>

    @PATCH("/api/v6/guilds/{id}")
    fun editGuild(
        @Path("id") id: Snowflake,
        @Body request: GuildEditRequest
    ): CallK<Id<GuildResponse>>

    @Deprecated("Bots can use this only 10 times")
    @DELETE("/api/v6/guilds/{id}")
    fun deleteGuild(@Path("id") id: Snowflake): CallK<Unit>

    @PATCH("/api/v6/guilds/{id}/members/@me/nick")
    fun editCurrentNickname(
        @Path("id") id: Snowflake,
        @Body request: NicknameEditRequest
    ): CallK<Id<String?>>

    @GET("/api/v6/guilds/{id}/invites")
    fun getInvites(@Path("id") id: Snowflake): CallK<ListK<InviteResponse<IGuildInvite>>>

    @GET("/api/v6/guilds/{id}/channels")
    fun getGuildChannels(@Path("id") id: Snowflake): CallK<ListK<ChannelResponse<IGuildChannel>>>

    @POST("/api/v6/guilds/{id}/channels")
    fun createGuildChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<ChannelResponse<IGuildChannel>>>

    @PATCH("/api/v6/guilds/{id}/channels")
    fun editGuildChannelPositions(
        @Path("id") id: Snowflake,
        @Body request: Array<PositionEditRequest>
    ): CallK<Unit>

    @GET("/api/v6/guilds/{guildId}/members/{userId}")
    fun getMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake
    ): CallK<Id<GuildMemberResponse>>

    @GET("/api/v6/guilds/{id}/members")
    fun getMembers(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): CallK<ListK<GuildMemberResponse>>

    @PUT("/api/v6/guilds/{guildId}/members/{userId}")
    fun newMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: MemberAddRequest
    ): CallK<Id<GuildMemberResponse>>

    @PATCH("/api/v6/guilds/{guildId}/members/{userId}")
    fun editMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: MemberEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @DELETE("/api/v6/guilds/{guildId}/members/{userId}")
    fun removeMember(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @PUT("/api/v6/guilds/{guildId}/members/{userId}/roles/{roleId}")
    fun addMemberRole(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @DELETE("/api/v6/guilds/{guildId}/members/{userId}/roles/{roleId}")
    fun deleteMemberRole(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @GET("/api/v6/guilds/{id}/roles")
    fun getRoles(@Path("id") id: Snowflake): CallK<ListK<RoleResponse>>

    @POST("/api/v6/guilds/{id}/roles")
    fun createRole(
        @Path("id") id: Snowflake,
        @Body request: GuildRoleCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<RoleResponse>>

    @PATCH("/api/v6/guilds/{id}/roles")
    fun editRolePositions(
        @Path("id") id: Snowflake,
        @Body request: Array<PositionEditRequest>
    ): CallK<ListK<RoleResponse>>

    @PATCH("/api/v6/guilds/{guildId}/roles/{roleId}")
    fun editRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Body request: GuildRoleEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<RoleResponse>>

    @DELETE("/api/v6/guilds/{guildId}/roles/{roleId}")
    fun deleteRole(
        @Path("guildId") guildId: Snowflake,
        @Path("roleId") roleId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @GET("/api/v6/guilds/{id}/bans")
    fun getBans(@Path("id") id: Snowflake): CallK<ListK<BanResponse>>

    @GET("/api/v6/guilds/{guildId}/bans/{userId}")
    fun getBan(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake
    ): CallK<Id<BanResponse>>

    @PUT("/api/v6/guilds/{guildId}/bans/{userId}")
    fun ban(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @QueryMap query: Query
    ): CallK<Unit>

    @DELETE("/api/v6/guilds/{guildId}/bans/{userId}")
    fun removeBan(
        @Path("guildId") guildId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @GET("/api/v6/guilds/{id}/prune")
    fun getPruneCount(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): CallK<Id<Int>>

    @POST("/api/v6/guilds/{id}/prune")
    fun startPrune(
        @Path("id") id: Snowflake,
        @QueryMap query: Query,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<Int>>

    @GET("/api/v6/guilds/{id}/integrations")
    fun getIntegrations(@Path("id") id: Snowflake): CallK<ListK<GuildIntegrationResponse>>

    @POST("/api/v6/guilds/{id}/integrations")
    fun createIntegration(
        @Path("id") id: Snowflake,
        @Body request: IntegrationCreateRequest
    ): CallK<Unit>

    @PATCH("/api/v6/guilds/{guildId}/integrations/{intId}")
    fun editIntegration(
        @Path("guildId") guildId: Snowflake,
        @Path("intId") integrationId: Snowflake,
        @Body request: IntegrationEditRequest
    ): CallK<Unit>

    @DELETE("/api/v6/guilds/{guildId}/integrations/{intId}")
    fun deleteIntegration(
        @Path("guildId") guildId: Snowflake,
        @Path("intId") integrationId: Snowflake
    ): CallK<Unit>

    @POST("/api/v6/guilds/{guildId}/integrations/{intId}")
    fun syncIntegration(
        @Path("guildId") guildId: Snowflake,
        @Path("intId") integrationId: Snowflake
    ): CallK<Unit>

    @GET("/api/v6/guilds/{id}/embed")
    fun getEmbed(@Path("id") id: Snowflake): CallK<Id<GuildEmbedResponse>>

    @PATCH("/api/v6/guilds/{id}/embed")
    fun editEmbed(
        @Path("id") id: Snowflake,
        @Body request: GuildEmbedEditRequest
    ): CallK<Id<GuildEmbedResponse>>
}