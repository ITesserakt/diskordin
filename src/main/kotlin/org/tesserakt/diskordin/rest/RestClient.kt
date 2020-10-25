package org.tesserakt.diskordin.rest

import arrow.Kind
import arrow.core.Id
import arrow.core.Option
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.callback
import arrow.typeclasses.Functor
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.DiscordResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.rest.service.*

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
abstract class RestClient(
    private val schedule: Schedule<*, *>
) {
    companion object;

    abstract val RestClient.channelService: ChannelService
    abstract val RestClient.emojiService: EmojiService
    abstract val RestClient.gatewayService: GatewayService
    abstract val RestClient.guildService: GuildService
    abstract val RestClient.inviteService: InviteService
    abstract val RestClient.userService: UserService
    abstract val RestClient.voiceService: VoiceService
    abstract val RestClient.webhookService: WebhookService
    abstract val RestClient.templateService: TemplateService

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

fun <E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.EmptyContext>> RestClient.stream(
    f: suspend RestClient.() -> Iterable<R>
) = Stream.callback {
    callRaw(f).map { it.unwrap() }.forEach(::emit)
    end()
}

fun <E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.GuildContext>> RestClient.stream(
    guildId: Snowflake,
    f: suspend RestClient.() -> Iterable<R>
) = Stream.callback {
    callRaw(f).map { it.unwrap(guildId) }.forEach(::emit)
    end()
}

fun <E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.PartialGuildContext>> RestClient.stream(
    guildId: Option<Snowflake>,
    f: suspend RestClient.() -> Iterable<R>
) = Stream.callback {
    callRaw(f).map { it.unwrap(guildId.orNull()) }.forEach(::emit)
    end()
}