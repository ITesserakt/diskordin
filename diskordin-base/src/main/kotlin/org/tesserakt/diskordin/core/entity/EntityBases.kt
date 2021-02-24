@file:JvmMultifileClass
@file:Suppress("unused")

package org.tesserakt.diskordin.core.entity

import arrow.core.getOrHandle
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.cache.CacheProcessor
import org.tesserakt.diskordin.core.cache.CacheSnapshot
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.DiscordResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.entity.builder.BuilderBase
import org.tesserakt.diskordin.impl.core.client.DiscordClient
import org.tesserakt.diskordin.rest.RestClient

interface IEntity : IDiscordObject {
    val id: Snowflake
}

interface IDiscordObject

/**
 * **Note:**
 * We cannot get client without starting it, excluding reflection and explicit implementation.
 * On its start [DiscordClient.client] becomes not null
 */
val IDiscordObject.client: IDiscordClient
    get() = runBlocking { DiscordClient.getInitialized() }.getOrHandle { error(it) }

val IDiscordObject.rest: RestClient
    inline get() = client.rest

val IDiscordObject.cacheSnapshot: CacheSnapshot get() = client.context[CacheProcessor].state.value

interface IGuildObject : IDiscordObject {
    val guild: DeferredIdentified<IGuild>
}

interface IMentioned : IEntity {
    val mention: String
}

interface INamed : IDiscordObject {
    val name: String
}

interface IDeletable : IEntity {
    suspend fun delete(reason: String? = null)
}

interface IEditable<E : IEntity, B : BuilderBase<*>> : IEntity {
    suspend fun edit(builder: B.() -> Unit): E
}

interface IPreviewed<E : IEntity> : IDiscordObject {
    suspend fun extend(): E
}

interface ICacheable<O : IDiscordObject, C : UnwrapContext, R : DiscordResponse<O, C>> {
    fun copy(changes: (R) -> R): O

    val raw: R
}

interface StaticMention<out P : IMentioned, in S : StaticMention<P, S>> {
    val mention: Regex
}
