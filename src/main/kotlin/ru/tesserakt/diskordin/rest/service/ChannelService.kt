package ru.tesserakt.diskordin.rest.service

import retrofit2.http.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.*
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.InviteResponse
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.query.Query

@Suppress("unused")
interface ChannelService {
    @GET("/api/v6/channels/{id}")
    suspend fun <C : IChannel> getChannel(@Path("id") id: Snowflake): ChannelResponse<C>

    @PATCH("/api/v6/channels/{id}")
    suspend fun <C : IChannel> editChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): ChannelResponse<C>

    @DELETE("/api/v6/channels/{id}")
    suspend fun <C : IChannel> deleteChannel(
        @Path("id") id: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): ChannelResponse<C>

    @POST("/api/v6/channels/{id}/typing")
    suspend fun triggerTyping(@Path("id") id: Snowflake)

    @GET("/api/v6/channels/{id}/messages")
    suspend fun getMessages(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): Array<MessageResponse>

    @GET("/api/v6/channels/{channelId}/messages/{messageId}")
    suspend fun getMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): MessageResponse

    @Multipart
    @POST("/api/v6/channels/{id}/messages")
    suspend fun createMessage(
        @Path("id") id: Snowflake,
        @Part("payload_json") request: MessageCreateRequest
    ): MessageResponse

    @PATCH("/api/v6/channels/{channelId}/messages/{messageId}")
    suspend fun editMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Body request: MessageEditRequest
    ): MessageResponse

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}")
    suspend fun deleteMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @POST("/api/v6/channels/{id}/messages/bulk-delete")
    suspend fun bulkDeleteMessages(
        @Path("id") channelId: Snowflake,
        @Body request: BulkDeleteRequest
    )

    @GET("/api/v6/channels/{id}/pins")
    suspend fun getPinnedMessages(@Path("id") id: Snowflake): Array<MessageResponse>

    @PUT("/api/v6/channels/{id}/pins/{messageId}")
    suspend fun pinMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    )

    @DELETE("/api/v6/channels/{id}/pins/{messageId}")
    suspend fun unpinMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    )

    @PUT("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    suspend fun addReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    suspend fun removeOwnReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/{userId}")
    suspend fun removeReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String,
        @Path("userId") userId: Snowflake
    )

    @GET("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}")
    suspend fun getReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String,
        @QueryMap query: Query
    ): Array<UserResponse<IUser>>

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions")
    suspend fun removeAllReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    )

    @GET("/api/v6/channels/{id}/invites")
    suspend fun getChannelInvites(@Path("id") id: Snowflake): Array<InviteResponse<IInvite>>

    @POST("/api/v6/channels/{id}/invites")
    suspend fun createChannelInvite(
        @Path("id") id: Snowflake,
        @Body request: InviteCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): InviteResponse<IInvite>

    @DELETE("/api/v6/channels/{channelId}/permissions/{overwriteId}")
    suspend fun deleteChannelPermissions(
        @Path("channelId") channelId: Snowflake,
        @Path("overwriteId") overwriteId: Long,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @PUT("/api/v6/channels/{channelId}/permissions/{overwriteId}")
    suspend fun editChannelPermissions(
        @Path("channelId") channelId: Snowflake,
        @Path("overwriteId") overwriteId: Long,
        @Body request: PermissionsEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    )

    @PUT("/api/v6/channels/{channelId}/recipients/{userId}")
    suspend fun addDMRecipient(
        @Path("channelId") channelId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: GroupRecipientAddRequest
    )

    @DELETE("/api/v6/channels/{channelId}/recipients/{userId}")
    suspend fun removeDMRecipient(
        @Path("channelId") channelId: Snowflake,
        @Path("userId") userId: Snowflake
    )
}