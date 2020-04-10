@file:JvmMultifileClass
@file:Suppress("unused")

package org.tesserakt.diskordin.core.entity

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.fix
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.EntityCache
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
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
    get() = DiscordClient.client.get().fix().unsafeRunSync()!!

val IDiscordObject.rest: RestClient<ForIO>
    inline get() = client.rest

internal val IDiscordObject.cache: EntityCache
    get() = (client as DiscordClient<*>).context.cache

interface IGuildObject<F> : IDiscordObject {
    val guild: IdentifiedF<F, IGuild>
}

interface IMentioned : IEntity {
    val mention: String
}

interface INamed : IDiscordObject {
    val name: String
}

interface IDeletable : IEntity {
    fun delete(reason: String? = null): IO<Unit>
}

interface IEditable<E : IEntity, B : BuilderBase<*>> : IEntity {
    fun edit(builder: B.() -> Unit): IO<E>
}

interface StaticMention<out P : IMentioned, in S : StaticMention<P, S>> {
    val mention: Regex
}
