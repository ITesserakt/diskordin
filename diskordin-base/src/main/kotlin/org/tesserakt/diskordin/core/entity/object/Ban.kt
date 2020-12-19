package org.tesserakt.diskordin.core.entity.`object`

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IUser

interface IBan : IDiscordObject {
    val reason: String?
    val user: IdentifiedF<ForId, IUser>
}