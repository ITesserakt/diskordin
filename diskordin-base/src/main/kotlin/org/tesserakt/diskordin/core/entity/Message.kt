package org.tesserakt.diskordin.core.entity

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import org.tesserakt.diskordin.core.entity.query.ReactedUsersQuery

interface IMessage : IEntity, IDeletable, IEditable<IMessage, MessageEditBuilder> {
    val channel: DeferredIdentified<IMessageChannel>
    val author: EagerIdentified<IUser>?
    val content: String
    val isTTS: Boolean
    val attachments: List<IAttachment>?
    val isPinned: Boolean

    suspend fun pin()
    suspend fun unpin()
    suspend fun addReaction(emoji: IEmoji)
    suspend fun deleteOwnReaction(emoji: IEmoji)
    suspend fun deleteReaction(emoji: IEmoji, user: IUser)
    suspend fun deleteReaction(emoji: IEmoji, userId: Snowflake)
    suspend fun deleteAllReactions(emoji: IEmoji)
    suspend fun reactedUsers(emoji: IEmoji, builder: ReactedUsersQuery.() -> Unit): List<IUser>
    suspend fun deleteAllReactions()
    suspend fun crosspostToFollowers(): IMessage?
}