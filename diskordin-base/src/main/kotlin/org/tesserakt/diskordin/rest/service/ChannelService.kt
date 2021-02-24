package org.tesserakt.diskordin.rest.service

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.query.Query

@Suppress("unused")
interface ChannelService {
    suspend fun getChannel(id: Snowflake): ChannelResponse<IChannel>

    suspend fun editChannel(
        id: Snowflake,
        request: ChannelEditRequest,
        reason: String?
    ): ChannelResponse<IChannel>

    suspend fun deleteChannel(
        id: Snowflake,
        reason: String?
    ): ChannelResponse<IChannel>

    suspend fun triggerTyping(id: Snowflake)

    suspend fun getMessages(
        id: Snowflake,
        query: Query
    ): List<MessageResponse>

    suspend fun getMessage(
        channelId: Snowflake,
        messageId: Snowflake
    ): MessageResponse

    suspend fun createMessage(
        id: Snowflake,
        request: MessageCreateRequest
    ): MessageResponse

    suspend fun crosspostMessage(
        channelId: Snowflake,
        messageId: Snowflake
    ): MessageResponse

    suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MessageEditRequest
    ): MessageResponse

    suspend fun deleteMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        reason: String?
    )

    suspend fun bulkDeleteMessages(
        channelId: Snowflake,
        request: BulkDeleteRequest
    )

    //TODO add webhooks support
    suspend fun followNewsChannel(
        id: Snowflake,
        request: FollowRequest
    ): FollowedChannelResponse

    suspend fun getPinnedMessages(id: Snowflake): List<MessageResponse>

    suspend fun pinMessage(
        channelId: Snowflake,
        messageId: Snowflake
    )

    suspend fun unpinMessage(
        channelId: Snowflake,
        messageId: Snowflake
    )

    suspend fun addReaction(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String
    )

    suspend fun removeOwnReaction(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String
    )

    suspend fun removeReaction(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String,
        userId: Snowflake
    )

    suspend fun getReactions(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String,
        query: Query
    ): List<UserResponse<IUser>>

    suspend fun removeAllReactions(
        channelId: Snowflake,
        messageId: Snowflake
    )

    suspend fun removeAllReactionsForEmoji(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String
    )

    suspend fun getChannelInvites(id: Snowflake): List<InviteResponse<IInvite>>

    suspend fun createChannelInvite(
        id: Snowflake,
        request: InviteCreateRequest,
        reason: String?
    ): InviteResponse<IInvite>

    suspend fun deleteChannelPermissions(
        channelId: Snowflake,
        overwriteId: Long,
        reason: String?
    )

    suspend fun editChannelPermissions(
        channelId: Snowflake,
        overwriteId: Long,
        request: PermissionsEditRequest,
        reason: String?
    )

    suspend fun addDMRecipient(
        channelId: Snowflake,
        userId: Snowflake,
        request: GroupRecipientAddRequest
    )

    suspend fun removeDMRecipient(
        channelId: Snowflake,
        userId: Snowflake
    )
}