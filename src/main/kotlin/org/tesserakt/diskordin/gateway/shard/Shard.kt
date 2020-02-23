package org.tesserakt.diskordin.gateway.shard

import org.tesserakt.diskordin.gateway.GatewayConnection
import java.time.Instant

data class Shard(
    val token: String,
    val shardData: Data,
    val connection: GatewayConnection
) {
    data class Data(val current: Int, val total: Int)
    enum class State {
        Disconnected, Connecting, Connected, Handshaking
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
        lastHeartbeatACK!!.toEpochMilli() - lastHeartbeat!!.toEpochMilli()
    else Long.MIN_VALUE

    fun isReady() = this::sessionId.isInitialized
}