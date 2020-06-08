package org.tesserakt.diskordin.impl.core.client

import arrow.core.Eval
import arrow.core.extensions.eval.applicative.just
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Schedule
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.monad.monad
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.fix
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.seconds
import arrow.syntax.function.partially1
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
import org.tesserakt.diskordin.impl.util.typeclass.suspended
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.NoopMap
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.enums.or
import org.tesserakt.diskordin.util.typeclass.Suspended
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

inline class ShardCount(val v: Int)

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder<F> private constructor(val CC: Concurrent<F>) {
    private var httpClient: Eval<OkHttpClient> = Eval.later(::defaultHttpClient)
    private var token: String = "Invalid"
    private var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
    private var gatewaySettings: GatewayBuilder.GatewaySettings<F> = GatewayBuilder(CC).create()
    private var restSchedule: Schedule<ForIO, Throwable, *> = Schedule.withMonad(IO.monad()) {
        (spaced<Throwable>(1.seconds) and recurs(5)).jittered(IO.monadDefer())
    }

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

    operator fun GatewayBuilder<F>.unaryPlus() {
        this@DiscordClientBuilder.gatewaySettings = this.create()
    }

    operator fun Schedule<ForIO, Throwable, *>.unaryPlus() {
        restSchedule = this
    }

    operator fun Unit.unaryPlus() {
        cache = NoopMap()
    }

    inline fun DiscordClientBuilder<F>.context(coroutineContext: CoroutineContext) = coroutineContext
    inline fun DiscordClientBuilder<F>.overrideHttpClient(client: Eval<OkHttpClient>) = client
    inline fun DiscordClientBuilder<F>.overrideHttpClient(noinline client: () -> OkHttpClient): Eval<OkHttpClient> =
        Eval.later(client)

    inline fun DiscordClientBuilder<F>.token(value: String) = value
    inline fun DiscordClientBuilder<F>.withCache(value: MutableMap<Snowflake, IEntity>) = value
    inline fun DiscordClientBuilder<F>.disableCaching() = Unit

    @InternalTestAPI
    inline fun DiscordClientBuilder<F>.disableTokenVerification() = VerificationStub
    inline fun <F> DiscordClientBuilder<F>.gatewaySettings(f: GatewayBuilder<F>.() -> Unit) =
        GatewayBuilder(CC).apply(f)

    inline fun DiscordClientBuilder<F>.restRetrySchedule(value: Schedule<ForIO, Throwable, *>) = value

    @RequiresOptIn("This statement should be used only in tests")
    annotation class InternalTestAPI
    object VerificationStub
    object CompressionStub

    companion object {
        operator fun <F> invoke(
            CC: Concurrent<F>,
            effectRunner: Suspended<F>,
            init: DiscordClientBuilder<F>.() -> Unit = {}
        ): IDiscordClient {
            val builder = DiscordClientBuilder(CC).apply(init)
            val token = System.getenv("token") ?: builder.token
            val httpClient = builder.httpClient.map {
                it.newBuilder()
                    .addInterceptor(AuthorityInterceptor(token))
                    .build()
            }
            val retrofit = httpClient.map(::setupRetrofit.partially1("https://discord.com/api/"))
            val rest = retrofit
                .map { RestClient.byRetrofit(it, builder.restSchedule, IO.concurrent()) }.memoize()
            val gatewayInfo = rest.map { it.call { gatewayService.getGatewayBot() }.fix().unsafeRunSync() }.memoize()

            val shardSettings = formShardSettings(token, builder, gatewayInfo)
            val connectionContext = formConnectionSettings(httpClient, builder, shardSettings)
            val gatewayContext = formGatewaySettings(builder, CC, effectRunner, connectionContext)
            val globalContext = formBootstrapContext(builder, rest, gatewayContext)
            return DiscordClient(globalContext).unsafeRunSync()
        }

        private fun <F> formBootstrapContext(
            builder: DiscordClientBuilder<F>,
            rest: Eval<RestClient<ForIO>>,
            gatewayContext: BootstrapContext.Gateway<F>
        ): BootstrapContext<ForIO, F> {
            val strategy = builder.gatewaySettings.intents

            val intents: ValuedEnum<Intents, Short> = if (strategy is IntentsStrategy.EnableOnly)
                strategy.enabled
                    .map { (_, code) -> ValuedEnum<Intents, Short>(code, Short.integral()) }
                    .fold(ValuedEnum(0, Short.integral())) { acc, i -> acc or i }
            else {
                ValuedEnum.all(Short.integral())
            }

            return BootstrapContext(
                EntityCache(EntitySifter(intents), builder.cache),
                rest,
                gatewayContext
            )
        }

        private fun <F> formGatewaySettings(
            builder: DiscordClientBuilder<F>,
            CC: Concurrent<F>,
            runner: Suspended<F>,
            connectionContext: BootstrapContext.Gateway.Connection
        ): BootstrapContext.Gateway<F> = BootstrapContext.Gateway(
            builder.gatewaySettings.coroutineContext,
            builder.gatewaySettings.interceptors,
            CC,
            runner,
            connectionContext
        )

        private fun <F> formConnectionSettings(
            httpClient: Eval<OkHttpClient>,
            builder: DiscordClientBuilder<F>,
            shardSettings: BootstrapContext.Gateway.Connection.ShardSettings
        ): BootstrapContext.Gateway.Connection = BootstrapContext.Gateway.Connection(
            httpClient,
            "wss://gateway.discord.gg",
            builder.gatewaySettings.compression,
            shardSettings
        )

        private fun <F> formShardSettings(
            token: String,
            builder: DiscordClientBuilder<F>,
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

        inline fun default(noinline init: DiscordClientBuilder<ForIO>.() -> Unit) =
            invoke(IO.concurrent(), IO.suspended(), init)
    }
}