package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.IdentifiedF

interface IWebhook : IEntity, IDeletable {
    val name: String?
    val avatar: String?
    val token: String
    val guild: IdentifiedF<ForIO, IGuild>?
    val channel: IdentifiedF<ForIO, IChannel>
    val user: IdentifiedF<ForId, IUser>?
}