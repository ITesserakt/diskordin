package ru.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.tesserakt.diskordin.gateway.Gateway

abstract class GatewayHandler {
    @ExperimentalCoroutinesApi
    protected abstract val gateway: Gateway
}
