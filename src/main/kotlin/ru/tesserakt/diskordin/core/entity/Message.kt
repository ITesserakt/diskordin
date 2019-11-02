package ru.tesserakt.diskordin.core.entity

import arrow.core.ListK
import arrow.fx.IO
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import ru.tesserakt.diskordin.core.entity.query.ReactedUsersQuery

interface IMessage : IEntity, IDeletable, IEditable<IMessage, MessageEditBuilder> {
    val channel: Identified<IMessageChannel>
    val author: Identified<IUser>
    val content: String
    val isTTS: Boolean
    val attachments: Flow<IAttachment>?
    val isPinned: Boolean

    fun pin(): IO<Unit>
    fun unpin(): IO<Unit>
    fun addReaction(emoji: IEmoji): IO<Unit>
    fun deleteOwnReaction(emoji: IEmoji): IO<Unit>
    fun deleteReaction(emoji: IEmoji, user: IUser): IO<Unit>
    fun deleteReaction(emoji: IEmoji, userId: Snowflake): IO<Unit>
    fun reactedUsers(emoji: IEmoji, builder: ReactedUsersQuery.() -> Unit): IO<ListK<IUser>>
    fun deleteAllReactions(): IO<Unit>
}