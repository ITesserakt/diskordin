package ru.tesserakt.diskordin.core.entity

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

    suspend fun pin()
    suspend fun unpin()
    suspend fun addReaction(emoji: IEmoji)
    suspend fun deleteOwnReaction(emoji: IEmoji)
    suspend fun deleteReaction(emoji: IEmoji, user: IUser)
    suspend fun deleteReaction(emoji: IEmoji, userId: Snowflake)
    suspend fun reactedUsers(emoji: IEmoji, builder: ReactedUsersQuery.() -> Unit): List<IUser>
    suspend fun deleteAllReactions()
}