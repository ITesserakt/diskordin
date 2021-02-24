package org.tesserakt.diskordin.core.entity

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.entity.builder.IntegrationEditBuilder

interface IIntegration : IEntity, INamed, IGuildObject, IDeletable,
    IEditable<IIntegration, IntegrationEditBuilder> {
    val type: String
    val enabled: Boolean
    val syncing: Boolean
    val role: DeferredIdentified<IRole>
    val expireBehavior: Int
    val expireGracePeriod: Int
    val user: EagerIdentified<IUser>
    val account: IAccount
    val syncedAt: Instant

    suspend fun sync()

    suspend fun edit(expireBehavior: Int, expireGracePeriod: Int, enableEmoticons: Boolean) = edit {
        this.enableEmoticons = enableEmoticons
        this.expireBehavior = expireBehavior
        this.expireGracePeriod = expireGracePeriod
    }

    interface IAccount : IEntity, INamed
}
