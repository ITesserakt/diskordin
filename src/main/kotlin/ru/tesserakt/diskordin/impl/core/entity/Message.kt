package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.fix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import ru.tesserakt.diskordin.core.entity.query.query
import ru.tesserakt.diskordin.rest.call

class Message(raw: MessageResponse) : IMessage {
    override suspend fun addReaction(emoji: IEmoji) = rest.effect {
        channelService.addReaction(channel.id, id, emoji.name)
    }.fix().suspended()

    override suspend fun deleteOwnReaction(emoji: IEmoji) = rest.effect {
        channelService.removeOwnReaction(channel.id, id, emoji.name)
    }.fix().suspended()

    override suspend fun deleteReaction(emoji: IEmoji, user: IUser) = rest.effect {
        channelService.removeReaction(channel.id, id, emoji.name, user.id)
    }.fix().suspended()

    override suspend fun deleteReaction(emoji: IEmoji, userId: Snowflake) = rest.effect {
        channelService.removeReaction(channel.id, id, emoji.name, userId)
    }.fix().suspended()

    override suspend fun reactedUsers(
        emoji: IEmoji,
        builder: ReactedUsersQuery.() -> Unit
    ): List<IUser> = rest.call(ListK.functor()) {
        channelService.getReactions(channel.id, id, emoji.name, builder.query())
    }.fix().suspended().fix()

    override suspend fun deleteAllReactions() = rest.effect {
        channelService.removeAllReactions(channel.id, id)
    }.fix().suspended()

    override suspend fun delete(reason: String?) = rest.effect {
        channelService.deleteMessage(channel.id, id, reason)
    }.fix().suspended()

    override val channel: Identified<IMessageChannel> = Identified(raw.channel_id) {
        client.getChannel(it) as IMessageChannel
    }

    override val author: Identified<IUser> = Identified(raw.author.id) {
        User(raw.author)
    }

    override val content: String = raw.content

    override val isTTS: Boolean = raw.tts

    override val attachments: Flow<IAttachment>? = raw.attachments.map { Attachment(it) }.asFlow()

    override val isPinned: Boolean = raw.pinned

    override val id: Snowflake = raw.id

    override suspend fun edit(builder: MessageEditBuilder.() -> Unit) = rest.call(Id.functor()) {
        channelService.editMessage(channel.id, id, builder.build())
    }.fix().suspended().extract()

    override suspend fun pin() = rest.effect {
        channelService.pinMessage(channel.id, id)
    }.fix().suspended()

    override suspend fun unpin() = rest.effect {
        channelService.unpinMessage(channel.id, id)
    }.fix().suspended()

    override fun toString(): String {
        return "Message(channel=$channel, author=$author, content='$content', isTTS=$isTTS, attachments=$attachments, isPinned=$isPinned, id=$id)"
    }
}