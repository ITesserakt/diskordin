package org.tesserakt.diskordin.rest

import arrow.fx.coroutines.Schedule
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.rest.service.*

class NoopRestClient(schedule: Schedule<*, *>) : RestClient(schedule) {
    override val RestClient.channelService: ChannelService
        get() = TODO("Not yet implemented")
    override val RestClient.emojiService: EmojiService
        get() = TODO("Not yet implemented")
    override val RestClient.gatewayService: GatewayService
        get() = TODO("Not yet implemented")
    override val RestClient.guildService: GuildService
        get() = TODO("Not yet implemented")
    override val RestClient.inviteService: InviteService
        get() = TODO("Not yet implemented")
    override val RestClient.userService: UserService
        get() = TODO("Not yet implemented")
    override val RestClient.voiceService: VoiceService
        get() = TODO("Not yet implemented")
    override val RestClient.webhookService: WebhookService
        get() = TODO("Not yet implemented")
}

suspend fun DiscordClientBuilder.RestBuildPhase.withoutRest() = defineRestBackend {
    NoopRestClient(schedule)
}