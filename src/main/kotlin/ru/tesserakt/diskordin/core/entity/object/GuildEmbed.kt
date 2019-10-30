package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IGuildChannel

interface IGuildEmbed : IDiscordObject {
    val enabled: Boolean
    val channel: Identified<IGuildChannel>?
}