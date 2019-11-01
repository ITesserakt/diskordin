package ru.tesserakt.diskordin.rest

import arrow.Kind
import arrow.core.Option
import arrow.fx.typeclasses.Async
import arrow.integrations.retrofit.adapter.CallK
import arrow.integrations.retrofit.adapter.unwrapBody
import arrow.mtl.Kleisli
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
class RestClient<F>(retrofit: Retrofit, private val discordClient: IDiscordClient, A: Async<F>) : Async<F> by A {
    val channelService = retrofit.create<ChannelService>()
    val emojiService = retrofit.create<EmojiService>()
    val gatewayService = retrofit.create<GatewayService>()
    val guildService = retrofit.create<GuildService>()
    val inviteService = retrofit.create<InviteService>()
    val userService = retrofit.create<UserService>()
    val voiceService = retrofit.create<VoiceService>()
    val webhookService = retrofit.create<WebhookService>()

    inline fun <G, C : UnwrapContext, E : IDiscordObject, R : DiscordResponse<E, C>> Functor<G>.call(
        ctx: C,
        crossinline f: RestClient<F>.() -> CallK<out Kind<G, R>>
    ) = Kleisli<F, C, Kind<G, E>> { c ->
        fx.async {
            val call = f().async(this).bind()
            val response = call.unwrapBody(this).bind()
            response.map { it.unwrap(c) }
        }
    }.run(ctx)
}

inline fun <F, G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.EmptyContext>> RestClient<F>.call(
    FN: Functor<G>,
    crossinline f: RestClient<F>.() -> CallK<out Kind<G, R>>
) = FN.call(UnwrapContext.EmptyContext, f)

inline fun <F, G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.GuildContext>> RestClient<F>.call(
    guildId: Snowflake,
    FN: Functor<G>,
    crossinline f: RestClient<F>.() -> CallK<out Kind<G, R>>
) = FN.call(UnwrapContext.GuildContext(guildId), f)

inline fun <F, G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.PartialGuildContext>> RestClient<F>.call(
    guildId: Option<Snowflake>,
    FN: Functor<G>,
    crossinline f: RestClient<F>.() -> CallK<out Kind<G, R>>
) = FN.call(UnwrapContext.PartialGuildContext(guildId.orNull()), f)