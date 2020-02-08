package org.tesserakt.diskordin.core.client

import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.rest.RestClient
import kotlin.coroutines.CoroutineContext

data class BootstrapContext<F>(
    val cache: MutableMap<Snowflake, IEntity>,
    val restClient: RestClient<F>,
    val gatewayContext: Gateway
) {
    data class Gateway(
        val scheduler: CoroutineContext,
        val httpClient: OkHttpClient,
        val lifecycleRegistry: GatewayLifecycleManager,
        val interceptors: List<Interceptor<Interceptor.Context>>,
        val connectionContext: Connection
    ) {
        data class Connection(
            val token: String,
            val url: String,
            val compression: String
        )
    }
}