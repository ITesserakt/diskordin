@file:Suppress("PropertyName")

package org.tesserakt.diskordin.gateway.shard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.gateway.GatewayLifecycleManager
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class Shard(
    val token: String,
    val shardData: Data,
    internal val lifecycle: GatewayLifecycleManager
) {
    data class Data(val index: Int, val total: Int)
    enum class State {
        Disconnected, Connecting, Connected, Handshaking, Closing
    }

    internal val _state: MutableStateFlow<State> = MutableStateFlow(State.Disconnected)
    val state: StateFlow<State> = _state

    internal val _heartbeats = MutableStateFlow<Instant?>(null)
    internal val _heartbeatACKs = MutableStateFlow<Instant?>(null)
    internal val _sessionId = MutableStateFlow<String?>(null)
    val sessionId: StateFlow<String?> = _sessionId
    internal val _sequence = MutableStateFlow<Int?>(null)
    val sequence: StateFlow<Int?> = _sequence

    @ExperimentalTime
    fun ping() = if (_heartbeatACKs.value != null && _heartbeats.value != null)
        (_heartbeatACKs.value!! - _heartbeats.value!!).absoluteValue
    else -Duration.INFINITE

    fun isReady() = sessionId.value != null
}