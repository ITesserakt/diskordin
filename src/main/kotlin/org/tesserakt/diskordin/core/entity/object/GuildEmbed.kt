package org.tesserakt.diskordin.core.entity.`object`

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuildChannel

interface IGuildEmbed : IDiscordObject {
    val enabled: Boolean
    val channel: IdentifiedF<ForIO, IGuildChannel>?
}