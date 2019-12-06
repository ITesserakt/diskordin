package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import arrow.core.ListK
import arrow.fx.ForIO
import arrow.fx.IO
import kotlinx.coroutines.flow.Flow
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import org.tesserakt.diskordin.core.entity.query.ReactedUsersQuery

interface IMessage : IEntity, IDeletable, IEditable<IMessage, MessageEditBuilder> {
    val channel: IdentifiedF<ForIO, IMessageChannel>
    val author: IdentifiedF<ForId, IUser>
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