package org.tesserakt.diskordin.rest.integration

import arrow.core.Eval
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.util.gsonBuilder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class KtorService(private val httpClient: Eval<OkHttpClient>) :
    ReadOnlyProperty<Nothing?, Eval<HttpClient>> {
    override fun getValue(thisRef: Nothing?, property: KProperty<*>) = httpClient.map {
        HttpClient(OkHttp) {
            engine {
                preconfigured = it
            }

            install(JsonFeature) {
                serializer = GsonSerializer(gsonBuilder)
            }
        }
    }
}

fun HttpRequestBuilder.reasonHeader(reason: String?) = header("X-Audit-Log-Reason", reason)

fun HttpRequestBuilder.parameters(query: Map<String, String>) = query.forEach { (k, v) ->
    parameter(k, v)
}