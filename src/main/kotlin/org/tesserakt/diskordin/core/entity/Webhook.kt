package org.tesserakt.diskordin.core.entity

import org.tesserakt.diskordin.core.data.Identified

interface IWebhook : IEntity, IDeletable {
    val name: String?
    val avatar: String?
    val token: String
    val guild: Identified<IGuild>?
    val channel: Identified<IChannel>
    val user: Identified<IUser>?
}