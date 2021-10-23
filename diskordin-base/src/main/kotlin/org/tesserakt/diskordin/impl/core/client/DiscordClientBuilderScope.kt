package org.tesserakt.diskordin.impl.core.client

import arrow.fx.coroutines.Schedule
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.InternalTestAPI
import org.tesserakt.diskordin.core.data.json.request.JsonRequest
import org.tesserakt.diskordin.core.entity.builder.BuilderBase
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.rest.RestClient

@JvmInline
value class ShardCount(val v: Int)

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
abstract class DiscordClientBuilderScope : BuilderBase<DiscordClientBuilderScope.DiscordClientSettings>() {
    data class DiscordClientSettings(
        val token: String,
        val cachingEnabled: Boolean,
        val gatewaySettings: GatewayBuilder.GatewaySettings,
        val restSchedule: Schedule<*, *>,
        val restClient: RestClient,
        val gatewayFactory: Gateway.Factory,
        val extensions: Map<BootstrapContext.Extension<*>, BootstrapContext.ExtensionContext>
    ) : JsonRequest()

    protected val discordApiUrl = "https://discord.com"

    protected var token: String? = null
        private set
    protected var cachingEnabled: Boolean = true
    protected var gatewaySettings: GatewayBuilder.GatewaySettings = GatewayBuilder().create()
        private set

    protected var restSchedule: Schedule<*, *> = (Schedule.spaced<Any>(1e9) and Schedule.recurs(5)).jittered()
        private set
    protected abstract val restClient: RestClient
    protected abstract val gatewayFactory: Gateway.Factory
    protected val extensions = mutableMapOf<BootstrapContext.Extension<*>, BootstrapContext.ExtensionContext>()

    operator fun String.unaryPlus() {
        token = this
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
        cachingEnabled = false
    }

    operator fun <E, C> Pair<E, C>.unaryPlus()
            where E : BootstrapContext.Extension<C>,
                  C : BootstrapContext.ExtensionContext {
        extensions += this
    }

    inline fun DiscordClientBuilderScope.token(value: String) = value
    inline fun DiscordClientBuilderScope.disableCaching() = Unit

    @InternalTestAPI
    inline fun DiscordClientBuilderScope.disableTokenVerification() = VerificationStub
    inline fun DiscordClientBuilderScope.gatewaySettings(f: GatewayBuilder.() -> Unit) =
        GatewayBuilder().apply(f)

    inline fun DiscordClientBuilderScope.restRetrySchedule(value: Schedule<*, *>) = value
    inline fun <C : BootstrapContext.ExtensionContext, E : BootstrapContext.Extension<C>> DiscordClientBuilderScope.install(
        type: E,
        value: () -> C
    ) = type to value()

    object VerificationStub
    object CompressionStub
}