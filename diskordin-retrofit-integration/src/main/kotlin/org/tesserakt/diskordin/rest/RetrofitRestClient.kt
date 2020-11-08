package org.tesserakt.diskordin.rest

import arrow.fx.coroutines.Schedule
import org.tesserakt.diskordin.rest.service.*
import retrofit2.Retrofit
import retrofit2.create

class RetrofitRestClient(
    _retrofit: () -> Retrofit,
    schedule: Schedule<*, *>
) : RestClient(schedule) {
    private val retrofit by lazy(_retrofit)

    private val _channelService: ChannelServiceImpl by lazy { retrofit.create() }
    private val _emojiService: EmojiServiceImpl by lazy { retrofit.create() }
    private val _gatewayService: GatewayServiceImpl by lazy { retrofit.create() }
    private val _guildService: GuildServiceImpl by lazy { retrofit.create() }
    private val _inviteService: InviteServiceImpl by lazy { retrofit.create() }
    private val _userService: UserServiceImpl by lazy { retrofit.create() }
    private val _voiceService: VoiceServiceImpl by lazy { retrofit.create() }
    private val _webhookService: WebhookServiceImpl by lazy { retrofit.create() }
    private val _templateService: TemplateServiceImpl by lazy { retrofit.create() }

    override val RestClient.channelService: ChannelService get() = _channelService
    override val RestClient.emojiService: EmojiService get() = _emojiService
    override val RestClient.gatewayService: GatewayService get() = _gatewayService
    override val RestClient.guildService: GuildService get() = _guildService
    override val RestClient.inviteService: InviteService get() = _inviteService
    override val RestClient.userService: UserService get() = _userService
    override val RestClient.voiceService: VoiceService get() = _voiceService
    override val RestClient.webhookService: WebhookService get() = _webhookService
    override val RestClient.templateService: TemplateService get() = _templateService
}