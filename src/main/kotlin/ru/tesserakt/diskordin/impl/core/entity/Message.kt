package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import ru.tesserakt.diskordin.impl.core.service.ChannelService
import ru.tesserakt.diskordin.util.Identified

internal class Message(raw: MessageResponse) : IMessage {
    override suspend fun addReaction(emoji: IEmoji) = ChannelService.addReaction(channel.id, id, emoji)

    override suspend fun deleteOwnReaction(emoji: IEmoji) = ChannelService.deleteOwnReaction(channel.id, id, emoji)

    override suspend fun deleteReaction(emoji: IEmoji, user: IUser) =
        ChannelService.deleteReaction(channel.id, id, emoji, user.id)

    override suspend fun deleteReaction(emoji: IEmoji, userId: Snowflake) =
        ChannelService.deleteReaction(channel.id, id, emoji, userId)

    override suspend fun reactedUsers(emoji: IEmoji, builder: ReactedUsersQuery.() -> Unit): List<IUser> =
        ChannelService.getReactedUsers(channel.id, id, emoji, builder)

    override suspend fun deleteAllReactions() = ChannelService.deleteAllReactions(channel.id, id)

    override suspend fun delete(reason: String?) = ChannelService.deleteMessage(channel.id, id, reason)

    override val channel: Identified<IMessageChannel> = Identified(raw.channel_id.asSnowflake()) {
        client.findChannel(it) as IMessageChannel
    }

    override val author: Identified<IUser> = Identified(raw.author.id.asSnowflake()) {
        User(raw.author)
    }

    override val content: String = raw.content

    override val isTTS: Boolean = raw.tts

    @ExperimentalCoroutinesApi
    override val attachments: Flow<IAttachment>? = raw.attachments.map { Attachment(it) }.asFlow()

    override val isPinned: Boolean = raw.pinned

    override val id: Snowflake = raw.id.asSnowflake()

    override suspend fun edit(builder: MessageEditBuilder.() -> Unit) =
        ChannelService.editMessage(channel.id, id, builder)

    override suspend fun pin() = ChannelService.pinMessage(channel.id, id)

    override suspend fun unpin() = ChannelService.unpinMessage(channel.id, id)
}