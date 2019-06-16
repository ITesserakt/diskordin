package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IGuildChannel
import ru.tesserakt.diskordin.util.Identified

interface IGuildEmbed : IDiscordObject {
    val enabled: Boolean
    val channel: Identified<IGuildChannel>?
}