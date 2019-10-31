package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import ru.tesserakt.diskordin.core.entity.query.query

class Message(raw: MessageResponse) : IMessage {
    override suspend fun addReaction(emoji: IEmoji) = channelService.addReaction(channel.id, id, emoji.name)

    override suspend fun deleteOwnReaction(emoji: IEmoji) = channelService.removeOwnReaction(channel.id, id, emoji.name)

    override suspend fun deleteReaction(emoji: IEmoji, user: IUser) =
        channelService.removeReaction(channel.id, id, emoji.name, user.id)

    override suspend fun deleteReaction(emoji: IEmoji, userId: Snowflake) =
        channelService.removeReaction(channel.id, id, emoji.name, userId)

    override suspend fun reactedUsers(emoji: IEmoji, builder: ReactedUsersQuery.() -> Unit): List<IUser> =
        channelService.getReactions(channel.id, id, emoji.name, builder.query()).map { it.unwrap() }

    override suspend fun deleteAllReactions() = channelService.removeAllReactions(channel.id, id)

    override suspend fun delete(reason: String?) = channelService.deleteMessage(channel.id, id, reason)

    override val channel: Identified<IMessageChannel> =
        Identified(raw.channel_id) {
            client.findChannel(it) as IMessageChannel
        }

    override val author: Identified<IUser> =
        Identified(raw.author.id) {
            User(raw.author)
        }

    override val content: String = raw.content

    override val isTTS: Boolean = raw.tts

    override val attachments: Flow<IAttachment>? = raw.attachments.map { Attachment(it) }.asFlow()

    override val isPinned: Boolean = raw.pinned

    override val id: Snowflake = raw.id

    override suspend fun edit(builder: MessageEditBuilder.() -> Unit) =
        channelService.editMessage(channel.id, id, builder.build()).unwrap()

    override suspend fun pin() = channelService.pinMessage(channel.id, id)

    override suspend fun unpin() = channelService.unpinMessage(channel.id, id)
}