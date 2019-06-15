package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import ru.tesserakt.diskordin.impl.core.service.ChannelService
import ru.tesserakt.diskordin.util.Identified

internal class Message(raw: MessageResponse, override val kodein: Kodein = Diskordin.kodein) : IMessage {
    override suspend fun addReaction(emoji: IEmoji) = ChannelService.addReaction(channel.state, id, emoji)

    override suspend fun deleteOwnReaction(emoji: IEmoji) = ChannelService.deleteOwnReaction(channel.state, id, emoji)

    override suspend fun deleteReaction(emoji: IEmoji, user: IUser) =
        ChannelService.deleteReaction(channel.state, id, emoji, user.id)

    override suspend fun deleteReaction(emoji: IEmoji, userId: Snowflake) =
        ChannelService.deleteReaction(channel.state, id, emoji, userId)

    override suspend fun reactedUsers(emoji: IEmoji, builder: ReactedUsersQuery.() -> Unit): List<IUser> =
        ChannelService.getReactedUsers(channel.state, id, emoji, builder)

    override suspend fun deleteAllReactions() = ChannelService.deleteAllReactions(channel.state, id)

    override suspend fun delete(reason: String?) = ChannelService.deleteMessage(channel.state, id, reason)

    override val client by instance<IDiscordClient>()

    override val channel: Identified<IMessageChannel> = Identified(raw.channel_id.asSnowflake()) {
        client.coroutineScope.async {
            client.findChannel(it) as IMessageChannel
        }
    }

    override val author: Identified<IUser> = Identified(raw.author.id.asSnowflake()) {
        client.coroutineScope.async {
            User(raw.author, kodein)
        }
    }

    override val content: String = raw.content

    override val isTTS: Boolean = raw.tts

    @ExperimentalCoroutinesApi
    override val attachments: Flow<IAttachment>? = raw.attachments.map { Attachment(it) }.asFlow()

    override val isPinned: Boolean = raw.pinned

    override val id: Snowflake = raw.id.asSnowflake()

    override suspend fun edit(builder: MessageEditBuilder.() -> Unit) =
        ChannelService.editMessage(channel.state, id, builder)

    override suspend fun pin() = ChannelService.pinMessage(channel.state, id)

    override suspend fun unpin() = ChannelService.unpinMessage(channel.state, id)
}