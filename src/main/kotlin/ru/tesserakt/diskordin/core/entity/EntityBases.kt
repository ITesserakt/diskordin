@file:JvmMultifileClass
@file:Suppress("unused")

package ru.tesserakt.diskordin.core.entity

import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.builder.BuilderBase
import ru.tesserakt.diskordin.rest.service.*
import ru.tesserakt.diskordin.util.Identified

interface IEntity : IDiscordObject {
    val id: Snowflake
}

interface IDiscordObject : KoinComponent

val IDiscordObject.client: IDiscordClient
    get() = get()
val IDiscordObject.userService
    get() = get<UserService>()
val IDiscordObject.channelService
    get() = get<ChannelService>()
val IDiscordObject.emojiService
    get() = get<EmojiService>()
val IDiscordObject.gatewayService
    get() = get<GatewayService>()
val IDiscordObject.guildService
    get() = get<GuildService>()
val IDiscordObject.inviteService
    get() = get<InviteService>()
val IDiscordObject.voiceService
    get() = get<VoiceService>()
val IDiscordObject.webhookService
    get() = get<WebhookService>()

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
