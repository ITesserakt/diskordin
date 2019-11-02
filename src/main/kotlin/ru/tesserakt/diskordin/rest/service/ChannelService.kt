package ru.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
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
    fun <C : IChannel> getChannel(@Path("id") id: Snowflake): CallK<Id<ChannelResponse<C>>>

    @PATCH("/api/v6/channels/{id}")
    fun <C : IChannel> editChannel(
        @Path("id") id: Snowflake,
        @Body request: ChannelEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<ChannelResponse<C>>>

    @DELETE("/api/v6/channels/{id}")
    fun <C : IChannel> deleteChannel(
        @Path("id") id: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<ChannelResponse<C>>>

    @POST("/api/v6/channels/{id}/typing")
    fun triggerTyping(@Path("id") id: Snowflake): CallK<Unit>

    @GET("/api/v6/channels/{id}/messages")
    fun getMessages(
        @Path("id") id: Snowflake,
        @QueryMap query: Query
    ): CallK<ListK<MessageResponse>>

    @GET("/api/v6/channels/{channelId}/messages/{messageId}")
    fun getMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): CallK<Id<MessageResponse>>

    @Multipart
    @POST("/api/v6/channels/{id}/messages")
    fun createMessage(
        @Path("id") id: Snowflake,
        @Part("payload_json") request: MessageCreateRequest
    ): CallK<Id<MessageResponse>>

    @PATCH("/api/v6/channels/{channelId}/messages/{messageId}")
    fun editMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Body request: MessageEditRequest
    ): CallK<Id<MessageResponse>>

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}")
    fun deleteMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @POST("/api/v6/channels/{id}/messages/bulk-delete")
    fun bulkDeleteMessages(
        @Path("id") channelId: Snowflake,
        @Body request: BulkDeleteRequest
    ): CallK<Unit>

    @GET("/api/v6/channels/{id}/pins")
    fun getPinnedMessages(@Path("id") id: Snowflake): CallK<ListK<MessageResponse>>

    @PUT("/api/v6/channels/{id}/pins/{messageId}")
    fun pinMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): CallK<Unit>

    @DELETE("/api/v6/channels/{channelId}/pins/{messageId}")
    fun unpinMessage(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): CallK<Unit>

    @PUT("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    fun addReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String
    ): CallK<Unit>

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/@me")
    fun removeOwnReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String
    ): CallK<Unit>

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}/{userId}")
    fun removeReaction(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String,
        @Path("userId") userId: Snowflake
    ): CallK<Unit>

    @GET("/api/v6/channels/{channelId}/messages/{messageId}/reactions/{emoji}")
    fun getReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake,
        @Path("emoji") emoji: String,
        @QueryMap query: Query
    ): CallK<ListK<UserResponse<IUser>>>

    @DELETE("/api/v6/channels/{channelId}/messages/{messageId}/reactions")
    fun removeAllReactions(
        @Path("channelId") channelId: Snowflake,
        @Path("messageId") messageId: Snowflake
    ): CallK<Unit>

    @GET("/api/v6/channels/{id}/invites")
    fun getChannelInvites(@Path("id") id: Snowflake): CallK<ListK<InviteResponse<IInvite>>>

    @POST("/api/v6/channels/{id}/invites")
    fun createChannelInvite(
        @Path("id") id: Snowflake,
        @Body request: InviteCreateRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Id<InviteResponse<IInvite>>>

    @DELETE("/api/v6/channels/{channelId}/permissions/{overwriteId}")
    fun deleteChannelPermissions(
        @Path("channelId") channelId: Snowflake,
        @Path("overwriteId") overwriteId: Long,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @PUT("/api/v6/channels/{channelId}/permissions/{overwriteId}")
    fun editChannelPermissions(
        @Path("channelId") channelId: Snowflake,
        @Path("overwriteId") overwriteId: Long,
        @Body request: PermissionsEditRequest,
        @Header("X-Audit-Log-Reason") reason: String?
    ): CallK<Unit>

    @PUT("/api/v6/channels/{channelId}/recipients/{userId}")
    fun addDMRecipient(
        @Path("channelId") channelId: Snowflake,
        @Path("userId") userId: Snowflake,
        @Body request: GroupRecipientAddRequest
    ): CallK<Unit>

    @DELETE("/api/v6/channels/{channelId}/recipients/{userId}")
    fun removeDMRecipient(
        @Path("channelId") channelId: Snowflake,
        @Path("userId") userId: Snowflake
    ): CallK<Unit>
}