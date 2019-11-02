@file:JvmMultifileClass
@file:Suppress("unused")

package ru.tesserakt.diskordin.core.entity

import arrow.fx.ForIO
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.builder.BuilderBase
import ru.tesserakt.diskordin.rest.RestClient

interface IEntity : IDiscordObject {
    val id: Snowflake
}

interface IDiscordObject : KoinComponent

val IDiscordObject.client: IDiscordClient
    inline get() = get()
val IDiscordObject.rest: RestClient<ForIO>
    inline get() = client.rest

interface IGuildObject : IDiscordObject {
    val guild: Identified<IGuild>
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
