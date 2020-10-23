package org.tesserakt.diskordin.rest

import arrow.core.Eval
import arrow.fx.coroutines.Schedule
import org.tesserakt.diskordin.rest.service.*
import retrofit2.Retrofit
import retrofit2.create

class RetrofitRestClient(
    _retrofit: () -> Retrofit,
    schedule: Schedule<*, *>
) : RestClient(schedule) {
    private val retrofit by lazy(_retrofit)

    private val _channelService: ChannelService by lazy { retrofit.create() }
    private val _emojiService: EmojiService by lazy { retrofit.create() }
    private val _gatewayService: GatewayService by lazy { retrofit.create() }
    private val _guildService: GuildService by lazy { retrofit.create() }
    private val _inviteService: InviteService by lazy { retrofit.create() }
    private val _userService: UserService by lazy { retrofit.create() }
    private val _voiceService: VoiceService by lazy { retrofit.create() }
    private val _webhookService: WebhookService by lazy { retrofit.create() }
    private val _templateService: TemplateService by lazy { retrofit.create() }

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

fun RestClient.Companion.byRetrofit(retrofit: Eval<Retrofit>, schedule: Schedule<*, *>): RestClient =
    RetrofitRestClient({ retrofit.memoize().extract() }, schedule)