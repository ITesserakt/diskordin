package org.tesserakt.diskordin.rest

import arrow.fx.coroutines.Schedule
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.rest.service.*

class NoopRestClient(schedule: Schedule<*, *>) : RestClient(schedule) {
    override val RestClient.channelService: ChannelService
        get() = throw NotImplementedError()
    override val RestClient.emojiService: EmojiService
        get() = throw NotImplementedError()
    override val RestClient.gatewayService: GatewayService
        get() = throw NotImplementedError()
    override val RestClient.guildService: GuildService
        get() = throw NotImplementedError()
    override val RestClient.inviteService: InviteService
        get() = throw NotImplementedError()
    override val RestClient.userService: UserService
        get() = throw NotImplementedError()
    override val RestClient.voiceService: VoiceService
        get() = throw NotImplementedError()
    override val RestClient.webhookService: WebhookService
        get() = throw NotImplementedError()
    override val RestClient.templateService: TemplateService
        get() = throw NotImplementedError()
}

suspend fun DiscordClientBuilder.RestBuildPhase.withoutRest() = defineRestBackend {
    NoopRestClient(schedule)
}