package ru.tesserakt.diskordin.core.entity

import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.entity.builder.RoleEditBuilder
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import java.awt.Color

interface IRole : IGuildObject, IMentioned, INamed, IDeletable, IEditable<IRole, RoleEditBuilder> {
    val permissions: ValuedEnum<Permission>
    val color: Color
    val isHoisted: Boolean
    val isMentionable: Boolean
}