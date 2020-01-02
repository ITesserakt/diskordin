package org.tesserakt.diskordin.rest

import arrow.Kind
import arrow.core.Id
import arrow.core.Option
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.fx.typeclasses.Async
import arrow.integrations.retrofit.adapter.CallK
import arrow.integrations.retrofit.adapter.unwrapBody
import arrow.typeclasses.Functor
import arrow.typeclasses.MonadSyntax
import org.tesserakt.diskordin.core.data.RateLimitException
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.DiscordResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.rest.service.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.create

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
class RestClient<F>(
    private val A: Async<F>,
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
        fun <F> byRetrofit(retrofit: Retrofit, A: Async<F>) = RestClient(
            A,
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
    ) = A.run {
        this@RestClient.f().async(this).flatMap {
            it.unwrapBody(this)
        }.handleErrorWith {
            if (it is HttpException && it.code() == 429)
                raiseError<R>(RateLimitException(it.message()))
            raiseError(it)
        }
    }

    fun <G, C : UnwrapContext, E : IDiscordObject, R : DiscordResponse<E, C>> Functor<G>.call(
        ctx: C,
        f: RestClient<F>.() -> CallK<out Kind<G, R>>
    ) = A.fx.monad {
        val call = callRaw(f).bind()
        call.map { it.unwrap(ctx) }
    }

    fun <E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.EmptyContext>> call(
        f: RestClient<F>.() -> CallK<Id<R>>
    ) = A.run { this@RestClient.call(Id.functor(), f).map { it.extract() } }

    fun effect(
        f: RestClient<F>.() -> CallK<Unit>
    ): Kind<F, Unit> = A.run {
        callRaw { f() }.handleError {
            if (it is IllegalStateException) Unit
        }
    }

    fun <A> monad(f: suspend MonadSyntax<F>.() -> A) = A.fx.monad(f)
}

fun <F, G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.EmptyContext>> RestClient<F>.call(
    FN: Functor<G>,
    f: RestClient<F>.() -> CallK<out Kind<G, R>>
): Kind<F, Kind<G, E>> = FN.call(UnwrapContext.EmptyContext, f)

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