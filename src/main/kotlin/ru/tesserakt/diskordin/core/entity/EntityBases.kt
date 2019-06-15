@file:JvmMultifileClass
@file:Suppress("unused")

package ru.tesserakt.diskordin.core.entity

import org.kodein.di.KodeinAware
import ru.tesserakt.diskordin.core.client.IDiscordClient

interface IEntity : IDiscordObject {

    val id: Snowflake
}

interface IDiscordObject : KodeinAware {
    val client: IDiscordClient
}

interface IGuildObject {
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
