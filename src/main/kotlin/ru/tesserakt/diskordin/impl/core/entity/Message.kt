package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.monad.map
import arrow.fx.fix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.MessageResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import ru.tesserakt.diskordin.core.entity.query.query
import ru.tesserakt.diskordin.rest.call

class Message(raw: MessageResponse) : IMessage {
    override fun addReaction(emoji: IEmoji) = rest.effect {
        channelService.addReaction(channel.id, id, emoji.name)
    }.fix()

    override fun deleteOwnReaction(emoji: IEmoji) = rest.effect {
        channelService.removeOwnReaction(channel.id, id, emoji.name)
    }.fix()

    override fun deleteReaction(emoji: IEmoji, user: IUser) = rest.effect {
        channelService.removeReaction(channel.id, id, emoji.name, user.id)
    }.fix()

    override fun deleteReaction(emoji: IEmoji, userId: Snowflake) = rest.effect {
        channelService.removeReaction(channel.id, id, emoji.name, userId)
    }.fix()

    override fun reactedUsers(
        emoji: IEmoji,
        builder: ReactedUsersQuery.() -> Unit
    ): IO<ListK<IUser>> = rest.call(ListK.functor()) {
        channelService.getReactions(channel.id, id, emoji.name, builder.query())
    }.map { it.fix() }

    override fun deleteAllReactions() = rest.effect {
        channelService.removeAllReactions(channel.id, id)
    }.fix()

    override fun delete(reason: String?) = rest.effect {
        channelService.deleteMessage(channel.id, id, reason)
    }.fix()

    override val channel: Identified<IMessageChannel> = raw.channel_id identify {
        client.getChannel(it).bind() as IMessageChannel
    }

    override val author: Identified<IUser> = raw.author.id identify {
        raw.author.unwrap()
    }

    override val content: String = raw.content

    override val isTTS: Boolean = raw.tts

    override val attachments: Flow<IAttachment>? = raw.attachments.map { Attachment(it) }.asFlow()

    override val isPinned: Boolean = raw.pinned

    override val id: Snowflake = raw.id

    override fun edit(builder: MessageEditBuilder.() -> Unit) = rest.call(Id.functor()) {
        channelService.editMessage(channel.id, id, builder.build())
    }.map { it.extract() }

    override fun pin() = rest.effect {
        channelService.pinMessage(channel.id, id)
    }.fix()

    override fun unpin() = rest.effect {
        channelService.unpinMessage(channel.id, id)
    }.fix()

    override fun toString(): String {
        return "Message(channel=$channel, author=$author, content='$content', isTTS=$isTTS, attachments=$attachments, isPinned=$isPinned, id=$id)"
    }
}