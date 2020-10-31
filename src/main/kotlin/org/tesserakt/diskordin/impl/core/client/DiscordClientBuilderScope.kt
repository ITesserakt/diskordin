package org.tesserakt.diskordin.impl.core.client

import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.seconds
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.NoopMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

inline class ShardCount(val v: Int)

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
abstract class DiscordClientBuilderScope {
    protected val discordApiUrl = "https://discord.com"

    var token: String? = null
        private set
    var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
        private set
    var gatewaySettings: GatewayBuilder.GatewaySettings = GatewayBuilder().create()
        private set
    var restSchedule: Schedule<*, *> = (Schedule.spaced<Any>(1.seconds) and Schedule.recurs(5)).jittered()
        private set
    abstract val restClient: RestClient

    abstract fun DiscordClientBuilder.finalize(): DiscordClientBuilderScope

    operator fun String.unaryPlus() {
        token = this
    }

    operator fun MutableMap<Snowflake, IEntity>.unaryPlus() {
        cache = this
    }

    operator fun VerificationStub.unaryPlus() {
        token = "NTQ3NDg5MTA3NTg1MDA3NjM2.123456.123456789"
    }

    operator fun GatewayBuilder.unaryPlus() {
        this@DiscordClientBuilderScope.gatewaySettings = this.create()
    }

    operator fun Schedule<*, *>.unaryPlus() {
        restSchedule = this
    }

    operator fun Unit.unaryPlus() {
        cache = NoopMap()
    }

    inline fun DiscordClientBuilderScope.context(coroutineContext: CoroutineContext) = coroutineContext

    inline fun DiscordClientBuilderScope.token(value: String) = value
    inline fun DiscordClientBuilderScope.withCache(value: MutableMap<Snowflake, IEntity>) = value
    inline fun DiscordClientBuilderScope.disableCaching() = Unit

    @InternalTestAPI
    inline fun DiscordClientBuilderScope.disableTokenVerification() = VerificationStub
    inline fun DiscordClientBuilderScope.gatewaySettings(f: GatewayBuilder.() -> Unit) =
        GatewayBuilder().apply(f)

    inline fun DiscordClientBuilderScope.restRetrySchedule(value: Schedule<*, *>) = value

    @RequiresOptIn("This statement should be used only in tests")
    annotation class InternalTestAPI
    object VerificationStub
    object CompressionStub
}