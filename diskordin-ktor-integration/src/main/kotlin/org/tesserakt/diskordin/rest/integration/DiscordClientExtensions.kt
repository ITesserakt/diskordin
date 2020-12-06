package org.tesserakt.diskordin.rest.integration

import io.ktor.client.engine.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.impl.core.client.BackendProvider
import org.tesserakt.diskordin.impl.core.client.KtorScope

@Suppress("FunctionName")
fun <T : HttpClientEngineConfig> Ktor(engine: HttpClientEngineFactory<T>) = BackendProvider { KtorScope(engine) }

fun HttpRequestBuilder.reasonHeader(reason: String?) = header("X-Audit-Log-Reason", reason)

fun HttpRequestBuilder.parameters(query: Map<String, String>) = query.forEach { (k, v) ->
    parameter(k, v)
}