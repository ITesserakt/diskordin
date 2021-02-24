package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuildChannel

interface IGuildEmbed : IDiscordObject {
    val enabled: Boolean
    val channel: DeferredIdentified<IGuildChannel>?
}