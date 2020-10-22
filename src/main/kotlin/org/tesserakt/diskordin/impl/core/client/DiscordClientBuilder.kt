package org.tesserakt.diskordin.impl.core.client

import arrow.core.Eval
import arrow.core.computations.either
import arrow.core.extensions.eval.applicative.just
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.seconds
import arrow.syntax.function.partially1
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.EntityCache
import org.tesserakt.diskordin.core.data.EntitySifter
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.`object`.IGatewayStats
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.DomainError
import org.tesserakt.diskordin.util.NoopMap
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.enums.or
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

inline class ShardCount(val v: Int)

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var httpClient: Eval<OkHttpClient> = Eval.later(::defaultHttpClient)
    private var token: String = "Invalid"
    private var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
    private var gatewaySettings: GatewayBuilder.GatewaySettings = GatewayBuilder().create()
    private var restSchedule: Schedule<*, *> = (Schedule.spaced<Any>(1.seconds) and Schedule.recurs(5)).jittered()

    operator fun String.unaryPlus() {
        token = this
    }

    operator fun MutableMap<Snowflake, IEntity>.unaryPlus() {
        cache = this
    }

    operator fun VerificationStub.unaryPlus() {
        token = "NTQ3NDg5MTA3NTg1MDA3NjM2.123456.123456789"
    }

    operator fun Eval<OkHttpClient>.unaryPlus() {
        httpClient = this
    }

    operator fun GatewayBuilder.unaryPlus() {
        this@DiscordClientBuilder.gatewaySettings = this.create()
    }

    operator fun Schedule<*, *>.unaryPlus() {
        restSchedule = this
    }

    operator fun Unit.unaryPlus() {
        cache = NoopMap()
    }

    inline fun DiscordClientBuilder.context(coroutineContext: CoroutineContext) = coroutineContext
    inline fun DiscordClientBuilder.overrideHttpClient(client: Eval<OkHttpClient>) = client
    inline fun DiscordClientBuilder.overrideHttpClient(noinline client: () -> OkHttpClient): Eval<OkHttpClient> =
        Eval.later(client)

    inline fun DiscordClientBuilder.token(value: String) = value
    inline fun DiscordClientBuilder.withCache(value: MutableMap<Snowflake, IEntity>) = value
    inline fun DiscordClientBuilder.disableCaching() = Unit

    @InternalTestAPI
    inline fun DiscordClientBuilder.disableTokenVerification() = VerificationStub
    inline fun DiscordClientBuilder.gatewaySettings(f: GatewayBuilder.() -> Unit) =
        GatewayBuilder().apply(f)

    inline fun DiscordClientBuilder.restRetrySchedule(value: Schedule<*, *>) = value

    @RequiresOptIn("This statement should be used only in tests")
    annotation class InternalTestAPI
    object VerificationStub
    object CompressionStub

    companion object {
        suspend operator fun invoke(
            init: suspend DiscordClientBuilder.() -> Unit = {}
        ) = either<DomainError, IDiscordClient> {
            val builder = DiscordClientBuilder().apply { this.init() }

            val token = System.getenv("token") ?: builder.token

            val httpClient = builder.httpClient.map {
                it.newBuilder().addInterceptor(AuthorityInterceptor(token)).build()
            }
            val retrofit = httpClient.map(::setupRetrofit.partially1("https://discord.com/api/"))
            val rest = retrofit.map { RestClient.byRetrofit(it, builder.restSchedule) }.memoize()
            val gatewayInfo = rest.map { runBlocking { it.call { gatewayService.getGatewayBot() } } }.memoize()

            val shardSettings = formShardSettings(token, builder, gatewayInfo)
            val connectionContext = formConnectionSettings(httpClient, builder, shardSettings)
            val gatewayContext = formGatewaySettings(builder, connectionContext)
            val globalContext = formBootstrapContext(builder, rest, gatewayContext)

            DiscordClient(globalContext).bind()
        }

        private fun formBootstrapContext(
            builder: DiscordClientBuilder,
            rest: Eval<RestClient>,
            gatewayContext: BootstrapContext.Gateway
        ): BootstrapContext {
            val strategy = builder.gatewaySettings.intents

            val intents: ValuedEnum<Intents, Short> = if (strategy is IntentsStrategy.EnableOnly)
                strategy.enabled
                    .map { (_, code) -> ValuedEnum<Intents, Short>(code, Short.integral()) }
                    .fold(ValuedEnum.none(Short.integral())) { acc, i -> acc or i }
            else {
                ValuedEnum.all(Short.integral())
            }

            return BootstrapContext(
                EntityCache(EntitySifter(intents), builder.cache),
                rest,
                gatewayContext
            )
        }

        private fun formGatewaySettings(
            builder: DiscordClientBuilder,
            connectionContext: BootstrapContext.Gateway.Connection
        ): BootstrapContext.Gateway = BootstrapContext.Gateway(
            builder.gatewaySettings.coroutineContext,
            builder.gatewaySettings.interceptors,
            connectionContext
        )

        private fun formConnectionSettings(
            httpClient: Eval<OkHttpClient>,
            builder: DiscordClientBuilder,
            shardSettings: BootstrapContext.Gateway.Connection.ShardSettings
        ): BootstrapContext.Gateway.Connection = BootstrapContext.Gateway.Connection(
            httpClient,
            "wss://gateway.discord.gg",
            builder.gatewaySettings.compression,
            shardSettings
        )

        private fun formShardSettings(
            token: String,
            builder: DiscordClientBuilder,
            gatewayInfo: Eval<IGatewayStats>
        ): BootstrapContext.Gateway.Connection.ShardSettings = BootstrapContext.Gateway.Connection.ShardSettings(
            token,
            builder.gatewaySettings.shardCount.takeIf { it != 0 }?.just() ?: gatewayInfo.map { it.shards },
            builder.gatewaySettings.compressionStrategy,
            builder.gatewaySettings.guildSubscriptionsStrategy,
            builder.gatewaySettings.threshold,
            builder.gatewaySettings.initialPresence,
            builder.gatewaySettings.intents
        )
    }
}