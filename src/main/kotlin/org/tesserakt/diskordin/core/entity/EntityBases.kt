@file:JvmMultifileClass
@file:Suppress("unused")

package org.tesserakt.diskordin.core.entity

import arrow.fx.ForIO
import arrow.fx.IO
import org.koin.core.KoinComponent
import org.koin.core.get
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.BuilderBase
import org.tesserakt.diskordin.rest.RestClient

interface IEntity : IDiscordObject {
    val id: Snowflake
}

interface IDiscordObject : KoinComponent

val IDiscordObject.client: IDiscordClient
    inline get() = get()
val IDiscordObject.rest: RestClient<ForIO>
    inline get() = client.rest

interface IGuildObject : IDiscordObject {
    val guild: IdentifiedF<ForIO, IGuild>
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
