package org.tesserakt.diskordin.gateway.shard

import org.tesserakt.diskordin.gateway.GatewayConnection

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

    fun isReady() = this::sessionId.isInitialized
}