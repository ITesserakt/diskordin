package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.util.Identified

interface IMessage : IEntity {

    val channel: Identified<IMessageChannel>

    val author: Identified<IUser>
    val content: String
    val isTTS: Boolean
    @FlowPreview
    val attachments: Flow<IAttachment>?
    val isPinned: Boolean
}