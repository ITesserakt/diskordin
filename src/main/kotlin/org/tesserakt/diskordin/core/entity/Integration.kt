package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import arrow.fx.ForIO
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.entity.builder.IntegrationEditBuilder

interface IIntegration : IEntity, INamed, IGuildObject, IDeletable,
    IEditable<IIntegration, IntegrationEditBuilder> {
    val type: String
    val enabled: Boolean
    val syncing: Boolean
    val role: IdentifiedF<ForIO, IRole>
    val expireBehavior: Int
    val expireGracePeriod: Int
    val user: IdentifiedF<ForId, IUser>
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
