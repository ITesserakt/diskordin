@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.rest.resource

import io.ktor.client.request.forms.MultiPartFormDataContent
import ru.tesserakt.diskordin.core.data.json.request.*
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.InviteResponse
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.impl.core.rest.Routes
import ru.tesserakt.diskordin.util.append

internal object ChannelResource {
    object General {
        suspend fun getChannel(channelId: Long) =
            Routes.getChannel(channelId)
                .newRequest()
                .resolve<ChannelResponse>()

        suspend fun modifyChannel(channelId: Long, request: ChannelEditRequest, reason: String?) =
            Routes.partialModifyChannel(channelId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<ChannelResponse>(request)


        suspend fun deleteChannel(channelId: Long, reason: String?) =
            Routes.closeChannel(channelId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<ChannelResponse>()


        suspend fun triggerTyping(channelId: Long) =
            Routes.triggerTypingIndicator(channelId)
                .newRequest()
                .resolve<Unit>()
    }

    object Messages {
        suspend fun getMessages(channelId: Long, query: List<Pair<String, *>>) =
            Routes.getMessages(channelId)
                .newRequest()
                .queryParams(query)
                .resolve<Array<MessageResponse>>()

        suspend fun getMessage(channelId: Long, messageId: Long) =
            Routes.getMessage(channelId, messageId)
                .newRequest()
                .resolve<MessageResponse>()


        suspend fun createMessage(channelId: Long, request: MultiPartFormDataContent) =
            Routes.createMessage(channelId)
                .newRequest()
                .resolve<MessageResponse>(request)


        suspend fun editMessage(channelId: Long, messageId: Long, request: MessageEditRequest) =
            Routes.editMessage(channelId, messageId)
                .newRequest()
                .resolve<MessageResponse>(request)


        suspend fun deleteMessage(channelId: Long, messageId: Long, reason: String?) =
            Routes.deleteMessage(channelId, messageId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()


        suspend fun bulkDeleteMessages(channelId: Long, request: BulkDeleteRequest) =
            Routes.bulkDeleteMessages(channelId)
                .newRequest()
                .resolve<Unit>(request)


        suspend fun getPinnedMessages(channelId: Long) =
            Routes.getPinnedMessages(channelId)
                .newRequest()
                .resolve<Array<MessageResponse>>()


        suspend fun newPinnedMessage(channelId: Long, messageId: Long) =
            Routes.addPinnedMessage(channelId, messageId)
                .newRequest()
                .resolve<Unit>()


        suspend fun deletePinnedMessage(channelId: Long, messageId: Long) =
            Routes.deletePinnedMessage(channelId, messageId)
                .newRequest()
                .resolve<Unit>()
    }

    object Reactions {

        suspend fun createReaction(channelId: Long, messageId: Long, emoji: String) =
            Routes.createReaction(channelId, messageId, emoji)
                .newRequest()
                .resolve<Unit>(Unit)


        suspend fun deleteOwnReaction(channelId: Long, messageId: Long, emoji: String) =
            Routes.deleteOwnReaction(channelId, messageId, emoji)
                .newRequest()
                .resolve<Unit>()


        suspend fun deleteReaction(channelId: Long, messageId: Long, emoji: String, userId: Long) =
            Routes.deleteReaction(channelId, messageId, emoji, userId)
                .newRequest()
                .resolve<Unit>()


        suspend fun getReactions(
            channelId: Long,
            messageId: Long,
            emoji: String,
            query: List<Pair<String, *>>
        ) =
            Routes.getReactions(channelId, messageId, emoji)
                .newRequest()
                .queryParams(query)
                .resolve<Array<UserResponse>>()


        suspend fun deleteAllReactions(channelId: Long, messageId: Long) =
            Routes.deleteAllReactions(channelId, messageId)
                .newRequest()
                .resolve<Unit>()
    }

    object Invites {

        suspend fun getChannelsInvites(channelId: Long) =
            Routes.getChannelInvites(channelId)
                .newRequest()
                .resolve<Array<InviteResponse>>()


        suspend fun createChannelInvite(channelId: Long, request: InviteCreateRequest, reason: String?) =
            Routes.createChannelInvite(channelId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<InviteResponse>(request)
    }

    object Permissions {
        suspend fun deleteChannelPermissions(channelId: Long, overwriteId: Long, reason: String?) =
            Routes.editChannelPermissions(channelId, overwriteId)
                .newRequest()
                .additionalHeaders {
                    append("X-Audit-Log-Reason", reason)
                }.resolve<Unit>()

        suspend fun editChannelPermissions(
            channelId: Long,
            overwriteId: Long,
            request: PermissionsEditRequest,
            reason: String?
        ) = Routes.editChannelPermissions(channelId, overwriteId)
            .newRequest()
            .additionalHeaders {
                append("X-Audit-Log-Reason", reason)
            }.resolve<Unit>(request)
    }

    object Recipients {

        suspend fun addGroupRecipient(channelId: Long, userId: Long, request: GroupRecipientAddRequest) =
            Routes.addGroupDMRecipient(channelId, userId)
                .newRequest()
                .resolve<Unit>(request)


        suspend fun deleteGroupRecipient(channelId: Long, userId: Long) =
            Routes.deleteGroupDMRecipient(channelId, userId)
                .newRequest()
                .resolve<Unit>()
    }
}