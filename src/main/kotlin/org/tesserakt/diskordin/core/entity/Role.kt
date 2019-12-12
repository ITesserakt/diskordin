package org.tesserakt.diskordin.core.entity

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.entity.builder.RoleEditBuilder
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.awt.Color

interface IRole : IGuildObject<ForIO>, IMentioned, INamed, IDeletable, IEditable<IRole, RoleEditBuilder> {
    val permissions: ValuedEnum<Permission, Long>
    val color: Color
    val isHoisted: Boolean
    val isMentionable: Boolean
    val isEveryone: Boolean
}