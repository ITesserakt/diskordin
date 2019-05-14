package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.util.Identified

interface IEmoji : INamed

interface ICustomEmoji : IEmoji, IDeletable, IMentioned {
    @FlowPreview
    val roles: Flow<IRole>
    val creator: Identified<IUser>
    val requireColons: Boolean
    val isManaged: Boolean
    val isAnimated: Boolean
    val guild: Identified<IGuild>
}