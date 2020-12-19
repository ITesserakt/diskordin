package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.IdentifiedIO
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.IWebhook

interface IFollowedChannel : IDiscordObject {
    val channel: IdentifiedIO<ITextChannel>
    val webhook: IdentifiedIO<IWebhook>
}