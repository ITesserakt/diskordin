package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.IdentifiedF

interface IWebhook : IEntity, IDeletable {
    val name: String?
    val avatar: String?
    val token: String
    val guild: Identified<IGuild>?
    val channel: Identified<IChannel>
    val user: IdentifiedF<ForId, IUser>?
}