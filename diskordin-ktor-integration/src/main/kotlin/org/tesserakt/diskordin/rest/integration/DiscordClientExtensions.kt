package org.tesserakt.diskordin.rest.integration

import io.ktor.client.engine.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.impl.core.client.KtorScope

suspend inline fun <T : HttpClientEngineConfig> DiscordClientBuilder.ktorBackend(
    engine: HttpClientEngineFactory<T>,
    noinline block: KtorScope<T>.() -> Unit
) = invoke({ KtorScope(engine) }, block)

fun HttpRequestBuilder.reasonHeader(reason: String?) = header("X-Audit-Log-Reason", reason)

fun HttpRequestBuilder.parameters(query: Map<String, String>) = query.forEach { (k, v) ->
    parameter(k, v)
}