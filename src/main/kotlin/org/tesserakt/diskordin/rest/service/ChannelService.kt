package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.query.Query
import retrofit2.http.*

@Suppress("unused")
interface ChannelService {
    @GET("/api/v6/channels/{id}")
    suspend fun getChannel(@Path("id") id: Snowflake): Id<ChannelResponse<IChannel>>

    @PATCH("/api/v6/channels/{id}")
    suspend fun editChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<ChannelResponse<IChannel>>

    @DELETE("/api/v6/channels/{id}")
    suspend fun deleteChannel(
        @Path("id") id: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<ChannelResponse<IChannel>>

    @POST("/api/v6/channels/{id}/typing")
    suspend fun triggerTyping(@Path("id") id: Snowflake)

    @GET("/api/v6/channels/{id}/messages")
    suspend fun getMessages(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): ListK<MessageResponse>

    @GET("/api/v6/channels/{channelId}/messages/{messageId}")
    suspend fun getMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): Id<MessageResponse>

    @Multipart
    @POST("/api/v6/channels/{id}/messages")
    suspend fun createMessage(
        @Path("id") id: Snowflake,
        @Part("payload_json") request: MessageCreateRequest
    ): Id<MessageResponse>

    @PATCH("/api/v6/channels/{channelId}/messages/{messageId}")
    suspend fun editMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Body request: MessageEditRequest
    ): Id<MessageResponse>

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
    suspend fun getPinnedMessages(@Path("id") id: Snowflake): ListK<MessageResponse>

    @PUT("/api/v6/channels/{channelId}/pins/{messageId}")
    suspend fun pinMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    )

    @DELETE("/api/v6/channels/{channelId}/pins/{messageId}")
    suspend fun unpinMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    )

    @PUT("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    suspend fun addReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    suspend fun removeOwnReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/{userId}")
    suspend fun removeReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String,
        @Path("userId") userId: Snowflake
    )

    @GET("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}")
    suspend fun getReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String,
        @QueryMap query: Query
    ): ListK<UserResponse<IUser>>

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions")
    suspend fun removeAllReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    )

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}")
    suspend fun removeAllReactionsForEmoji(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji", encoded = true) emoji: String
    )

    @GET("/api/v6/channels/{id}/invites")
    suspend fun getChannelInvites(@Path("id") id: Snowflake): ListK<InviteResponse<IInvite>>

    @POST("/api/v6/channels/{id}/invites")
    suspend fun createChannelInvite(
        @Path("id") id: Snowflake,
        @Body request: InviteCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): Id<InviteResponse<IInvite>>

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