package org.tesserakt.diskordin.core.entity

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified

interface IWebhook : IEntity, IDeletable {
    val name: String?
    val avatar: String?
    val token: String
    val guild: DeferredIdentified<IGuild>?
    val channel: DeferredIdentified<IChannel>
    val user: EagerIdentified<IUser>?
}