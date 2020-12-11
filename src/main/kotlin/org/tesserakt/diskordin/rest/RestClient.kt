package org.tesserakt.diskordin.rest

import arrow.Kind
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.callback
import arrow.typeclasses.Functor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.core.cache.CacheProcessor
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.DiscordResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.rest.service.*
import kotlin.experimental.ExperimentalTypeInference

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
abstract class RestClient(
    private val schedule: Schedule<*, *>
) : BootstrapContext.ExtensionContext, IDiscordObject {
    companion object : BootstrapContext.PersistentExtension<RestClient>;

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
    ): R = retry(schedule as Schedule<Throwable, R>) { this.f() }

    suspend inline fun <G, C : UnwrapContext, E : IDiscordObject, R : DiscordResponse<E, C>> Functor<G>.call(
        ctx: C,
        crossinline f: suspend RestClient.() -> Kind<G, R>
    ) = callRaw { this.f() }.map { it.unwrap(ctx) }

    suspend inline fun <E : IDiscordObject, reified R : DiscordResponse<E, UnwrapContext.EmptyContext>> call(
        crossinline f: suspend RestClient.() -> R
    ) = callRaw { this.f() }.unwrap(UnwrapContext.EmptyContext)

    suspend inline fun effect(
        crossinline f: suspend RestClient.() -> Unit
    ) = callRaw { this.f() }
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

@OptIn(ExperimentalTypeInference::class)
@JvmName("callNullable")
@OverloadResolutionByLambdaReturnType
suspend fun <G, E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.PartialGuildContext>> RestClient.call(
    guildId: Snowflake?,
    FN: Functor<G>,
    f: suspend RestClient.() -> Kind<G, R>
) = FN.call(UnwrapContext.PartialGuildContext(guildId), f)

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

@OptIn(ExperimentalTypeInference::class)
@JvmName("streamNullable")
@OverloadResolutionByLambdaReturnType
fun <E : IDiscordObject, R : DiscordResponse<E, UnwrapContext.PartialGuildContext>> RestClient.stream(
    guildId: Snowflake?,
    f: suspend RestClient.() -> Iterable<R>
) = Stream.callback {
    callRaw(f).map { it.unwrap(guildId) }.forEach(::emit)
    end()
}

@OptIn(ExperimentalCoroutinesApi::class)
internal suspend inline fun <R : DiscordResponse<O, UnwrapContext.EmptyContext>, reified O : IDiscordObject> RestClient.callCaching(
    crossinline f: suspend RestClient.() -> R
) = callRaw { this.f() }.unwrap().also {
    val processor = client.context[CacheProcessor]
    if (processor.hasHandler(O::class))
        processor.updateData(it)
}

@OptIn(ExperimentalCoroutinesApi::class)
internal suspend inline fun <R : DiscordResponse<O, UnwrapContext.GuildContext>, reified O : IDiscordObject> RestClient.callCaching(
    guildId: Snowflake,
    crossinline f: suspend RestClient.() -> R
) = callRaw { this.f() }.unwrap(guildId).also {
    val processor = client.context[CacheProcessor]
    if (processor.hasHandler(O::class))
        processor.updateData(it)
}