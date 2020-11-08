package org.tesserakt.diskordin.impl.core.entity

import arrow.core.ForId
import arrow.core.ListK
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.*
import org.tesserakt.diskordin.core.data.json.response.MessageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.core.entity.query.ReactedUsersQuery
import org.tesserakt.diskordin.core.entity.query.query
import org.tesserakt.diskordin.rest.call

internal class Message(raw: MessageResponse) : IMessage {
    override suspend fun addReaction(emoji: IEmoji) = rest.effect {
        channelService.addReaction(channel.id, id, emoji.name)
    }

    override suspend fun deleteOwnReaction(emoji: IEmoji) = rest.effect {
        channelService.removeOwnReaction(channel.id, id, emoji.name)
    }

    override suspend fun deleteReaction(emoji: IEmoji, user: IUser) = rest.effect {
        channelService.removeReaction(channel.id, id, emoji.name, user.id)
    }

    override suspend fun deleteReaction(emoji: IEmoji, userId: Snowflake) = rest.effect {
        channelService.removeReaction(channel.id, id, emoji.name, userId)
    }

    override suspend fun reactedUsers(
        emoji: IEmoji,
        builder: ReactedUsersQuery.() -> Unit
    ) = rest.call(ListK.functor()) {
        channelService.getReactions(channel.id, id, emoji.name, builder.query(::ReactedUsersQuery))
    }.fix()

    override suspend fun crosspostToFollowers(): IMessage? =
        if (channel() is IAnnouncementChannel) (channel() as IAnnouncementChannel).crosspostToFollowers(id)
        else null

    override suspend fun deleteAllReactions() = rest.effect {
        channelService.removeAllReactions(channel.id, id)
    }

    override suspend fun deleteAllReactions(emoji: IEmoji) = rest.effect {
        channelService.removeAllReactionsForEmoji(channel.id, id, emoji.name)
    }

    override suspend fun delete(reason: String?) = rest.effect {
        channelService.deleteMessage(channel.id, id, reason)
    }

    override val channel: IdentifiedF<ForIO, IMessageChannel> = raw.channel_id.identify<IMessageChannel> {
        client.getChannel(it) as IMessageChannel
    }

    override val author: IdentifiedF<ForId, IUser>? = raw.author?.id?.identifyId {
        raw.author.unwrap()
    }

    override val content: String = raw.content

    override val isTTS: Boolean = raw.tts

    override val attachments: List<IAttachment>? = raw.attachments?.map { it.unwrap() }

    override val isPinned: Boolean = raw.pinned

    override val id: Snowflake = raw.id

    override suspend fun edit(builder: MessageEditBuilder.() -> Unit) = rest.call {
        channelService.editMessage(channel.id, id, builder.build(::MessageEditBuilder))
    }

    override suspend fun pin() = rest.effect {
        channelService.pinMessage(channel.id, id)
    }

    override suspend fun unpin() = rest.effect {
        channelService.unpinMessage(channel.id, id)
    }

    override fun toString(): String {
        return StringBuilder("Message(")
            .appendLine("channel=$channel, ")
            .appendLine("author=$author, ")
            .appendLine("content='$content', ")
            .appendLine("isTTS=$isTTS, ")
            .appendLine("attachments=$attachments, ")
            .appendLine("isPinned=$isPinned, ")
            .appendLine("id=$id")
            .appendLine(")")
            .toString()
    }
}