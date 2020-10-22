package org.tesserakt.diskordin.rest

import arrow.Kind
import arrow.core.Id
import arrow.core.Option
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import arrow.typeclasses.Functor
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.DiscordResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.rest.service.*
import retrofit2.Retrofit
import retrofit2.create

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
class RestClient(
    private val schedule: Schedule<*, *>,
    private val _channelService: ChannelService,
    private val _emojiService: EmojiService,
    private val _gatewayService: GatewayService,
    private val _guildService: GuildService,
    private val _inviteService: InviteService,
    private val _userService: UserService,
    private val _voiceService: VoiceService,
    private val _webhookService: WebhookService
) {
    companion object {
        fun byRetrofit(retrofit: Retrofit, schedule: Schedule<*, *>) = RestClient(
            schedule,
            retrofit.create(),
            retrofit.create(),
            retrofit.create(),
            retrofit.create(),
            retrofit.create(),
            retrofit.create(),
            retrofit.create(),
            retrofit.create()
        )
    }

    val RestClient.channelService: ChannelService
        get() = _channelService
    val RestClient.emojiService: EmojiService
        get() = _emojiService
    val RestClient.gatewayService: GatewayService
        get() = _gatewayService
    val RestClient.guildService: GuildService
        get() = _guildService
    val RestClient.inviteService: InviteService
        get() = _inviteService
    val RestClient.userService: UserService
        get() = _userService
    val RestClient.voiceService: VoiceService
        get() = _voiceService
    val RestClient.webhookService: WebhookService
        get() = _webhookService

    @Suppress("UNCHECKED_CAST")
    suspend fun <R> callRaw(
        f: suspend RestClient.() -> R
    ): R = retry(schedule as Schedule<R, *>) { this.f() }

    suspend fun <G, C : UnwrapContext, E : IDiscordObject, R : DiscordResponse<E, C>> Functor<G>.call(
        ctx: C,
        f: suspend RestClient.() -> Kind<G, R>
    ) = callRaw(f).map { it.unwrap(ctx) }

    suspend fun <E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.EmptyContext>> call(
        f: suspend RestClient.() -> Id<R>
    ) = callRaw(f).extract().unwrap(UnwrapContext.EmptyContext)

    suspend fun effect(
        f: suspend RestClient.() -> Unit
    ) = callRaw { f() }
}

suspend fun <G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.EmptyContext>> RestClient.call(
    FN: Functor<G>,
    f: suspend RestClient.() -> Kind<G, R>
) = FN.call(UnwrapContext.EmptyContext, f)

suspend fun <G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.GuildContext>> RestClient.call(
    guildId: Snowflake,
    FN: Functor<G>,
    f: suspend RestClient.() -> Kind<G, R>
) = FN.call(UnwrapContext.GuildContext(guildId), f)

suspend fun <G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.PartialGuildContext>> RestClient.call(
    guildId: Option<Snowflake>,
    FN: Functor<G>,
    f: suspend RestClient.() -> Kind<G, R>
) = FN.call(UnwrapContext.PartialGuildContext(guildId.orNull()), f)