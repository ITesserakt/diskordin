package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.*
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.query.Query
import org.tesserakt.diskordin.rest.integration.parameters
import org.tesserakt.diskordin.rest.integration.reasonHeader
import org.tesserakt.diskordin.util.toJson

class ChannelServiceImpl(private val ktor: HttpClient, private val discordApiURL: String) : ChannelService {
    override suspend fun getChannel(id: Snowflake): Id<ChannelResponse<IChannel>> =
        ktor.get("$discordApiURL/api/v6/channels/$id")

    override suspend fun editChannel(
        id: Snowflake,
        request: ChannelEditRequest,
        reason: String?
    ): Id<ChannelResponse<IChannel>> = ktor.patch("$discordApiURL/api/v6/channels/$id") {
        body = request
        reasonHeader(reason)
    }

    override suspend fun deleteChannel(id: Snowflake, reason: String?): Id<ChannelResponse<IChannel>> =
        ktor.delete("$discordApiURL/api/v6/channels/$id") {
            reasonHeader(reason)
        }

    override suspend fun triggerTyping(id: Snowflake): Unit = ktor.post("$discordApiURL/api/v6/channels/$id/typing")

    override suspend fun getMessages(id: Snowflake, query: Query): ListK<MessageResponse> =
        ktor.get("$discordApiURL/api/v6/channels/$id/messages") {
            parameters(query)
        }

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Id<MessageResponse> =
        ktor.get("$discordApiURL/api/v6/channels/$channelId/messages/$messageId")

    override suspend fun createMessage(id: Snowflake, request: MessageCreateRequest): Id<MessageResponse> =
        ktor.submitFormWithBinaryData("$discordApiURL/api/v6/channels/$id/messages", formData {
            append("payload_json", request.toJson())
        }) { method = HttpMethod.Post }

    override suspend fun crosspostMessage(channelId: Snowflake, messageId: Snowflake): Id<MessageResponse> =
        ktor.post("$discordApiURL/api/v6/channels/$channelId/messages/$messageId/crosspost")

    override suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        request: MessageEditRequest
    ): Id<MessageResponse> = ktor.patch("$discordApiURL/api/v6/channels/$channelId/messages/$messageId") {
        body = request
    }

    override suspend fun deleteMessage(channelId: Snowflake, messageId: Snowflake, reason: String?): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/messages/$messageId") {
            reasonHeader(reason)
        }

    override suspend fun bulkDeleteMessages(channelId: Snowflake, request: BulkDeleteRequest): Unit =
        ktor.post("$discordApiURL/api/v6/channels/$channelId/messages/bulk-delete") {
            body = request
        }

    override suspend fun followNewsChannel(id: Snowflake, request: FollowRequest): Id<FollowedChannelResponse> =
        ktor.post("$discordApiURL/api/v6/channels/$id/followers") {
            body = request
        }

    override suspend fun getPinnedMessages(id: Snowflake): ListK<MessageResponse> =
        ktor.get("$discordApiURL/api/v6/channels/{id}/pins")

    override suspend fun pinMessage(channelId: Snowflake, messageId: Snowflake): Unit =
        ktor.put("$discordApiURL/api/v6/channels/$channelId/pins/$messageId")

    override suspend fun unpinMessage(channelId: Snowflake, messageId: Snowflake): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/pins/$messageId")

    override suspend fun addReaction(channelId: Snowflake, messageId: Snowflake, emoji: String): Unit =
        ktor.put("$discordApiURL/api/v6/channels/$channelId/messages/$messageId/reactions/${emoji.encodeURLPath()}/@me")

    override suspend fun removeOwnReaction(channelId: Snowflake, messageId: Snowflake, emoji: String): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/messages/$messageId/reactions/${emoji.encodeURLPath()}/@me")

    override suspend fun removeReaction(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String,
        userId: Snowflake
    ): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/messages/$messageId/reactions/${emoji.encodeURLPath()}/$userId")

    override suspend fun getReactions(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: String,
        query: Query
    ): ListK<UserResponse<IUser>> =
        ktor.get("$discordApiURL/api/v6/channels/$channelId/messages/$messageId/reactions/${emoji.encodeURLPath()}") {
            parameters(query)
        }

    override suspend fun removeAllReactions(channelId: Snowflake, messageId: Snowflake): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/messages/$messageId/reactions")

    override suspend fun removeAllReactionsForEmoji(channelId: Snowflake, messageId: Snowflake, emoji: String): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/messages/$messageId/reactions/${emoji.encodeURLPath()}")

    override suspend fun getChannelInvites(id: Snowflake): ListK<InviteResponse<IInvite>> =
        ktor.get("$discordApiURL/api/v6/channels/$id/invites")

    override suspend fun createChannelInvite(
        id: Snowflake,
        request: InviteCreateRequest,
        reason: String?
    ): Id<InviteResponse<IInvite>> = ktor.post("$discordApiURL/api/v6/channels/{id}/invites") {
        body = request
        reasonHeader(reason)
    }

    override suspend fun deleteChannelPermissions(channelId: Snowflake, overwriteId: Long, reason: String?): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/permissions/$overwriteId") {
            reasonHeader(reason)
        }

    override suspend fun editChannelPermissions(
        channelId: Snowflake,
        overwriteId: Long,
        request: PermissionsEditRequest,
        reason: String?
    ) = ktor.put<Unit>("$discordApiURL/api/v6/channels/$channelId/permissions/$overwriteId") {
        body = request
        reasonHeader(reason)
    }

    override suspend fun addDMRecipient(channelId: Snowflake, userId: Snowflake, request: GroupRecipientAddRequest) =
        ktor.put<Unit>("$discordApiURL/api/v6/channels/$channelId/recipients/$userId") {
            body = request
        }

    override suspend fun removeDMRecipient(channelId: Snowflake, userId: Snowflake): Unit =
        ktor.delete("$discordApiURL/api/v6/channels/$channelId/recipients/$userId")
}