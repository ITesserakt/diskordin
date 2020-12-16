package org.tesserakt.diskordin.gateway

import kotlinx.coroutines.CoroutineScope

interface GatewayLifecycleManager {
    suspend fun start()
    suspend fun restart()
    suspend fun stop(code: Short, message: String)

    val connection: GatewayConnection
    val coroutineScope: CoroutineScope
}