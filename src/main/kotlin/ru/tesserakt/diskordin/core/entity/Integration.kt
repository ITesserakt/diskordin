package ru.tesserakt.diskordin.core.entity

import ru.tesserakt.diskordin.core.entity.builder.IntegrationEditBuilder

interface IIntegration : IEntity, INamed, IEditable<IIntegration, IntegrationEditBuilder>, IDeletable, IGuildObject {
    val type: String
    val enabled: Boolean
    val syncing: Boolean
    val role: Identified<IRole>
    val expireBehavior: Int
    val expireGracePeriod: Int
    val user: Identified<IUser>
    val account: IAccount
    val syncedAt: Instant

    suspend fun sync()

    interface IAccount : IEntity, INamed
}
