package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import arrow.core.ListK
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.MessageEditBuilder
import org.tesserakt.diskordin.core.entity.query.ReactedUsersQuery

interface IMessage : IEntity, IDeletable, IEditable<IMessage, MessageEditBuilder> {
    val channel: IdentifiedF<ForIO, IMessageChannel>
    val author: IdentifiedF<ForId, IUser>?
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
    suspend fun reactedUsers(emoji: IEmoji, builder: ReactedUsersQuery.() -> Unit): ListK<IUser>
    suspend fun deleteAllReactions()
    suspend fun crosspostToFollowers(): IMessage?
}