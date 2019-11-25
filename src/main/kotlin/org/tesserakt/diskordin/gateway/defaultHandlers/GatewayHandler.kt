package org.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.gateway.Gateway

abstract class GatewayHandler {
    @ExperimentalCoroutinesApi
    protected abstract val gateway: Gateway<*>
}
