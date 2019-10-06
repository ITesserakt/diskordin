@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.core.data.json.request.*
import ru.tesserakt.diskordin.rest.Routes

internal object ChannelResource {
    object General {
        suspend fun getChannel(channelId: Long) =
            Routes.getChannel(channelId)
                .newRequest()
                .resolve()

        suspend fun modifyChannel(channelId: Long, request: ChannelEditRequest, reason: String?) =
            Routes.partialModifyChannel(channelId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)


        suspend fun deleteChannel(channelId: Long, reason: String?) =
            Routes.closeChannel(channelId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()


        suspend fun triggerTyping(channelId: Long) =
            Routes.triggerTypingIndicator(channelId)
                .newRequest()
                .resolve()
    }

    object Messages {
        suspend fun getMessages(channelId: Long, query: List<Pair<String, *>>) =
            Routes.getMessages(channelId)
                .newRequest()
                .queryParams(query)
                .resolve()

        suspend fun getMessage(channelId: Long, messageId: Long) =
            Routes.getMessage(channelId, messageId)
                .newRequest()
                .resolve()


        suspend fun createMessage(channelId: Long, request: MessageCreateRequest) =
            Routes.createMessage(channelId)
                .newRequest()
                .resolve(request)


        suspend fun editMessage(channelId: Long, messageId: Long, request: MessageEditRequest) =
            Routes.editMessage(channelId, messageId)
                .newRequest()
                .resolve(request)


        suspend fun deleteMessage(channelId: Long, messageId: Long, reason: String?) =
            Routes.deleteMessage(channelId, messageId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()


        suspend fun bulkDeleteMessages(channelId: Long, request: BulkDeleteRequest) =
            Routes.bulkDeleteMessages(channelId)
                .newRequest()
                .resolve(request)


        suspend fun getPinnedMessages(channelId: Long) =
            Routes.getPinnedMessages(channelId)
                .newRequest()
                .resolve()


        suspend fun newPinnedMessage(channelId: Long, messageId: Long) =
            Routes.addPinnedMessage(channelId, messageId)
                .newRequest()
                .resolve()


        suspend fun deletePinnedMessage(channelId: Long, messageId: Long) =
            Routes.deletePinnedMessage(channelId, messageId)
                .newRequest()
                .resolve()
    }

    object Reactions {

        suspend fun createReaction(channelId: Long, messageId: Long, emoji: String) =
            Routes.createReaction(channelId, messageId, emoji)
                .newRequest()
                .resolve(Unit)


        suspend fun deleteOwnReaction(channelId: Long, messageId: Long, emoji: String) =
            Routes.deleteOwnReaction(channelId, messageId, emoji)
                .newRequest()
                .resolve()


        suspend fun deleteReaction(channelId: Long, messageId: Long, emoji: String, userId: Long) =
            Routes.deleteReaction(channelId, messageId, emoji, userId)
                .newRequest()
                .resolve()


        suspend fun getReactions(
            channelId: Long,
            messageId: Long,
            emoji: String,
            query: List<Pair<String, *>>
        ) =
            Routes.getReactions(channelId, messageId, emoji)
                .newRequest()
                .queryParams(query)
                .resolve()


        suspend fun deleteAllReactions(channelId: Long, messageId: Long) =
            Routes.deleteAllReactions(channelId, messageId)
                .newRequest()
                .resolve()
    }

    object Invites {

        suspend fun getChannelsInvites(channelId: Long) =
            Routes.getChannelInvites(channelId)
                .newRequest()
                .resolve()


        suspend fun createChannelInvite(channelId: Long, request: InviteCreateRequest, reason: String?) =
            Routes.createChannelInvite(channelId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve(request)
    }

    object Permissions {
        suspend fun deleteChannelPermissions(channelId: Long, overwriteId: Long, reason: String?) =
            Routes.editChannelPermissions(channelId, overwriteId)
                .newRequest()
                .additionalHeaders("X-Audit-Log-Reason" to reason)
                .resolve()

        suspend fun editChannelPermissions(
            channelId: Long,
            overwriteId: Long,
            request: PermissionsEditRequest,
            reason: String?
        ) = Routes.editChannelPermissions(channelId, overwriteId)
            .newRequest()
            .additionalHeaders("X-Audit-Log-Reason" to reason)
            .resolve(request)
    }

    object Recipients {

        suspend fun addGroupRecipient(channelId: Long, userId: Long, request: GroupRecipientAddRequest) =
            Routes.addGroupDMRecipient(channelId, userId)
                .newRequest()
                .resolve(request)


        suspend fun deleteGroupRecipient(channelId: Long, userId: Long) =
            Routes.deleteGroupDMRecipient(channelId, userId)
                .newRequest()
                .resolve()
    }
}