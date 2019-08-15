@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IEmoji
import ru.tesserakt.diskordin.core.entity.IGuildChannel
import ru.tesserakt.diskordin.core.entity.IMessage
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.MessagesQuery
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import ru.tesserakt.diskordin.core.entity.query.query
import ru.tesserakt.diskordin.impl.core.entity.Message
import ru.tesserakt.diskordin.impl.core.entity.User
import ru.tesserakt.diskordin.rest.resource.ChannelResource
import ru.tesserakt.diskordin.util.enums.not

internal object ChannelService {
    //private val channelCache = genericCache<IChannel>()
    //private val messageCache = genericCache<IMessage>()

    suspend fun <C : IChannel> getChannel(id: Snowflake): C? = runCatching {
        ChannelResource.General.getChannel(id.asLong())
    }.mapCatching { IChannel.typed<C>(it) }.getOrNull()

    suspend inline fun <C : IGuildChannel, reified B : GuildChannelEditBuilder<C>> editChannel(
        id: Snowflake,
        noinline builder: B.() -> Unit
    ): C = ChannelResource.General
        .modifyChannel(id.asLong(), builder.build(), builder.extractReason())
        .let { IChannel.typed(it) }

    suspend inline fun <reified C : IChannel> deleteChannel(id: Snowflake, reason: String?): C =
        ChannelResource.General
            .deleteChannel(id.asLong(), reason)
            .let { IChannel.typed(it) }

    suspend fun triggerTyping(id: Snowflake) = ChannelResource.General.triggerTyping(id.asLong())

    suspend fun getMessages(
        id: Snowflake,
        query: MessagesQuery.() -> Unit
    ) = ChannelResource.Messages
        .getMessages(id.asLong(), query.query())
        .map { Message(it) }

    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): IMessage = ChannelResource.Messages
        .getMessage(channelId.asLong(), messageId.asLong())
        .let { Message(it) }

    suspend fun createMessage(
        id: Snowflake,
        builder: MessageCreateBuilder.() -> Unit
    ): IMessage = Message(ChannelResource.Messages.createMessage(id.asLong(), builder.build()))

    suspend fun editMessage(
        channelId: Snowflake,
        messageId: Snowflake,
        builder: MessageEditBuilder.() -> Unit
    ): IMessage =
        ChannelResource.Messages.editMessage(
            channelId.asLong(),
            messageId.asLong(),
            builder.build()
        ).let { Message(it) }

    suspend fun deleteMessage(channelId: Snowflake, messageId: Snowflake, reason: String?) =
        ChannelResource.Messages.deleteMessage(channelId.asLong(), messageId.asLong(), reason)

    suspend fun bulkDeleteMessages(channelId: Snowflake, builder: BulkDeleteBuilder.() -> Unit) =
        ChannelResource.Messages.bulkDeleteMessages(channelId.asLong(), builder.build())

    suspend fun getPinnedMessages(channelId: Snowflake) =
        ChannelResource.Messages.getPinnedMessages(channelId.asLong())
            .map { Message(it) }

    suspend fun pinMessage(channelId: Snowflake, messageId: Snowflake) =
        ChannelResource.Messages.newPinnedMessage(channelId.asLong(), messageId.asLong())

    suspend fun unpinMessage(channelId: Snowflake, messageId: Snowflake) =
        ChannelResource.Messages.deletePinnedMessage(channelId.asLong(), messageId.asLong())

    suspend fun addReaction(channelId: Snowflake, messageId: Snowflake, emoji: IEmoji) =
        ChannelResource.Reactions.createReaction(channelId.asLong(), messageId.asLong(), emoji.name)

    suspend fun deleteOwnReaction(channelId: Snowflake, messageId: Snowflake, emoji: IEmoji) =
        ChannelResource.Reactions.deleteOwnReaction(channelId.asLong(), messageId.asLong(), emoji.name)

    suspend fun deleteReaction(channelId: Snowflake, messageId: Snowflake, emoji: IEmoji, userId: Snowflake) =
        ChannelResource.Reactions.deleteReaction(channelId.asLong(), messageId.asLong(), emoji.name, userId.asLong())

    suspend fun getReactedUsers(
        channelId: Snowflake,
        messageId: Snowflake,
        emoji: IEmoji,
        builder: ReactedUsersQuery.() -> Unit
    ) = ChannelResource.Reactions
        .getReactions(channelId.asLong(), messageId.asLong(), emoji.name, builder.query())
        .map { User(it) }

    suspend fun deleteAllReactions(channelId: Snowflake, messageId: Snowflake) =
        ChannelResource.Reactions.deleteAllReactions(channelId.asLong(), messageId.asLong())

    suspend inline fun <reified I : IInvite> getChannelInvites(channelId: Snowflake) =
        ChannelResource.Invites
            .getChannelsInvites(channelId.asLong())
            .map { IInvite.typed<I>(it) }

    suspend inline fun <reified I : IInvite> newChannelInvite(
        channelId: Snowflake,
        builder: InviteCreateBuilder.() -> Unit
    ): I = ChannelResource.Invites
        .createChannelInvite(channelId.asLong(), builder.build(), builder.extractReason())
        .let { IInvite.typed(it) }

    suspend fun removeChannelPermissions(channelId: Snowflake, overwrite: IPermissionOverwrite, reason: String?) =
        ChannelResource.Permissions.deleteChannelPermissions(
            channelId.asLong(),
            (overwrite.allowed and !overwrite.denied).code,
            reason
        )

    suspend fun editChannelPermissions(
        channelId: Snowflake,
        overwrite: IPermissionOverwrite,
        builder: PermissionEditBuilder.() -> Unit
    ) = ChannelResource.Permissions.editChannelPermissions(
        channelId.asLong(),
        (overwrite.allowed and !overwrite.denied).code,
        builder.build(),
        builder.extractReason()
    )

    suspend fun addGroupRecipients( //TODO: add GroupChannel
        channelId: Snowflake,
        userId: Snowflake,
        builder: GroupRecipientAddBuilder.() -> Unit
    ) = ChannelResource.Recipients
        .addGroupRecipient(channelId.asLong(), userId.asLong(), builder.build())

    suspend fun kickGroupRecipient(channelId: Snowflake, userId: Snowflake) = //TODO: add GroupChannel
        ChannelResource.Recipients.deleteGroupRecipient(channelId.asLong(), userId.asLong())
}
