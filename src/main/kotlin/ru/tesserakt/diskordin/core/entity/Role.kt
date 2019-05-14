package ru.tesserakt.diskordin.core.entity

import ru.tesserakt.diskordin.core.data.Permission
import java.awt.Color
import java.util.*

interface IRole : IGuildObject, IMentioned, INamed, IDeletable {
    @ExperimentalUnsignedTypes
    val permissions: EnumSet<Permission>
    val color: Color
    val isHoisted: Boolean
    val isMentionable: Boolean
}