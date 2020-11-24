package org.tesserakt.diskordin.core.client

import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import kotlin.coroutines.CoroutineContext

data class GatewayContext(
    val scheduler: CoroutineContext,
    val interceptors: List<Interceptor<out Interceptor.Context>>
) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<GatewayContext>
}