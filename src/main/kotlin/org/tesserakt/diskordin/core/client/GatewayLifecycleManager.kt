package org.tesserakt.diskordin.core.client

import org.tesserakt.diskordin.gateway.GatewayConnection

interface GatewayLifecycleManager {
    suspend fun start()
    suspend fun restart()
    suspend fun stop(code: Short, message: String)

    val connection: GatewayConnection
}