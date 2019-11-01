@file:JvmMultifileClass
@file:Suppress("unused")

package ru.tesserakt.diskordin.core.entity

import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.builder.BuilderBase

interface IEntity : IDiscordObject {
    val id: Snowflake
}

interface IDiscordObject : KoinComponent

val IDiscordObject.client: IDiscordClient
    get() = get()
val IDiscordObject.userService
    get() = client.rest.userService
val IDiscordObject.channelService
    get() = client.rest.channelService
val IDiscordObject.emojiService
    get() = client.rest.emojiService
val IDiscordObject.gatewayService
    get() = client.rest.gatewayService
val IDiscordObject.guildService
    get() = client.rest.guildService
val IDiscordObject.inviteService
    get() = client.rest.inviteService
val IDiscordObject.voiceService
    get() = client.rest.voiceService
val IDiscordObject.webhookService
    get() = client.rest.webhookService

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
