package ru.tesserakt.diskordin.rest

import arrow.Kind
import arrow.core.Option
import arrow.fx.typeclasses.Async
import arrow.integrations.retrofit.adapter.CallK
import arrow.integrations.retrofit.adapter.unwrapBody
import arrow.typeclasses.Functor
import retrofit2.Retrofit
import retrofit2.create
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.DiscordResponse
import ru.tesserakt.diskordin.core.data.json.response.UnwrapContext
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.rest.service.*

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
class RestClient<F>(retrofit: Retrofit, private val discordClient: IDiscordClient, private val A: Async<F>) {
    private val _channelService = retrofit.create<ChannelService>()
    private val _emojiService = retrofit.create<EmojiService>()
    private val _gatewayService = retrofit.create<GatewayService>()
    private val _guildService = retrofit.create<GuildService>()
    private val _inviteService = retrofit.create<InviteService>()
    private val _userService = retrofit.create<UserService>()
    private val _voiceService = retrofit.create<VoiceService>()
    private val _webhookService = retrofit.create<WebhookService>()

    val RestClient<F>.channelService: ChannelService
        get() = _channelService
    val RestClient<F>.emojiService: EmojiService
        get() = _emojiService
    val RestClient<F>.gatewayService: GatewayService
        get() = _gatewayService
    val RestClient<F>.guildService: GuildService
        get() = _guildService
    val RestClient<F>.inviteService: InviteService
        get() = _inviteService
    val RestClient<F>.userService: UserService
        get() = _userService
    val RestClient<F>.voiceService: VoiceService
        get() = _voiceService
    val RestClient<F>.webhookService: WebhookService
        get() = _webhookService

    fun <R> callRaw(
        f: RestClient<F>.() -> CallK<out R>
    ) = A.fx.async {
        val call = f().async(this).bind()
        call.unwrapBody(this).bind()
    }

    fun <G, C : UnwrapContext, E : IDiscordObject, R : DiscordResponse<E, C>> Functor<G>.call(
        ctx: C,
        f: RestClient<F>.() -> CallK<out Kind<G, R>>
    ) = A.fx.monad {
        val call = callRaw(f).bind()
        call.map { it.unwrap(ctx) }
    }

    fun effect(
        f: RestClient<F>.() -> CallK<Unit>
    ): Kind<F, Unit> = A.run {
        callRaw { f() }.handleError {
            if (it is IllegalStateException) Unit
        }
    }
}

fun <F, G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.EmptyContext>> RestClient<F>.call(
    FN: Functor<G>,
    f: RestClient<F>.() -> CallK<out Kind<G, R>>
) = FN.call(UnwrapContext.EmptyContext, f)

fun <F, G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.GuildContext>> RestClient<F>.call(
    guildId: Snowflake,
    FN: Functor<G>,
    f: RestClient<F>.() -> CallK<out Kind<G, R>>
) = FN.call(UnwrapContext.GuildContext(guildId), f)

fun <F, G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.PartialGuildContext>> RestClient<F>.call(
    guildId: Option<Snowflake>,
    FN: Functor<G>,
    f: RestClient<F>.() -> CallK<out Kind<G, R>>
) = FN.call(UnwrapContext.PartialGuildContext(guildId.orNull()), f)