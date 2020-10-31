package org.tesserakt.diskordin.impl.core.client

import arrow.core.Eval
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.rest.KtorRestClient
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.gsonBuilder
import java.util.*

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

    override lateinit var restClient: RestClient private set
    private lateinit var httpClient: Eval<HttpClient>
    private val config: HttpClientConfig<T> = HttpClientConfig<T>().apply {
        install(JsonFeature) { serializer = GsonSerializer(gsonBuilder) }
        install(UserAgent) { agent = "Discord bot (Diskordin ${diskordinVersion})" }
    }

    override fun DiscordClientBuilder.finalize(): DiscordClientBuilderScope = this@KtorScope.apply {
        +httpClientConfig {
            defaultRequest { header("Authorization", "Bot $token") }
        }
        httpClient = Eval.later { HttpClient(engineFactory.create(), config) }
        restClient = KtorRestClient(httpClient, discordApiUrl, restSchedule)
    }

    operator fun HttpClientConfig<T>.unaryPlus() {
        config += this
    }

    fun KtorScope<T>.httpClientConfig(block: HttpClientConfig<T>.() -> Unit) = HttpClientConfig<T>().apply(block)
}