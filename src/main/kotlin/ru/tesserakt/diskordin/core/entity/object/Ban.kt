package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IUser

interface IBan : IDiscordObject {
    val reason: String?
    val user: Identified<IUser>
}