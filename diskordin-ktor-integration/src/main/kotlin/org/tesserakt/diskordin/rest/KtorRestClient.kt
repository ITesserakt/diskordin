package org.tesserakt.diskordin.rest

import arrow.core.Eval
import arrow.fx.coroutines.Schedule
import io.ktor.client.*
import org.tesserakt.diskordin.rest.service.*

class KtorRestClient(
    _ktor: Eval<HttpClient>,
    private val discordApiUrl: String,
    schedule: Schedule<*, *>
) : RestClient(schedule) {
    private val ktor by lazy { _ktor.memoize().extract() }

    private val _channelService: ChannelService by lazy { ChannelServiceImpl(ktor, discordApiUrl) }
    private val _gatewayService: GatewayService by lazy { GatewayServiceImpl(ktor, discordApiUrl) }
    private val _emojiService: EmojiService by lazy { EmojiServiceImpl(ktor, discordApiUrl) }
    private val _guildService: GuildService by lazy { GuildServiceImpl(ktor, discordApiUrl) }
    private val _inviteService: InviteService by lazy { InviteServiceImpl(ktor, discordApiUrl) }
    private val _userService: UserService by lazy { UserServiceImpl(ktor, discordApiUrl) }
    private val _voiceService: VoiceService by lazy { VoiceServiceImpl(ktor, discordApiUrl) }
    private val _webhookService: WebhookService by lazy { WebhookServiceImpl(ktor, discordApiUrl) }
    private val _templateService: TemplateService by lazy { TemplateServiceImpl(ktor, discordApiUrl) }

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