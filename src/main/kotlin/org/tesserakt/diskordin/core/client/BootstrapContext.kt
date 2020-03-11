package org.tesserakt.diskordin.core.client

import arrow.core.Eval
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.rest.RestClient
import kotlin.coroutines.CoroutineContext

data class BootstrapContext<F>(
    val token: String,
    val cache: MutableMap<Snowflake, IEntity>,
    val restClient: Eval<RestClient<F>>,
    val gatewayContext: Gateway
) {
    data class Gateway(
        val scheduler: CoroutineContext,
        val httpClient: Eval<OkHttpClient>,
        val lifecycleRegistry: IGatewayLifecycleManager,
        val connectionContext: Connection
    ) {
        data class Connection(
            val url: String,
            val compression: String
        )
    }
}