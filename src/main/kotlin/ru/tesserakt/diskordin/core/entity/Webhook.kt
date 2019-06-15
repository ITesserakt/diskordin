package ru.tesserakt.diskordin.core.entity

interface IWebhook : IEntity, IDeletable {
    val name: String?
    val avatar: String?
    val token: String
    val guild: Identified<IGuild>?
    val channel: Identified<IChannel>
    val user: Identified<IUser>?
}