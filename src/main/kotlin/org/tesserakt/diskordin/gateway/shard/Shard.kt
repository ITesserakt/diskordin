@file:Suppress("PropertyName")

package org.tesserakt.diskordin.gateway.shard

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.tesserakt.diskordin.gateway.GatewayLifecycleManager
import java.time.Instant
import kotlin.math.abs

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalCoroutinesApi
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

    fun ping() = if (_heartbeatACKs.value != null && _heartbeats.value != null)
        abs(_heartbeatACKs.value!!.toEpochMilli() - _heartbeats.value!!.toEpochMilli())
    else Long.MIN_VALUE

    fun isReady() = sessionId.value != null
}