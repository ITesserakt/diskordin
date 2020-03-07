package org.tesserakt.diskordin.gateway.shard

import org.tesserakt.diskordin.core.client.GatewayLifecycleManager
import org.tesserakt.diskordin.gateway.GatewayConnection
import java.time.Instant
import kotlin.math.abs

data class Shard(
    val token: String,
    val shardData: Data,
    val connection: GatewayConnection,
    val lifecycle: GatewayLifecycleManager
) {
    data class Data(val current: Int, val total: Int)
    enum class State {
        Disconnected, Connecting, Connected, Handshaking, Invalidated
    }

    lateinit var sessionId: String internal set
    var sequence: Int? = null
        internal set
    var state: State = State.Disconnected
        internal set
    var lastHeartbeat: Instant? = null
        internal set
    var lastHeartbeatACK: Instant? = null
        internal set

    fun ping() = if (lastHeartbeatACK != null && lastHeartbeat != null)
        abs(lastHeartbeatACK!!.toEpochMilli() - lastHeartbeat!!.toEpochMilli())
    else Long.MIN_VALUE

    fun isReady() = this::sessionId.isInitialized
}