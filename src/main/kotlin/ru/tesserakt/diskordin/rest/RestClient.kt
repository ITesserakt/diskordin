package ru.tesserakt.diskordin.rest

import retrofit2.Retrofit
import retrofit2.create
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.rest.service.*

class RestClient(retrofit: Retrofit, private val discordClient: IDiscordClient) {
    val channelService = retrofit.create<ChannelService>()
    val emojiService = retrofit.create<EmojiService>()
    val gatewayService = retrofit.create<GatewayService>()
    val guildService = retrofit.create<GuildService>()
    val inviteService = retrofit.create<InviteService>()
    val userService = retrofit.create<UserService>()
    val voiceService = retrofit.create<VoiceService>()
    val webhookService = retrofit.create<WebhookService>()
}