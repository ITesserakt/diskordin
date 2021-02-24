package org.tesserakt.diskordin.rest.service


import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.query.Query
import retrofit2.http.*

interface ChannelServiceImpl : ChannelService {
    @GET("/api/v6/channels/{id}")
    override suspend fun getChannel(@Path("id") id: Snowflake): ChannelResponse<IChannel>

    @PATCH("/api/v6/channels/{id}")
    override suspend fun editChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): ChannelResponse<IChannel>

    @DELETE("/api/v6/channels/{id}")
    override suspend fun deleteChannel(
        @Path("id") id: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): ChannelResponse<IChannel>

    @POST("/api/v6/channels/{id}/typing")
    override suspend fun triggerTyping(@Path("id") id: Snowflake)

    @GET("/api/v6/channels/{id}/messages")
    override suspend fun getMessages(@Path("id") id: Snowflake, @QueryMap query: Query): List<MessageResponse>

    @GET("/api/v6/channels/{channelId}/messages/{messageId}")
    override suspend fun getMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): MessageResponse

    @Multipart
    @POST("/api/v6/channels/{id}/messages")
    override suspend fun createMessage(
        @Path("id") id: Snowflake,
        @Part("payload_json") request: MessageCreateRequest
    ): MessageResponse

    @POST("/api/channels/{channelId}/messages/{messageId}/crosspost")
    override suspend fun crosspostMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): MessageResponse

    @PATCH("/api/v6/channels/{channelId}/messages/{messageId}")
    override suspend fun editMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Body request: MessageEditRequest
    ): MessageResponse

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}")
    override suspend fun deleteMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @POST("/api/v6/channels/{id}/messages/bulk-delete")
    override suspend fun bulkDeleteMessages(@Path("id") channelId: Snowflake, @Body request: BulkDeleteRequest)

    @POST("/api/v6/channels/{id}/followers")
    override suspend fun followNewsChannel(
        @Path("id") id: Snowflake,
        @Body request: FollowRequest
    ): FollowedChannelResponse

    @GET("/api/v6/channels/{id}/pins")
    override suspend fun getPinnedMessages(@Path("id") id: Snowflake): List<MessageResponse>

    @PUT("/api/v6/channels/{channelId}/pins/{messageId}")
    override suspend fun pinMessage(@Path("channelId") channelId: Snowflake, @Path("messageId") messageId: Snowflake)

    @DELETE("/api/v6/channels/{channelId}/pins/{messageId}")
    override suspend fun unpinMessage(@Path("channelId") channelId: Snowflake, @Path("messageId") messageId: Snowflake)

    @PUT("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    override suspend fun addReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    override suspend fun removeOwnReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/{userId}")
    override suspend fun removeReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String,
        @Path("userId") userId: Snowflake
    )

    @GET("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}")
    override suspend fun getReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String,
        @QueryMap query: Query
    ): List<UserResponse<IUser>>

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions")
    override suspend fun removeAllReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}")
    override suspend fun removeAllReactionsForEmoji(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String
    )

    @GET("/api/v6/channels/{id}/invites")
    override suspend fun getChannelInvites(@Path("id") id: Snowflake): List<InviteResponse<IInvite>>

    @POST("/api/v6/channels/{id}/invites")
    override suspend fun createChannelInvite(
        @Path("id") id: Snowflake,
        @Body request: InviteCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): InviteResponse<IInvite>

    @DELETE("/api/v6/channels/{channelId}/permissions/{overwriteId}")
    override suspend fun deleteChannelPermissions(
        @Path("channelId") channelId: Snowflake,
        @Path("overwriteId") overwriteId: Long,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @PUT("/api/v6/channels/{channelId}/permissions/{overwriteId}")
    override suspend fun editChannelPermissions(
        @Path("channelId") channelId: Snowflake,
        @Path("overwriteId") overwriteId: Long,
        @Body request: PermissionsEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @PUT("/api/v6/channels/{channelId}/recipients/{userId}")
    override suspend fun addDMRecipient(
        @Path("channelId") channelId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: GroupRecipientAddRequest
    )

    @DELETE("/api/v6/channels/{channelId}/recipients/{userId}")
    override suspend fun removeDMRecipient(@Path("channelId") channelId: Snowflake, @Path("userId") userId: Snowflake)
}