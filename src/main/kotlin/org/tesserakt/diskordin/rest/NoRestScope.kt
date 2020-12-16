package org.tesserakt.diskordin.rest

import arrow.fx.coroutines.Schedule
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.GatewayContext
import org.tesserakt.diskordin.core.client.ShardContext
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.impl.core.client.BackendProvider
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import org.tesserakt.diskordin.rest.service.*
import kotlin.coroutines.EmptyCoroutineContext

class NoRestScope : DiscordClientBuilderScope() {
    override val restClient: RestClient = object : RestClient(Schedule.once<Any?>()) {
        override val RestClient.channelService: ChannelService get() = throw NotImplementedError()
        override val RestClient.emojiService: EmojiService get() = throw NotImplementedError()
        override val RestClient.gatewayService: GatewayService get() = throw NotImplementedError()
        override val RestClient.guildService: GuildService get() = throw NotImplementedError()
        override val RestClient.inviteService: InviteService get() = throw NotImplementedError()
        override val RestClient.userService: UserService get() = throw NotImplementedError()
        override val RestClient.voiceService: VoiceService get() = throw NotImplementedError()
        override val RestClient.webhookService: WebhookService get() = throw NotImplementedError()
        override val RestClient.templateService: TemplateService get() = throw NotImplementedError()
    }

    override val gatewayFactory: Gateway.Factory = object : Gateway.Factory() {
        override fun BootstrapContext.createGateway(): Gateway =
            Gateway(this[GatewayContext].interceptors, EmptyCoroutineContext, this[ShardContext], emptyList())
    }

    override fun create(): DiscordClientSettings = DiscordClientSettings(
        System.getenv("DISKORDIN_TOKEN") ?: token ?: error(DiscordClientBuilder.NoTokenProvided),
        cache,
        gatewaySettings,
        restSchedule,
        restClient,
        gatewayFactory,
        extensions
    )
}

@DiscordClientBuilderScope.InternalTestAPI
val WithoutRest = BackendProvider(::NoRestScope)