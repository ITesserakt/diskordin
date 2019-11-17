package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IUser

interface IBan : IDiscordObject {
    val reason: String?
    val user: Identified<IUser>
}