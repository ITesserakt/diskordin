package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.IWebhook

interface IFollowedChannel : IDiscordObject {
    val channel: DeferredIdentified<ITextChannel>
    val webhook: DeferredIdentified<IWebhook>
}