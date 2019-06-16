package ru.tesserakt.diskordin.core.entity

import ru.tesserakt.diskordin.util.Identified

interface IWebhook : IEntity, IDeletable {
    val name: String?
    val avatar: String?
    val token: String
    val guild: Identified<IGuild>?
    val channel: Identified<IChannel>
    val user: Identified<IUser>?
}