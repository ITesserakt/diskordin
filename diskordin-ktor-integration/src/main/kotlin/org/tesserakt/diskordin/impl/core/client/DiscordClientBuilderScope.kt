package org.tesserakt.diskordin.impl.core.client

import arrow.core.Eval
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.util.*
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.integration.WebSocketFactory
import org.tesserakt.diskordin.rest.KtorRestClient
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.gsonBuilder
import java.util.*
import io.ktor.client.HttpClient as KHttpClient

@JvmInline
value class HttpClient(private val inner: Eval<KHttpClient>) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<HttpClient>

    operator fun invoke() = inner.value()
}

@Suppress("unused")
class KtorScope<T : HttpClientEngineConfig>(private val engineFactory: HttpClientEngineFactory<T>) :
    DiscordClientBuilderScope() {
    companion object {
        @JvmStatic
        private val diskordinVersion = try {
            PropertyResourceBundle.getBundle("gradle.properties").getString("diskordin_version")
        } catch (e: Throwable) {
            "test environment"
        }
    }

    override lateinit var gatewayFactory: Gateway.Factory private set
    override lateinit var restClient: RestClient private set
    private lateinit var httpClient: Eval<KHttpClient>

    @OptIn(KtorExperimentalAPI::class)
    private val config: HttpClientConfig<T> = HttpClientConfig<T>().apply {
        install(JsonFeature) { serializer = GsonSerializer(gsonBuilder) }
        install(UserAgent) { agent = "Discord bot (Diskordin ${diskordinVersion})" }
        install(WebSockets)
    }

    override fun create(): DiscordClientSettings {
        val token = System.getenv("DISKORDIN_TOKEN") ?: token ?: error(DiscordClientBuilder.NoTokenProvided)
        +httpClientConfig {
            defaultRequest { header("Authorization", "Bot $token") }
        }
        httpClient = Eval.later { KHttpClient(engineFactory.create(), config) }
        +install(HttpClient) { HttpClient(httpClient) }
        restClient = KtorRestClient(httpClient, discordApiUrl, restSchedule)
        gatewayFactory = WebSocketFactory(httpClient, gatewaySettings.coroutineContext)

        return DiscordClientSettings(
            token,
            cachingEnabled,
            gatewaySettings,
            restSchedule,
            restClient,
            gatewayFactory,
            extensions
        )
    }

    operator fun HttpClientConfig<T>.unaryPlus() {
        config += this
    }

    fun KtorScope<T>.httpClientConfig(block: HttpClientConfig<T>.() -> Unit) = HttpClientConfig<T>().apply(block)
}